-- V2 inserts explicit ids, move the identity column behind the highest id (H2 is only used in tests)
ALTER TABLE client_credentials ALTER COLUMN id RESTART WITH 7;
