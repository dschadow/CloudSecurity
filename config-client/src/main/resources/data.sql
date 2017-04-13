INSERT INTO users (id, firstname, lastname) VALUES (1, 'Arthur', 'Dent');
INSERT INTO users (id, firstname, lastname) VALUES (2, 'Zaphod', 'Beeblebrox');
INSERT INTO users (id, firstname, lastname) VALUES (3, 'Ford', 'Prefect');

INSERT INTO credentials (id, user_id, username, password) VALUES (1, 1, 'arthur1', 'dent');
INSERT INTO credentials (id, user_id, username, password) VALUES (2, 1, 'arthur2', 'dent');
INSERT INTO credentials (id, user_id, username, password) VALUES (3, 2, 'zaphod1', 'beeblebrox');
INSERT INTO credentials (id, user_id, username, password) VALUES (4, 3, 'ford1', 'prefect');
INSERT INTO credentials (id, user_id, username, password) VALUES (5, 3, 'ford2', 'prefect');
INSERT INTO credentials (id, user_id, username, password) VALUES (6, 3, 'ford3', 'prefect');