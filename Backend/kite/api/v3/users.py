"""API Endpoints relating to users"""
import bcrypt
from flask import Blueprint
from flask_restful import Api, Resource, request

from kite.api.response import Error, Fail, Success
from kite.api.v3.parsers.user_parse import post_parser, put_parser
from kite.auth import token_auth_required
from kite.models import User, db
from kite.settings import FORUM_ADMIN, LOGGER


class UserLookup(Resource):
    method_decorators = [token_auth_required]

    def get(self, username, jwt_payload=None):
        """Get info on a user.

        Args:
            username: Username to lookup.
        """
        LOGGER.debug({"Requested user": username})
        user = User.get_user(username)
        if user is not None:
            user_json = user.to_json()
            return Success(user_json).to_json(), 200
        return Fail(f"user {username} not found").to_json(), 404

    def put(self, username, jwt_payload=None):
        """Update user info.

        Args:
            username: The user to be updated.
            jwt_payload: The payload data of the JWT passed in the request
        """
        args = put_parser.parse_args(strict=True)
        user = User.get_user(username)

        if user is not None:
            if args.is_admin is not None and jwt_payload.is_admin:
                user.is_admin = args.is_admin
            if args.is_mod is not None and jwt_payload.is_mod:
                user.is_mod = args.is_mod if not user.is_admin else True
            if args.displayName is not None and username == jwt_payload.username:
                user.displayName = args.displayName
            if args.bio is not None and username == jwt_payload.username:
                user.bio = args.bio
            if args.password is not None and username == jwt_payload.username:
                user.pw_hash = bcrypt.hashpw(
                    args.password.encode("utf8"), bcrypt.gensalt()
                )
            db.session.commit()
            data = {"message": f"{username} updated"}
            return Success(data).to_json(), 200
        return Fail(f"user {username} does not exist").to_json(), 404

    def delete(self, username, jwt_payload=None):
        """Delete a user.

        Args:
            username: The user to be deleted.
        """
        user = User.get_user(username)
        if user is None:
            return Fail(f"user {username} does not exist").to_json(), 404

        if user.username == jwt_payload.username or jwt_payload.is_admin:
            user.delete()
            return Success(None).to_json(), 204

        return (
            Fail(f"Invalid permissions, cannot delete user {username}").to_json(),
            403,
        )


class UserList(Resource):
    def post(self):
        """Create a new user.

        Required in Payload:
            userame: Username of the new user to be created.
            password: Passowrd of the user to be created.

        Optional in Payload:
            bio: Bio of the user to be created.
        """
        args = post_parser.parse_args(strict=True)
        LOGGER.info({"Args": args})

        user = User.get_user(args.username)
        if user is None:
            hashed = bcrypt.hashpw(args.password.encode("utf8"), bcrypt.gensalt())
            record = User(
                username=args.username,
                pw_hash=hashed,
                bio=args.bio,
                displayName=args.displayName,
            )
            record.save()
            data = {"message": f"user {args.username} created"}
            return Success(data).to_json(), 201
        return Fail(f"user {args.username} exists").to_json(), 400

    @token_auth_required
    def get(self, jwt_payload=None):
        """Get list of all users."""
        LOGGER.debug({"JWT payload": jwt_payload})
        user_filter = {}
        users = User.get_all()
        users_json = [res.to_json() for res in users]
        return Success({"users": users_json}).to_json(), 200


users_bp_v3 = Blueprint("users v3", __name__)
api = Api(users_bp_v3)

api.add_resource(UserList, "/api/v3/users")
api.add_resource(UserLookup, "/api/v3/users/<string:username>")
