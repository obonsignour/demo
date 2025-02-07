-- Create schema if it doesn't exist
CREATE SCHEMA IF NOT EXISTS demo;

-- Create users table
CREATE TABLE demo.users (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    CONSTRAINT users_email_key UNIQUE (email)
);

-- Create index on email for faster lookups
CREATE INDEX idx_users_email ON demo.users(email);

-- Grant permissions if needed (adjust according to your needs)
-- GRANT USAGE ON SCHEMA own TO your_user;
-- GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA own TO your_user;
-- GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA own TO your_user;