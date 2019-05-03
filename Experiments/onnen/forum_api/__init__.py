from flask import Flask

# from flask_sqlalchemy import SQLAlchemy
from kite.resources.auth import auth_bp

app = Flask(__name__)

# db = SQLAlchemy(app)

app.register_blueprint(auth_bp)
