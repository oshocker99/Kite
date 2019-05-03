import time
from functools import wraps

import bcrypt
import jwt
from flask import Blueprint
from flask_restful import Api, Resource, request

from kite.api.response import Fail
from kite.models import User
from kite.settings import LOGGER, SECRET_KEY


class JWTPayload:
    def __init__(self, payload):
        self.username = payload["sub"]
        self.is_admin = payload["is_admin"]
        self.is_mod = payload["is_mod"]


class Auth(Resource):
    def post(self):
        if request.authorization is None:
            return {"errors": {"detail": "Basic auth header missing"}}, 400

        username = request.authorization.get("username")
        password = request.authorization.get("password")
        LOGGER.debug({username: password})

        user = User.query.filter_by(username=username).first()
        if user is not None:
            try:
                if bcrypt.checkpw(password.encode("utf8"), user.pw_hash):
                    payload = {
                        "sub": username,
                        "is_admin": user.is_admin,
                        "is_mod": user.is_mod,
                        "iat": int(time.time()),
                    }
                    token = jwt.encode(payload, SECRET_KEY, algorithm="HS256")
                    LOGGER.debug({"Token": token})
                    return {"data": {"access_token": token.decode("utf-8")}}, 200
            except Exception as e:
                LOGGER.error({"Exception": e})
                return {"errors": {"detail": "server error"}}, 500
        return {"errors": {"detail": "Invalid Credentials"}}, 403


class Refresh(Resource):
    def post(self):
        pass


def token_auth_required(f):
    """Wraps a Restful API, validating the JWT and passing its payload to the 
    endpoint.Api

    Args: f: API Route.

    Returns: 
        calls API route if JWT is valid, else returns 401 response.
    """

    @wraps(f)
    def decorated_function(*args, **kwargs):
        try:
            token = request.headers.get("authorization").split(" ")[1]
            LOGGER.debug({"Token": token})

            decoded = jwt.decode(token, SECRET_KEY, algorithms=["HS256"])
            LOGGER.debug({"Decoded": decoded})
        except Exception as e:
            LOGGER.debug({"Message": str(e)})
            return Fail("Invalid or missing JWT").to_json(), 401

        return f(jwt_payload=JWTPayload(decoded), *args, **kwargs)

    return decorated_function


auth_bp = Blueprint("auth", __name__)
api = Api(auth_bp)
api.add_resource(Auth, "/api/auth/login")
api.add_resource(Refresh, "/api/auth/refresh")
