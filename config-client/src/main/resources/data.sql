INSERT INTO user (id, firstname, lastname) VALUES (1, 'Arthur', 'Dent');
INSERT INTO user (id, firstname, lastname) VALUES (2, 'Zaphod', 'Beeblebrox');
INSERT INTO user (id, firstname, lastname) VALUES (3, 'Ford', 'Prefect');

INSERT INTO credentials (id, user_id, username, password) VALUES (1, 1, 'arthur_dent', 'dent1');
INSERT INTO credentials (id, user_id, username, password) VALUES (2, 1, 'arthur_dent', 'dent2');
INSERT INTO credentials (id, user_id, username, password) VALUES (3, 2, 'zaphod_beeblebrox', 'beeblebrox1');
INSERT INTO credentials (id, user_id, username, password) VALUES (4, 3, 'ford_prefect', 'prefect1');
INSERT INTO credentials (id, user_id, username, password) VALUES (5, 3, 'ford_prefect', 'prefect2');
INSERT INTO credentials (id, user_id, username, password) VALUES (6, 3, 'ford_prefect', 'prefect3');