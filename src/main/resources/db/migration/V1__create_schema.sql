CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS item
(
    id              SERIAL PRIMARY KEY,
    name            TEXT                        NOT NULL,
    timestamp       TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date            DATE                        NOT NULL DEFAULT CURRENT_DATE,
    zoned_timestamp TIMESTAMP WITH TIME ZONE    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER SEQUENCE item_id_seq RESTART 1000;
