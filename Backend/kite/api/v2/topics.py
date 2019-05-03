"""API Endpoints relating to topics"""
from flask import Blueprint
from flask_restful import Api, Resource

from kite.api.response import Error, Fail, Success
from kite.api.v2.parsers.topic_parse import post_parser, put_parser
from kite.models import Topic, db
from kite.settings import LOGGER


class TopicLookup(Resource):
    def get(self, topicName):
        """Get info on a topic

         Args:
            topicName: Topic to lookup
        """
        LOGGER.debug({"Requested Topic": topicName})
        topic = Topic.get_topic(topicName)
        if topic is not None:
            topic_json = topic.to_json(posts=True)
            return Success({"topic": topic_json}).to_json(), 200
        return Fail(f"topic {topicName} not found").to_json(), 404

    def put(self, topicName):
        """Update topic info
        Args:
            This topic will be updated
        """
        args = put_parser.parse_args(strict=True)
        topic = Topic.get_topic(topicName)
        if topic is not None:
            if args.descript is not None:
                topic.descript = args.descript
            db.session.commit()
            return Success(f"{topicName} updated").to_json(), 200
        return Fail(f"topic {topicName} not found").to_json(), 404

    def delete(self, topicName):
        """

        :param topicName:
        :return:
        """
        topic = Topic.get_topic(topicName)
        if topic is not None:
            topic.delete()
            return Success(f"{topicName} deleted").to_json(), 204
        return Fail(f"topic {topicName} not found").to_json(), 404


class TopicList(Resource):

    # this one needs work
    def post(self):
        """Create a new Topic."""

        args = post_parser.parse_args(strict=True)
        LOGGER.info({"Args": args})

        topic = Topic.get_topic(args.name)
        if topic is None:
            try:
                record = Topic(name=args.name, descript=args.descript)
                record.save()
                return Success({"message": f"topic {args.name} created"}).to_json(), 201
            except Exception as e:
                LOGGER.error({"Exception": e})
                return Error(str(e)).to_json(), 500
        return Fail(f"topic {args.name} exists").to_json(), 400

    def get(self):
        """Get list of all topics."""
        topic_filter = {}
        topics = Topic.get_all()
        topics_json = [res.to_json() for res in topics]
        return Success({"topics": topics_json}).to_json(), 200


topics_bp_v2 = Blueprint("topics", __name__)
api = Api(topics_bp_v2)
api.add_resource(TopicLookup, "/api/v2/topics/<string:topicName>")
api.add_resource(TopicList, "/api/v2/topics")
