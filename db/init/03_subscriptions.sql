CREATE TABLE IF NOT EXISTS subscriptions (
    follower_id UUID NOT NULL,
    followee_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (follower_id, followee_id)
);

-- Индекс для оптимизации работы воркера
CREATE INDEX IF NOT EXISTS idx_subscriptions_followee_id ON subscriptions(followee_id);

INSERT INTO subscriptions (follower_id, followee_id) VALUES
-- Dwight подписывается на Michael
('22222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-111111111111'),
-- Jim подписывается на Dwight
('33333333-3333-3333-3333-333333333333', '22222222-2222-2222-2222-222222222222'),
-- Michael подписывается на Jim
('11111111-1111-1111-1111-111111111111', '33333333-3333-3333-3333-333333333333')
ON CONFLICT DO NOTHING;
