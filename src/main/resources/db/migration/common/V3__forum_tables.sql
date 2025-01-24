CREATE TABLE category
(
    category_id  INT GENERATED ALWAYS AS IDENTITY,
    name         VARCHAR(100) UNIQUE NOT NULL,
    description  VARCHAR(320),
    created      TIMESTAMP           NOT NULL,
    last_updated TIMESTAMP           NOT NULL,
    PRIMARY KEY (category_id)
);

CREATE TABLE post
(
    post_id      INT GENERATED ALWAYS AS IDENTITY,
    category_id  INT          NOT NULL,
    account_id   INT          NOT NULL,
    title        varchar(100) NOT NULL,
    body         TEXT         NOT NULL,
    created      TIMESTAMP    NOT NULL,
    last_updated TIMESTAMP    NOT NULL,
    PRIMARY KEY (post_id),
    CONSTRAINT category_id
        FOREIGN KEY (category_id)
            REFERENCES category (category_id)
);

CREATE TABLE reply
(
    reply_id     INT GENERATED ALWAYS AS IDENTITY,
    post_id      INT       NOT NULL,
    account_id   INT       NOT NULL,
    body         TEXT      NOT NULL,
    created      TIMESTAMP NOT NULL,
    last_updated TIMESTAMP NOT NULL,
    PRIMARY KEY (reply_id),
    CONSTRAINT post_id
        FOREIGN KEY (post_id)
            REFERENCES post (post_id),
    CONSTRAINT account_id
        FOREIGN KEY (account_id)
            REFERENCES account (account_id)
);

