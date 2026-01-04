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

    -- 國別表
    CREATE TABLE countries (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        code CHAR(2) NOT NULL UNIQUE,                      -- ISO 3166-1國別碼 (TW、US、JP...)
        name VARCHAR(50) NOT NULL                          -- 國籍名稱 (台灣、美國、日本...)
    );

    -- 貨幣表
    CREATE TABLE currencies (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        country_id BIGINT NOT NULL,
        code CHAR(3) NOT NULL UNIQUE,                      -- 貨幣碼 (TWD、USD、JPY...)
        name VARCHAR(50) NOT NULL,                         -- 貨幣名稱 (新台幣、美元、日幣...)
        symbol VARCHAR(10),                                -- 貨幣符號 (NT$、$、¥、€、£、₩、₽...)
        FOREIGN KEY (country_id) REFERENCES countries(id)
    );

    -- 匯率表
    CREATE TABLE exchange_rates (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,

        base_currency_id BIGINT NOT NULL,                  -- 基準幣別 (USD)
        quote_currency_id BIGINT NOT NULL,                 -- 報價幣別 (TWD)

        rate DECIMAL(15,8) NOT NULL,                       -- 匯率 (1 USD = 30 TWD)
        rate_date DATE NOT NULL,                           -- 匯率日期

        created_date DATETIME NOT NULL,                    -- 建立時間 (由後端寫入)
        updated_date DATETIME NOT NULL,                    -- 更新時間 (由後端寫入)

        UNIQUE (base_currency_id, quote_currency_id, rate_date),

        FOREIGN KEY (base_currency_id) REFERENCES currencies(id),
        FOREIGN KEY (quote_currency_id) REFERENCES currencies(id)
    );

    -- 投資市場表
    CREATE TABLE markets (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        code VARCHAR(30) NOT NULL UNIQUE,                  -- 市場代號 (TW、US、JP、CN、EU、EM、EMERGING、DEVELOPED、ASIA_TIGERS)
        name VARCHAR(50) NOT NULL,                         -- 市場名稱 (台灣、美國、日本、中國、歐洲、新興市場、開發中國家、亞洲四小龍)
        market_type VARCHAR(30) NOT NULL,                  -- 市場種類 (地理 geographic、經濟 economic、金融 financial、政治 politics)
        note VARCHAR(255)
    );

    -- 市場國別關聯表
    CREATE TABLE market_countries (
        market_id BIGINT NOT NULL,
        country_id BIGINT NOT NULL,
        PRIMARY KEY (market_id, country_id),
        FOREIGN KEY (market_id) REFERENCES markets(id) ON DELETE CASCADE,
        FOREIGN KEY (country_id) REFERENCES countries(id) ON DELETE CASCADE
    );

    -- 金融機構表
    CREATE TABLE financial_institutions (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        user_id BIGINT NOT NULL,
        country_id BIGINT,
        name VARCHAR(100) NOT NULL,                        -- 金融機構名稱 (例如：國泰世華、富邦、IB、Binance)
        type VARCHAR(50) NOT NULL,                         -- 金融機構類型 (銀行 bank、券商 broker、保險公司 insurance、交易所 exchange、平台 plat)
        note VARCHAR(255),
        created_date DATETIME NOT NULL,                    -- 建立時間 (由後端寫入)
        updated_date DATETIME NOT NULL,                    -- 更新時間 (由後端寫入)
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
        FOREIGN KEY (country_id) REFERENCES countries(id)
    );

    -- 頻率類型表
    CREATE TABLE frequency_types (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        code VARCHAR(30) NOT NULL UNIQUE,                  -- repayment、payment、dividend、payout
        name VARCHAR(50) NOT NULL,                         -- 還款、配息
        note VARCHAR(255)
    );

    -- 頻率表(付款、收款、配息、不配息)
    CREATE TABLE frequencies (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        frequency_type_id BIGINT NOT NULL,
        code VARCHAR(30) NOT NULL,                         -- year、month、week、day、monthly、quarterly、semi_annual、annual、none
        name VARCHAR(50) NOT NULL,                         -- 年繳、月繳、週繳、日繳、月配、季配、半年配、年配、不配息
        note VARCHAR(255),
        UNIQUE(code, frequency_type_id),
        FOREIGN KEY (frequency_type_id) REFERENCES frequency_types(id)
    );

    -- 單位類型表
    CREATE TABLE unit_types (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        code VARCHAR(50) NOT NULL UNIQUE,                  -- inventory、financial、commodity
        name VARCHAR(50) NOT NULL,                         -- 存貨、金融商品、商品期貨
        note VARCHAR(255)
    );

    -- 單位表
    CREATE TABLE units (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        code VARCHAR(20) NOT NULL UNIQUE,                  -- 單位代碼 (例如 pkg、bottle、piece、contract、share、oz、barrel)
        name VARCHAR(50) NOT NULL,                         -- 名稱 (例如 包、瓶、個、合約、股、盎司、桶)
        unit_type_id BIGINT NOT NULL,
        note VARCHAR(255),
        FOREIGN KEY (unit_type_id) REFERENCES unit_types(id)
    );

    -- 帳本種類表
    CREATE TABLE ledger_types (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        code VARCHAR(50) NOT NULL UNIQUE,                  -- cashflow、investment、debt、receivable、fixed_asset、inventory
        name VARCHAR(50) NOT NULL                          -- 收支帳、投資帳、負債帳、應收帳、固定資產帳、存貨帳
    );


    -- 帳本表
    CREATE TABLE ledgers (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        user_id BIGINT NOT NULL,
        ledger_type_id BIGINT NOT NULL,
        
        name VARCHAR(100) NOT NULL,                         -- 帳本名稱
        note VARCHAR(255),                                  -- 備註
        
        created_date DATETIME NOT NULL,                     -- 建立時間 (由後端寫入)
        updated_date DATETIME NOT NULL,                     -- 更新時間 (由後端寫入)
        
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
        FOREIGN KEY (ledger_type_id) REFERENCES ledger_types(id)
    );

    -- 帳本成員表 (可設定多人共同維護一個帳戶)
    CREATE TABLE ledger_members (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        user_id BIGINT NOT NULL,
        ledger_id BIGINT NOT NULL,
        
        role VARCHAR(50) NOT NULL,                          -- 權限設定 (如 owner、admin、editor、viewer，由後端定義，不使用 ENUM，方便未來擴充）
        joined_date DATETIME NOT NULL,                      -- 加入時間
        
        created_date DATETIME NOT NULL,                     -- 建立時間 (由後端寫入)
        updated_date DATETIME NOT NULL,                     -- 更新時間 (由後端寫入)
        
        UNIQUE KEY uk_ledger_user (ledger_id, user_id),
        FOREIGN KEY (ledger_id) REFERENCES ledgers(id) ON DELETE CASCADE,
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );

    -- 帳戶種類表
    CREATE TABLE account_types (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        ledger_type_id BIGINT NOT NULL,
        
        code VARCHAR(50) NOT NULL,
        name VARCHAR(50) NOT NULL,                          -- 帳戶性質（如：
                                                               收支帳下的現金 cash、銀行 bank、信用卡 credit_card ，
                                                               投資帳下的股票 stock、基金 fund、債券 bond、外幣 forex、虛擬貨幣 crypto、
                                                                        貴金屬 gold/silver、定存 fixed_deposits、保險 insurance、
                                                                        期貨 futures、選擇權 option，
                                                               負債帳下的房貸 mortgage、信貸 personal_loan、車貸 auto_loan，
                                                               應收帳下的借出款 lent，
                                                               固定資產帳下的房貸產 property、汽車 automobile、機車 motorcycle、家電、3C
                                                               存貨帳下的生活用品或是備品
        note VARCHAR(255),

        FOREIGN KEY (ledger_type_id) REFERENCES ledger_types(id),
        UNIQUE (ledger_type_id, code)
    );

    -- 帳戶表
    CREATE TABLE accounts (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        user_id BIGINT NOT NULL,
        ledger_id BIGINT NOT NULL,
        account_type_id BIGINT NOT NULL,
        currency_id BIGINT NOT NULL,

        name VARCHAR(100) NOT NULL,
        balance DECIMAL(15,2) DEFAULT 0,                   -- 初始餘額
        note VARCHAR(255),                                 -- 備註
        
        created_date DATETIME NOT NULL,		               -- 建立時間 (由後端寫入)
        updated_date DATETIME NOT NULL,		               -- 更新時間 (由後端寫入)
        
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
        FOREIGN KEY (ledger_id) REFERENCES ledgers(id) ON DELETE CASCADE,
        FOREIGN KEY (account_type_id) REFERENCES account_types(id),
        FOREIGN KEY (currency_id) REFERENCES currencies(id)
    );

    -- 帳戶表子表 (收支帳 → 帳戶)
    CREATE TABLE bank_accounts (
        account_id BIGINT PRIMARY KEY,
        financial_institution_id BIGINT,
        interest_rate DECIMAL(5,2),                        -- 利率 %
        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE,
        FOREIGN KEY (financial_institution_id) REFERENCES financial_institutions(id)
    );

    -- 帳戶表子表 (收支帳 → 信用卡)
    CREATE TABLE credit_card_accounts (
        account_id BIGINT PRIMARY KEY,
        financial_institution_id BIGINT,
        
        cycle_day TINYINT NOT NULL,                        -- 結帳日 (1~31)
        due_day TINYINT NOT NULL,                          -- 繳款日 (1~31)
        credit_limit DECIMAL(15,2),                        -- 信用額度
        annual_fee DECIMAL(15,2),                          -- 年費
        
        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE,
        FOREIGN KEY (financial_institution_id) REFERENCES financial_institutions(id)
    );
    
    -- 帳戶表子表 (投資帳 → 股票、ETF、基金、債券、外幣、虛擬貨幣、貴金屬、期貨 futures、選擇權 option，）
    CREATE TABLE investment_accounts (
        account_id BIGINT PRIMARY KEY,
        financial_institution_id BIGINT,
        market_id BIGINT,
        risk_level TINYINT,                                -- 風險等級（1~5，可選）
        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE,
        FOREIGN KEY (financial_institution_id) REFERENCES financial_institutions(id),
        FOREIGN KEY (market_id) REFERENCES markets(id)
    );

    -- 帳戶表子表 (投資帳 → 定存、保險）
    CREATE TABLE term_investment_accounts (
        account_id BIGINT PRIMARY KEY,
        financial_institution_id BIGINT,
        interest_type VARCHAR(20) NOT NULL,                -- 固定 fixed / 流動 floating
        interest_rate DECIMAL(5,2),                        -- 利率 %
        start_date DATE NOT NULL,                          -- 開始日
        end_date DATE NOT NULL,                            -- 到期日
        early_termination_allowed BOOLEAN,                 -- 是否可提前贖回
        payout_frequency_id BIGINT,                        -- 配息類型
        penalty_rate DECIMAL(5,2),                         -- 違約利率
        status VARCHAR(20) NOT NULL,                       -- 狀態
        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE,
        FOREIGN KEY (financial_institution_id) REFERENCES financial_institutions(id),
        FOREIGN KEY (payout_frequency_id) REFERENCES frequencies(id)
    );

    -- 帳戶表子表 (負債帳 → 房貸、車貸、信貸）
    CREATE TABLE debt_accounts (
        account_id BIGINT PRIMARY KEY,
        financial_institution_id BIGINT,
        repayment_frequency_id BIGINT NOT NULL,
        
        interest_rate DECIMAL(5,2) NOT NULL,               -- 利率 %

        cycle_day TINYINT NOT NULL,                        -- 結帳日 (1~31)
        due_day TINYINT NOT NULL,                          -- 繳款日 (1~31)
        
        start_date DATE NOT NULL,                          -- 開始日
        end_date DATE NOT NULL,                            -- 到期日
        
        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE,
        FOREIGN KEY (financial_institution_id) REFERENCES financial_institutions(id),
        FOREIGN KEY (repayment_frequency_id) REFERENCES frequencies(id)
    );

    -- 負債合約表
    CREATE TABLE debt_contracts (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        account_id BIGINT NOT NULL,

        total_amount DECIMAL(15,2) NOT NULL,               -- 總金額
        total_terms INT NOT NULL,                          -- 總期數

        created_date DATETIME NOT NULL,

        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
    );

    -- 債務人表
    CREATE TABLE debtors (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        name VARCHAR(100) NOT NULL,
        contact_info VARCHAR(255)
    );

    -- 帳戶表子表 (應收帳款)
    CREATE TABLE receivable_accounts (
        account_id BIGINT PRIMARY KEY,
        debtor_id BIGINT NOT NULL,
        repayment_frequency_id BIGINT NOT NULL,
        
        interest_rate DECIMAL(5,2) NOT NULL,               -- 利率 %
        cycle_day TINYINT NOT NULL,                        -- 結帳日 (1~31)
        due_day TINYINT NOT NULL,                          -- 繳款日 (1~31)
        
        start_date DATE NOT NULL,                          -- 開始日
        end_date DATE NOT NULL,                            -- 到期日
        
        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE,
        FOREIGN KEY (debtor_id) REFERENCES debtors(id),
        FOREIGN KEY (repayment_frequency_id) REFERENCES frequencies(id)
    );

    -- 應收帳款合約表
    CREATE TABLE receivable_contracts (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        account_id BIGINT NOT NULL,
        debtor_id BIGINT NOT NULL,
        
        total_amount DECIMAL(15,2) NOT NULL,               -- 總金額
        interest_rate DECIMAL(5,2),                        -- 利率 %

        start_date DATE NOT NULL,                          -- 開始日
        end_date DATE NOT NULL,                            -- 到期日

        created_date DATETIME NOT NULL,

        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE,
        FOREIGN KEY (debtor_id) REFERENCES debtors(id)
    );

    -- 固定資產分類表
    CREATE TABLE asset_categories (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        code VARCHAR(50) NOT NULL UNIQUE,                   -- 房屋 house、車輛 car、家電 appliance、3C electronics
        name VARCHAR(50) NOT NULL,                          -- 名稱
        note VARCHAR(255)
    );

    -- 折舊方法表
    CREATE TABLE depreciation_methods (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        code VARCHAR(50) NOT NULL UNIQUE,                   -- straight_line、declining_balance、double_declining、sum_of_years
        name VARCHAR(50) NOT NULL,                          -- 直線法、餘額遞減法/定率法、雙倍餘額遞減法、年數總和法
        note VARCHAR(255)
    );

    -- 帳戶表子表 (固定資產帳 → 房屋、車輛、家電、3C)
    CREATE TABLE fixed_asset_accounts (
        account_id BIGINT PRIMARY KEY,
        asset_category_id BIGINT,

        asset_tag VARCHAR(50) UNIQUE,                      -- 識別碼
        purchase_date DATE NOT NULL,                       -- 購買日
        purchase_price DECIMAL(15,2) NOT NULL,             -- 購買價格
        useful_life_years INT,                             -- 使用年限
        depreciation_method_id BIGINT,
        salvage_value DECIMAL(15,2),                       -- 殘值
        location VARCHAR(100),                             -- 位置
        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE,
        FOREIGN KEY (asset_category_id) REFERENCES asset_categories(id),
        FOREIGN KEY (depreciation_method_id) REFERENCES depreciation_methods(id)
    );

    -- 存貨分類表
    CREATE TABLE inventory_categories (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        code VARCHAR(50) NOT NULL UNIQUE,                  -- 食品 food、飲品 beverage、消耗品 consumable、備件 spare_part、辦公用品office_supplies
        name VARCHAR(50) NOT NULL,                         -- 名稱
        note VARCHAR(255)
    );

    -- 存貨成本方法表
    CREATE TABLE cost_methods (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        code VARCHAR(50) NOT NULL UNIQUE,                  -- 先進先出法 fifo、後進先出法 lifo、加權平均法 average
        name VARCHAR(50) NOT NULL,                         -- 名稱
        note VARCHAR(255)
    );

    -- 帳戶表子表 (存貨帳 → 生活用品 / 備品)
    CREATE TABLE inventory_accounts (
        account_id BIGINT PRIMARY KEY,
        inventory_category_id BIGINT,
        unit_id BIGINT NOT NULL,                           -- 單位
        quantity DECIMAL(10,2) DEFAULT 0,                  -- 目前數量
        warning_level DECIMAL(10,2),                       -- 低於提醒
        cost_method_id BIGINT,
        expiry_date DATE,                                  -- 有效期限
        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE,
        FOREIGN KEY (inventory_category_id) REFERENCES inventory_categories(id),
        FOREIGN KEY (cost_method_id) REFERENCES cost_methods(id),
        FOREIGN KEY (unit_id) REFERENCES units(id)
    );

    -- 投資產品種類表
    CREATE TABLE investment_product_types (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        code VARCHAR(30) NOT NULL UNIQUE,                  -- stock、etf、fund、bond、crypto、forex、gold、futures、option
        name VARCHAR(50) NOT NULL,                         -- 股票、ETF、基金、債券、虛擬貨幣、外匯、黃金、期貨、選擇權
        is_derivative BOOLEAN DEFAULT FALSE,               -- 是否為衍生性商品
        note VARCHAR(255)
    );

    -- 時區表
    CREATE TABLE timezones (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        code VARCHAR(50) NOT NULL UNIQUE,                  -- 時區代碼 (UTC)
        name VARCHAR(100),                                 -- 名稱 (協調世界時間、美國東部時間、中原標準時間)
        utc_offset VARCHAR(6),                             -- 偏移 (+00:00、-05:00)
        note VARCHAR(255)
    );

    -- 交易所表
    CREATE TABLE exchanges (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        code VARCHAR(20) NOT NULL UNIQUE,                  -- NASDAQ、NYSE、TSE、BINANCE
        name VARCHAR(100) NOT NULL,                        -- 美國納斯達克交易所、紐約證券交易所
        country_id BIGINT,                                 -- 所屬國家
        timezone_id BIGINT,                                -- 時區
        note VARCHAR(255),
        FOREIGN KEY (country_id) REFERENCES countries(id),
        FOREIGN KEY (timezone_id) REFERENCES timezones(id)
    );

    -- 投資產品表
    CREATE TABLE investment_products (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        product_type_id BIGINT NOT NULL,
        
        market_id BIGINT,                                  -- 所屬市場 (美股、台股、幣圈)
        currency_id BIGINT NOT NULL,                       -- 報價幣別 (USD、TWD)
        exchange_id BIGINT,                                -- 交易所

        code VARCHAR(50) NOT NULL,                         -- 商品代碼 (AAPL、TSLA、BTC、0050)
        name VARCHAR(100) NOT NULL,                        -- 商品名稱 (Apple Inc.)
        isin VARCHAR(20),                                  -- 國際證券識別碼 ISIN（可為 NULL）
        is_active BOOLEAN DEFAULT TRUE,                    -- 是否仍可交易

        unit_id BIGINT,                                    -- 商品單位 (口、股、張)
        note VARCHAR(255),

        created_date DATETIME NOT NULL,                    -- 建立時間 (由後端寫入)
        updated_date DATETIME NOT NULL,                    -- 更新時間 (由後端寫入)

        UNIQUE (code, market_id),
        FOREIGN KEY (product_type_id) REFERENCES investment_product_types(id),
        FOREIGN KEY (market_id) REFERENCES markets(id),
        FOREIGN KEY (currency_id) REFERENCES currencies(id),
        FOREIGN KEY (exchange_id) REFERENCES exchanges(id),
        FOREIGN KEY (unit_id) REFERENCES units(id)
    );

    -- 基金類型表
    CREATE TABLE fund_types (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        code VARCHAR(30) NOT NULL UNIQUE,                  -- equity、bond、balanced、money_market、index、etc
        name VARCHAR(50) NOT NULL,                         -- 股票型、債券型、平衡型、貨幣市場型、指數型、其他
        note VARCHAR(255)
    );

    -- 投資產品表子表 (基金)
    CREATE TABLE fund_products (
        investment_product_id BIGINT PRIMARY KEY,
        
        financial_institution_id BIGINT,                  -- 發行金融機構
        expense_ratio DECIMAL(5,2),                       -- 總費用率
        dividend_frequency_id BIGINT,                     -- 配息頻率
        fund_type_id BIGINT,                              -- 基金類型
        
        FOREIGN KEY (investment_product_id) REFERENCES investment_products(id) ON DELETE CASCADE,
        FOREIGN KEY (financial_institution_id) REFERENCES financial_institutions(id),
        FOREIGN KEY (dividend_frequency_id) REFERENCES frequencies(id),
        FOREIGN KEY (fund_type_id) REFERENCES fund_types(id)
    );

    CREATE TABLE bond_issuers (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        code VARCHAR(50) NOT NULL UNIQUE,                   -- 代碼
        name VARCHAR(100) NOT NULL,                         -- 發行機構名稱
        country_id BIGINT NOT NULL,                         -- 所屬國家
        note VARCHAR(255),
        FOREIGN KEY (country_id) REFERENCES countries(id)
    );

    -- 債券種類表
    CREATE TABLE bond_types (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        code VARCHAR(30) NOT NULL UNIQUE,                 -- government、corporate、municipal
        name VARCHAR(50) NOT NULL,                        -- 政府債、公司債、市政債
        note VARCHAR(255)
    );

    -- 投資產品表子表 (債券)
    CREATE TABLE bond_products (
        investment_product_id BIGINT PRIMARY KEY,

        bond_issuer_id BIGINT,                            -- 發行機構
        coupon_rate DECIMAL(5,2),                         -- 票面利率
        coupon_frequency_id BIGINT,                       -- 票面配息頻率
        maturity_date DATE NOT NULL,                      -- 到期日
        face_value DECIMAL(15,2),                         -- 面額
        bond_type_id BIGINT,                              -- 債券種類

        FOREIGN KEY (investment_product_id) REFERENCES investment_products(id) ON DELETE CASCADE,
        FOREIGN KEY (bond_issuer_id) REFERENCES bond_issuers(id),
        FOREIGN KEY (coupon_frequency_id) REFERENCES frequencies(id),
        FOREIGN KEY (bond_type_id) REFERENCES bond_types(id)
    );

    -- 投資產品表子表 (期貨)
    CREATE TABLE derivative_products (
        investment_product_id BIGINT PRIMARY KEY,
        contract_size DECIMAL(15,2),                      -- 每張合約的標的數量
        expiration_date DATE NOT NULL,                    -- 到期日
        FOREIGN KEY (investment_product_id) REFERENCES investment_products(id) ON DELETE CASCADE
    );

    -- 選擇權類型表
    CREATE TABLE option_types (
        id SMALLINT PRIMARY KEY,
        code VARCHAR(10) NOT NULL UNIQUE,                 -- CALL、PUT
        name VARCHAR(50) NOT NULL                         -- 買權 Call Option、賣權 Put Option
    );

    -- 投資產品表子表 (選擇權)
    CREATE TABLE option_products (
        investment_product_id BIGINT PRIMARY KEY,
        strike_price DECIMAL(15,2) NOT NULL,              -- 履約價
        option_type_id SMALLINT NOT NULL,
        FOREIGN KEY (investment_product_id) REFERENCES derivative_products(investment_product_id),
        FOREIGN KEY (option_type_id) REFERENCES option_types(id)
    );

    -- 分類類型表
    CREATE TABLE category_types (
        id BIGINT PRIMARY KEY,
        code VARCHAR(20) NOT NULL UNIQUE,                 -- income、expense、transfer
        name VARCHAR(50) NOT NULL                         -- 收入、支出、轉帳
    );
    
    -- 大分類表
    CREATE TABLE category_groups (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        user_id BIGINT NOT NULL,
        ledger_id BIGINT NOT NULL,
        name VARCHAR(50) NOT NULL,                         -- 大分類名稱
        category_type_id BIGINT NOT NULL,                  -- 分類類型
        created_date DATETIME NOT NULL,		               -- 建立時間 (由後端寫入)
        updated_date DATETIME NOT NULL,		               -- 更新時間 (由後端寫入)
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
        FOREIGN KEY (ledger_id) REFERENCES ledgers(id) ON DELETE CASCADE,
        FOREIGN KEY (category_type_id) REFERENCES category_types(id)
    );

    -- 小分類表
    CREATE TABLE categories (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        user_id BIGINT NOT NULL,
        ledger_id BIGINT NOT NULL,
        category_group_id BIGINT NOT NULL,                 -- (對應大分類)
        name VARCHAR(50) NOT NULL,                         -- 小分類名稱
        created_date DATETIME NOT NULL,		               -- 建立時間 (由後端寫入)
        updated_date DATETIME NOT NULL,		               -- 更新時間 (由後端寫入)
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
        FOREIGN KEY (ledger_id) REFERENCES ledgers(id) ON DELETE CASCADE,
        FOREIGN KEY (category_group_id) REFERENCES category_groups(id) ON DELETE CASCADE
    );

    -- 商店類型表
    CREATE TABLE merchant_types (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        code VARCHAR(50) NOT NULL UNIQUE,                  -- restaurant, supermarket, utility
        name VARCHAR(100) NOT NULL,                        -- 餐廳、超市、公共事業
        note VARCHAR(255)
    );

    -- 交易商店表 
    CREATE TABLE merchants (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        user_id BIGINT NOT NULL,
        ledger_id BIGINT NOT NULL,
        merchant_type_id BIGINT NOT NULL,                  -- 商店 / 公司類型
        name VARCHAR(100) NOT NULL,                        -- 商店 / 公司名稱                   
        created_date DATETIME NOT NULL,	                   -- 建立時間 (由後端寫入)
        updated_date DATETIME NOT NULL,	                   -- 更新時間 (由後端寫入)
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
        FOREIGN KEY (ledger_id) REFERENCES ledgers(id) ON DELETE CASCADE,
        FOREIGN KEY (merchant_type_id) REFERENCES merchant_types(id)
    );

    -- 交易類型表
    CREATE TABLE transaction_types (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        ledger_id BIGINT NOT NULL,
        code VARCHAR(50) NOT NULL,                         -- internal code (buy, sell, interest)
        name VARCHAR(50) NOT NULL,                         -- 交易類型名稱 (如：
                                                              income / expense / buy / sell / interest / depreciation / in /
                                                              投資
                                                              負債活動 (principal / interest / fee)
                                                              應收帳款活動 (lend / receive / interest)
                                                              固定資產活動 (depreciation / repair / revalue)
                                                              存貨 (in / out / adjust)
        direction TINYINT NOT NULL,                        -- 正向 / 負向
        affects_balance BOOLEAN DEFAULT TRUE,
        note VARCHAR(255),
        created_date DATETIME NOT NULL,
        updated_date DATETIME NOT NULL,
        UNIQUE (ledger_id, code),
        FOREIGN KEY (ledger_id) REFERENCES ledgers(id) ON DELETE CASCADE
    );


    -- 交易表
    CREATE TABLE transactions (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        user_id BIGINT NOT NULL,
        ledger_id BIGINT NOT NULL,
        account_id BIGINT NOT NULL,
        transaction_type_id BIGINT NOT NULL,
        
        price DECIMAL(15,2) NULL,                           -- 價格
        quantity DECIMAL(15,4) NULL,                        -- 數量
        amount DECIMAL(15,2) NOT NULL,                      -- 合計 (實際影響帳戶餘額的金額)

        original_currency_id BIGINT NULL,                   -- 原始交易幣別 (外幣交易使用)
        original_amount DECIMAL(15,2) NULL,                 -- 原始交易金額 (外幣交易使用)
        exchange_rate_used DECIMAL(15,8) NULL,              -- 當下換算匯率 (外幣交易使用)

        transaction_date DATETIME NOT NULL,                 -- 交易日
        note VARCHAR(255),

        created_date DATETIME NOT NULL,                     -- 建立時間 (由後端寫入)
        updated_date DATETIME NOT NULL,                     -- 更新時間 (由後端寫入)

        FOREIGN KEY (user_id) REFERENCES users(id),
        FOREIGN KEY (ledger_id) REFERENCES ledgers(id),
        FOREIGN KEY (account_id) REFERENCES accounts(id),
        FOREIGN KEY (transaction_type_id) REFERENCES transaction_types(id),
        FOREIGN KEY (original_currency_id) REFERENCES currencies(id)
    );

    -- 交易表子表 (收支)
    CREATE TABLE cashflow_transaction_details (
        transaction_id BIGINT PRIMARY KEY,
        category_id BIGINT NOT NULL,
        merchant_id BIGINT NULL,
        FOREIGN KEY (transaction_id) REFERENCES transactions(id) ON DELETE CASCADE,
        FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
        FOREIGN KEY (merchant_id) REFERENCES merchants(id) ON DELETE SET NULL
    );

    -- 交易表子表 (投資)
    CREATE TABLE investment_transaction_details (
        transaction_id BIGINT PRIMARY KEY,
        
        investment_product_id BIGINT NOT NULL,
        
        fee DECIMAL(15,2) NOT NULL DEFAULT 0,               -- 手續費
        tax DECIMAL(15,2) DEFAULT 0,                        -- 稅額

        FOREIGN KEY (transaction_id) REFERENCES transactions(id) ON DELETE CASCADE,
        FOREIGN KEY (investment_product_id) REFERENCES investment_products(id)
    );
    
    -- 交易表子表 (存貨)
    CREATE TABLE inventory_transaction_details (
        transaction_id BIGINT PRIMARY KEY,
        expiry_date DATE,                                  -- 這批存貨的到期日
        FOREIGN KEY (transaction_id) REFERENCES transactions(id) ON DELETE CASCADE
    );

    -- 重複交易表
    CREATE TABLE recurring_transactions (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        user_id BIGINT NOT NULL,
        ledger_id BIGINT NOT NULL,
        account_id BIGINT NOT NULL,
        transaction_type_id BIGINT NOT NULL,

        price DECIMAL(15,2) NULL,                           -- 價格
        quantity DECIMAL(15,4) NULL,                        -- 數量
        amount DECIMAL(15,2) NOT NULL,                      -- 合計 (實際影響帳戶餘額的金額)

        original_currency_id BIGINT NULL,                   -- 原始交易幣別 (外幣交易使用)
        original_amount DECIMAL(15,2) NULL,                 -- 原始交易金額 (外幣交易使用)
        exchange_rate_used DECIMAL(15,8) NULL,              -- 當下換算匯率 (外幣交易使用)

        recurrence_frequency_id BIGINT NOT NULL,            -- 週期規則(如 一月一次、每日一次 等等，應由後端設定)
        start_date DATE NOT NULL,
        end_date DATE,

        note VARCHAR(255),

        created_date DATETIME NOT NULL,                     -- 建立時間 (由後端寫入)
        updated_date DATETIME NOT NULL,                     -- 更新時間 (由後端寫入)

        FOREIGN KEY (user_id) REFERENCES users(id),
        FOREIGN KEY (ledger_id) REFERENCES ledgers(id),
        FOREIGN KEY (account_id) REFERENCES accounts(id),
        FOREIGN KEY (transaction_type_id) REFERENCES transaction_types(id),
        FOREIGN KEY (recurrence_frequency_id) REFERENCES frequencies(id),
        FOREIGN KEY (original_currency_id) REFERENCES currencies(id)
    );


    -- 重複交易子表 (收支)
    CREATE TABLE cashflow_recurring_transaction_details (
        recurring_transaction_id BIGINT PRIMARY KEY,
        category_id BIGINT NOT NULL,
        merchant_id BIGINT NULL,
        FOREIGN KEY (recurring_transaction_id) REFERENCES recurring_transactions(id) ON DELETE CASCADE,
        FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
        FOREIGN KEY (merchant_id) REFERENCES merchants(id) ON DELETE SET NULL
    );

    
    -- 重複交易子表 (投資)
    CREATE TABLE investment_recurring_transaction_details (
        recurring_transaction_id BIGINT PRIMARY KEY,
        investment_product_id BIGINT NOT NULL,
        
        fee DECIMAL(15,2) NOT NULL DEFAULT 0,               -- 手續費
        tax DECIMAL(15,2) DEFAULT 0,                        -- 稅額

        FOREIGN KEY (recurring_transaction_id) REFERENCES recurring_transactions(id) ON DELETE CASCADE,
        FOREIGN KEY (investment_product_id) REFERENCES investment_products(id)
    );

    -- 帳戶調整表 (會計用)
    CREATE TABLE account_adjustments (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,

        account_id BIGINT NOT NULL,

        before_balance DECIMAL(15,2) NOT NULL,            -- 調整前餘額
        after_balance DECIMAL(15,2) NOT NULL,             -- 調整後餘額

        reason VARCHAR(255) NOT NULL,                     -- 調整原因
        note VARCHAR(255),

        created_date DATETIME NOT NULL,

        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
    );

    -- 投資持倉快照 (增加效能用)
    CREATE TABLE investment_positions (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,

        account_id BIGINT NOT NULL,
        investment_product_id BIGINT NOT NULL,

        quantity DECIMAL(20,8) NOT NULL,                  -- 持有數量
        avg_cost DECIMAL(15,4) NOT NULL,                  -- 平均成本
        market_value DECIMAL(15,2),                       -- 市值 (快照時計算)

        snapshot_date DATE NOT NULL,                      -- 快照日期
        created_date DATETIME NOT NULL,

        UNIQUE (account_id, investment_product_id, snapshot_date),

        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE,
        FOREIGN KEY (investment_product_id) REFERENCES investment_products(id)
    );


