CREATE TABLE role
(
    role_id      INT GENERATED ALWAYS AS IDENTITY,
    role_name    VARCHAR(30) UNIQUE  NOT NULL CHECK (UPPER(role_name) = role_name),
    description  VARCHAR(320) UNIQUE NOT NULL,
    created      TIMESTAMP           NOT NULL,
    last_updated TIMESTAMP           NOT NULL,
    PRIMARY KEY (role_id)
);

CREATE TABLE account_role
(
    account_role_id INT GENERATED ALWAYS AS IDENTITY,
    account_id      INT       NOT NULL,
    role_id         INT       NOT NULL,
    created         TIMESTAMP NOT NULL,
    PRIMARY KEY (account_role_id),
    CONSTRAINT account_id
        FOREIGN KEY (account_id)
            REFERENCES account (account_id),
    CONSTRAINT role_id
        FOREIGN KEY (role_id)
            REFERENCES role (role_id),
    UNIQUE (account_id, role_id)
);

INSERT INTO role(role_name, description, created, last_updated)
VALUES ('ADMINISTRATOR', 'An Administrator account to access restricted resources', now(), now());
INSERT INTO role(role_name, description, created, last_updated)
VALUES ('USER', 'A regular account', now(), now());
INSERT INTO role(role_name, description, created, last_updated)
VALUES ('MODERATOR', 'An account that can access some restricted resources', now(), now());

