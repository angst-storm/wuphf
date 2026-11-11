CREATE SCHEMA IF NOT EXISTS ums;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS ums.users (
    id            UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name          VARCHAR(100) NOT NULL,
    email         VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS ums.roles (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name        VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS ums.users_roles (
    user_id UUID NOT NULL REFERENCES ums.users (id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES ums.roles (id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE IF NOT EXISTS ums.sessions (
    id            UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id       UUID NOT NULL REFERENCES ums.users (id) ON DELETE CASCADE,
    logged_in_at  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    logged_out_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_ums_sessions_user_id ON ums.sessions (user_id);
CREATE INDEX IF NOT EXISTS idx_ums_sessions_logged_in_at ON ums.sessions (logged_in_at DESC);

INSERT INTO ums.roles (id, name, description)
VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'ADMIN', 'Full administrative access'),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'PRODUCER', 'Can publish content'),
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'SUBSCRIBER', 'Can follow and consume content')
ON CONFLICT (name) DO NOTHING;

-- Bootstrap superadmin (password: superadmin). Assigns ADMIN to others after login.
INSERT INTO ums.users (id, name, email, password_hash, created_at, updated_at)
VALUES (
    '00000000-0000-0000-0000-000000000001',
    'Super Admin',
    'admin@wuphf.local',
    '$2b$10$N0h/gjHm/DLowuQQT1HZQ.g238h2z0Dw2X2pLF5suJWBy7N0gZHO2',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
)
ON CONFLICT (email) DO NOTHING;

INSERT INTO ums.users_roles (user_id, role_id)
VALUES (
    '00000000-0000-0000-0000-000000000001',
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'
)
ON CONFLICT DO NOTHING;
