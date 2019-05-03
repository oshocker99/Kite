from flask_restful import reqparse

from kite.utils import validate_length

post_parser = reqparse.RequestParser()
put_parser = reqparse.RequestParser()

post_parser.add_argument(
    "body",
    dest="body",
    location="json",
    required=True,
    help="Type: String. The reply's body, required. Length: 10-255 characters",
    type=validate_length(500, 10, "body"),
)
post_parser.add_argument(
    "author",
    dest="author",
    location="json",
    required=True,
    help="Type: String. The post's Author, required.",
)
post_parser.add_argument(
    "post_id",
    dest="post_id",
    location="json",
    required=True,
    help="Type: String. The post the reply belongs to, required.",
)

put_parser.add_argument(
    "body",
    dest="body",
    location="json",
    required=True,
    help="Type: String. The reply's updated body.",
    type=validate_length(500, 10, "body"),
)
