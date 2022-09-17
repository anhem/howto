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
