"""API Endpoints relating to posts"""

from flask import Blueprint
from flask_restful import Api, Resource

from kite.api.response import Error, Fail, Success
from kite.api.v3.parsers.reply_parse import post_parser, put_parser
from kite.auth import token_auth_required
from kite.models import Post, Reply, User, db
from kite.settings import LOGGER
from kite.utils import validate_uuid


class ReplyUpdate(Resource):
    method_decorators = [token_auth_required]

    def get(self, reply_id, jwt_payload=None):
        """Get info on a specific reply.

        Args:
            reply_id: UUID of the post to lookup.
        """
        if not validate_uuid(reply_id):
            return Fail("invalid reply ID").to_json(), 400
        LOGGER.debug({"Requested reply": reply_id})
        reply = Reply.get_reply(reply_id)
        if reply is not None:
            return Success({"reply": reply.to_json()}).to_json(), 200
        return Fail(f"post with ID {reply_id} not found").to_json(), 404

    def put(self, reply_id, jwt_payload=None):
        """Update info for a specific reply.

        Args:
            reply_id: UUID of the reply to update.
        """
        if not validate_uuid(reply_id):
            return Fail("invalid reply ID").to_json(), 400
        reply = Reply.get_reply(reply_id)
        if reply is None:
            return Fail(f"reply with ID {reply_id} not found").to_json(), 404
        if jwt_payload.username != reply.author:
            return Fail("Replies can only be updated by their creators").to_json(), 403

        if reply is not None:
            args = put_parser.parse_args(strict=True)
            reply.body = args.body
            reply.edited = True
            reply.save()
            return Success(f"reply with ID {reply_id} updated").to_json(), 200

    def delete(self, reply_id, jwt_payload=None):
        """Delete a specific reply from the database.

        Args:
            reply_id: UUID of the reply to delete.
        """
        if not validate_uuid(reply_id):
            return Fail("invalid reply ID").to_json(), 400
        reply = Reply.get_reply(reply_id)
        if reply is None:
            return Fail(f"Reply ID {reply_id} does not exist").to_json(), 404
        if not (
            jwt_payload.is_admin
            or jwt_payload.is_mod
            or jwt_payload.username == reply.author
        ):
            return Fail("You do not have permission to delete replies").to_json(), 403
        else:
            if reply is not None:
                reply.delete()
                return Success(None).to_json(), 204


class Replies(Resource):
    method_decorators = [token_auth_required]

    def get(self, jwt_payload=None):
        """Get list of existing replies."""
        replies = Reply.get_all()
        reply_json = [reply.to_json() for reply in replies]
        return Success({"replies": replies}).to_json(), 200

    def post(self, jwt_payload=None):
        """Create a new reply.

        Required Args:
            post_id: ID of the post to reply to
            author: Username of post author
            body: Body text of post

        """
        args = post_parser.parse_args(strict=True)
        LOGGER.info({"Args": args})

        if User.get_user(args.author) is None:
            return Fail(f"author {args.author} does not exist").to_json(), 404

        if Post.get_post(args.post_id) is None:
            return Fail(f"topic {args.post_id} does not exist").to_json(), 404

        reply = Reply(body=args.body, author=args.author, post_id=args.post_id)
        db.session.add(reply)
        db.session.flush()
        reply_uuid = reply.id
        db.session.commit()

        return Success(reply.to_json()).to_json(), 201


replies_bp_v3 = Blueprint("replies v3", __name__)
api = Api(replies_bp_v3)
api.add_resource(ReplyUpdate, "/api/v3/replies/<string:reply_id>")
api.add_resource(Replies, "/api/v3/replies")
