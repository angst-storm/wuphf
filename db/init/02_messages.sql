CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS messages (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4 (),
    text VARCHAR(280) NOT NULL,
    author_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Индексы для оптимизации выборок
CREATE INDEX IF NOT EXISTS idx_messages_author_id ON messages (author_id);

CREATE INDEX IF NOT EXISTS idx_messages_created_at ON messages (created_at DESC);

-- author_id соответствует UUID пользователей из БД Users
INSERT INTO
    messages (id, text, author_id)
VALUES (
        uuid_generate_v4 (),
        'WUPHF! I just sent a WUPHF!',
        '11111111-1111-1111-1111-111111111111'
    ),
    (
        uuid_generate_v4 (),
        'Bears. Beets. Battlestar Galactica.',
        '22222222-2222-2222-2222-222222222222'
    ),
    (
        uuid_generate_v4 (),
        'Question. What kind of bear is best?',
        '33333333-3333-3333-3333-333333333333'
    ),
    (
        uuid_generate_v4 (),
        'Fact. Bears eat beets.',
        '22222222-2222-2222-2222-222222222222'
    );