import os

from spylogger import get_logger

LOGGER = get_logger(log_level="DEBUG")

FLASK_ENV = os.getenv("FLASK_ENV", "dev")

DEBUG = True
SECRET_KEY = os.getenv(
    "API_SECRET", "6150645367566B59703373367638792F423F4528482B4D6251655468576D5A71"
)

# DATABASE
SQLALCHEMY_DATABASE_URI = "postgresql://{}:{}@{}:{}/{}".format(
    os.getenv("API_DB_USERNAME", "admin"),
    os.getenv("API_DB_PASSWORD", "pass"),
    os.getenv("API_DB_HOST", "localhost" if FLASK_ENV != "test" else "postgres"),
    os.getenv("API_DB_PORT", "5432"),
    os.getenv("API_DB_NAME", "forum_db"),
)
SQLALCHEMY_TRACK_MODIFICATIONS = False

FORUM_ADMIN = {
    "username": os.getenv("FORUM_ADMIN", "admin"),
    "password": os.getenv("FORUM_ADMIN_PASS", "password"),
}
