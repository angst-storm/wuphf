\connect messages

CREATE SCHEMA IF NOT EXISTS messages;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS messages.messages (
    id         UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    author_id  UUID NOT NULL,
    text       VARCHAR(280) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_messages_author_created
    ON messages.messages (author_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_messages_created
    ON messages.messages (created_at DESC);

CREATE TABLE IF NOT EXISTS messages.subscriptions (
    follower_id UUID NOT NULL,
    followee_id UUID NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (follower_id, followee_id),
    CHECK (follower_id <> followee_id)
);

CREATE INDEX IF NOT EXISTS idx_subscriptions_followee
    ON messages.subscriptions (followee_id);
