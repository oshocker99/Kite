import json

from tests import API_VERSION
from tests import ForumBaseTest as bt

from kite.settings import FORUM_ADMIN, LOGGER


class UserTest(bt):
    def test_001_load_admin(self):
        resp = self.app.get("/api/v2/users")
        data = json.loads(resp.data)
        self.logger.debug({"Resp Data": data})
        self.assertEquals(
            data.get("data").get("users")[0].get("username"),
            FORUM_ADMIN.get("username"),
        )
        self.assertEquals(data.get("data").get("users")[0].get("is_mod"), True)
        self.assertEquals(data.get("data").get("users")[0].get("is_mod"), True)

    def test_002_user_post_success(self):
        resp = self.post_user(username="TestUser")
        self.logger.debug({"Resp Data": resp.json})
        self.assertEquals(
            "user TestUser created", bt.get_value(resp.json, "data", "message")
        )
        self.assertEquals(resp.status_code, 201)

    def test_003_user_post_same_user_fail(self):
        self.post_user(username="Mike")
        data = self.post_user(username="Mike")
        self.logger.debug({"Resp Data": data.json})
        self.assertEquals(data.status_code, 400)

    def test_004_user_record_get_success(self):
        self.post_user("Foo")
        resp = self.get_user("Foo")
        self.logger.debug({"Resp Data": resp.json})
        self.assertEquals(resp.status_code, 200)
        self.assertEquals(resp.json.get("status"), "success")
        self.assertEquals(bt.get_value(resp.json, "data", "username"), "Foo")

    def test_005_user_record_get_failure(self):
        resp = self.get_user("bar")
        self.logger.debug({"Resp Data": resp.json})
        self.assertEquals(resp.status_code, 404)
        self.assertEquals(resp.json.get("status"), "fail")
        self.assertEquals(
            bt.get_value(resp.json, "data", "title"), "user bar not found"
        )

    def test_006_user_record_put_success(self):
        self.post_user("Test6")
        resp = self.put_user("Test6", bio="New Bio")
        self.logger.debug({"Resp Data": resp.json})
        self.assertEquals(resp.status_code, 200)
        self.assertEquals(bt.get_value(resp.json, "data", "message"), "Test6 updated")

    def test_007_user_record_put_success_002(self):
        self.post_user("Test7")
        resp = self.app.put(
            "/api/v2/users/foo", json={"password": "longpass", "is_mod": True}
        )
        resp = self.put_user("Test7", password="longpass", is_mod=True)
        self.logger.debug({"Resp Data": resp.json})
        self.assertEquals(resp.status_code, 200)
        self.assertEquals(resp.json.get("status"), "success")

    def test_008_user_record_put_failure(self):
        resp = self.put_user("Test8", bio="I Don't Exist")
        self.logger.debug({"Resp Data": resp.json})
        self.assertEquals(resp.status_code, 404)
        self.assertEquals(resp.json.get("status"), "fail")
        self.assertEquals(
            bt.get_value(resp.json, "data", "title"), "user Test8 does not exist"
        )

    def test_009_user_attributes_updated(self):
        self.post_user("Test9")
        resp = self.get_user("Test9")
        self.assertFalse(bt.get_value(resp.json, "data", "is_admin"))
        self.assertFalse(bt.get_value(resp.json, "data", "is_mod"))

        self.put_user("Test9", is_mod=True, is_admin=True)

        resp = self.get_user("Test9")
        self.logger.debug({"Resp Data": resp.json})
        self.assertEquals(resp.status_code, 200)

        self.assertTrue(bt.get_value(resp.json, "data", "is_admin"))
        self.assertTrue(bt.get_value(resp.json, "data", "is_mod"))

    def test_010_use_record_delete_success(self):
        self.post_user("Test10")
        resp = self.delete_user("Test10")
        self.assertEquals(resp.status_code, 204)

    def test_011_user_record_delete_failure(self):
        resp = self.delete_user("Test11")
        self.logger.debug({"Resp Data": resp.json})
        self.assertEquals(resp.status_code, 404)
        self.assertEquals(resp.json.get("status"), "fail")
        self.assertEquals(
            bt.get_value(resp.json, "data", "title"), "user Test11 does not exist"
        )
