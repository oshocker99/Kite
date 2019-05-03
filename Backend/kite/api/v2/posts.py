"""API Endpoints relating to posts"""

from flask import Blueprint
from flask_restful import Api, Resource

from kite.api.response import Error, Fail, Success
from kite.api.v2.parsers.post_parse import post_parser, put_parser
from kite.models import Post, Topic, User, db
from kite.settings import LOGGER
from kite.utils import validate_uuid


class PostUpdate(Resource):
    def get(self, post_id):
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

    def put(self, post_id):
        """Update info for a specific post.

        Args:
            post_id: UUID of the post to update.
        """
        if not validate_uuid(post_id):
            return Fail("invalid post ID").to_json(), 400

        post = Post.get_post(post_id)
        if post is not None:
            args = put_parser.parse_args(strict=True)
            post.body = args.body
            post.edited = True
            post.save()
            return Success(f"post with ID {post_id} updated").to_json(), 200
        return Fail(f"post with ID {post_id} not found").to_json(), 404

    def delete(self, post_id):
        """Delete a specific post from the database.

        Args:
            post_id: UUID of the post to delete.
        """
        if not validate_uuid(post_id):
            return Fail("invalid post ID").to_json(), 400
        post = Post.get_post(post_id)
        if post is not None:
            post.delete()
            return Success(None).to_json(), 204
        return Fail(f"Post ID {post_id} does not exist").to_json(), 404


class Posts(Resource):
    def get(self):
        """Get list of existing posts."""
        posts = Post.get_all()
        posts_json = [post.to_json() for post in posts]
        return Success({"posts": posts_json}).to_json(), 200

    def post(self):
        """Create a new post.

        Required Args:
            topic: Topic to post to
            author: Username of post author
            body: Body text of post
            title: Title of the post 

        """
        args = post_parser.parse_args(strict=True)
        LOGGER.info({"Args": args})

        if User.get_user(args.author) is None:
            return Fail(f"author {args.author} does not exist").to_json(), 404

        if Topic.get_topic(args.topic_name) is None:
            return Fail(f"topic {args.topic_name} does not exist").to_json(), 404

        post = Post(
            title=args.title,
            body=args.body,
            author=args.author,
            topic_name=args.topic_name,
        )
        db.session.add(post)
        db.session.flush()
        post_uuid = post.id
        db.session.commit()

        return Success(post.to_json()).to_json(), 201


posts_bp_v2 = Blueprint("posts", __name__)
api = Api(posts_bp_v2)
api.add_resource(PostUpdate, "/api/v2/posts/<string:post_id>")
api.add_resource(Posts, "/api/v2/posts")
