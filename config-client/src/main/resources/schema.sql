CREATE TABLE client_users (
    id SERIAL PRIMARY KEY,
    firstname VARCHAR NOT NULL,
    lastname VARCHAR NOT NULL
);

CREATE TABLE client_credentials (
    id SERIAL PRIMARY KEY,
    user_id INT,
    username VARCHAR NOT NULL,
    password VARCHAR NOT NULL,
    CONSTRAINT fk_user
        FOREIGN KEY(user_id)
            REFERENCES client_users(id)
);