CREATE TABLE users (
    username text PRIMARY KEY,
    pw_hash binary(60),
    is_admin boolean DEFAULT false,
    is_mod boolean DEFAULT false,
);

CREATE TABLE topics ();

CREATE TABLE posts ();

CREATE TABLE replies ();