INSERT INTO account(username, email, created, last_updated)
VALUES ('moderator', 'moderator@example.com', now(), now());

INSERT INTO account_password(account_id, password, created)
VALUES ((SELECT account_id FROM account WHERE username = 'moderator'),
        '{bcrypt}$2a$10$GE1dAUsFtkmPHdygYt1Zw.YXxrXFd90LsNXALwUrtmsgsQu9vGnqu', now());

INSERT INTO account_role(account_id, role_id, created)
VALUES ((SELECT account_id FROM account WHERE username = 'moderator'),
        (SELECT role_id FROM role where role_name = 'MODERATOR'), now())

