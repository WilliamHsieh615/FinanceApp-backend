# FinFun：方方記帳，讓理財變有趣

    USE finance_app;
    
    -- 使用者表
    CREATE TABLE users (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        name VARCHAR(100) NOT NULL,                        -- 使用者名稱
        email VARCHAR(100) NOT NULL UNIQUE,                -- 使用者電子郵件
        password VARCHAR(255) NOT NULL,                    -- 使用者密碼
        created_date DATETIME NOT NULL,                    -- 建立時間 (由後端寫入)
        updated_date DATETIME NOT NULL                     -- 更新時間 (由後端寫入)
    );

    -- 帳本表
    CREATE TABLE ledgers (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        user_id BIGINT NOT NULL,
        name VARCHAR(100) NOT NULL,                         -- 帳本名稱
        type VARCHAR(50) NOT NULL,                          -- 帳本類型 (如 收支帳、投資帳、負債帳 等，由後端定義，不使用 ENUM，方便未來擴充)
        note VARCHAR(255),                                  -- 備註
        created_date DATETIME NOT NULL,
        updated_date DATETIME NOT NULL,
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );

    -- 帳本成員表 (可設定多人共同維護一個帳戶)
    CREATE TABLE ledger_members (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        user_id BIGINT NOT NULL,
        ledger_id BIGINT NOT NULL,
        role VARCHAR(50) NOT NULL,                          -- 權限設定 (如 owner、admin、editor、viewer，由後端定義，不使用 ENUM，方便未來擴充）
        joined_date DATETIME NOT NULL,
        created_date DATETIME NOT NULL,
        updated_date DATETIME NOT NULL,
        UNIQUE KEY uk_ledger_user (ledger_id, user_id),
        FOREIGN KEY (ledger_id) REFERENCES ledgers(id) ON DELETE CASCADE,
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );

    -- 帳戶表 
    CREATE TABLE accounts (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        user_id BIGINT NOT NULL,
        ledger_id BIGINT NOT NULL,
        name VARCHAR(50) NOT NULL,                         -- 帳戶名稱
        type VARCHAR(50) NOT NULL,                         -- 帳戶類型(如 現金、信用卡、銀行)
        balance DECIMAL(15,2) DEFAULT 0,                   -- 初始餘額
        currency CHAR(3) DEFAULT 'TWD',                    -- 幣別
        note VARCHAR(255),                                 -- 備註
        cycle_day TINYINT NULL,                            -- 結帳日 (1~31，僅使用信用卡時顯示此欄位)
        due_day TINYINT NULL,                              -- 付款日 (1~31，僅使用信用卡時顯示此欄位)
        created_date DATETIME NOT NULL,		               -- 建立時間 (由後端寫入)
        updated_date DATETIME NOT NULL,		               -- 更新時間 (由後端寫入)
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
        FOREIGN KEY (ledger_id) REFERENCES ledgers(id) ON DELETE CASCADE
    );

    -- 大分類表
    CREATE TABLE category_groups (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        user_id BIGINT NOT NULL,
        ledger_id BIGINT NOT NULL,
        name VARCHAR(50) NOT NULL,                         -- 大分類名稱
        type ENUM('income','expense','transfer') NOT NULL, -- 分類類型
        created_date DATETIME NOT NULL,		               -- 建立時間 (由後端寫入)
        updated_date DATETIME NOT NULL,		               -- 更新時間 (由後端寫入)
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
        FOREIGN KEY (ledger_id) REFERENCES ledgers(id) ON DELETE CASCADE
    );

    -- 小分類表
    CREATE TABLE categories (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        user_id BIGINT NOT NULL,
        ledger_id BIGINT NOT NULL,
        category_group_id BIGINT NOT NULL,                 -- (對應大分類)
        name VARCHAR(50) NOT NULL,                         -- 小分類名稱
        type ENUM('income','expense','transfer') NOT NULL, -- (冗餘，協助報表快速查找，後端應由注意是否有與大分類)
        created_date DATETIME NOT NULL,		               -- 建立時間 (由後端寫入)
        updated_date DATETIME NOT NULL,		               -- 更新時間 (由後端寫入)
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
        FOREIGN KEY (ledger_id) REFERENCES ledgers(id) ON DELETE CASCADE,
        FOREIGN KEY (category_group_id) REFERENCES category_groups(id) ON DELETE CASCADE
    );


    -- 交易商店表 
    CREATE TABLE merchants (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        user_id BIGINT NOT NULL,
        ledger_id BIGINT NOT NULL,
        name VARCHAR(100) NOT NULL,                        -- 商店 / 公司名稱
        type VARCHAR(50) NOT NULL,		                   -- 商店 / 公司類型
        created_date DATETIME NOT NULL,	                   -- 建立時間 (由後端寫入)
        updated_date DATETIME NOT NULL,	                   -- 更新時間 (由後端寫入)
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
        FOREIGN KEY (ledger_id) REFERENCES ledgers(id) ON DELETE CASCADE
    );

    -- 單筆帳目表
    CREATE TABLE transactions (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        user_id BIGINT NOT NULL,
        ledger_id BIGINT NOT NULL,
        account_id BIGINT NOT NULL,
        category_id BIGINT NOT NULL,
        merchant_id BIGINT NULL,
        type ENUM('income','expense','transfer') NOT NULL,  -- (冗餘，協助報表快速查找，後端應由注意是否有與大分類表的類型一致)
        counterparty_type_id BIGINT NULL,
        amount DECIMAL(15,2) NOT NULL,
        note VARCHAR(255),
        transaction_date DATETIME NOT NULL,                 -- 交易時間 (由後端寫入)
        created_date DATETIME NOT NULL,		                -- 建立時間 (由後端寫入)
        updated_date DATETIME NOT NULL,		                -- 更新時間 (由後端寫入)
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
        FOREIGN KEY (ledger_id) REFERENCES ledgers(id) ON DELETE CASCADE,
        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE,
        FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
        FOREIGN KEY (merchant_id) REFERENCES merchants(id) ON DELETE SET NULL
    );

    -- 重複單筆帳目表
    CREATE TABLE recurring_transactions (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        user_id BIGINT NOT NULL,
        ledger_id BIGINT NOT NULL,
        account_id BIGINT NOT NULL,
        category_id BIGINT NOT NULL,
        merchant_id BIGINT NULL,
        type ENUM('income','expense','transfer') NOT NULL,  -- (冗餘，協助報表快速查找，後端應由注意是否有與cotegories表一致)
        counterparty_type_id BIGINT NULL,
        amount DECIMAL(15,2) NOT NULL,
        note VARCHAR(255),
        recurrence_rule VARCHAR(50) NOT NULL,               -- 週期規則(如 一月一次、每日一次 等等，應由後端設定)
        start_date DATETIME NOT NULL,                       -- 開始時間 (由後端寫入)
        end_date DATETIME,                                  -- 結束時間 (由後端寫入)
        created_date DATETIME NOT NULL,		                -- 建立時間 (由後端寫入)
        updated_date DATETIME NOT NULL,		                -- 更新時間 (由後端寫入)
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
        FOREIGN KEY (ledger_id) REFERENCES ledgers(id) ON DELETE CASCADE,
        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE,
        FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
        FOREIGN KEY (merchant_id) REFERENCES merchants(id) ON DELETE SET NULL
    );

    CREATE INDEX idx_transactions_user_date ON transactions(user_id, transaction_date);
    CREATE INDEX idx_transactions_ledger_date ON transactions(ledger_id, transaction_date);
    CREATE INDEX idx_transactions_account ON transactions(account_id);
    CREATE INDEX idx_transactions_category ON transactions(category_id);
    CREATE INDEX idx_transactions_merchant ON transactions(merchant_id);

