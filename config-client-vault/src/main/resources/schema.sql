DROP TABLE IF EXISTS client_credentials;

CREATE TABLE client_credentials (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    pin VARCHAR(10) NOT NULL NOT NULL
);