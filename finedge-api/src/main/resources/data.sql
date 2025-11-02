INSERT INTO users (id, username, password)
VALUES
(5000, 'alice', '$2a$12$qTQJNSTH3/YAVGceodxd4OHSidPoW.K5hkFVVU3HRcpX.g7/QOmYe');

INSERT INTO user_role (user_id, role)
VALUES
(5000, 'CUSTOMER');

SHOW COLUMNS FROM users;