-- 初始化腳本：建立資料表並插入初始資料

-- 1. 建立 users 表
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    phone_number VARCHAR(50),
    card_number VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- 2. 建立 wallets 表
CREATE TABLE IF NOT EXISTS wallets (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    balance NUMERIC(19, 2) NOT NULL DEFAULT 0,
    currency VARCHAR(3) NOT NULL DEFAULT 'TWD',
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_wallet_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 3. 建立 transactions 表
CREATE TABLE IF NOT EXISTS transactions (
    id BIGSERIAL PRIMARY KEY,
    from_user_id BIGINT NOT NULL,
    to_user_id BIGINT NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'TWD',
    transaction_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    description TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_transaction_from_user FOREIGN KEY (from_user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_transaction_to_user FOREIGN KEY (to_user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 4. 建立 idempotency_keys 表（冪等性表）
CREATE TABLE IF NOT EXISTS idempotency_keys (
    id BIGSERIAL PRIMARY KEY,
    idempotency_key VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    endpoint VARCHAR(255) NOT NULL,
    request_hash VARCHAR(64),
    response TEXT,
    status_code INT,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL
);

-- 5. 建立索引以提升查詢效能
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_wallets_user_id ON wallets(user_id);
CREATE INDEX IF NOT EXISTS idx_transactions_from_user_id ON transactions(from_user_id);
CREATE INDEX IF NOT EXISTS idx_transactions_to_user_id ON transactions(to_user_id);
CREATE INDEX IF NOT EXISTS idx_transactions_created_at ON transactions(created_at);
CREATE INDEX IF NOT EXISTS idx_idempotency_key ON idempotency_keys(idempotency_key);
CREATE INDEX IF NOT EXISTS idx_user_endpoint ON idempotency_keys(user_id, endpoint);
CREATE INDEX IF NOT EXISTS idx_expires_at ON idempotency_keys(expires_at);

-- 6. 插入初始用戶資料
INSERT INTO users (username, email, password, full_name, phone_number, card_number, created_at, updated_at)
VALUES 
    ('alice', 'alice@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Alice Chen', '0912345678', '1234567890123456', NOW(), NOW()),
    ('bob', 'bob@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Bob Wang', '0923456789', '2345678901234567', NOW(), NOW()),
    ('charlie', 'charlie@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Charlie Lin', '0934567890', '3456789012345678', NOW(), NOW()),
    ('diana', 'diana@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Diana Liu', '0945678901', '4567890123456789', NOW(), NOW()),
    ('eve', 'eve@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Eve Zhang', '0956789012', '5678901234567890', NOW(), NOW())
ON CONFLICT (username) DO NOTHING;

-- 7. 為每個用戶建立錢包並設置初始餘額
INSERT INTO wallets (user_id, balance, currency, created_at, updated_at)
SELECT 
    u.id,
    CASE 
        WHEN u.username = 'alice' THEN 10000.00
        WHEN u.username = 'bob' THEN 5000.00
        WHEN u.username = 'charlie' THEN 8000.00
        WHEN u.username = 'diana' THEN 12000.00
        WHEN u.username = 'eve' THEN 3000.00
        ELSE 0.00
    END,
    'TWD',
    NOW(),
    NOW()
FROM users u
WHERE NOT EXISTS (SELECT 1 FROM wallets w WHERE w.user_id = u.id);

-- 8. 插入一些範例交易記錄（可選）
INSERT INTO transactions (from_user_id, to_user_id, amount, currency, transaction_type, status, description, created_at, updated_at)
SELECT 
    u1.id,
    u2.id,
    500.00,
    'TWD',
    'TRANSFER',
    'COMPLETED',
    '測試轉帳',
    NOW() - INTERVAL '1 day',
    NOW() - INTERVAL '1 day'
FROM users u1, users u2
WHERE u1.username = 'alice' AND u2.username = 'bob'
  AND NOT EXISTS (
      SELECT 1 FROM transactions t 
      WHERE t.from_user_id = u1.id 
        AND t.to_user_id = u2.id 
        AND t.amount = 500.00
  )
LIMIT 1;

