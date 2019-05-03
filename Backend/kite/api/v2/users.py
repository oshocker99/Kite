"""API Endpoints relating to users"""
import bcrypt
from flask import Blueprint
from flask_restful import Api, Resource, request

from kite.api.response import Error, Fail, Success
from kite.api.v2.parsers.user_parse import post_parser, put_parser
from kite.models import User, db
from kite.settings import FORUM_ADMIN, LOGGER


class UserLookup(Resource):
    def get(self, username):
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

    def put(self, username):
        """Update user info.

        Args:
            username: The user to be updated.
        """
        args = put_parser.parse_args(strict=True)
        user = User.get_user(username)
        if user is not None:
            if args.is_admin is not None:
                user.is_admin = args.is_admin
            if args.bio is not None:
                user.bio = args.bio
            if args.is_mod is not None:
                user.is_mod = args.is_mod
            if args.displayName is not None:
                user.displayName = args.displayName
            if args.password is not None:
                user.pw_hash = bcrypt.hashpw(
                    args.password.encode("utf8"), bcrypt.gensalt()
                )
            db.session.commit()
            data = {"message": f"{username} updated"}
            return Success(data).to_json(), 200
        return Fail(f"user {username} does not exist").to_json(), 404

    def delete(self, username):
        """Delete a user.

        Args:
            username: The user to be deleted.
        """
        user = User.get_user(username)
        if user is not None:
            user.delete()
            return Success(None).to_json(), 204
        return Fail(f"user {username} does not exist").to_json(), 404


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

    def get(self):
        """Get list of all users."""
        user_filter = {}
        users = User.get_all()
        users_json = [res.to_json() for res in users]
        return Success({"users": users_json}).to_json(), 200


users_bp_v2 = Blueprint("users", __name__)
api = Api(users_bp_v2)

api.add_resource(UserList, "/api/v2/users")
api.add_resource(UserLookup, "/api/v2/users/<string:username>")
