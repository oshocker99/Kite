import json

from tests import API_VERSION, ForumBaseTest


class PostTest(ForumBaseTest):
    def setUp(self):
        super(PostTest, self).setUp()
        self.app.post("/api/v2/users", json={"username": "foo", "password": "testpass"})
        self.app.post("/api/v2/users", json={"username": "bar", "password": "testpass"})
        self.app.post(
            "/api/v2/topics", json={"name": "Cars", "description": "Things about cars."}
        )

    def test_001_create_post(self):
        resp = self.app.post(
            "/api/v2/posts",
            json={
                "topic": "Cars",
                "author": "foo",
                "title": "I Like Cars",
                "body": "I like them",
            },
        )
        data = json.loads(resp.data)
        self.logger.debug({"Resp Data": data})
        self.assertEquals(resp.status_code, 201)
        self.assertEquals(data.get("data").get("title"), "I Like Cars")

    def test_002_create_post_author_fail(self):
        resp = self.app.post(
            "/api/v2/posts",
            json={
                "topic": "Cars",
                "author": "xxx",
                "title": "I Like Cars",
                "body": "I like them",
            },
        )
        data = json.loads(resp.data)
        self.logger.debug({"Resp Data": data})
        self.assertEquals(resp.status_code, 404)

    def test_003_create_post_topic_fail(self):
        resp = self.app.post(
            "/api/v2/posts",
            json={
                "topic": "xxx",
                "author": "foo",
                "title": "I Like Cars",
                "body": "I like them",
            },
        )
        data = json.loads(resp.data)
        self.logger.debug({"Resp Data": data})
        self.assertEquals(resp.status_code, 404)

    def test_004_get_posts(self):
        resp = self.app.get("/api/v2/posts")
        data = json.loads(resp.data)
        self.logger.debug({"Resp Data": data})
        self.assertEquals(resp.status_code, 200)

    def test_005_get_post_info(self):
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
        resp = self.app.get(f"/api/v2/posts/{post_id}")
        data = json.loads(resp.data)
        self.logger.debug({"Resp Data": data})
        self.assertEquals(resp.status_code, 200)
        self.assertEquals(data.get("data").get("post").get("title"), "I Like Cars")

    def test_006_invalid_uuid(self):
        resp = self.app.get("/api/v2/posts/notuuid")
        data = json.loads(resp.data)
        self.logger.debug({"Resp Data": data})
        self.assertEquals(resp.status_code, 400)
        self.assertEquals(data.get("data").get("title"), "invalid post ID")

    def test_007_post_not_found(self):
        """If this fails because the post was found... what luck."""
        post_id = "e98ffad9-9381-4f56-a91a-1a67b830e9ee"
        resp = self.app.get(f"/api/v2/posts/{post_id}")
        data = json.loads(resp.data)
        self.logger.debug({"Resp Data": data})
        self.assertEquals(resp.status_code, 404)
        self.assertEquals(
            data.get("data").get("title"), f"post with ID {post_id} not found"
        )

    def test_008_delete_doesnt_exist(self):
        post_id = "e98ffad9-9381-4f56-a91a-1a67b830e9ee"
        resp = self.app.delete(f"/api/v2/posts/{post_id}")
        data = json.loads(resp.data)
        self.logger.debug({"Resp Data": data})
        self.assertEquals(resp.status_code, 404)
        self.assertEquals(
            data.get("data").get("title"), f"Post ID {post_id} does not exist"
        )

    def test_009_delete_invalid_uuid(self):
        resp = self.app.delete("/api/v2/posts/notuuid")
        data = json.loads(resp.data)
        self.logger.debug({"Resp Data": data})
        self.assertEquals(resp.status_code, 400)
        self.assertEquals(data.get("data").get("title"), "invalid post ID")

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
        resp = self.app.delete(f"/api/v2/posts/{post_id}")
        self.assertEquals(resp.status_code, 204)

    def test_011_update_post(self):
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
        resp = self.app.put(
            f"/api/v2/posts/{post_id}",
            json={
                "body": "I kinda like them",
            },
        )
        data = json.loads(resp.data)
        self.logger.debug({"Resp Data": data})
        self.assertEquals(resp.status_code, 200)
        self.assertEquals(data.get("data"), f"post with ID {post_id} updated")
