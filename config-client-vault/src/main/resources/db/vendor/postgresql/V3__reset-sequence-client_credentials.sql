-- V2 inserts explicit ids, move the SERIAL sequence behind the highest id
SELECT setval(pg_get_serial_sequence('client_credentials', 'id'), (SELECT MAX(id) FROM client_credentials));
