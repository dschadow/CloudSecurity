INSERT INTO user (id, firstname, lastname) VALUES (1, 'Arthur', 'Dent');
INSERT INTO user (id, firstname, lastname) VALUES (2, 'Zaphod', 'Beeblebrox');
INSERT INTO user (id, firstname, lastname) VALUES (3, 'Ford', 'Prefect');

INSERT INTO credential (id, user_id, username, password) VALUES (1, 1, 'arthur1', 'dent');
INSERT INTO credential (id, user_id, username, password) VALUES (2, 1, 'arthur2', 'dent');
INSERT INTO credential (id, user_id, username, password) VALUES (3, 2, 'zaphod', 'beeblebrox');
INSERT INTO credential (id, user_id, username, password) VALUES (4, 3, 'ford1', 'prefect');
INSERT INTO credential (id, user_id, username, password) VALUES (5, 3, 'ford2', 'prefect');
INSERT INTO credential (id, user_id, username, password) VALUES (6, 3, 'ford3', 'prefect');