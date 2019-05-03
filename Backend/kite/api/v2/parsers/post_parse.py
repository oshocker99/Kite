from flask_restful import reqparse

from kite.utils import validate_length

post_parser = reqparse.RequestParser()
put_parser = reqparse.RequestParser()

post_parser.add_argument(
    "title",
    dest="title",
    location="json",
    required=True,
    help="Type: String. The post's title, required. Length: 5-30 characters",
    type=validate_length(50, 5, "title"),
)
post_parser.add_argument(
    "body",
    dest="body",
    location="json",
    required=True,
    help="Type: String. The post's body, required. Length: 10-250 characters",
    type=validate_length(1000, 10, "body"),
)
post_parser.add_argument(
    "author",
    dest="author",
    location="json",
    required=True,
    help="Type: String. The post's Author, required.",
)
post_parser.add_argument(
    "topic",
    dest="topic_name",
    location="json",
    required=True,
    help="Type: String. The Topic the post belongs to, required. Length: 5-30 characters",
)

put_parser.add_argument(
    "body",
    dest="body",
    location="json",
    required=True,
    help="Type: String. The post's updated body.",
    type=validate_length(1000, 10, "body"),
)
