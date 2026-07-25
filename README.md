# FinFun｜讓理財變得更有趣

    USE finance_app;

    -- (V1)資源提供者
    CREATE TABLE resource_providers (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- 代號 (EMOJI、LUCIDE、MATERIAL、CUSTOM_UPLOAD)
        name                             VARCHAR(100)   NOT NULL,                        -- 名稱 (avatar、logo、banner、receipt、attachment、thumbnail、icon、statement)
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        resource_url                     VARCHAR(255)   NULL,
        note                             VARCHAR(255),
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL                             -- 刪除時間 (由後端寫入)
    );

    -- (V1)儲存提供者
    CREATE TABLE storage_providers (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- 代號
        name                             VARCHAR(100)   NOT NULL,                        -- 名稱
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        provider_url                     VARCHAR(255)   NULL,
        note                             VARCHAR(255),
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL                             -- 刪除時間 (由後端寫入)
    );

    -- (V1)實體類型表(用於資料表對應資料表)
    CREATE TABLE entity_types (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- 資料表代號
        name                             VARCHAR(100)   NOT NULL,                        -- 資料表名稱
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL                             -- 刪除時間 (由後端寫入)
    );

    -- (V1)檔案角色
    CREATE TABLE file_roles (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- 代號
        name                             VARCHAR(100)   NOT NULL,                        -- 名稱 (avatar、logo、banner、receipt、attachment、thumbnail、icon、statement)
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL                             -- 刪除時間 (由後端寫入)
    );
    
    -- (V1)檔案類型表
    CREATE TABLE file_types (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- 代號 (JPG、PNG、PDF、SVG、WEBP)
        name                             VARCHAR(100)   NOT NULL,                        -- 名稱 (JPEG Image、PNG Image、PDF File)
        mime_type                        VARCHAR(100)   NOT NULL,                        -- 格式類型 (text/plain、image/jpeg、image/png、application/pdf)
        extension                        VARCHAR(20)    NOT NULL,                        -- 副檔名 (.jpg、.png、.pdf)
        is_image                         BOOLEAN        NOT NULL DEFAULT FALSE,          -- 是否是影像
        is_previewable                   BOOLEAN        NOT NULL DEFAULT FALSE,          -- 是否可以預覽
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL                             -- 刪除時間 (由後端寫入)
    );

    -- (V1)檔案表
    CREATE TABLE files (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        user_id                          BIGINT         NULL,                            -- 誰上傳的檔案
        resource_provider_id             BIGINT         NULL,                            -- 資源提供者 (EMOJI、LUCIDE、MATERIAL、CUSTOM_UPLOAD)
        storage_provider_id              BIGINT         NOT NULL,                        -- 儲存空間提供者 LOCAL、S3、CLOUDINARY、GCS
        entity_type_id                   BIGINT         NOT NULL,                        -- 使用在哪一張表
        entity_id                        BIGINT         NOT NULL,                        -- 對應表中哪一筆資料的id
        file_role_id                     BIGINT         NOT NULL,                        -- 檔案的角色
        original_name                    VARCHAR(255)   NOT NULL,                        -- 使用者原始檔名
        storage_key                      VARCHAR(255)   NULL,                            -- 系統儲存檔名(UUID)
        file_path                        VARCHAR(500)   NOT NULL,                        -- 檔案連結
        thumbnail_path                   VARCHAR(500)   NULL,                            -- 縮圖連結
        file_type_id                     BIGINT         NOT NULL,                        -- 檔案類型 JPG、PNG、GIF、TXT 
        file_size                        BIGINT         NULL,                            -- 檔案大小
        width                            INT            NULL,                            -- 圖片寬
        height                           INT            NULL,                            -- 圖片高
        checksum                         VARCHAR(100)   NULL,                            -- 雜湊值 SHA256、MD5(把檔案或圖片變成一串代碼)
        is_public                        BOOLEAN        NOT NULL DEFAULT FALSE,          -- 是否公開
        sort_order                       INT            NOT NULL DEFAULT 0,              -- 排序順序
        is_system                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否來自系統
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)
        INDEX (entity_type_id, entity_id),
        INDEX (user_id),
        INDEX (file_role_id),
        UNIQUE(entity_type_id, entity_id, file_role_id, sort_order),
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL ON UPDATE CASCADE,
        FOREIGN KEY (resource_provider_id) REFERENCES resource_providers(id) ON DELETE SET NULL ON UPDATE CASCADE,
        FOREIGN KEY (storage_provider_id) REFERENCES storage_providers(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (entity_type_id) REFERENCES entity_types(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (file_role_id) REFERENCES file_roles(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (file_type_id) REFERENCES file_types(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- (V1)圖示表
    CREATE TABLE icons (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        resource_provider_id             BIGINT         NOT NULL,
        file_id                          BIGINT         NULL,                            -- 圖片型icon需要綁file_id，文字型icon不用
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- 代碼 (FOOD、INVESTMENT、HOUSE、INSURANCE)
        name                             VARCHAR(100)   NOT NULL,                        -- 名稱 (Food、Investment)
        icon_value                       VARCHAR(255)   NULL,                            -- 圖示 (圖片型icon此沒有值，文字型icon就有值)
        hex_color                        VARCHAR(10)    NULL,                            -- 圖示十六進位色彩碼
        background_color                 VARCHAR(10)    NULL,                            -- 背景顏色
        is_system                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否來自系統
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)
        FOREIGN KEY (resource_provider_id) REFERENCES resource_providers(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (file_id) REFERENCES files(id) ON DELETE SET NULL ON UPDATE CASCADE
    );
    
    -- (V1)國別表
    CREATE TABLE countries (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        iso2                             CHAR(2)        NOT NULL UNIQUE,                 -- ISO 3166-1國別碼 (兩碼)
        iso3                             CHAR(3)        NULL,                            -- ISO 3166-1國別碼 (三碼)
        iso_numeric                      CHAR(3)        NULL UNIQUE,                     -- ISO 3166-1國別碼 (數字)
        phone_code                       VARCHAR(10)    NULL,                            -- 國際電話區碼
        name                             VARCHAR(100)   NOT NULL,                        -- 國家名稱 (英文)
        native_name                      VARCHAR(100)   NOT NULL,                        -- 國家名稱 (本地文字)
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL                             -- 刪除時間 (由後端寫入)
    );

    -- (V1)時區表
    CREATE TABLE timezones (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL,                        -- 時區代碼 (UTC、EST、CST)
        iana_name                        VARCHAR(100)   NOT NULL UNIQUE,                 -- IANA 時區名稱 (Etc/UTC、America/New_York、Asia/Taipei)
        name                             VARCHAR(100),                                   -- 名稱 (協調世界時間、美國東部時間、中原標準時間)
        utc_offset                       int            NOT NULL,                        -- 偏移 (存秒數)，為時區參考值，方便後端計算，實際為 IANA 計算
        has_dst                          BOOLEAN        NOT NULL DEFAULT FALSE,          -- 是否有夏令時間
        note                             VARCHAR(255),
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL                             -- 刪除時間 (由後端寫入)
    );

    -- (V1)國別與時區關聯表
    CREATE TABLE country_timezones (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        country_id                       BIGINT         NOT NULL,
        timezone_id                      BIGINT         NOT NULL,

        is_default                       BOOLEAN        DEFAULT TRUE,                    -- 是否為預設時區

        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)

        UNIQUE (country_id, timezone_id),

        FOREIGN KEY (country_id) REFERENCES countries(id) ON DELETE CASCADE ON UPDATE CASCADE,
        FOREIGN KEY (timezone_id) REFERENCES timezones(id) ON DELETE CASCADE ON UPDATE CASCADE
    );

    -- (V1)貨幣表
    CREATE TABLE currencies (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- 貨幣碼 (TWD、USD、JPY...)
        name                             VARCHAR(100)   NOT NULL,                        -- 貨幣名稱 (新台幣、美元、日幣...)
        symbol                           VARCHAR(10)    NULL,                            -- 貨幣符號 (NT$、$、¥、€、£、₩、₽...)
        decimal_places                   DECIMAL(18,8)  NULL,                            -- 該貨幣最小法定單位（用於前端顯示
                                                                                            例如：TWD 0.01
                                                                                                 USD 0.01
                                                                                                 EUR 0.01
                                                                                                 JPY 1
                                                                                                 KRW 1
                                                                                                 VND 1
                                                                                                 KWD 0.001
                                                                                                 BHD 0.001
                                                                                                 BTC 0.00000001）
        is_fiat                          BOOLEAN        NOT NULL,                       -- 是否為法定貨幣
        is_crypto                        BOOLEAN        NOT NULL,                       -- 是否為虛擬貨幣
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL                             -- 刪除時間 (由後端寫入)
    );

    -- (V1)國別與貨幣關聯表
    CREATE TABLE currency_countries (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        country_id                       BIGINT         NOT NULL,
        currency_id                      BIGINT         NOT NULL,
        
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)

        UNIQUE (currency_id, country_id),
        FOREIGN KEY (country_id) REFERENCES countries(id) ON DELETE CASCADE ON UPDATE CASCADE,
        FOREIGN KEY (currency_id) REFERENCES currencies(id) ON DELETE CASCADE ON UPDATE CASCADE
    );

    -- (V1)語言表
    CREATE TABLE languages (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- 代號
        name                             VARCHAR(100)   NOT NULL,                        -- 名稱
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL                             -- 刪除時間 (由後端寫入)
    );

    -- (V1)國別與語言關聯表
    CREATE TABLE country_languages (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        country_id                       BIGINT         NOT NULL,
        language_id                      BIGINT         NOT NULL,

        is_official                      BOOLEAN        DEFAULT FALSE,                   -- 是否為官方語言
        is_default                       BOOLEAN        DEFAULT FALSE,                   -- 是否預設語言

        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)

        UNIQUE (country_id, language_id),
        FOREIGN KEY (country_id) REFERENCES countries(id) ON DELETE CASCADE ON UPDATE CASCADE,
        FOREIGN KEY (language_id) REFERENCES languages(id) ON DELETE CASCADE ON UPDATE CASCADE
    );

    -- 翻譯鍵表
    CREATE TABLE translation_keys (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        entity_type_id                   BIGINT         NOT NULL,                        -- 使用在哪一張表
        entity_id                        BIGINT         NOT NULL,                        -- 對應表中哪一筆資料的id
        field_code                       VARCHAR(50)    NOT NULL,                        -- 代號
        note                             VARCHAR(255),
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)
        UNIQUE (entity_type_id, entity_id, field_code),
        FOREIGN KEY (entity_type_id) REFERENCES entity_types(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- 翻譯表
    CREATE TABLE translations (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        translation_key_id               BIGINT         NOT NULL,
        language_id                      BIGINT         NOT NULL,
        
        value                            TEXT           NOT NULL,
        version                          INT            DEFAULT 1,                       -- 版本
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        is_default                       BOOLEAN        DEFAULT FALSE,                   -- 是否預設

        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)

        UNIQUE (translation_key_id, language_id, version),
        FOREIGN KEY (translation_key_id) REFERENCES translation_keys(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (language_id) REFERENCES languages(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- (V1)金融機構大類表
    CREATE TABLE financial_institution_type_groups (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- 代號 (BANK, BROKER, INSURANCE, EXCHANGE, CRYPTO)
        name                             VARCHAR(100)   NOT NULL,                        -- 名稱
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL                             -- 刪除時間 (由後端寫入)
    );

    -- (V1)金融機構小類表
    CREATE TABLE financial_institution_types (
        id                                  BIGINT         AUTO_INCREMENT PRIMARY KEY,
        financial_institution_type_group_id BIGINT         NOT NULL,
        code                                VARCHAR(50)    NOT NULL UNIQUE,                 -- 代號
        name                                VARCHAR(100)   NOT NULL,                        -- 名稱
        note                                VARCHAR(255),
        is_active                           BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                        DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                        DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                        DATETIME       NULL,                            -- 刪除時間 (由後端寫入)
        FOREIGN KEY (financial_institution_type_group_id) REFERENCES financial_institution_type_groups(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- (V1)金融機構集團表
    CREATE TABLE financial_institution_groups (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        country_id                       BIGINT         NOT NULL,                        -- 金融機構集團總部國
        code                             VARCHAR(50)    NOT NULL,
        name                             VARCHAR(100)   NOT NULL,                        -- 金融機構集團名稱 (例如：國泰金控、富邦金控)
        legal_name                       VARCHAR(150)   NOT NULL,                        -- 金融機構集團正式名稱
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        note                             VARCHAR(255),
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)
        UNIQUE(country_id, code),
        FOREIGN KEY (country_id) REFERENCES countries(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- (V1)金融機構表
    CREATE TABLE financial_institutions (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        country_id                       BIGINT         NOT NULL,                        -- 金融機構總部國
        financial_institution_type_id    BIGINT         NOT NULL,
        financial_institution_group_id   BIGINT         NULL,                            -- 金融機構集團
        code                             VARCHAR(50)    NOT NULL,
        name                             VARCHAR(100)   NOT NULL,                        -- 金融機構名稱 (例如：國泰世華銀行、台本富邦銀行、國泰人壽、IB、Binance)
        legal_name                       VARCHAR(150)   NOT NULL,                        -- 金融機構正式名稱
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        note                             VARCHAR(255),
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)
        UNIQUE(country_id, code),
        FOREIGN KEY (country_id) REFERENCES countries(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (financial_institution_type_id) REFERENCES financial_institution_types(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (financial_institution_group_id) REFERENCES financial_institution_groups(id) ON DELETE SET NULL ON UPDATE CASCADE
    );

    -- (V1)金融機構識別碼類型表
    CREATE TABLE financial_institution_identifier_types (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- 代號 (SWIFT、BANK_CODE、TAX_ID、STOCK_CODE、ROUTING_NUMBER、IBAN_PREFIX、LICENSE_NO)
        name                             VARCHAR(100)   NOT NULL,                        -- 名稱
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL                             -- 刪除時間 (由後端寫入)
    );

    -- (V1)金融機構識別碼表
    CREATE TABLE financial_institution_identifiers (
        id                                       BIGINT         AUTO_INCREMENT PRIMARY KEY,
        country_id                               BIGINT         NULL,
        financial_institution_group_id           BIGINT         NULL,
        financial_institution_id                 BIGINT         NULL,
        financial_institution_identifier_type_id BIGINT         NOT NULL,
        identifier_value                         VARCHAR(100)   NOT NULL,
        is_primary                               BOOLEAN        DEFAULT FALSE,
        created_date                             DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                             DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                             DATETIME       NULL,                            -- 刪除時間 (由後端寫入)
        UNIQUE(financial_institution_identifier_type_id, identifier_value),
        FOREIGN KEY (country_id) REFERENCES countries(id) ON DELETE SET NULL ON UPDATE CASCADE,
        FOREIGN KEY (financial_institution_group_id) REFERENCES financial_institution_groups(id) ON DELETE SET NULL ON UPDATE CASCADE,
        FOREIGN KEY (financial_institution_id) REFERENCES financial_institutions(id) ON DELETE SET NULL ON UPDATE CASCADE,
        FOREIGN KEY (financial_institution_identifier_type_id) REFERENCES financial_institution_identifier_types(id) ON DELETE CASCADE ON UPDATE CASCADE
    );

    -- 金融機構能力表 (業務範圍)
    CREATE TABLE financial_institution_capabilities (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- 代號
        name                             VARCHAR(100)   NOT NULL,                        -- 名稱
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL                             -- 刪除時間 (由後端寫入)
    );

    -- 金融機構在某個國家的能力關聯表 (金融機構在某國家的業務範圍)
    CREATE TABLE financial_institution_country_capabilities (
        id                                  BIGINT         AUTO_INCREMENT PRIMARY KEY,
        country_id                          BIGINT         NOT NULL,
        financial_institution_id            BIGINT         NOT NULL,
        financial_institution_capability_id BIGINT         NOT NULL,
        
        is_active                           BOOLEAN        NOT NULL DEFAULT TRUE,
        effective_from                      DATE           NULL,
        effective_to                        DATE           NULL,

        created_date                        DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                        DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                        DATETIME       NULL,                            -- 刪除時間 (由後端寫入)

        UNIQUE(country_id, financial_institution_id, financial_institution_capability_id, effective_from),
        FOREIGN KEY (country_id) REFERENCES countries(id) ON DELETE CASCADE ON UPDATE CASCADE,
        FOREIGN KEY (financial_institution_id) REFERENCES financial_institutions(id) ON DELETE CASCADE ON UPDATE CASCADE,
        FOREIGN KEY (financial_institution_capability_id) REFERENCES financial_institution_capabilities(id) ON DELETE CASCADE ON UPDATE CASCADE
    );

    -- 金融機構變更類型表
    CREATE TABLE financial_institution_change_types (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- 代號(MERGER、ACQUISITION、RENAME、SPLIT、DISSOLUTION、LICENSE_REVOKED)
        name                             VARCHAR(100)   NOT NULL,                        -- 名稱
        note                             VARCHAR(255),
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL                             -- 刪除時間 (由後端寫入)
    );

    -- 金融機構集團變更表
    CREATE TABLE financial_institution_group_changes (
        id                                    BIGINT         AUTO_INCREMENT PRIMARY KEY,
        financial_institution_change_type_id  BIGINT         NOT NULL,
        source_financial_institution_group_id BIGINT         NOT NULL,
        target_financial_institution_group_id BIGINT         NULL,
        effective_date                        DATE           NULL,
        note                                  VARCHAR(255),
        created_date                          DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                          DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                          DATETIME       NULL,                         -- 刪除時間 (由後端寫入)
        FOREIGN KEY (financial_institution_change_type_id) REFERENCES financial_institution_change_types(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (source_financial_institution_group_id) REFERENCES financial_institution_groups(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (target_financial_institution_group_id) REFERENCES financial_institution_groups(id) ON DELETE SET NULL ON UPDATE CASCADE
    );
    
    -- 金融機構變更表
    CREATE TABLE financial_institution_changes (
        id                                   BIGINT         AUTO_INCREMENT PRIMARY KEY,
        financial_institution_change_type_id BIGINT         NOT NULL,
        source_financial_institution_id      BIGINT         NOT NULL,
        target_financial_institution_id      BIGINT         NULL,
        effective_date                       DATE           NULL,
        note                                 VARCHAR(255),
        created_date                         DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                         DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                         DATETIME       NULL,                            -- 刪除時間 (由後端寫入)
        FOREIGN KEY (financial_institution_change_type_id) REFERENCES financial_institution_change_types(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (source_financial_institution_id) REFERENCES financial_institutions(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (target_financial_institution_id) REFERENCES financial_institutions(id) ON DELETE SET NULL ON UPDATE CASCADE
    );

    -- 匯率來源表
    CREATE TABLE exchange_rate_sources (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        financial_institution_id         BIGINT         NULL,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- 代碼 (ECB、yahoo)
        name                             VARCHAR(100)   NOT NULL,                        -- 名稱
        api_url                          VARCHAR(255)   NULL,
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        last_success_sync_date           DATETIME       NULL,                            -- 最後同步成功時間 (此值每天會一修改，不會新增)
        last_attempt_sync_date           DATETIME       NULL,                            -- 最後企圖同步時間 (此值每天會一修改，不會新增，同步失敗時會與last_success_sync_date值不同)
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)
        FOREIGN KEY (financial_institution_id) REFERENCES financial_institutions(id) ON DELETE SET NULL ON UPDATE CASCADE
    );
    
    -- 匯率表
    CREATE TABLE exchange_rates (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        exchange_rate_source_id          BIGINT         NOT NULL,

        base_currency_id                 BIGINT         NOT NULL,                        -- 基準幣別 (USD)
        quote_currency_id                BIGINT         NOT NULL,                        -- 報價幣別 (TWD)

        rate                             DECIMAL(18,8)  NOT NULL,                        -- 匯率 (例 1 USD = 30 TWD)
        rate_datetime                    DATETIME       NOT NULL,                        -- 匯率時間

        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)

        UNIQUE (exchange_rate_source_id, base_currency_id, quote_currency_id, rate_datetime),
        INDEX (base_currency_id, quote_currency_id),
        CHECK (base_currency_id <> quote_currency_id),
        FOREIGN KEY (exchange_rate_source_id) REFERENCES exchange_rate_sources(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (base_currency_id) REFERENCES currencies(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (quote_currency_id) REFERENCES currencies(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );
    
    -- (測試中)使用者表
    CREATE TABLE users (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        country_id                       BIGINT         NULL,
        timezone_id                      BIGINT         NULL,
        language_id                      BIGINT         NOT NULL,

        user_number                      VARCHAR(10)    NOT NULL UNIQUE,                 -- 使用者編號，10碼隨機碼
        name                             VARCHAR(255)   NOT NULL,                        -- 姓名
        nickname                         VARCHAR(100)   NOT NULL,                        -- 綽號
        email                            VARCHAR(255)   NOT NULL UNIQUE,                 -- 使用者電子郵件
        password                         VARCHAR(255)   NOT NULL,                        -- 使用者密碼 (後端使用hashcode寫入)
        birthday                         DATE           NOT NULL,                        -- 使用者生日
        phone                            VARCHAR(20)    NULL,                            -- 手機號碼

        email_verified                   BOOLEAN        DEFAULT FALSE,                   -- 電子郵件驗證
        sms_verified                     BOOLEAN        DEFAULT FALSE,                   -- 簡訊驗證
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)
        FOREIGN KEY (country_id) REFERENCES countries(id) ON DELETE SET NULL ON UPDATE CASCADE,
        FOREIGN KEY (timezone_id) REFERENCES timezones(id) ON DELETE SET NULL ON UPDATE CASCADE,
        FOREIGN KEY (language_id) REFERENCES languages(id) ON DELETE CASCADE ON UPDATE CASCADE
    );

    -- (測試中)角色表
    CREATE TABLE roles (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- ADMIN, STAFF, USER
        name                             VARCHAR(100)   NOT NULL,                        -- 管理者、員工、使用者
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL                             -- 刪除時間 (由後端寫入)
    );

    -- (測試中)使用者與角色關聯表
    CREATE TABLE user_roles (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        user_id                          BIGINT         NOT NULL,
        role_id                          BIGINT         NOT NULL,
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)
        UNIQUE (user_id, role_id),
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
        FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE ON UPDATE CASCADE
    );

    -- 推薦碼類型表
    CREATE TABLE referral_code_types (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- 代號 (USER、CAMPAIGN、INFLUENCER)
        name                             VARCHAR(100)   NOT NULL,                        -- 名稱
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL                             -- 刪除時間 (由後端寫入)
    );

    -- 推薦碼表
    CREATE TABLE referral_codes (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        user_id                          BIGINT         NULL,                            -- 擁有者
        code                             VARCHAR(50)    NOT NULL UNIQUE,
        referral_code_type_id            BIGINT         NOT NULL,
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL ON UPDATE CASCADE,
        FOREIGN KEY (referral_code_type_id) REFERENCES referral_code_types(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- 使用者推薦狀態表
    CREATE TABLE user_referral_statuses (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- 代號 (PENDING、COMPLETED、INVALID)
        name                             VARCHAR(100)   NOT NULL,                        -- 名稱
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL                             -- 刪除時間 (由後端寫入)
    );

    -- 使用者推薦表
    CREATE TABLE user_referrals (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        referrer_id                      BIGINT         NOT NULL,                        -- 推薦人        
        referee_id                       BIGINT         NOT NULL,                        -- 被推薦人

        referral_code_id                 BIGINT         NOT NULL,                        -- 當時使用的推薦碼
        user_referral_status_id          BIGINT         NOT NULL,

        reward_granted                   BOOLEAN        DEFAULT FALSE,                   -- 推薦獎金是否已發放

        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        completed_date                   DATETIME       NULL,

        UNIQUE (referee_id, referral_code_id),
        FOREIGN KEY (referrer_id) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (referee_id) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (referral_code_id) REFERENCES referral_codes(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (user_referral_status_id) REFERENCES user_referral_statuses(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- 發行平台公司表
    CREATE TABLE distribution_platform_companies (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- 代號 (APPLE、GOOGLE)
        name                             VARCHAR(100)   NOT NULL,                        -- 名稱
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)
    );

    -- 發行平台表
    CREATE TABLE distribution_platforms (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        distribution_platform_company_id BIGINT         NOT NULL,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- 代號 (APP_STORE、GOOGLE_PLAY)
        name                             VARCHAR(100)   NOT NULL,                        -- 名稱
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)
        FOREIGN KEY (distribution_platform_company_id) REFERENCES distribution_platform_companies(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- 訂閱方案表
    CREATE TABLE subscription_plans (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        distribution_platform_id         BIGINT         NOT NULL,
        code                             VARCHAR(50)    NOT NULL,                        -- FREE, PRO, PREMIUM
        name                             VARCHAR(100)   NOT NULL,
        duration_days                    INT            NOT NULL,                        -- 訂閱時效 (30天、365天)
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)
        UNIQUE (distribution_platform_id, code),
        FOREIGN KEY (distribution_platform_id) REFERENCES distribution_platforms(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- 訂閱方案價格表
    CREATE TABLE subscription_plan_prices (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        country_id                       BIGINT         NULL,
        currency_id                      BIGINT         NOT NULL,
        subscription_plan_id             BIGINT         NOT NULL,
        price                            DECIMAL(18,8)  NOT NULL,
        start_date                       DATE           NOT NULL,
        end_date                         DATE           NOT NULL,
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)

        UNIQUE (subscription_plan_id, country_id, currency_id, start_date, end_date),
        FOREIGN KEY (country_id) REFERENCES countries(id) ON DELETE SET NULL ON UPDATE CASCADE,
        FOREIGN KEY (currency_id) REFERENCES currencies(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (subscription_plan_id) REFERENCES subscription_plans(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- 使用者訂閱狀態表
    CREATE TABLE user_subscription_statuses (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- 代號 (ACTIVE、EXPIRED、CANCELLED)
        name                             VARCHAR(100)   NOT NULL,                        -- 名稱 (啟動、過期、取消)
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL                             -- 刪除時間 (由後端寫入)
    );

    -- 使用者與訂閱關聯表
    CREATE TABLE user_subscriptions (
        id                               BIGINT AUTO_INCREMENT PRIMARY KEY,
        user_id                          BIGINT         NOT NULL,
        subscription_plan_id             BIGINT         NOT NULL,
        currency_id                      BIGINT         NOT NULL,
        price                            DECIMAL(18,8)  NOT NULL,
        start_date                       DATE           NOT NULL,
        end_date                         DATE           NOT NULL,

        user_subscription_status_id      BIGINT         NOT NULL,   
        auto_renew                       BOOLEAN        DEFAULT TRUE,                    -- 是否自動續費
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)

        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (subscription_plan_id) REFERENCES subscription_plans(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (currency_id) REFERENCES currencies(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (user_subscription_status_id) REFERENCES user_subscription_statuses(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- 訂單狀態表
    CREATE TABLE order_statuses (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- 代號 (PENDING、SUCCESS、FAILED、REFUNDED)
        name                             VARCHAR(100)   NOT NULL,                        -- 名稱
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL                             -- 刪除時間 (由後端寫入)
    );

    -- 訂單表
    CREATE TABLE orders (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        user_id                          BIGINT         NOT NULL,
        distribution_platform_id         BIGINT         NOT NULL,
        user_subscription_id             BIGINT         NOT NULL,
        currency_id                      BIGINT         NOT NULL,

        original_amount                  DECIMAL(18,8)  NOT NULL,
        total_discount_amount            DECIMAL(18,8)  NOT NULL DEFAULT 0,
        total_tax_amount                 DECIMAL(18,8)  NOT NULL DEFAULT 0,
        total_fee_amount                 DECIMAL(18,8)  NOT NULL DEFAULT 0,
        refund_amount                    DECIMAL(18,8)  NOT NULL DEFAULT 0,
        total_amount                     DECIMAL(18,8)  NOT NULL,
        
        order_status_id                  BIGINT         NOT NULL,
        paid_date                        DATETIME       NULL,

        provider_transaction_number      VARCHAR(100)   NULL,                            -- 本次扣款編號 (訂閱平台提供)
        original_transaction_number      VARCHAR(100)   NULL,                            -- 訂閱起點編號 (訂閱平台提供)

        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)

        UNIQUE (distribution_platform_id, provider_transaction_number),
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (distribution_platform_id) REFERENCES distribution_platforms(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (user_subscription_id) REFERENCES user_subscriptions(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (currency_id) REFERENCES currencies(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (order_status_id) REFERENCES order_statuses(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- 折扣類型表
    CREATE TABLE discount_types (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- 代號 (PERCENTAGE、FIXED)
        name                             VARCHAR(100)   NOT NULL,                        -- 名稱 (百分比、固定)
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL                             -- 刪除時間 (由後端寫入)
    );

    -- 折扣表
    CREATE TABLE discounts (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        discount_type_id                 BIGINT         NOT NULL,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- 代號
        name                             VARCHAR(100)   NOT NULL,                        -- 名稱
        amount                           DECIMAL(18,8)  NOT NULL,                        -- 金額
        total_usage_limit                INT,                                            -- 總使用次數限制
        used_count                       INT            DEFAULT 0,                       -- 已使用次數
        max_discount_amount              DECIMAL(18,8)  DEFAULT 0,                       -- 最大折扣金額
        is_stackable                     BOOLEAN        DEFAULT FALSE,                   -- 是否累計折扣
        is_auto_apply                    BOOLEAN        DEFAULT FALSE,                   -- 是否自動折扣
        priority                         INT            DEFAULT 0,                       -- 優先順序 (1~10)
        note                             VARCHAR(255),
        start_date                       DATE           NOT NULL,                        -- 開始日
        end_date                         DATE           NOT NULL,                        -- 到期日
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)
        FOREIGN KEY (discount_type_id) REFERENCES discount_types(id)
    );

    -- 會員與折扣關聯表
    CREATE TABLE user_discounts ( 
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        user_id                          BIGINT         NOT NULL,
        discount_id                      BIGINT         NOT NULL,
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)
        UNIQUE (user_id, discount_id),
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
        FOREIGN KEY (discount_id) REFERENCES discounts(id) ON DELETE CASCADE ON UPDATE CASCADE
    );

    -- 折扣使用表
    CREATE TABLE discount_usage (
        id                               BIGINT         PRIMARY KEY AUTO_INCREMENT,
        discount_id                      BIGINT         NOT NULL,
        user_id                          BIGINT         NOT NULL,
        order_id                         BIGINT         NOT NULL,
        used_date                        DATETIME       NOT NULL,                        -- 使用時間
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)
        UNIQUE (discount_id, order_id),
        FOREIGN KEY (discount_id) REFERENCES discounts(id) ON DELETE CASCADE ON UPDATE CASCADE,
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
        FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE ON UPDATE CASCADE
    );

    -- 稅種表
    CREATE TABLE tax_types (
        id                               BIGINT         PRIMARY KEY AUTO_INCREMENT,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- VAT、SALES_TAX
        name                             VARCHAR(100)   NOT NULL,                        -- 加值稅、營業稅
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)
    );

    -- 稅費表
    CREATE TABLE taxes (
        id                               BIGINT         PRIMARY KEY AUTO_INCREMENT,
        country_id                       BIGINT         NOT NULL,
        tax_type_id                      BIGINT         NOT NULL,

        code                             VARCHAR(50)    NOT NULL UNIQUE,
        name                             VARCHAR(100)   NOT NULL,    
        rate                             DECIMAL(18,8)  NULL,                            -- 稅率 (與稅費二選一填寫)
        amount                           DECIMAL(18,8)  NULL,                            -- 稅費 (與稅率二選一填寫)
        note                             VARCHAR(255),

        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        min_amount                       DECIMAL(18,8)  NOT NULL DEFAULT 0,              -- 下限
        max_amount                       DECIMAL(18,8)  NULL,                            -- 上限
        effective_from                   DATE           NOT NULL,                        -- 有限時間(起)
        effective_to                     DATE           NULL,                            -- 有限時間(迄)

        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)


        FOREIGN KEY (country_id) REFERENCES countries(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (tax_type_id) REFERENCES tax_types(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- 訂單與稅費關聯表
    CREATE TABLE order_taxes (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        order_id                         BIGINT         NOT NULL,
        tax_id                           BIGINT         NOT NULL,
        tax_amount                       DECIMAL(18,8)  NOT NULL DEFAULT 0,              -- 小計稅金
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)
        UNIQUE (order_id, tax_id),
        FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE ON UPDATE CASCADE,
        FOREIGN KEY (tax_id) REFERENCES taxes(id) ON DELETE CASCADE ON UPDATE CASCADE
    );

    -- 費用類型表
    CREATE TABLE fee_types (
        id                               BIGINT         PRIMARY KEY AUTO_INCREMENT,
        code                             VARCHAR(50)    NOT NULL UNIQUE,
        name                             VARCHAR(100)   NOT NULL,
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)
    );

    -- 費用表
    CREATE TABLE fees (
        id                               BIGINT         PRIMARY KEY AUTO_INCREMENT,
        country_id                       BIGINT         NULL,
        financial_institution_id         BIGINT         NULL,
        distribution_platform_id         BIGINT         NULL,
        fee_type_id                      BIGINT         NOT NULL,
        
        code                             VARCHAR(50)    NOT NULL UNIQUE,
        name                             VARCHAR(100)   NOT NULL,
        rate                             DECIMAL(18,8)  NULL,                            -- 費率 (與費用二選一填寫)
        amount                           DECIMAL(18,8)  NULL,                            -- 費用 (與費率二選一填寫)
        note                             VARCHAR(255),
        
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        min_amount                       DECIMAL(18,8)  NOT NULL DEFAULT 0,              -- 下限
        max_amount                       DECIMAL(18,8)  NULL,                            -- 上限
        effective_from                   DATE           NOT NULL,                        -- 有限時間(起)
        effective_to                     DATE           NULL,                            -- 有限時間(迄)
        
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)

        FOREIGN KEY (country_id) REFERENCES countries(id) ON DELETE SET NULL ON UPDATE CASCADE,
        FOREIGN KEY (financial_institution_id) REFERENCES financial_institutions(id) ON DELETE SET NULL ON UPDATE CASCADE,
        FOREIGN KEY (distribution_platform_id) REFERENCES distribution_platforms(id) ON DELETE SET NULL ON UPDATE CASCADE,
        FOREIGN KEY (fee_type_id) REFERENCES fee_types(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- 訂單與費用關聯表
    CREATE TABLE order_fees (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        order_id                         BIGINT         NOT NULL,
        fee_id                           BIGINT         NOT NULL,
        fee_amount                       DECIMAL(18,8)  NOT NULL DEFAULT 0,              -- 小計費用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)
        UNIQUE (order_id, fee_id),
        FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE ON UPDATE CASCADE,
        FOREIGN KEY (fee_id) REFERENCES fees(id) ON DELETE CASCADE ON UPDATE CASCADE
    );

    -- 退款原因表
    CREATE TABLE refund_transaction_reasons (
        id                               BIGINT         PRIMARY KEY AUTO_INCREMENT,
        code                             VARCHAR(50)    NOT NULL UNIQUE,
        name                             VARCHAR(100)   NOT NULL,
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)
    );

    -- 退款狀態表
    CREATE TABLE refund_transaction_statuses (
        id                               BIGINT         PRIMARY KEY AUTO_INCREMENT,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- PENDING、SUCCESS、FAILED
        name                             VARCHAR(100)   NOT NULL,                        -- 待退款、退款成功、退款失敗
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)
    );

    -- 訂單與退款交易關聯表
    CREATE TABLE order_refund_transactions (
        id                               BIGINT         PRIMARY KEY AUTO_INCREMENT,
        order_id                         BIGINT         NOT NULL,
        refund_transaction_reason_id     BIGINT         NOT NULL,

        refund_amount                    DECIMAL(18,8)  NOT NULL,
        refunded_date                    DATETIME,

        refund_transaction_status_id     BIGINT         NOT NULL,

        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)

        FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (refund_transaction_reason_id) REFERENCES refund_transaction_reasons(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (refund_transaction_status_id) REFERENCES refund_transaction_statuses(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- 投資市場類型表
    CREATE TABLE market_types (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- 代號 (GEOGRAPHIC、ECONOMIC、FINANCIAL、POLITICS)
        name                             VARCHAR(100)   NOT NULL,                        -- 名稱 (地理、經濟、金融、政治)
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL                             -- 刪除時間 (由後端寫入)
    );

    -- 投資市場表
    CREATE TABLE markets (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- 市場代號 (TW、US、JP、CN、EU、EM、EMERGING、DEVELOPED、ASIA_TIGERS)
        name                             VARCHAR(100)   NOT NULL,                        -- 市場名稱 (台灣、美國、日本、中國、歐洲、新興市場、開發中國家、亞洲四小龍)
        market_type_id                   BIGINT         NOT NULL,                        -- 市場種類
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)
        UNIQUE(name),
        FOREIGN KEY (market_type_id) REFERENCES market_types(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- 市場與國別關聯表
    CREATE TABLE market_countries (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        market_id                        BIGINT         NOT NULL,
        country_id                       BIGINT         NOT NULL,
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)
        UNIQUE (market_id, country_id),
        FOREIGN KEY (market_id) REFERENCES markets(id) ON DELETE CASCADE ON UPDATE CASCADE,
        FOREIGN KEY (country_id) REFERENCES countries(id) ON DELETE CASCADE ON UPDATE CASCADE
    );

    -- 債務人表
    CREATE TABLE debtors (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        user_id                          BIGINT         NOT NULL,
        name                             VARCHAR(100)   NOT NULL,
        phone                            VARCHAR(50)    NULL,
        email                            VARCHAR(100)   NULL,
        address                          VARCHAR(255)   NULL,
        notes                            VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                             -- 刪除時間 (由後端寫入)
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- (測試中)流向類型表
    CREATE TABLE flow_types (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- REPAYMENT、PAYOUT、RECOVERY、PAYMENT 
        name                             VARCHAR(100)   NOT NULL,                        -- 你還錢給他(債務面)、他支付給你(投資面)、他把錢還給你(債務面)、你支付給他(投資面)
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL                             -- 刪除時間 (由後端寫入)
    );

    -- (測試中)頻率表
    CREATE TABLE frequencies (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL,                        -- 代號
        name                             VARCHAR(100)   NOT NULL,                        -- 年繳、月繳、週繳、日繳、月配、季配、半年配、年配、不配息
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL                             -- 刪除時間 (由後端寫入)
    );

    -- (測試中)流向頻率表(付款、收款、配息、不配息)
    CREATE TABLE flow_frequencies (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        flow_type_id                     BIGINT         NOT NULL,
        frequency_id                     BIGINT         NOT NULL,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- 代號
        name                             VARCHAR(100)   NOT NULL,                        -- 年繳、月繳、週繳、日繳、月配、季配、半年配、年配、不配息
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)
        UNIQUE(code, flow_type_id, frequency_id),
        FOREIGN KEY (flow_type_id) REFERENCES flow_types(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (frequency_id) REFERENCES frequencies(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- 單位類型表
    CREATE TABLE unit_types (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- inventory、financial、commodity
        name                             VARCHAR(100)   NOT NULL,                        -- 存貨、金融商品、商品期貨
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL                             -- 刪除時間 (由後端寫入)
    );

    -- 單位表
    CREATE TABLE units (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- 單位代碼 (例如 pkg、bottle、piece、contract、share、oz、barrel)
        name                             VARCHAR(100)   NOT NULL,                        -- 名稱 (例如 包、瓶、個、合約、股、盎司、桶)
        unit_type_id                     BIGINT         NOT NULL,
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)
        FOREIGN KEY (unit_type_id) REFERENCES unit_types(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- (測試中)帳本種類表
    CREATE TABLE ledger_types (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- cashflow、investment、debt、receivable、fixed_asset、inventory
        name                             VARCHAR(100)   NOT NULL,                        -- 收支帳、投資帳、負債帳、應收帳、固定資產帳、存貨帳
        icon_id                          BIGINT         NULL,
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)
        FOREIGN KEY (icon_id) REFERENCES icons(id) ON DELETE SET NULL ON UPDATE CASCADE
    );

    -- (測試中)帳本表
    CREATE TABLE ledgers (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        user_id                          BIGINT         NOT NULL,
        ledger_type_id                   BIGINT         NOT NULL,
        base_currency_id                 BIGINT         NOT NULL,                        -- 帳本基準幣
        
        name                             VARCHAR(100)   NOT NULL,                        -- 帳本名稱
        note                             VARCHAR(255),                                   -- 備註
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        is_archived                      BOOLEAN        NOT NULL DEFAULT FALSE,          -- 是否封存    
        archived_date                    DATETIME       NULL,                            -- 封存時間
        
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)

        UNIQUE(user_id, ledger_type_id, name),
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (ledger_type_id) REFERENCES ledger_types(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (base_currency_id) REFERENCES currencies(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- 帳本成員角色表
    CREATE TABLE ledger_member_roles (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                -- OWNER、ADMIN、EDITOR、VIEWER
        name                             VARCHAR(100)   NOT NULL,                       -- 擁有者、管理員、可編輯、只能看、
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,          -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL                            -- 刪除時間 (由後端寫入)
    );

    -- 帳本成員表 (可設定多人共同維護一個帳戶)
    CREATE TABLE ledger_members (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        user_id                          BIGINT         NOT NULL,
        ledger_id                        BIGINT         NOT NULL,
        ledger_member_role_id            BIGINT         NOT NULL,                       -- 權限設定
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,          -- 是否啟用
        joined_date                      DATETIME       NOT NULL,                       -- 加入時間
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                           -- 刪除時間 (由後端寫入)
        UNIQUE (user_id, ledger_id),
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
        FOREIGN KEY (ledger_id) REFERENCES ledgers(id) ON DELETE CASCADE ON UPDATE CASCADE,
        FOREIGN KEY (ledger_member_role_id) REFERENCES ledger_member_roles(id) ON DELETE CASCADE ON UPDATE CASCADE
    );

    -- 投資產品種類表
    CREATE TABLE investment_product_types (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- stock、etf、fund、bond、crypto、forex、gold、futures、option
        name                             VARCHAR(100)   NOT NULL,                        -- 股票、ETF、基金、債券、虛擬貨幣、外匯、黃金、期貨、選擇權
        is_derivative                    BOOLEAN        DEFAULT FALSE,                   -- 是否為衍生性商品
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL                             -- 刪除時間 (由後端寫入)
    );

    -- 交易所表
    CREATE TABLE exchanges (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- NASDAQ、NYSE、TSE、BINANCE
        name                             VARCHAR(100)   NOT NULL,                        -- 美國納斯達克交易所、紐約證券交易所
        country_id                       BIGINT         NULL,                            -- 所屬國家
        market_id                        BIGINT         NULL,                            -- 所屬市場
        timezone_id                      BIGINT         NULL,                            -- 時區
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)
        
        FOREIGN KEY (country_id) REFERENCES countries(id) ON DELETE SET NULL ON UPDATE CASCADE,
        FOREIGN KEY (market_id) REFERENCES markets(id) ON DELETE SET NULL ON UPDATE CASCADE,
        FOREIGN KEY (timezone_id) REFERENCES timezones(id) ON DELETE SET NULL ON UPDATE CASCADE
    );

    -- 投資產品表
    CREATE TABLE investment_products (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        product_type_id                  BIGINT         NOT NULL,
        market_id                        BIGINT         NULL,                            -- 所屬市場 (美股、台股、幣圈)
        financial_institution_id         BIGINT         NULL,                            -- 發行金融機構
        listing_currency_id              BIGINT         NOT NULL,                        -- 標的幣別 (USD、TWD)
        trading_currency_id              BIGINT         NOT NULL,                        -- 計價幣種 (虛擬貨幣、外匯與差價合約會與標的幣別不同)
        exchange_id                      BIGINT         NULL,                            -- 交易所

        code                             VARCHAR(50)    NOT NULL,                        -- 商品代碼 (AAPL、TSLA、BTC、0050)
        name                             VARCHAR(100)   NOT NULL,                        -- 商品名稱 (Apple Inc.)
        isin                             VARCHAR(20),                                    -- 國際證券識別碼 ISIN（可為 NULL）
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用

        unit_id                          BIGINT         NULL,                            -- 商品單位 (口、股、張)
        note                             VARCHAR(255),

        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)

        UNIQUE(market_id, exchange_id, code),
        FOREIGN KEY (product_type_id) REFERENCES investment_product_types(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (market_id) REFERENCES markets(id) ON DELETE SET NULL ON UPDATE CASCADE,
        FOREIGN KEY (financial_institution_id) REFERENCES financial_institutions(id) ON DELETE SET NULL ON UPDATE CASCADE,
        FOREIGN KEY (listing_currency_id) REFERENCES currencies(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (trading_currency_id) REFERENCES currencies(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (exchange_id) REFERENCES exchanges(id) ON DELETE SET NULL ON UPDATE CASCADE,
        FOREIGN KEY (unit_id) REFERENCES units(id) ON DELETE SET NULL ON UPDATE CASCADE
    );

    -- 基金類型表
    CREATE TABLE fund_types (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- equity、bond、balanced、money_market、index、etc
        name                             VARCHAR(100)   NOT NULL,                        -- 股票型、債券型、平衡型、貨幣市場型、指數型、其他
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL                             -- 刪除時間 (由後端寫入)
    );

    -- 投資產品表子表 (基金)
    CREATE TABLE fund_products (
        investment_product_id            BIGINT        PRIMARY KEY,
        
        expense_ratio                    DECIMAL(18,8) NOT NULL,                        -- 總費用率
        dividend_frequency_id            BIGINT        NULL,                            -- 配息頻率
        fund_type_id                     BIGINT        NULL,                            -- 基金類型
        
        FOREIGN KEY (investment_product_id) REFERENCES investment_products(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (dividend_frequency_id) REFERENCES flow_frequencies(id) ON DELETE SET NULL ON UPDATE CASCADE,
        FOREIGN KEY (fund_type_id) REFERENCES fund_types(id) ON DELETE SET NULL ON UPDATE CASCADE
    );

    -- 債券發行單位表
    CREATE TABLE bond_issuers (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- 代碼
        name                             VARCHAR(100)   NOT NULL,                        -- 發行機構名稱
        country_id                       BIGINT         NOT NULL,                        -- 所屬國家
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)
        FOREIGN KEY (country_id) REFERENCES countries(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- 債券種類表
    CREATE TABLE bond_types (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- government、corporate、municipal
        name                             VARCHAR(100)   NOT NULL,                        -- 政府債、公司債、市政債
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL                             -- 刪除時間 (由後端寫入)
    );
    
    -- 投資產品表子表 (債券)
    CREATE TABLE bond_products (
        investment_product_id            BIGINT        PRIMARY KEY,

        bond_issuer_id                   BIGINT        NULL,                            -- 發行機構
        coupon_rate                      DECIMAL(18,8) NOT NULL,                        -- 票面利率
        coupon_frequency_id              BIGINT        NULL,                            -- 票面配息頻率
        maturity_date                    DATE          NOT NULL,                        -- 到期日
        face_value                       DECIMAL(18,8) NOT NULL,                        -- 面額
        bond_type_id                     BIGINT        NULL,                            -- 債券種類

        FOREIGN KEY (investment_product_id) REFERENCES investment_products(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (bond_issuer_id) REFERENCES bond_issuers(id) ON DELETE SET NULL ON UPDATE CASCADE,
        FOREIGN KEY (coupon_frequency_id) REFERENCES flow_frequencies(id) ON DELETE SET NULL ON UPDATE CASCADE,
        FOREIGN KEY (bond_type_id) REFERENCES bond_types(id) ON DELETE SET NULL ON UPDATE CASCADE
    );

    -- 投資產品表子表 (期貨)
    CREATE TABLE derivative_products (
        investment_product_id            BIGINT        PRIMARY KEY,
        contract_size                    DECIMAL(18,8) NOT NULL,                        -- 每張合約的標的數量
        expiration_date                  DATE          NOT NULL,                        -- 到期日
        FOREIGN KEY (investment_product_id) REFERENCES investment_products(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- 選擇權類型表
    CREATE TABLE option_types (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- CALL、PUT
        name                             VARCHAR(100)   NOT NULL,                        -- 買權 Call Option、賣權 Put Option
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL                             -- 刪除時間 (由後端寫入)
    );

    -- 投資產品表子表 (選擇權)
    CREATE TABLE option_products (
        investment_product_id            BIGINT        PRIMARY KEY,
        strike_price                     DECIMAL(18,8) NOT NULL,                        -- 履約價
        option_type_id                   BIGINT        NOT NULL,
        FOREIGN KEY (investment_product_id) REFERENCES derivative_products(investment_product_id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (option_type_id) REFERENCES option_types(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- 投資產品歷史價格表
    CREATE TABLE investment_price_histories (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        investment_product_id            BIGINT         NOT NULL,
        price                            DECIMAL(18,8)  NOT NULL,
        price_date                       DATE           NOT NULL,
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        INDEX (investment_product_id, price_date),
        INDEX (price_date, investment_product_id),
        UNIQUE (investment_product_id, price_date),
        FOREIGN KEY (investment_product_id) REFERENCES investment_products(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- 風險評分系統
    CREATE TABLE risk_rating_systems (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        country_id                       BIGINT         NULL,                            -- 國家
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- S&P、晨星、Moody’s
        name                             VARCHAR(100)   NOT NULL,
        scale_min                        INT NOT NULL,                                  -- 1
        scale_max                        INT NOT NULL,                                  -- 5 / 10
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)
        FOREIGN KEY (country_id) REFERENCES countries(id) ON DELETE SET NULL ON UPDATE CASCADE
    );

    -- 風險等級規模表
    CREATE TABLE risk_rating_scales (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        risk_rating_system_id            BIGINT         NOT NULL,

        code                             VARCHAR(50)    NOT NULL,                         -- AAA、AA+、BBB、Aaa、⭐5
        name                             VARCHAR(100)   NOT NULL,                         -- 顯示名稱

        score                            DECIMAL(5,2)   NULL,                            -- 可選：轉換成數值 (方便排序)
        rank_order                       INT            NOT NULL,                        -- 排序用 (1 = 最安全)
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)
        
        UNIQUE(risk_rating_system_id, code),
        FOREIGN KEY (risk_rating_system_id) REFERENCES risk_rating_systems(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- 投資商品風險表
    CREATE TABLE investment_product_risk_ratings (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        investment_product_id            BIGINT         NOT NULL,
        risk_rating_system_id            BIGINT         NOT NULL,
        risk_rating_scale_id             BIGINT         NOT NULL,

        effective_from                   DATE           NOT NULL,
        effective_to                     DATE           NULL,
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)

        UNIQUE(investment_product_id, risk_rating_system_id, effective_from),
        FOREIGN KEY (investment_product_id) REFERENCES investment_products(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (risk_rating_system_id) REFERENCES risk_rating_systems(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (risk_rating_scale_id) REFERENCES risk_rating_scales(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- (測試中)帳戶種類表
    CREATE TABLE account_types (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        ledger_type_id                   BIGINT         NOT NULL,
        
        code                             VARCHAR(50)    NOT NULL,
        name                             VARCHAR(100)   NOT NULL,                        -- 帳戶性質（如：
                                                                                         -- 收支帳下的現金 cash、銀行 bank、信用卡 credit_card ，
                                                                                         -- 投資帳下的股票 stock、基金 fund、債券 bond、外幣 forex、虛擬貨幣 crypto、
                                                                                         --          貴金屬 gold/silver、定存 fixed_deposits、保險 insurance、
                                                                                         --          期貨 futures、選擇權 option，
                                                                                         -- 負債帳下的房貸 mortgage、信貸 personal_loan、車貸 auto_loan，
                                                                                         -- 應收帳下的借出款 lent，
                                                                                         -- 固定資產帳下的房貸產 property、汽車 automobile、機車 motorcycle、家電、3C
                                                                                         -- 存貨帳下的生活用品或是備品
        icon_id                          BIGINT         NULL,
        direction                        TINYINT        NOT NULL,                        -- 正向 / 負向
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)

        CHECK (direction IN (1, -1)),
        UNIQUE (ledger_type_id, code),
        FOREIGN KEY (ledger_type_id) REFERENCES ledger_types(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (icon_id) REFERENCES icons(id) ON DELETE SET NULL ON UPDATE CASCADE
    );

    -- (測試中)帳戶表
    CREATE TABLE accounts (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        ledger_id                        BIGINT         NOT NULL,
        account_type_id                  BIGINT         NOT NULL,
        currency_id                      BIGINT         NOT NULL,

        name                             VARCHAR(100)   NOT NULL,
        legal_name                       VARCHAR(150)   NULL,
        initial_balance                  DECIMAL(18,8)  NOT NULL DEFAULT 0,              -- 初始餘額 (不會變更)
        current_balance                  DECIMAL(18,8)  NOT NULL DEFAULT 0,              -- 現在餘額 (會一直 update)
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        note                             VARCHAR(255),                                   -- 備註
        
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)

        UNIQUE(ledger_id, name),
        INDEX(ledger_id),
        INDEX(account_type_id),
        INDEX(currency_id),
        FOREIGN KEY (ledger_id) REFERENCES ledgers(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (account_type_id) REFERENCES account_types(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (currency_id) REFERENCES currencies(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- 利率類型表
    CREATE TABLE interest_rate_types (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- FIXED、FLOATING
        name                             VARCHAR(100)   NOT NULL,                        -- 固定、流動
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL                             -- 刪除時間 (由後端寫入)
    );

    -- 利率排程表
    CREATE TABLE interest_rate_schedules (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        account_id                       BIGINT         NOT NULL,
        interest_rate_type_id            BIGINT         NOT NULL,
        base_rate                        DECIMAL(18,8)  NOT NULL COMMENT '基準利率(%)',
        spread_rate                      DECIMAL(18,8)  NOT NULL COMMENT '加碼利率(%)',
        effective_apr                    DECIMAL(18,8)  NOT NULL COMMENT '總費用年百分率(%)',
        start_date                       DATE           NOT NULL,                        -- 開始日
        end_date                         DATE           NULL,                            -- 到期日
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)
        INDEX(account_id, start_date, end_date),
        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (interest_rate_type_id) REFERENCES interest_rate_types(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- 帳戶與稅費關聯表
    CREATE TABLE account_taxes (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        account_id                       BIGINT         NOT NULL,
        tax_id                           BIGINT         NOT NULL,
        tax_amount                       DECIMAL(18,8)  NOT NULL DEFAULT 0,              -- 小計稅金
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)
        UNIQUE (account_id, tax_id),
        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (tax_id) REFERENCES taxes(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- 帳戶與費用關聯表
    CREATE TABLE account_fees (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        account_id                       BIGINT         NOT NULL,
        fee_id                           BIGINT         NOT NULL,
        fee_amount                       DECIMAL(18,8)  NOT NULL DEFAULT 0,              -- 小計費用
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)
        UNIQUE (account_id, fee_id),
        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (fee_id) REFERENCES fees(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- (測試中)銀行帳戶類型表
    CREATE TABLE bank_account_types (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,
        name                             VARCHAR(100)   NOT NULL,
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL                             -- 刪除時間 (由後端寫入)
    );

    -- (測試中)帳戶表子表 (收支帳 → 銀行帳戶)
    CREATE TABLE bank_accounts (
        account_id                       BIGINT        PRIMARY KEY,
        financial_institution_id         BIGINT        NULL,
        bank_account_type_id             BIGINT        NULL,
        account_number                   VARCHAR(50)   NULL,
        is_digital                       BOOLEAN       NOT NULL DEFAULT FALSE,
        has_passbook                     BOOLEAN       NOT NULL DEFAULT FALSE,
        statement_day                    TINYINT       NULL,
        settlement_days                  INT           NULL,
        payout_frequency_id              BIGINT        NULL,                            -- 配息頻率
        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE ON UPDATE CASCADE,
        FOREIGN KEY (financial_institution_id) REFERENCES financial_institutions(id) ON DELETE SET NULL ON UPDATE CASCADE,
        FOREIGN KEY (bank_account_type_id) REFERENCES bank_account_types(id) ON DELETE SET NULL ON UPDATE CASCADE,
        FOREIGN KEY (payout_frequency_id) REFERENCES flow_frequencies(id) ON DELETE SET NULL ON UPDATE CASCADE
    );

    -- (測試中)支付網路類型表
    CREATE TABLE payment_network_types (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,
        name                             VARCHAR(100)   NOT NULL,
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL
    );

    -- (測試中)支付網路表
    CREATE TABLE payment_networks (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        country_id                       BIGINT         NULL,                            -- 母公司所在國（Amex=US, JCB=JP）
        payment_network_type_id          BIGINT         NOT NULL,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- VISA, MASTERCARD, JCB, AMEX, UNIONPAY
        name                             VARCHAR(100)   NOT NULL,                        -- Visa, Mastercard...
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        note                             VARCHAR(255),
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)
        FOREIGN KEY (country_id) REFERENCES countries(id) ON DELETE SET NULL ON UPDATE CASCADE,
        FOREIGN KEY (payment_network_type_id) REFERENCES payment_network_types(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- (測試中)卡片等級表
    CREATE TABLE card_tiers (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,
        name                             VARCHAR(100)   NOT NULL,
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        note                             VARCHAR(255),
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL
    );

    -- (測試中)支付網路可支援的卡片等級表
    CREATE TABLE payment_network_card_tiers (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        payment_network_id               BIGINT         NOT NULL,
        card_tier_id                     BIGINT         NOT NULL,
        display_name                     VARCHAR(100)   NOT NULL, -- 實際顯示名稱
        level_rank                       INT            NOT NULL, -- 在該 network 內的排序
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        note                             VARCHAR(255),
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,
        UNIQUE(payment_network_id, card_tier_id),
        FOREIGN KEY (payment_network_id) REFERENCES payment_networks(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (card_tier_id) REFERENCES card_tiers(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- (測試中)卡片能力表
    CREATE TABLE card_capabilities (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,
        name                             VARCHAR(100)   NOT NULL,
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL
    );

    -- 帳戶表子表 (收支帳 → 銀行卡) 
    CREATE TABLE bank_cards (
        account_id                       BIGINT         PRIMARY KEY,
        payment_network_card_tier_id     BIGINT         NULL,
        name                             VARCHAR(100)   NOT NULL,
        card_number                      VARCHAR(20)    NULL,
        card_holder_name                 VARCHAR(100)   NULL,
        expiry_date                      DATE           NULL,
        is_virtual                       BOOLEAN        NOT NULL DEFAULT FALSE,          -- 是否為虛擬卡
        note                             VARCHAR(255),
        FOREIGN KEY (account_id) REFERENCES bank_accounts(account_id) ON DELETE CASCADE ON UPDATE CASCADE,
        FOREIGN KEY (payment_network_card_tier_id) REFERENCES payment_network_card_tiers(id) ON DELETE SET NULL ON UPDATE CASCADE
    );

    -- 銀行卡能力關聯表
    CREATE TABLE bank_card_capabilities (
        account_id                       BIGINT         NOT NULL,
        card_capability_id               BIGINT         NOT NULL,
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)
        PRIMARY KEY (account_id, card_capability_id),
        FOREIGN KEY (account_id) REFERENCES bank_cards(account_id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (card_capability_id) REFERENCES card_capabilities(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- (測試中)帳戶表子表 (收支帳 → 信用卡)
    CREATE TABLE credit_card_accounts (
        account_id                       BIGINT         PRIMARY KEY,
        financial_institution_id         BIGINT         NULL,
        payment_network_card_tier_id     BIGINT         NULL,
        card_number                      VARCHAR(20)    NULL,
        card_holder_name                 VARCHAR(100)   NULL,
        credit_limit                     DECIMAL(18,8)  NULL,                            -- 信用額度
        expiry_date                      DATE           NULL,
        is_virtual                       BOOLEAN        NOT NULL DEFAULT FALSE,          -- 是否為虛擬卡
        cycle_day                        TINYINT        NOT NULL,		                 -- 結帳日 (1~31)
        due_day                          TINYINT        NOT NULL,                        -- 繳款日 (1~31)
        annual_fee                       DECIMAL(18,8)  NULL,                            -- 年費
        is_auto_pay                      BOOLEAN        DEFAULT FALSE,                   -- 是否自動扣款
        auto_pay_account_id              BIGINT         NULL,                            -- 自動扣款帳戶

        UNIQUE(account_id, auto_pay_account_id),
        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE ON UPDATE CASCADE,
        FOREIGN KEY (financial_institution_id) REFERENCES financial_institutions(id) ON DELETE SET NULL ON UPDATE CASCADE,
        FOREIGN KEY (payment_network_card_tier_id) REFERENCES payment_network_card_tiers(id) ON DELETE SET NULL ON UPDATE CASCADE,
        FOREIGN KEY (auto_pay_account_id) REFERENCES accounts(id) ON DELETE SET NULL ON UPDATE CASCADE
    );

    -- 信用卡能力關聯表
    CREATE TABLE credit_card_capabilities (
        account_id                       BIGINT         NOT NULL,
        card_capability_id               BIGINT         NOT NULL,
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)
        PRIMARY KEY (account_id, card_capability_id),
        FOREIGN KEY (account_id) REFERENCES credit_card_accounts(account_id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (card_capability_id) REFERENCES card_capabilities(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- (測試中)卡片發行類型表
    CREATE TABLE card_issuance_types (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,
        name                             VARCHAR(100)   NOT NULL,
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL
    );

    -- 卡片發行表
    CREATE TABLE card_issuances (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        account_id                       BIGINT         NOT NULL,
        previous_card_issuance_id        BIGINT         NULL,
        card_issuance_type_id            BIGINT         NOT NULL,
        card_number                      VARCHAR(20)    NULL,
    
        issued_date                      DATE           NOT NULL,                    -- 發行時間
        activated_date                   DATE           NULL,                        -- 啟用時間
        expiry_date                      DATE           NULL,                        -- 有效時間
        terminated_date                  DATE           NULL,                        -- 終止時間

        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,
        
        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (previous_card_issuance_id) REFERENCES card_issuances(id) ON DELETE SET NULL ON UPDATE CASCADE,
        FOREIGN KEY (card_issuance_type_id) REFERENCES card_issuance_types(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );
    
    -- 帳戶表子表 (投資帳 → 股票、ETF、基金、外幣、虛擬貨幣、貴金屬、期貨 futures、選擇權 option，）
    CREATE TABLE investment_accounts (
        account_id                       BIGINT         PRIMARY KEY,
        market_id                        BIGINT         NULL,
        financial_institution_id         BIGINT         NULL,
        realized_pnl                     DECIMAL(18,8)  NOT NULL DEFAULT 0,               -- 已實現損益
        realized_pnl_rate                DECIMAL(18,8)  NOT NULL DEFAULT 0,               -- 已實現損益率
        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE ON UPDATE CASCADE,
        FOREIGN KEY (market_id) REFERENCES markets(id) ON DELETE SET NULL ON UPDATE CASCADE,
        FOREIGN KEY (financial_institution_id) REFERENCES financial_institutions(id) ON DELETE SET NULL ON UPDATE CASCADE
    );

    -- 投資持倉表
    CREATE TABLE investment_positions (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        account_id                       BIGINT         NOT NULL,
        investment_product_id            BIGINT         NOT NULL,

        quantity                         DECIMAL(18,8)  NOT NULL DEFAULT 0,              -- 持有數量
        avg_unit_cost                    DECIMAL(18,8)  NOT NULL DEFAULT 0,              -- 平均成本
        total_tax_amount                 DECIMAL(18,8)  NOT NULL DEFAULT 0,              -- 總稅費
        total_fee_amount                 DECIMAL(18,8)  NOT NULL DEFAULT 0,              -- 總費用
        total_cost                       DECIMAL(18,8)  NOT NULL DEFAULT 0,              -- 總成本

        last_price                       DECIMAL(18,8)  NOT NULL DEFAULT 0,              -- 最後報價
        last_updated_price_date          DATETIME       NOT NULL,                        -- 報價更新時間
        market_value                     DECIMAL(18,8)  NOT NULL DEFAULT 0,              -- 市價
        unrealized_pnl                   DECIMAL(18,8)  NOT NULL DEFAULT 0,              -- 未實現損益
        unrealized_pnl_rate              DECIMAL(18,8)  NOT NULL DEFAULT 0,              -- 未實現損益率

        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

        UNIQUE(account_id, investment_product_id),

        FOREIGN KEY (account_id) REFERENCES investment_accounts(account_id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (investment_product_id) REFERENCES investment_products(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- 投資持倉手數表
    CREATE TABLE investment_position_lots (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        investment_position_id           BIGINT         NOT NULL,
        purchase_transaction_id          BIGINT         NOT NULL,
        purchase_date                    DATETIME       NOT NULL,
        original_quantity                DECIMAL(18,8)  NOT NULL,
        remaining_quantity               DECIMAL(18,8)  NOT NULL,
        purchase_unit_price              DECIMAL(18,8)  NOT NULL,
        fee_amount                       DECIMAL(18,8)  NOT NULL DEFAULT 0,
        tax_amount                       DECIMAL(18,8)  NOT NULL DEFAULT 0,
        subtotal_cost                    DECIMAL(18,8)  NOT NULL,
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        INDEX(remaining_quantity),
        FOREIGN KEY (investment_position_id) REFERENCES investment_positions(id) ON DELETE CASCADE ON UPDATE CASCADE,
        FOREIGN KEY (purchase_transaction_id) REFERENCES transactions(id) ON DELETE CASCADE ON UPDATE CASCADE
    );

    -- 帳戶表子表 (投資帳 → 定存、保險、基金、債券）
    CREATE TABLE term_investment_accounts (
        account_id                       BIGINT        PRIMARY KEY,
        financial_institution_id         BIGINT        NULL,
        investment_product_id            BIGINT        NULL,
        total_tax_amount                 DECIMAL(18,8) NOT NULL DEFAULT 0,              -- 總稅費
        total_fee_amount                 DECIMAL(18,8) NOT NULL DEFAULT 0,              -- 總費用
        start_date                       DATE          NOT NULL,                        -- 開始日
        end_date                         DATE          NOT NULL,                        -- 到期日
        early_termination_allowed        BOOLEAN       NOT NULL DEFAULT FALSE,          -- 是否可提前贖回
        payout_frequency_id              BIGINT        NULL,                            -- 配息類型
        penalty_rate                     DECIMAL(18,8) NULL,                            -- 違約利率
        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE ON UPDATE CASCADE,
        FOREIGN KEY (financial_institution_id) REFERENCES financial_institutions(id) ON DELETE SET NULL ON UPDATE CASCADE,
        FOREIGN KEY (investment_product_id) REFERENCES investment_products(id) ON DELETE SET NULL ON UPDATE CASCADE,
        FOREIGN KEY (payout_frequency_id) REFERENCES flow_frequencies(id) ON DELETE SET NULL ON UPDATE CASCADE
    );

    -- 帳戶表子表 (負債帳 → 房貸、車貸、信貸）
    CREATE TABLE debt_accounts (
        account_id                       BIGINT        PRIMARY KEY,
        financial_institution_id         BIGINT        NULL,
        repayment_frequency_id           BIGINT        NULL,

        total_tax_amount                 DECIMAL(18,8) NOT NULL DEFAULT 0,              -- 總稅費
        total_fee_amount                 DECIMAL(18,8) NOT NULL DEFAULT 0,              -- 總費用
        cycle_day                        TINYINT       NULL,                            -- 結帳日 (1~31)
        due_day                          TINYINT       NULL,                            -- 繳款日 (1~31)
        start_date                       DATE          NOT NULL,                        -- 開始日
        end_date                         DATE          NULL,                            -- 到期日

        CHECK (cycle_day BETWEEN 1 AND 31),
        CHECK (due_day BETWEEN 1 AND 31),
        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE ON UPDATE CASCADE,
        FOREIGN KEY (financial_institution_id) REFERENCES financial_institutions(id) ON DELETE SET NULL ON UPDATE CASCADE,
        FOREIGN KEY (repayment_frequency_id) REFERENCES flow_frequencies(id) ON DELETE SET NULL ON UPDATE CASCADE
    );

    -- 帳戶表子表 (應收帳款)
    CREATE TABLE receivable_accounts (
        account_id                       BIGINT        PRIMARY KEY,
        recovery_frequency_id            BIGINT        NULL,

        total_tax_amount                 DECIMAL(18,8) NOT NULL DEFAULT 0,              -- 總稅費
        total_fee_amount                 DECIMAL(18,8) NOT NULL DEFAULT 0,              -- 總費用
        cycle_day                        TINYINT       NULL,                            -- 結帳日 (1~31)
        due_day                          TINYINT       NULL,                            -- 繳款日 (1~31)
        start_date                       DATE          NOT NULL,                        -- 開始日
        end_date                         DATE          NULL,                            -- 到期日

        CHECK (cycle_day BETWEEN 1 AND 31),
        CHECK (due_day BETWEEN 1 AND 31),
        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE ON UPDATE CASCADE,
        FOREIGN KEY (recovery_frequency_id) REFERENCES flow_frequencies(id) ON DELETE SET NULL ON UPDATE CASCADE
    );

    -- 固定資產分類表
    CREATE TABLE asset_categories (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- 代號 (HOUSE、CAR、APPLIANCE、ELECTRONICS)
        name                             VARCHAR(100)   NOT NULL,                        -- 名稱 (房屋、車輛、家電、電子產品)
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL                             -- 刪除時間 (由後端寫入)
    );

    -- 折舊方法表
    CREATE TABLE depreciation_methods (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- 代號 (STRAIGHT_LINE、DECLINING_BALANCE、DOUBLE_DECLINING、SUM_OF_YEARS)
        name                             VARCHAR(100)   NOT NULL,                        -- 名稱 (直線法、餘額遞減法/定率法、雙倍餘額遞減法、年數總和法)
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL                             -- 刪除時間 (由後端寫入)
    );

    -- 帳戶表子表 (固定資產帳 → 房屋、車輛、家電、3C)
    CREATE TABLE fixed_asset_accounts (
        account_id                       BIGINT        PRIMARY KEY,
        asset_category_id                BIGINT        NULL,

        asset_tag                        VARCHAR(50)   UNIQUE,                          -- 識別碼
        purchase_date                    DATE          NOT NULL,                        -- 購買日
        purchase_price                   DECIMAL(18,8) NOT NULL,                        -- 購買價格
        useful_life_years                INT,                                           -- 使用年限
        depreciation_method_id           BIGINT        NULL,
        salvage_value                    DECIMAL(18,8) NULL,                            -- 殘值
        location                         VARCHAR(100),                                  -- 位置
        
        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE ON UPDATE CASCADE,
        FOREIGN KEY (asset_category_id) REFERENCES asset_categories(id) ON DELETE SET NULL ON UPDATE CASCADE,
        FOREIGN KEY (depreciation_method_id) REFERENCES depreciation_methods(id) ON DELETE SET NULL ON UPDATE CASCADE
    );

    -- 存貨分類表
    CREATE TABLE inventory_categories (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- 代號 (FOOD、BEVERAGE、CONSUMABLE、SPARE_PART、OFFICE_SUPPLIES)
        name                             VARCHAR(100)   NOT NULL,                        -- 名稱 (食品、飲品、消耗品、備件、辦公用品)
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL                             -- 刪除時間 (由後端寫入)
    );

    -- 存貨成本方法表
    CREATE TABLE cost_methods (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- 代號 (FIFO、LIFO、AVERAGE)
        name                             VARCHAR(100)   NOT NULL,                        -- 名稱 (先進先出法、後進先出法、加權平均法)
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL                             -- 刪除時間 (由後端寫入)
    );

    -- 帳戶表子表 (存貨帳 → 生活用品 / 備品)
    CREATE TABLE inventory_accounts (
        account_id                       BIGINT        PRIMARY KEY,
        inventory_category_id            BIGINT        NULL,
        unit_id                          BIGINT        NOT NULL,                        -- 單位
        quantity                         DECIMAL(18,8) NOT NULL DEFAULT 0,              -- 目前數量
        warning_level                    DECIMAL(18,8) NULL,                            -- 低於提醒
        cost_method_id                   BIGINT        NULL,
        expiry_date                      DATE,                                          -- 有效期限
        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE ON UPDATE CASCADE,
        FOREIGN KEY (inventory_category_id) REFERENCES inventory_categories(id) ON DELETE SET NULL ON UPDATE CASCADE,
        FOREIGN KEY (cost_method_id) REFERENCES cost_methods(id) ON DELETE SET NULL ON UPDATE CASCADE,
        FOREIGN KEY (unit_id) REFERENCES units(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- 合約種類表
    CREATE TABLE contract_types (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                -- 代號
        name                             VARCHAR(100)   NOT NULL,                       -- 名稱
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL                            -- 刪除時間 (由後端寫入)
    );
    
    -- 合約表
    CREATE TABLE contracts (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        account_id                       BIGINT         NOT NULL,
        contract_type_id                 BIGINT         NOT NULL,
        parent_contract_id               BIGINT         NULL,                           -- (用於母子合約關聯)

        contract_number                  VARCHAR(255)   NULL UNIQUE,
        total_amount                     DECIMAL(18,8)  NULL,                           -- 總金額
        total_terms                      INT            NULL,                           -- 總期數
        start_date                       DATE           NOT NULL,                       -- 開始日
        end_date                         DATE           NULL,                           -- 到期日
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,          -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                           -- 刪除時間 (由後端寫入)

        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (contract_type_id) REFERENCES contract_types(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (parent_contract_id) REFERENCES contracts(id) ON DELETE SET NULL ON UPDATE CASCADE
    );

    -- 合約子表 (保險)
    CREATE TABLE insurance_contracts (
        contract_id                      BIGINT        PRIMARY KEY,
        premium                          DECIMAL(18,8),                                 -- 保費
        coverage_amount                  DECIMAL(18,8),                                 -- 保額
        beneficiary                      VARCHAR(100),                                  -- 受益人
        FOREIGN KEY (contract_id) REFERENCES contracts(id) ON DELETE CASCADE ON UPDATE CASCADE
    );

    -- 還款種類表
    CREATE TABLE repayment_types (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                -- 代號 (EQUAL_PRINCIPAL_INTEREST、EQUAL_PRINCIPAL、INTEREST_ONLY)
        name                             VARCHAR(100)   NOT NULL,                       -- 名稱 (等額本息或本息平均、等額本金或本金平均、只還利息)
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL                            -- 刪除時間 (由後端寫入)
    );

    -- 合約子表 (負債)
    CREATE TABLE debt_contracts (
        contract_id                      BIGINT         PRIMARY KEY,
        repayment_type_id                BIGINT         NOT NULL,
        apr                              DECIMAL(18,8)  NULL,                           -- 總費用率
        overdue_apr                      DECIMAL(18,8)  NULL,                           -- 逾期利率 (逾期後適用的利率)
        fee_amount                       DECIMAL(18,8)  NULL,                           -- 開辦手續費
        
        cycle_day                        TINYINT        NULL,                           -- 結帳日 (1~31)
        due_day                          TINYINT        NULL,                           -- 繳款日 (1~31)
        grace_period_days                TINYINT        NULL,                           -- 寬限天數 (超過幾天才算遲繳)

        penalty_type                     VARCHAR(20)    NULL,                           -- 'FIXED'(固定), 'TIERED'(階梯), 'PERCENT'(百分比)
        penalty_fee                      DECIMAL(18,8)  NULL,                           -- 基礎違約金金額
        
        lockin_period                    TINYINT        NULL,                           -- 閉鎖期 (月數)
        prepayment_penalty_type          VARCHAR(20)    NULL,                           -- 'PERCENT'(餘額百分比), 'FIXED'(固定)
        prepayment_penalty               DECIMAL(18,8)  NULL,                           -- 提前清償違約金 (數值)
        FOREIGN KEY (contract_id) REFERENCES contracts(id) ON DELETE CASCADE ON UPDATE CASCADE,
        FOREIGN KEY (repayment_type_id) REFERENCES repayment_types(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- 合約子表 (應收帳款)
    CREATE TABLE receivable_contracts (
        contract_id                      BIGINT        PRIMARY KEY,
        debtor_id                        BIGINT        NOT NULL,
        FOREIGN KEY (contract_id) REFERENCES contracts(id) ON DELETE CASCADE ON UPDATE CASCADE,
        FOREIGN KEY (debtor_id) REFERENCES debtors(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- 合約子表 (租賃合約)
    CREATE TABLE lease_contracts (
        contract_id                      BIGINT        PRIMARY KEY,
        tenant_name                      VARCHAR(100)  NOT NULL,
        rent_amount                      DECIMAL(18,8),
        deposit_amount                   DECIMAL(18,8),
        payment_day                      TINYINT, -- 每月幾號
        FOREIGN KEY (contract_id) REFERENCES contracts(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- 合約子表 (薪資合約)
    CREATE TABLE salary_contracts (
        contract_id                      BIGINT        PRIMARY KEY,
        employer_name                    VARCHAR(100),
        base_salary                      DECIMAL(18,8),
        bonus_rule                       VARCHAR(100),
        pay_day                          TINYINT,
        FOREIGN KEY (contract_id) REFERENCES contracts(id) ON DELETE CASCADE ON UPDATE CASCADE
    );

    -- (測試中)交易來源種類表
    CREATE TABLE transaction_source_types (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- MANUAL、CONTRACT、IMPORT
        name                             VARCHAR(100)   NOT NULL,                        -- 手動、合約、匯入
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL                             -- 刪除時間 (由後端寫入)
    );

    -- (測試中)交易來源表 (可自動生成交易)
    CREATE TABLE transaction_sources (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        transaction_source_type_id       BIGINT         NOT NULL,
        source_type_id                   BIGINT         NULL,                            -- 對應合約表或其他匯入的表
        source_id                        BIGINT         NULL,                            -- transaction_source_type 為 MANUAL 時 = NULL
                                                                                            transaction_source_type 為 CONTRACT 時 = contract_id
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                           -- 刪除時間 (由後端寫入)

        FOREIGN KEY (transaction_source_type_id) REFERENCES transaction_source_types(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (source_type_id) REFERENCES entity_types(id) ON DELETE SET NULL ON UPDATE CASCADE
    );

    -- (測試中)分類類型表
    CREATE TABLE category_types (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- income、expense、transfer
        name                             VARCHAR(100)   NOT NULL,                        -- 名稱
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL                             -- 刪除時間 (由後端寫入)
    );
    
    -- (測試中)大分類表
    CREATE TABLE category_groups (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        user_id                          BIGINT         NULL,                            -- 使用者名稱 (使用者可以自訂大分類與小分類)
        ledger_type_id                   BIGINT         NOT NULL,
        category_type_id                 BIGINT         NOT NULL,                        -- 分類類型
        code                             VARCHAR(50)    NOT NULL,                        -- 大分類代號
        name                             VARCHAR(100)   NOT NULL,                        -- 大分類名稱
        icon_id                          BIGINT         NULL,
        note                             VARCHAR(255),

        is_system                        BOOLEAN        NOT NULL DEFAULT FALSE,
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)

        UNIQUE(user_id, ledger_type_id, code),
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL ON UPDATE CASCADE,
        FOREIGN KEY (ledger_type_id) REFERENCES ledger_types(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (category_type_id) REFERENCES category_types(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (icon_id) REFERENCES icons(id) ON DELETE SET NULL ON UPDATE CASCADE
    );

    -- (測試中)小分類表
    CREATE TABLE categories (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        category_group_id                BIGINT         NOT NULL,		                -- (對應大分類)
        code                             VARCHAR(50)    NOT NULL,                        -- 小分類代號
        name                             VARCHAR(100)   NOT NULL,                        -- 小分類名稱
        icon_id                          BIGINT         NULL,
        note                             VARCHAR(255),
        is_system                        BOOLEAN        NOT NULL DEFAULT FALSE,
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)
        UNIQUE(category_group_id, code),
        INDEX(category_group_id),
        FOREIGN KEY (category_group_id) REFERENCES category_groups(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (icon_id) REFERENCES icons(id) ON DELETE SET NULL ON UPDATE CASCADE
    );

    -- (測試中)商店類型表
    CREATE TABLE merchant_types (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- restaurant、supermarket、utility
        name                             VARCHAR(100)   NOT NULL,                        -- 餐廳、超市、公共事業
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL                             -- 刪除時間 (由後端寫入)
    );

    -- (測試中)交易商店表 
    CREATE TABLE merchants (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        country_id                       BIGINT         NULL,                            -- 國別 (系統預設帶出該國別的商店)
        user_id                          BIGINT         NULL,                            -- 使用者名稱 (使用者可以自訂商店)
        merchant_type_id                 BIGINT         NOT NULL,                        -- 商店 / 公司類型
        code                             VARCHAR(50)    NOT NULL,                        -- 商店 / 公司代號
        name                             VARCHAR(100)   NOT NULL,                        -- 商店 / 公司名稱
        note                             VARCHAR(255),
        is_system                        BOOLEAN        NOT NULL DEFAULT FALSE,
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)
        UNIQUE(user_id, country_id, code),
        FOREIGN KEY (country_id) REFERENCES countries(id) ON DELETE SET NULL ON UPDATE CASCADE,
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL ON UPDATE CASCADE,
        FOREIGN KEY (merchant_type_id) REFERENCES merchant_types(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- (測試中)交易類型表
    CREATE TABLE transaction_types (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        ledger_type_id                   BIGINT         NOT NULL,
        code                             VARCHAR(50)    NOT NULL,	                     -- 代號
        name                             VARCHAR(100)   NOT NULL,                        -- 交易類型名稱 (如：
                                                                                         -- 收支活動 (income / expense / buy / sell)
                                                                                         -- 投資活動
                                                                                         -- 負債活動 (principal / interest / fee)
                                                                                         -- 應收帳款活動 (lend / receive / interest)
                                                                                         -- 固定資產活動 (depreciation / repair / revalue)
                                                                                         -- 存貨 (in / out / adjust)
        direction                        TINYINT        NOT NULL,                        -- 正向 / 負向 (income=1、expense=-1、buy=-1、sell=1、transfer=0)
        affects_balance                  BOOLEAN        DEFAULT TRUE,
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)
        
        UNIQUE (ledger_type_id, code),
        FOREIGN KEY (ledger_type_id) REFERENCES ledger_types(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- (測試中)交易狀態表
    CREATE TABLE transaction_statuses (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,
        name                             VARCHAR(100)   NOT NULL,
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL
    );

    -- (測試中)交易表
    CREATE TABLE transactions (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        ledger_id                        BIGINT         NOT NULL,
        account_id                       BIGINT         NOT NULL,
        transaction_source_id            BIGINT         NOT NULL,
        transaction_type_id              BIGINT         NOT NULL,
        
        price                            DECIMAL(18,8)  NULL,                            -- 價格
        quantity                         DECIMAL(18,8)  NULL,                            -- 數量
        amount                           DECIMAL(18,8)  NOT NULL DEFAULT 0,              -- 合計 (實際影響帳戶餘額的金額)

        original_currency_id             BIGINT         NULL,                            -- 原始交易幣別 (外幣交易使用)
        original_amount                  DECIMAL(18,8)  NULL,                            -- 原始交易金額 (外幣交易使用)
        exchange_rate_used               DECIMAL(18,8)  NULL,                            -- 當下換算匯率 (外幣交易使用)

        transaction_date                 DATETIME       NOT NULL,                        -- 交易日
        note                             VARCHAR(255),
        transaction_status_id            BIGINT         NOT NULL,                        -- 交易狀態

        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)
        
        INDEX (account_id, transaction_date),
        INDEX (ledger_id, transaction_date),
        INDEX (transaction_type_id),
        INDEX (transaction_date),
        
        FOREIGN KEY (ledger_id) REFERENCES ledgers(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (transaction_source_id) REFERENCES transaction_sources(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (transaction_type_id) REFERENCES transaction_types(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (original_currency_id) REFERENCES currencies(id) ON DELETE SET NULL ON UPDATE CASCADE,
        FOREIGN KEY (transaction_status_id) REFERENCES transaction_statuses(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- (測試中)交易關聯表 (不同交易之間的關聯)
    CREATE TABLE transaction_links (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        transaction_id                   BIGINT         NOT NULL,
        related_transaction_id           BIGINT         NOT NULL,

        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)

        UNIQUE(transaction_id, related_transaction_id),
        FOREIGN KEY (transaction_id) REFERENCES transactions(id) ON DELETE CASCADE ON UPDATE CASCADE,
        FOREIGN KEY (related_transaction_id) REFERENCES transactions(id) ON DELETE CASCADE ON UPDATE CASCADE
    );

    -- (測試中)交易表子表 (收支)
    CREATE TABLE cashflow_transaction_details (
        transaction_id                   BIGINT        PRIMARY KEY,
        category_id                      BIGINT        NOT NULL,
        merchant_id                      BIGINT        NULL,
        INDEX(category_id),
        FOREIGN KEY (transaction_id) REFERENCES transactions(id) ON DELETE CASCADE ON UPDATE CASCADE,
        FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (merchant_id) REFERENCES merchants(id) ON DELETE SET NULL
    );
    
    -- 交易表子表 (存貨)
    CREATE TABLE inventory_transaction_details (
        transaction_id                   BIGINT        PRIMARY KEY,
        expiry_date                      DATE,                                          -- 這批存貨的到期日
        FOREIGN KEY (transaction_id) REFERENCES transactions(id) ON DELETE CASCADE ON UPDATE CASCADE
    );

    -- (測試中)重複交易表
    CREATE TABLE recurring_transactions (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        ledger_id                        BIGINT         NOT NULL,
        account_id                       BIGINT         NOT NULL,
        transaction_source_id            BIGINT         NOT NULL,
        transaction_type_id              BIGINT         NOT NULL,

        price                            DECIMAL(18,8)  NULL,                            -- 價格
        quantity                         DECIMAL(18,8)  NULL,                            -- 數量
        amount                           DECIMAL(18,8)  NOT NULL DEFAULT 0,              -- 合計 (實際影響帳戶餘額的金額)

        original_currency_id             BIGINT         NULL,                            -- 原始交易幣別 (外幣交易使用)
        original_amount                  DECIMAL(18,8)  NULL,                            -- 原始交易金額 (外幣交易使用)
        exchange_rate_used               DECIMAL(18,8)  NULL,                            -- 當下換算匯率 (外幣交易使用)

        recurrence_frequency_id          BIGINT         NOT NULL,                        -- 週期規則(如 一月一次、每日一次 等等，應由後端設定)
        next_run_date                    DATE           NULL,                            -- 下次執行日期
        last_run_date                    DATE           NULL,                            -- 上次執行日期
        start_date                       DATE           NOT NULL,
        end_date                         DATE           NULL,
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用

        note                             VARCHAR(255),
        transaction_status_id            BIGINT         NOT NULL,

        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)

        INDEX(start_date),
        INDEX(account_id),
        INDEX(ledger_id),
        INDEX(recurrence_frequency_id),
        INDEX(next_run_date, is_active),
        
        FOREIGN KEY (ledger_id) REFERENCES ledgers(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (transaction_source_id) REFERENCES transaction_sources(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (transaction_type_id) REFERENCES transaction_types(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (recurrence_frequency_id) REFERENCES flow_frequencies(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (original_currency_id) REFERENCES currencies(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (transaction_status_id) REFERENCES transaction_statuses(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- (測試中)重複交易關聯表 (不同交易之間的關聯)
    CREATE TABLE recurring_transaction_links (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        recurring_transaction_id         BIGINT         NOT NULL,
        related_recurring_transaction_id BIGINT         NOT NULL,

        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)

        UNIQUE(recurring_transaction_id, related_recurring_transaction_id),
        FOREIGN KEY (recurring_transaction_id) REFERENCES recurring_transactions(id) ON DELETE CASCADE ON UPDATE CASCADE,
        FOREIGN KEY (related_recurring_transaction_id) REFERENCES recurring_transactions(id) ON DELETE CASCADE ON UPDATE CASCADE
    );

    -- (測試中)重複交易子表 (收支)
    CREATE TABLE cashflow_recurring_transaction_details (
        recurring_transaction_id         BIGINT        PRIMARY KEY,
        category_id                      BIGINT        NOT NULL,
        merchant_id                      BIGINT        NULL,
        FOREIGN KEY (recurring_transaction_id) REFERENCES recurring_transactions(id) ON DELETE CASCADE ON UPDATE CASCADE,
        FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (merchant_id) REFERENCES merchants(id) ON DELETE SET NULL ON UPDATE CASCADE
    );

    -- 交易標籤表
    CREATE TABLE transaction_tags (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        ledger_id                        BIGINT         NOT NULL,
        name                             VARCHAR(100)   NOT NULL,
        color                            VARCHAR(20),
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)
        UNIQUE (ledger_id, name),
        FOREIGN KEY (ledger_id) REFERENCES ledgers(id) ON DELETE CASCADE ON UPDATE CASCADE
    );

    -- 交易與標籤關聯表
    CREATE TABLE transaction_tag_mappings (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        transaction_id                   BIGINT         NOT NULL,
        tag_id                           BIGINT         NOT NULL,
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)
        UNIQUE (transaction_id, tag_id),
        FOREIGN KEY (transaction_id) REFERENCES transactions(id) ON DELETE CASCADE ON UPDATE CASCADE,
        FOREIGN KEY (tag_id) REFERENCES transaction_tags(id) ON DELETE CASCADE ON UPDATE CASCADE
    );

    -- 重複交易與標籤關聯表
    CREATE TABLE recurring_transaction_tags (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        recurring_transaction_id         BIGINT         NOT NULL,
        tag_id                           BIGINT         NOT NULL,
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)
        UNIQUE (recurring_transaction_id, tag_id),
        FOREIGN KEY (recurring_transaction_id) REFERENCES recurring_transactions(id) ON DELETE CASCADE ON UPDATE CASCADE,
        FOREIGN KEY (tag_id) REFERENCES transaction_tags(id) ON DELETE CASCADE ON UPDATE CASCADE
    );

    -- 總預算表
    CREATE TABLE general_budgets (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        ledger_id                        BIGINT         NOT NULL,

        name                             VARCHAR(100)   NOT NULL,
        start_date                       DATE           NOT NULL,
        end_date                         DATE           NOT NULL,

        currency_id                      BIGINT         NOT NULL,

        total_amount                     DECIMAL(18,8)  NOT NULL,                        -- 預算金額
        safety_amount                    DECIMAL(18,8)  NULL,                            -- 安全金額 (低於安全金額可設定提醒)
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)

        UNIQUE (ledger_id, name),
        FOREIGN KEY (ledger_id) REFERENCES ledgers(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (currency_id) REFERENCES currencies(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- 個別預算表
    CREATE TABLE individual_budgets (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        general_budget_id                BIGINT         NOT NULL,
        category_id                      BIGINT         NOT NULL,

        amount                           DECIMAL(18,8)  NOT NULL,                        -- 預算金額
        spent_amount                     DECIMAL(18,8)  DEFAULT 0,                       -- 已花費金額（可快取）
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)

        UNIQUE (general_budget_id, category_id),
        FOREIGN KEY (general_budget_id) REFERENCES general_budgets(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- 使用者餘額快照表 (可作總資產、總負債或淨資產 圓餅圖或曲線圖)
    CREATE TABLE user_balance_snapshots (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        user_id                          BIGINT         NOT NULL,

        total_assets                     DECIMAL(18,8)  NOT NULL,                      -- 總資產
        total_liabilities                DECIMAL(18,8)  NOT NULL,                      -- 總負債
        net_worth                        DECIMAL(18,8)  NOT NULL,                      -- 淨資產

        snapshot_date                    DATE           NOT NULL,                      -- 快照日期
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,

        UNIQUE (user_id, snapshot_date),
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- 帳本餘額快照表 (單一帳本)
    CREATE TABLE ledger_balance_snapshots (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        ledger_id                        BIGINT         NOT NULL,
        balance                          DECIMAL(18,8)  NOT NULL,
        snapshot_date                    DATE           NOT NULL,                      -- 快照日期
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        UNIQUE (ledger_id, snapshot_date),
        FOREIGN KEY (ledger_id) REFERENCES ledgers(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- 帳戶餘額快照表 (單一帳戶)
    CREATE TABLE account_balance_snapshots (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        account_id                       BIGINT         NOT NULL,
        
        balance                          DECIMAL(18,8)  NOT NULL,
        currency_id                      BIGINT         NOT NULL,

        base_balance                     DECIMAL(18,8)  NOT NULL,                      -- 折算本位幣價值
        base_currency_id                 BIGINT         NOT NULL,

        snapshot_date                    DATE           NOT NULL,                      -- 快照日期
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
        UNIQUE (account_id, snapshot_date),
        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (currency_id) REFERENCES currencies(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (base_currency_id) REFERENCES currencies(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- 投資持倉快照表 (單一投資商品)
    CREATE TABLE investment_position_snapshots (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        account_id                       BIGINT         NOT NULL,
        investment_product_id            BIGINT         NOT NULL,

        quantity                         DECIMAL(18,8)  NOT NULL DEFAULT 0,              -- 持有數量
        avg_unit_cost                    DECIMAL(18,8)  NOT NULL DEFAULT 0,              -- 平均成本
        total_tax_amount                 DECIMAL(18,8)  NOT NULL DEFAULT 0,              -- 總稅費
        total_fee_amount                 DECIMAL(18,8)  NOT NULL DEFAULT 0,              -- 總費用
        total_cost                       DECIMAL(18,8)  NOT NULL DEFAULT 0,              -- 總成本
        market_value                     DECIMAL(18,8)  DEFAULT 0,                       -- 市值 (快照時計算)

        snapshot_date                    DATE           NOT NULL,                        -- 快照日期
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,

        UNIQUE (account_id, investment_product_id, snapshot_date),
        INDEX(account_id),

        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (investment_product_id) REFERENCES investment_products(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- 提醒類型表
    CREATE TABLE notification_types (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- BILL_DUE、BUDGET_OVER、SUBSCRIPTION_EXPIRED、PRICE_ALERT
        name                             VARCHAR(100)   NOT NULL,
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL                             -- 刪除時間 (由後端寫入)
    );

    -- 提醒優先順序表
    CREATE TABLE notification_priorities (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- LOW、NORMAL、HIGH、URGENT
        name                             VARCHAR(100)   NOT NULL,
        sort_order                       INT            NOT NULL DEFAULT 0,
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL                             -- 刪除時間 (由後端寫入)
    );

    -- 提醒表
    CREATE TABLE notifications (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        notification_type_id             BIGINT         NOT NULL,
        user_id                          BIGINT         NOT NULL,
        
        entity_type_id                   BIGINT         NULL,
        entity_id                        BIGINT         NULL,
        
        title                            VARCHAR(100)   NOT NULL,
        message                          VARCHAR(500)   NOT NULL,
        payload                          JSON           NULL,
        notification_priority_id         BIGINT         NOT NULL,                        -- 通知先順序
        expires_date                     DATETIME       NULL,                            -- 通知時效 (通知的生命週期)
        
        created_by                       BIGINT         NOT NULL,
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,

        INDEX(notification_type_id),
        INDEX(entity_type_id, entity_id),
        FOREIGN KEY (notification_type_id) REFERENCES notification_types(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (entity_type_id) REFERENCES entity_types(id) ON DELETE SET NULL ON UPDATE CASCADE,
        FOREIGN KEY (notification_priority_id) REFERENCES notification_priorities(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE CASCADE  
    );

    -- 提醒排程狀態表
    CREATE TABLE notification_schedule_statuses (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- ACTIVE、PAUSED、FINISHED、CANCELLED
        name                             VARCHAR(100)   NOT NULL,
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL                             -- 刪除時間 (由後端寫入)
    );

    -- 提醒排程表
    CREATE TABLE notification_schedules (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        notification_id                  BIGINT         NOT NULL,
        frequency_id                     BIGINT         NULL,                            -- 週期性通知使用(提醒頻率)
        scheduled_date                   DATETIME       NULL,                            -- 一次性通知使用(提醒日)

        is_repeat                        BOOLEAN        NOT NULL DEFAULT FALSE,          -- 是否週期性重複提醒
        is_retry                         BOOLEAN        NOT NULL DEFAULT FALSE,          -- 是否單次重複提醒
        
        repeat_limit                     INT            NULL,                            -- 最多重複提醒幾次
        repeat_count                     INT            DEFAULT 0,                       -- 目前已提醒幾次
        next_repeat_date                 DATETIME       NULL,                            -- 下一次提醒日
        last_repeat_date                 DATETIME       NULL,                            -- 上次提醒日

        retry_limit                      INT            NULL,                            -- 最多單次重複提醒幾次
        retry_count                      INT            DEFAULT 0,                       -- 目前單次已提醒幾次
        next_retry_date                  DATETIME       NULL,                            -- 下一次單次重複提醒日

        notification_schedule_status_id  BIGINT         NOT NULL,
        
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,                            -- 刪除時間 (由後端寫入)
        FOREIGN KEY (notification_id) REFERENCES notifications(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (frequency_id) REFERENCES frequencies(id) ON DELETE SET NULL ON UPDATE CASCADE,
        FOREIGN KEY (notification_schedule_status_id) REFERENCES notification_schedule_statuses(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- 提醒狀態表
    CREATE TABLE notification_statuses (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- PENDING、SENT、READ、FAILED、CANCELLED
        name                             VARCHAR(100)   NOT NULL,
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL                             -- 刪除時間 (由後端寫入)
    );

    -- 使用者提醒關聯表
    CREATE TABLE user_notifications (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        user_id                          BIGINT         NOT NULL,
        notification_id                  BIGINT         NOT NULL,
        notification_status_id           BIGINT         NOT NULL,
        notification_status_date         DATETIME       NOT NULL,
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL,
        UNIQUE(user_id, notification_id),
        INDEX(notification_status_id),
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (notification_id) REFERENCES notifications(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (notification_status_id) REFERENCES notification_statuses(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- 提醒通路表
    CREATE TABLE notification_channels (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- APP_PUSH、EMAIL、SMS、LINE、WEBHOOK
        name                             VARCHAR(100)   NOT NULL,
        sort_order                       INT            NOT NULL DEFAULT 0,              -- 排序順序
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL                             -- 刪除時間 (由後端寫入)
    );

    -- 使用者提醒通路設定表
    CREATE TABLE user_notification_channel_settings (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        user_id                          BIGINT         NOT NULL,
        notification_type_id             BIGINT         NOT NULL,
        notification_channel_id          BIGINT         NOT NULL,
        is_enabled                       BOOLEAN        NOT NULL DEFAULT TRUE,
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        UNIQUE(user_id, notification_type_id, notification_channel_id),
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (notification_type_id) REFERENCES notification_types(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (notification_channel_id) REFERENCES notification_channels(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- 提醒寄送狀態表
    CREATE TABLE notification_send_statuses (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- PENDING、SUCCESS、FAILED、RETRY、CANCELLED
        name                             VARCHAR(100)   NOT NULL,
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL
    );

    -- 提醒寄送紀錄表
    CREATE TABLE notification_send_logs (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        user_notification_id             BIGINT         NOT NULL,
        notification_schedule_id         BIGINT         NOT NULL,
        notification_channel_id          BIGINT         NOT NULL,
        notification_send_status_id      BIGINT         NOT NULL,
        notification_send_status_date    DATETIME       NOT NULL,
        retry_no                         INT            NOT NULL DEFAULT 0,               -- 單次重寄的寄次(第幾次)
        error_message                    VARCHAR(255)   NULL,
        provider_response                JSON           NULL,
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        
        INDEX(user_notification_id),
        INDEX(notification_schedule_id),
        FOREIGN KEY (user_notification_id) REFERENCES user_notifications(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (notification_schedule_id) REFERENCES notification_schedules(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (notification_channel_id) REFERENCES notification_channels(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (notification_send_status_id) REFERENCES notification_send_statuses(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- 帳戶調整原因表
    CREATE TABLE account_adjustment_reasons (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- 代碼
        name                             VARCHAR(100)   NOT NULL,                        -- 名稱
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL                             -- 刪除時間 (由後端寫入)
    );

    -- 帳戶調整表 (會計用)
    CREATE TABLE account_adjustments (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        account_id                       BIGINT         NOT NULL,
        account_adjustment_reason_id     BIGINT         NOT NULL,
        adjustment_amount                DECIMAL(18,8)  NOT NULL DEFAULT 0,
        note                             VARCHAR(255),
        created_by                       BIGINT         NOT NULL,                        -- 誰做的調整
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (account_adjustment_reason_id) REFERENCES account_adjustment_reasons(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- 稽核活動類型表
    CREATE TABLE audit_action_types (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- CREATE、UPDATE、SOFT_DELETE、RESTORE、LOGIN、EXPORT
        name                             VARCHAR(100)   NOT NULL,
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL                             -- 刪除時間 (由後端寫入)
    );

    -- 稽核紀錄表
    CREATE TABLE audit_logs (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        user_id                          BIGINT         NULL,
        audit_action_type_id             BIGINT         NOT NULL,
        entity_type_id                   BIGINT         NOT NULL,
        entity_id                        BIGINT         NOT NULL,

        old_value                        JSON           NULL,
        new_value                        JSON           NULL,

        ip_address                       VARCHAR(45)    NULL,
        user_agent                       VARCHAR(255)   NULL,
        note                             VARCHAR(255),
        created_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

        INDEX(user_id, created_date),
        INDEX(entity_type_id, entity_id),
        INDEX(audit_action_type_id, created_date),

        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL ON UPDATE CASCADE,
        FOREIGN KEY (audit_action_type_id) REFERENCES audit_action_types(id) ON DELETE RESTRICT ON UPDATE CASCADE,
        FOREIGN KEY (entity_type_id) REFERENCES entity_types(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );

    -- 使用者登入狀態表
    CREATE TABLE user_login_statuses (
        id                               BIGINT         AUTO_INCREMENT PRIMARY KEY,
        code                             VARCHAR(50)    NOT NULL UNIQUE,                 -- 代碼（SUCCESS、INVALID_PASSWORD、USER_NOT_FOUND、ACCOUNT_LOCKED
                                                                                                 ACCOUNT_DISABLED、EMAIL_NOT_VERIFIED、TOKEN_EXPIRED
                                                                                                 REFRESH_TOKEN_EXPIRED、MFA_REQUIRED、MFA_FAILED、UNKNOWN_ERROR）
        name                             VARCHAR(100)   NOT NULL,                        -- 名稱
        note                             VARCHAR(255),
        is_active                        BOOLEAN        NOT NULL DEFAULT TRUE,           -- 是否啟用
        created_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_date                     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        deleted_date                     DATETIME       NULL                             -- 刪除時間 (由後端寫入)
    );

    -- 使用者登入紀錄表
    CREATE TABLE user_login_logs (
        id                               BIGINT        AUTO_INCREMENT PRIMARY KEY,
        user_id                          BIGINT        NULL,
        email                            VARCHAR(255)  NOT NULL,
        user_login_status_id             BIGINT        NOT NULL,
        login_time                       DATETIME      NOT NULL,
        logout_time                      DATETIME      NULL,
        ip_address                       VARCHAR(45)   NULL,
        user_agent                       VARCHAR(500)  NULL,
        device_name                      VARCHAR(100)  NULL,
        os_name                          VARCHAR(50)   NULL,
        browser_name                     VARCHAR(50)   NULL,
        country_id                       BIGINT        NULL,
        city                             VARCHAR(100)  NULL,
        session_id                       VARCHAR(100)  NULL,
        refresh_token_id                 VARCHAR(100)  NULL,

        INDEX(user_id, login_time),
        INDEX(email),
        INDEX(ip_address),
        INDEX(user_login_status_id),

        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL ON UPDATE CASCADE,
        FOREIGN KEY (country_id) REFERENCES countries(id) ON DELETE SET NULL ON UPDATE CASCADE,
        FOREIGN KEY (user_login_status_id) REFERENCES user_login_statuses(id) ON DELETE RESTRICT ON UPDATE CASCADE
    );



## 國別資料來源
(1)dr5hn：https://github.com/dr5hn/countries-states-cities-database/tree/master
(2)REST Countries：https://restcountries.com/

    dr5hn countries.json
    ├── countries
    ├── files（FLAG）
    ├── currencies
    ├── timezones
    ├── currency_countries
    └── country_timezones
    REST Countries
    ├── languages
    └── country_languages
