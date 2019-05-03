"""API Endpoints relating to topics"""
from flask import Blueprint
from flask_restful import Api, Resource

from kite.api.response import Error, Fail, Success
from kite.api.v3.parsers.topic_parse import post_parser, put_parser
from kite.auth import token_auth_required
from kite.models import Topic, db
from kite.settings import LOGGER


class TopicLookup(Resource):
    method_decorators = [token_auth_required]

    def get(self, topicName, jwt_payload=None):
        """Get info on a topic.

         Args:
            topicName: Topic to lookup
        """

        LOGGER.debug({"Requested Topic": topicName})
        topic = Topic.get_topic(topicName)
        if topic is not None:
            topic_json = topic.to_json(posts=True)
            return Success({"topic": topic_json}).to_json(), 200
        return Fail(f"topic {topicName} not found").to_json(), 404

    def put(self, topicName, jwt_payload=None):
        """Update topic info.
        Only available to admins and mods
        Args:
            This topic will be updated
        """

        if not (jwt_payload.is_admin or jwt_payload.is_mod):
            return (
                Fail("User does not have permissions for this request").to_json(),
                403,
            )

        else:
            args = put_parser.parse_args(strict=True)
            topic = Topic.get_topic(topicName)
            if topic is not None:
                if args.descript is not None:
                    topic.descript = args.descript
                db.session.commit()
                return Success({"message": f"{topicName} updated"}).to_json(), 200
            return Fail(f"topic {topicName} not found").to_json(), 404

    def delete(self, topicName, jwt_payload=None):
        """ Delete a topic

        Only an admin has permission to delete (others should not be shown the option)
        :param topicName:
        :return:
        """

        if not jwt_payload.is_admin:
            return Fail("User does not have permission").to_json(), 403
        else:
            topic = Topic.get_topic(topicName)
            if topic is not None:
                topic.delete()
                return Success({"message": f"{topicName} deleted"}).to_json(), 204
            return Fail(f"topic {topicName} not found").to_json(), 404


class TopicList(Resource):
    method_decorators = [token_auth_required]


    def post(self, jwt_payload=None):
        """Create a new Topic."""

        args = post_parser.parse_args(strict=True)
        LOGGER.info({"Args": args})

        topic = Topic.get_topic(args.name)
        if topic is None:
            try:
                record = Topic(name=args.name, descript=args.descript)
                record.save()
                return Success({"message": f"topic {args.name} created"}).to_json(), 200
            except Exception as e:
                LOGGER.error({"Exception": e})
                return Error(str(e)).to_json(), 500
        return Fail(f"topic {args.name} exists").to_json(), 400

    def get(self, jwt_payload=None):
        """Get list of all topics."""
        topic_filter = {}
        topics = Topic.get_all()
        topics_json = [res.to_json() for res in topics]
        return Success({"topics": topics_json}).to_json(), 200


topics_bp_v3 = Blueprint("topics v3", __name__)
api = Api(topics_bp_v3)
api.add_resource(TopicLookup, "/api/v3/topics/<string:topicName>")
api.add_resource(TopicList, "/api/v3/topics")
