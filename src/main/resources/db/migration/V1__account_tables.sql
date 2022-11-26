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

CREATE TABLE account_password
(
    account_password_id INT GENERATED ALWAYS AS IDENTITY,
    account_id          INT UNIQUE   NOT NULL,
    password            VARCHAR(100) NOT NULL,
    created             TIMESTAMP    NOT NULL,
    PRIMARY KEY (account_password_id),
    CONSTRAINT account_id
        FOREIGN KEY (account_id)
            REFERENCES account (account_id)
);

