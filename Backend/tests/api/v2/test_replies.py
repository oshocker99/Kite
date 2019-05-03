import json
from unittest.mock import MagicMock, patch

from tests import API_VERSION, ForumBaseTest

from kite.api.response import Fail, Success

MOCK_REPLY = MagicMock()

MOCK_REPLY().to_json.return_value = {"mock": "data"}


class ReplyTest(ForumBaseTest):
    valid_uuid = "e98ffad9-9381-4f56-a91a-1a67b830e9ee"
    invalid_uuid = "lmao"

    def setUp(self):
        super(ReplyTest, self).setUp()
        self.app.post("/api/v2/users", json={"username": "foo", "password": "testpass"})
        self.app.post("/api/v2/users", json={"username": "bar", "password": "testpass"})
        self.app.post(
            "/api/v2/topics", json={"name": "Cars", "description": "Things about cars."})



    @patch("kite.api.v2.replies.Reply.get_all", side_effect=MOCK_REPLY)
    def test_001_get_replies_success(self, mock):
        resp = self.app.get("/api/v2/replies")
        data = json.loads(resp.data)
        self.logger.debug({"Resp Data": data})
        self.assertEquals(resp.status_code, 200)

    @patch("kite.api.v2.replies.Reply.get_reply", side_effect=MOCK_REPLY)
    def test_002_get_reply_success(self, mock):
        resp = self.app.get(f"/api/v2/replies/{self.valid_uuid}")
        data = json.loads(resp.data)
        self.logger.debug({"Resp Data": data})
        self.assertEquals(resp.status_code, 200)

    @patch("kite.api.v2.replies.Reply.get_reply", return_value=None)
    def test_003_get_reply_failure_does_not_exist(self, mock):
        resp = self.app.get(f"/api/v2/replies/{self.valid_uuid}")
        data = json.loads(resp.data)
        self.assertEquals(resp.status_code, 404)

    def test_004_get_reply_failure_invalid_uuid(self):
        resp = self.app.get(f"/api/v2/replies/{self.invalid_uuid}")
        self.assertEquals(resp.status_code, 400)
    def test_005_create_reply(self):
        resp = self.app.post(
            "/api/v2/posts",
            json={
                "topic": "Cars",
                "author": "foo",
                "title": "I Like Cars",
                "body": "I like them",
            },
        )
        post_id = json.loads(resp.data).get("data").get("id")
        resp = self.app.post(
            "/api/v2/replies",
            json={
                "author": "bar",
                "body": "I don't like your post",
                "post_id": post_id
            },
        )
        data = json.loads(resp.data)
        self.logger.debug({"Resp Data": data})
        self.assertEquals(resp.status_code, 201)
        self.assertEquals(data.get("data").get("body"), "I don't like your post")


    def test_006_create_reply_author_fail(self):
        resp = self.app.post(
            "/api/v2/posts",
            json={
                "topic": "Cars",
                "author": "foo",
                "title": "I Like Cars",
                "body": "I like them",
            },
        )
        post_id = json.loads(resp.data).get("data").get("id")
        resp = self.app.post(
            "/api/v2/replies",
            json={
                "author": "xxx",
                "body": "I don't like your post",
                "post_id": post_id
            },
        )
        data = json.loads(resp.data)
        self.logger.debug({"Resp Data": data})
        self.assertEquals(resp.status_code, 404)

    def test_007_get_posts(self):
        resp = self.app.get("/api/v2/replies")
        data = json.loads(resp.data)
        self.logger.debug({"Resp Data": data})
        self.assertEquals(resp.status_code, 200)

    def test_008_get_reply_info(self):
        resp = self.app.post(
            "/api/v2/posts",
            json={
                "topic": "Cars",
                "author": "foo",
                "title": "I Like Cars",
                "body": "I like them",
            },
        )
        post_id = json.loads(resp.data).get("data").get("id")
        resp = self.app.post(
            "/api/v2/replies",
            json={
                "author": "bar",
                "body": "I don't like your post",
                "post_id": post_id
            },
        )
        reply_id = json.loads(resp.data).get("data").get("id")
        resp = self.app.get(f"/api/v2/replies/{reply_id}")
        data = json.loads(resp.data)
        self.logger.debug({"Resp Data": data})
        self.assertEquals(resp.status_code, 200)
        self.assertEquals(data.get("data").get("reply").get("body"), "I don't like your post")

    def test_009_invalid_reply_id(self):
        resp = self.app.get("/api/v2/replies/notid")
        data = json.loads(resp.data)
        self.logger.debug({"Resp Data": data})
        self.assertEquals(resp.status_code, 400)
        self.assertEquals(data.get("data").get("title"), "invalid reply ID")

    def test_010_delete_success(self):
        resp = self.app.post(
            "/api/v2/posts",
            json={
                "topic": "Cars",
                "author": "foo",
                "title": "I Like Cars",
                "body": "I like them",
            },
        )
        post_id = json.loads(resp.data).get("data").get("id")
        resp = self.app.post(
            "/api/v2/replies",
            json={
                "author": "bar",
                "body": "I don't like your post",
                "post_id": post_id
            },
        )
        reply_id = json.loads(resp.data).get("data").get("id")
        resp = self.app.delete(f"/api/v2/replies/{reply_id}")
        self.assertEquals(resp.status_code, 204)

    def test_011_reply_not_found(self):
        reply_id = "e98ffad9-9381-4f56-a91a-1a67b830e9ee"
        resp = self.app.get(f"/api/v2/replies/{reply_id}")
        data = json.loads(resp.data)
        self.logger.debug({"Resp Data": data})
        self.assertEquals(resp.status_code, 404)
        self.assertEquals(
            data.get("data").get("title"), f"reply with ID {reply_id} not found"
        )

    def test_012_delete_doesnt_exist(self):
        reply_id = "e98ffad9-9381-4f56-a91a-1a67b830e9ee"
        resp = self.app.delete(f"/api/v2/replies/{reply_id}")
        data = json.loads(resp.data)
        self.logger.debug({"Resp Data": data})
        self.assertEquals(resp.status_code, 404)
        self.assertEquals(
            data.get("data").get("title"), f"Reply ID {reply_id} does not exist"
        )

    def test_013_delete_invalid_uuid(self):
        resp = self.app.delete("/api/v2/replies/notuuid")
        data = json.loads(resp.data)
        self.logger.debug({"Resp Data": data})
        self.assertEquals(resp.status_code, 400)
        self.assertEquals(data.get("data").get("title"), "invalid reply ID")

    def test_014_update_reply(self):
        resp = self.app.post(
            "/api/v2/posts",
            json={
                "topic": "Cars",
                "author": "foo",
                "title": "I Like Cars",
                "body": "I like them",
            },
        )
        post_id = json.loads(resp.data).get("data").get("id")
        resp = self.app.post(
            "/api/v2/replies",
            json={
                "author": "bar",
                "body": "I don't like your post",
                "post_id": post_id
            },
        )
        reply_id = json.loads(resp.data).get("data").get("id")
        resp = self.app.put(f"/api/v2/replies/{reply_id}",
            json={

                "body": "I like your post",
            },
        )
        data = json.loads(resp.data)
        self.logger.debug({"Resp Data": data})
        self.assertEquals(resp.status_code, 200)
        self.assertEquals(data.get("data"), f"reply with ID {reply_id} updated")
