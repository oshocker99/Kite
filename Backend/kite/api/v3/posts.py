"""API Endpoints relating to posts"""

from flask import Blueprint
from flask_restful import Api, Resource

from kite.api.response import Error, Fail, Success
from kite.api.v3.parsers.post_parse import post_parser, put_parser
from kite.auth import token_auth_required
from kite.models import Post, Topic, User, db
from kite.settings import LOGGER
from kite.utils import validate_uuid


class PostUpdate(Resource):
    method_decorators = [token_auth_required]

    def get(self, post_id, jwt_payload=None):
        """Get info on a specific post.

        Args:
            post_id: UUID of the post to lookup.
        """
        if not validate_uuid(post_id):
            return Fail("invalid post ID").to_json(), 400
        LOGGER.debug({"Requested Post": post_id})
        post = Post.get_post(post_id)
        if post is not None:
            return Success({"post": post.to_json()}).to_json(), 200
        return Fail(f"post with ID {post_id} not found").to_json(), 404

    def put(self, post_id, jwt_payload=None):
        """Update info for a specific post.

        Args:
            post_id: UUID of the post to update.
        """
        if not validate_uuid(post_id):
            return Fail("invalid post ID").to_json(), 400

        post = Post.get_post(post_id)

        if post.author != jwt_payload.username:
            return Fail(f"Cannot edit other users posts").to_json(), 403

        if post is not None:
            args = put_parser.parse_args(strict=True)
            post.body = args.body
            post.edited = True
            post.save()
            return Success(f"post with ID {post_id} updated").to_json(), 200
        return Fail(f"post with ID {post_id} not found").to_json(), 404

    def delete(self, post_id, jwt_payload=None):
        """Delete a specific post from the database.
        Only available to admin, mod, and author
        Args:
            post_id: UUID of the post to delete.
        """
        post = Post.get_post(post_id)
        if post is None:
            return Fail(f"Post ID {post_id} does not exist").to_json(), 404
        if jwt_payload.username != post.author or not (
            jwt_payload.is_mod or jwt_payload.is_admin
        ):
            return (
                Fail("Permission denied, you can not delete other's posts").to_json(),
                403,
            )
        else:
            if not validate_uuid(post_id):
                return Fail("invalid post ID").to_json(), 400

            if post is not None:
                post.delete()
                return Success(None).to_json(), 204


class Posts(Resource):
    method_decorators = [token_auth_required]

    def get(self, jwt_payload=None):
        """Get list of existing posts."""
        posts = Post.get_all()
        posts_json = [post.to_json() for post in posts]
        return Success({"posts": posts_json}).to_json(), 200

    def post(self, jwt_payload=None):
        """Create a new post.

        Required Args:
            topic: Topic to post to
            body: Body text of post
            title: Title of the post 

        """
        args = post_parser.parse_args(strict=True)
        LOGGER.info({"Args": args})

        user = User.get_user(jwt_payload.username)

        if user is None:
            return Fail(f"author does not exist").to_json(), 404

        if Topic.get_topic(args.topic_name) is None:
            return Fail(f"topic {args.topic_name} does not exist").to_json(), 404

        post = Post(
            title=args.title,
            body=args.body,
            author=user.username,
            topic_name=args.topic_name,
        )
        db.session.add(post)
        db.session.flush()
        post_uuid = post.id
        db.session.commit()

        user.post_count += 1
        user.save()

        return Success(post.to_json()).to_json(), 201


posts_bp_v3 = Blueprint("posts v3", __name__)
api = Api(posts_bp_v3)
api.add_resource(PostUpdate, "/api/v3/posts/<string:post_id>")
api.add_resource(Posts, "/api/v3/posts")
