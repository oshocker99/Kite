import bcrypt
from flask import Flask, jsonify

from kite.api import register_blueprints
from kite.models import User, db
from kite.settings import FORUM_ADMIN
from kite.api.response import Success

app = Flask(__name__)
app.config.from_object("kite.settings")

register_blueprints(app)

db.init_app(app)


@app.before_first_request
def init_forum():
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


@app.route("/api/status")
def healthcheck():
    return jsonify({"Status": "Online"})
