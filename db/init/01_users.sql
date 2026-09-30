CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4 (),
    username VARCHAR(50) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone_number VARCHAR(20),
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO
    users (
        id,
        username,
        email,
        phone_number,
        password
    )
VALUES (
        '11111111-1111-1111-1111-111111111111',
        'michael_scott',
        'michael@dundermifflin.com',
        '+1234567890',
        '$2b$10$hashed_password_1'
    ),
    (
        '22222222-2222-2222-2222-222222222222',
        'dwight_schrute',
        'dwight@schrutefarms.com',
        '+1987654321',
        '$2b$10$hashed_password_2'
    ),
    (
        '33333333-3333-3333-3333-333333333333',
        'jim_halpert',
        'jim@dundermifflin.com',
        '+1555555555',
        '$2b$10$hashed_password_3'
    )
ON CONFLICT (id) DO NOTHING;