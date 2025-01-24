INSERT INTO account(username, email, created, last_updated)
VALUES ('user', 'user@example.com', now(), now());

INSERT INTO account_password(account_id, password, created)
VALUES ((SELECT account_id FROM account WHERE username = 'user'),
        '{bcrypt}$2a$10$1sbVdjj1RBNWqBy3G5t07enFofQqBvlyYMUgyUYjoo6hsFa.MwRwO', now());

INSERT INTO account_role(account_id, role_id, created)
VALUES ((SELECT account_id FROM account WHERE username = 'user'),
        (SELECT role_id FROM role where role_name = 'USER'), now())

