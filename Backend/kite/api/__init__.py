def register_blueprints(app):

    from kite.auth import auth_bp

    app.register_blueprint(auth_bp)

    # v2 API Endpoints
    from kite.api.v2.posts import posts_bp_v2
    from kite.api.v2.replies import replies_bp_v2
    from kite.api.v2.topics import topics_bp_v2
    from kite.api.v2.users import users_bp_v2

    app.register_blueprint(replies_bp_v2)
    app.register_blueprint(posts_bp_v2)
    app.register_blueprint(topics_bp_v2)
    app.register_blueprint(users_bp_v2)