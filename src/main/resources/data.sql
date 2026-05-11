-- Optional: Insert test data
INSERT INTO users (user_name, user_last_name, user_email, user_password, user_location, user_type, user_preferences, n_commissions, user_com_date, user_com_message, user_com_discount, user_twitter, user_facebook, user_blue_sky, user_patreon)
VALUES
    ('Test', 'User', 'test@example.com', 'password123', 'Test City', 'Standard', 'Digital Art', 5, CURRENT_TIMESTAMP, 'Test message', 0, 'https://twitter.com/test', NULL, NULL, NULL);
