CREATE TABLE user (
    id SERIAL PRIMARY KEY,
    firstname VARCHAR NOT NULL,
    lastname VARCHAR NOT NULL
);

CREATE TABLE credential (
    id SERIAL PRIMARY KEY,
    user_id INT,
    username VARCHAR NOT NULL,
    password VARCHAR NOT NULL,
    CONSTRAINT fk_user
        FOREIGN KEY(user_id)
            REFERENCES user(id)
);