INSERT INTO account(username, email, created, last_updated)
VALUES ('admin', 'admin@example.com', now(), now());

INSERT INTO account_password(account_id, password, created)
VALUES ((SELECT account_id FROM account WHERE username = 'admin'),
        '{bcrypt}$2a$10$eZ65RlntCZ2Nyrnfr1DxmOykkPQ1cx6wr39m3lEPfQdfRuOO/jMh2', now());

INSERT INTO account_role(account_id, role_id, created)
VALUES ((SELECT account_id FROM account WHERE username = 'admin'),
        (SELECT role_id FROM role where role_name = 'ADMINISTRATOR'), now())

