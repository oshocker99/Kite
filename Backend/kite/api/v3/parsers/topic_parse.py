from flask_restful import reqparse

from kite.utils import validate_length

put_parser = reqparse.RequestParser()
put_parser.add_argument(
    "description",
    dest="descript",
    location="json",
    required=False,
    help="Type: String. The Topic's updated description.",
    type=validate_length(150, 10, "description"),
)

post_parser = reqparse.RequestParser()
post_parser.add_argument(
    "name",
    dest="name",
    location="json",
    required=True,
    help="Type: String. The new Topic's name, required.",
    type=validate_length(30, 3, "Name"),
)
post_parser.add_argument(
    "description",
    dest="descript",
    location="json",
    required=False,
    help="Type: String. The new Topic's description.",
    type=validate_length(150, 10, "description"),
)
