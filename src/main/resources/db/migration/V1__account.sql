CREATE TABLE account
(
    account_id   INT GENERATED ALWAYS AS IDENTITY,
    username     VARCHAR(30) UNIQUE  NOT NULL,
    email        VARCHAR(320) UNIQUE NOT NULL,
    first_name   VARCHAR(100),
    last_name    VARCHAR(100),
    created      TIMESTAMP           NOT NULL,
    last_updated TIMESTAMP           NOT NULL,
    last_login   TIMESTAMP,
    PRIMARY KEY (account_id)
);
