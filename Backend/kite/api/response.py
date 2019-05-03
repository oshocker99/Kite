import json


class Response:
    """Object for Forum API responses."""

    def __init__(self, status="", data={}):
        self.status = status
        self.data = data

    def to_json(self):
        return {"status": self.status, "data": self.data}


class Success(Response):
    def __init__(self, data):
        self.status = "success"
        self.data = data


class Fail(Response):
    def __init__(self, data):
        self.status = "fail"
        self.data = {"title": data}


class Error(Response):
    def __init__(self, data):
        self.status = "error"
        self.data = {"message": data}
