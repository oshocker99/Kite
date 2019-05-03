import json
import re
from unittest import TestCase

import bcrypt

from kite import app
from kite.models import User, db
from kite.settings import FORUM_ADMIN, LOGGER

API_VERSION = "v3"


class Resp:
    def __init__(self, status_code, json):
        self.json = json
        self.status_code = status_code


class ForumBaseTest(TestCase):

    logger = LOGGER

    @classmethod
    def setUp(self):
        self.app = app.test_client()
        app.config.from_object("kite.settings")

    def clean_data(self, data):
        data = data.decode("utf-8")
        data = re.sub("\n", " ", data)
        data = re.sub(" +", " ", data)
        return data

    @classmethod
    def tearDownClass(self):
        with app.app_context():
            db.drop_all()
            db.create_all()
            if User.get_user(username=FORUM_ADMIN.get("username")) is None:
                hashed = bcrypt.hashpw(
                    FORUM_ADMIN.get("password").encode("utf8"), bcrypt.gensalt()
                )
                admin = User(
                    username=FORUM_ADMIN.get("username"),
                    pw_hash=hashed,
                    is_admin=True,
                    is_mod=True,
                )
                admin.save()

    @classmethod
    def post_user(self, username, password="password", bio="test bio", displayName=""):
        resp = self.app.post(
            "/api/v2/users",
            json={
                "username": username,
                "password": password,
                "bio": bio,
                "displayName": displayName,
            },
        )
        return Resp(resp.status_code, json.loads(resp.data))

    @classmethod
    def get_user(self, username=""):
        resp = self.app.get(f"/api/v2/users/{username}")
        return Resp(resp.status_code, json.loads(resp.data))

    def put_user(
        self,
        username,
        password="password",
        bio="test bio",
        displayName="",
        is_mod=False,
        is_admin=False,
    ):
        resp = self.app.put(
            f"/api/v2/users/{username}",
            json={
                "password": password,
                "bio": bio,
                "displayName": displayName,
                "is_mod": is_mod,
                "is_admin": is_admin,
            },
        )
        return Resp(resp.status_code, json.loads(resp.data))

    @classmethod
    def delete_user(self, username):
        resp = self.app.delete(f"/api/v2/users/{username}")
        if not resp.data:
            return Resp(resp.status_code, None)
        return Resp(resp.status_code, json.loads(resp.data))

    @staticmethod
    def get_value(dct, *keys):
        for key in keys:
            try:
                dct = dct[key]
            except KeyError:
                return None
        return dct
