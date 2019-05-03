from flask_restful import reqparse

from kite.utils import validate_length

post_parser = reqparse.RequestParser()

post_parser.add_argument(
    "username",
    dest="username",
    location="json",
    required=True,
    help="Type: String. The new user's username, required.",
    type=validate_length(30, 3, "username"),
)
post_parser.add_argument(
    "bio",
    dest="bio",
    location="json",
    required=False,
    help="Type: String. The new user's bio.",
    type=validate_length(100, 5, "bio"),
)
post_parser.add_argument(
    "password",
    dest="password",
    location="json",
    required=True,
    help="Type: String. The new user's password, required.",
    type=validate_length(55, 5, "password"),
)
post_parser.add_argument(
    "displayName",
    dest="displayName",
    location="json",
    required=False,
    help="Type: String. The new user's display name.",
)

put_parser = reqparse.RequestParser()

put_parser.add_argument(
    "is_admin",
    dest="is_admin",
    location="json",
    required=False,
    type=bool,
    help="Type: Boolean. Is user an admin.",
)
put_parser.add_argument(
    "is_mod",
    dest="is_mod",
    location="json",
    required=False,
    type=bool,
    help="Type: Boolean. Is user an moderator.",
)
put_parser.add_argument(
    "bio",
    dest="bio",
    location="json",
    required=False,
    help="Type: String. The user's updated bio.",
    type=validate_length(100, 5, "bio"),
)
put_parser.add_argument(
    "password",
    dest="password",
    location="json",
    required=False,
    help="Type: String. The user's updated password.",
    type=validate_length(55, 5, "password"),
)
put_parser.add_argument(
    "displayName",
    dest="displayName",
    location="json",
    required=False,
    help="Type: String. The new user's display name.",
)
