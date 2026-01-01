# FinFun：方方面面記帳，讓理財變有趣

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
        type VARCHAR(50) NOT NULL,                          -- 帳本類型 (如 收支帳、投資帳、負債帳、應收帳款、固定資產帳、庫存帳、自訂帳，由後端定義，不使用 ENUM，方便未來擴充)
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
        type VARCHAR(50) NOT NULL,                         -- 帳戶性質（如：
                                                              收支帳下的現金、信用卡、銀行，
                                                              投資帳下的股票、基金、債券、外幣、虛擬貨幣、貴金屬、定存、保險，
                                                              負債帳下的房貸、信貸、車貸，
                                                              應收帳下的借出款，
                                                              固定資產帳下的房貸產、汽車、機車、家電、3C
                                                              存貨帳下的生活用品或是備品
                                                              由後端定義，不使用 ENUM，方便未來擴充）
        balance DECIMAL(15,2) DEFAULT 0,                   -- 初始餘額
        currency CHAR(3) DEFAULT 'TWD',                    -- 幣別 (應獨立成一個資料表)
        note VARCHAR(255),                                 -- 備註
        created_date DATETIME NOT NULL,		               -- 建立時間 (由後端寫入)
        updated_date DATETIME NOT NULL,		               -- 更新時間 (由後端寫入)
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
        FOREIGN KEY (ledger_id) REFERENCES ledgers(id) ON DELETE CASCADE
    );

    -- 帳戶表子表 (收支帳 → 信用卡)
    CREATE TABLE credit_card_accounts (
        account_id BIGINT PRIMARY KEY,
        cycle_day TINYINT NOT NULL,                        -- 結帳日 (1~31)
        due_day TINYINT NOT NULL,                          -- 繳款日 (1~31)
        credit_limit DECIMAL(15,2),                        -- 信用額度
        annual_fee DECIMAL(15,2),                          -- 年費                   
        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
    );

    -- 帳戶表子表 (投資帳 → 股票、基金、債券、外幣、貴金屬）
    CREATE TABLE investment_accounts (
        account_id BIGINT PRIMARY KEY,
        investment_type VARCHAR(50) NOT NULL,              -- stock / fund / bond / forex / crypto / gold
        broker VARCHAR(100),                               -- 券商 / 平台
        risk_level TINYINT,                                -- 風險等級（1~5，可選）
        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
    );

    -- 帳戶表子表 (投資帳 → 定存、保險）
    CREATE TABLE term_investment_accounts (
        account_id BIGINT PRIMARY KEY,
        interest_rate DECIMAL(5,2),                        -- 利率 %
        start_date DATE NOT NULL,                          -- 開始日
        end_date DATE NOT NULL,                            -- 到期日
        payout_type VARCHAR(50),                           -- 配息類型 (到期 / 定期配息)
        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
    );

    -- 帳戶表子表 (負債帳 → 房貸、車貸、信貸）
    CREATE TABLE loan_accounts (
        account_id BIGINT PRIMARY KEY,
        repayment_type VARCHAR(50) NOT NULL,               -- 還款類型 (年繳、月繳、週繳、日繳，不使用 ENUM，方便未來擴充)
        total_amount DECIMAL(15,2) NOT NULL,               -- 總金額
        interest_rate DECIMAL(5,2) NOT NULL,               -- 利率 %
        total_terms INT NOT NULL,                          -- 總期數
        remaining_terms INT NOT NULL                       -- 剩餘期數
        remaining_amount DECIMAL(15,2)                     -- 剩餘金額
        cycle_day TINYINT NOT NULL,                        -- 結帳日 (1~31)
        due_day TINYINT NOT NULL,                          -- 繳款日 (1~31)
        start_date DATE NOT NULL,                          -- 開始日
        end_date DATE NOT NULL,                            -- 到期日
        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
    );

    -- 帳戶表子表 (應收帳款)
    CREATE TABLE receivable_accounts (
        account_id BIGINT PRIMARY KEY,
        debtor_name VARCHAR(100) NOT NULL,                 -- 借款人 (應獨立成一資料表)
        total_amount DECIMAL(15,2) NOT NULL,               -- 總金額
        interest_rate DECIMAL(5,2) NOT NULL,               -- 利率 %
        cycle_day TINYINT NOT NULL,                        -- 結帳日 (1~31)
        due_day TINYINT NOT NULL,                          -- 繳款日 (1~31)
        start_date DATE NOT NULL,                          -- 開始日
        end_date DATE NOT NULL,                            -- 到期日
        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
    );

    -- 帳戶表子表 (固定資產帳 → 房屋、車輛、家電、3C)
    CREATE TABLE fixed_asset_accounts (
        account_id BIGINT PRIMARY KEY,
        purchase_date DATE NOT NULL,                       -- 購買日
        purchase_price DECIMAL(15,2) NOT NULL,             -- 購買價格
        useful_life_years INT,                             -- 使用年限
        salvage_value DECIMAL(15,2),                       -- 殘值
        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
    );

    -- 帳戶表子表 (存貨帳 → 生活用品 / 備品)
    CREATE TABLE inventory_accounts (
        account_id BIGINT PRIMARY KEY,
        unit VARCHAR(20) NOT NULL,                         -- 包 / 瓶 / 個
        quantity DECIMAL(10,2) DEFAULT 0,                  -- 目前數量
        warning_level DECIMAL(10,2),                       -- 低於提醒
        average_cost DECIMAL(15,2),                        -- 平均成本
        expiry_date DATE,                                  -- 有效期限
        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
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

    -- 交易表
    CREATE TABLE transactions (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        user_id BIGINT NOT NULL,
        ledger_id BIGINT NOT NULL,
        account_id BIGINT NOT NULL,

        ledger_type VARCHAR(50) NOT NULL,                   -- (冗餘，協助報表快速查找帳戶類型 income / investment / debt / asset / inventory / custom)
        transaction_type VARCHAR(50) NOT NULL,              -- 交易類型 (income / expense / buy / sell / interest / depreciation / in / out …，不使用 ENUM，方便未來擴充)

        price DECIMAL(15,2) NULL,                           -- 價格
        quantity DECIMAL(15,4) NULL,                        -- 數量
        amount DECIMAL(15,2) NOT NULL,                      -- 合計

        transaction_date DATETIME NOT NULL,                 -- 交易日
        note VARCHAR(255),

        created_date DATETIME NOT NULL,                     -- 建立時間 (由後端寫入)
        updated_date DATETIME NOT NULL,                     -- 更新時間 (由後端寫入)

        FOREIGN KEY (user_id) REFERENCES users(id),
        FOREIGN KEY (ledger_id) REFERENCES ledgers(id),
        FOREIGN KEY (account_id) REFERENCES accounts(id)
    );

    -- 交易表子表 (單筆收支)
    CREATE TABLE income_transaction_details (
        transaction_id BIGINT PRIMARY KEY,
        category_id BIGINT NOT NULL,
        merchant_id BIGINT NULL,
        FOREIGN KEY (transaction_id) REFERENCES transactions(id) ON DELETE CASCADE
        FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
        FOREIGN KEY (merchant_id) REFERENCES merchants(id) ON DELETE SET NULL
    );

    -- 交易表子表 (單筆投資)
    CREATE TABLE investment_transaction_details (
        transaction_id BIGINT PRIMARY KEY,
        
        asset_type VARCHAR(50) NOT NULL,                    -- 投資商品總類 (股票、基金、債券、外幣、虛擬貨幣、貴金屬、定存、保險、其他，不使用 ENUM，方便未來擴充)
        asset_code VARCHAR(100) NOT NULL,                   -- 投資商品代號或名稱 (2330、AAPL、USDJPY)
        market VARCHAR(50) NOT NULL,                        -- 投資的市場(如 台股 美股 日股 A股 歐股 等等)
        action VARCHAR(50) NOT NULL,                        -- 投資活動 (買、賣、配息或配股，不使用 ENUM，方便未來擴充)
        
        fee DECIMAL(15,2) NOT NULL DEFAULT 0,               -- 手續費
        tax DECIMAL(15,2) DEFAULT 0,                        -- 稅額

        FOREIGN KEY (transaction_id) REFERENCES transactions(id) ON DELETE CASCADE
    );

    -- 交易表子表 (單筆負債)
    CREATE TABLE debt_transaction_details (
        transaction_id BIGINT PRIMARY KEY,

        action VARCHAR(50) NOT NULL,                        -- 負債活動 (principal / interest / fee)

        FOREIGN KEY (transaction_id) REFERENCES transactions(id) ON DELETE CASCADE
    );

    -- 交易表子表 (單筆應收帳款)
    CREATE TABLE receivable_transactions_details (
        transaction_id BIGINT PRIMARY KEY,

        action VARCHAR(50) NOT NULL,                        -- 應收帳款活動 (lend / receive / interest)

        FOREIGN KEY (transaction_id) REFERENCES transactions(id) ON DELETE CASCADE
    );

    -- 交易表子表 (單筆固定資產)
    CREATE TABLE fixed_asset_transaction_details (
        transaction_id BIGINT PRIMARY KEY,

        action VARCHAR(50) NOT NULL,                        -- 固定資產活動 (depreciation / repair / revalue)

        FOREIGN KEY (transaction_id) REFERENCES transactions(id) ON DELETE CASCADE
    );

    -- 交易表子表 (單筆存貨)
    CREATE TABLE inventory_transaction_details (
        transaction_id BIGINT PRIMARY KEY,

        action VARCHAR(50) NOT NULL,                        -- 存貨 (in / out / adjust)

        FOREIGN KEY (transaction_id) REFERENCES transactions(id) ON DELETE CASCADE
    );

    


    -- 重複交易表
    CREATE TABLE recurring_transactions (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        user_id BIGINT NOT NULL,
        ledger_id BIGINT NOT NULL,
        account_id BIGINT NOT NULL,

        ledger_type VARCHAR(50) NOT NULL,
        transaction_type VARCHAR(50) NOT NULL,

        amount DECIMAL(15,2),
        quantity DECIMAL(15,4),

        recurrence_rule VARCHAR(50) NOT NULL,               -- 週期規則(如 一月一次、每日一次 等等，應由後端設定)
        start_date DATE NOT NULL,
        end_date DATE,

        note VARCHAR(255),

        created_date DATETIME NOT NULL,
        updated_date DATETIME NOT NULL
    );


    

    -- 收支單筆重複帳目表
    CREATE TABLE income_recurring_transactions (
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

    
    -- 投資單筆重複帳目表
    CREATE TABLE investment_recurring_transactions (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        user_id BIGINT NOT NULL,
        ledger_id BIGINT NOT NULL,
        account_id BIGINT NOT NULL,
        
        asset_type VARCHAR(50) NOT NULL,                    -- 投資商品總類 (股票、基金、債券、外幣、虛擬貨幣、貴金屬、定存、保險、其他，不使用 ENUM，方便未來擴充)
        asset_code VARCHAR(100) NOT NULL,                   -- 投資商品代號或名稱 (2330、AAPL、USDJPY)
        market VARCHAR(50) NOT NULL,                        -- 投資的市場(如 台股 美股 日股 A股 歐股 等等)
        action VARCHAR(50) NOT NULL,                        -- 投資活動 (買、賣、配息或配股，不使用 ENUM，方便未來擴充)

        amount DECIMAL(15,2),                               -- 定期購買金額               
        quantity DECIMAL(15,4),                             -- 定期購買數量
        
        price DECIMAL(15,2) NOT NULL,                       -- 實際購買價格
        quantity DECIMAL(15,4),                             -- 實際購買數量
        
        note VARCHAR(255),
        recurrence_rule VARCHAR(50) NOT NULL,               -- 週期規則(如 一月一次、每日一次 等等，應由後端設定)
        start_date DATE NOT NULL,
        end_date DATE,

        created_date DATETIME NOT NULL,
        updated_date DATETIME NOT NULL,

        FOREIGN KEY (user_id) REFERENCES users(id),
        FOREIGN KEY (ledger_id) REFERENCES ledgers(id),
        FOREIGN KEY (account_id) REFERENCES accounts(id)
    );

    

    
    -- 負債單筆重複帳目表
    CREATE TABLE debt_recurring_transactions (

    );

    -- 應收帳款單筆重複帳目表

    

    -- 存貨單筆重複帳目表
    

    CREATE INDEX idx_transactions_user_date ON transactions(user_id, transaction_date);
    CREATE INDEX idx_transactions_ledger_date ON transactions(ledger_id, transaction_date);
    CREATE INDEX idx_transactions_account ON transactions(account_id);
    CREATE INDEX idx_transactions_category ON transactions(category_id);
    CREATE INDEX idx_transactions_merchant ON transactions(merchant_id);

