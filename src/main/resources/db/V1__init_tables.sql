CREATE TABLE symbol_info (
    id UUID PRIMARY KEY,
    create_date TIMESTAMP,
    symbol VARCHAR(255),
    market_cap_usd NUMERIC(38, 18),
    circulating_supply NUMERIC(38, 18),
    vol DOUBLE PRECISION,
    open DOUBLE PRECISION,
    close DOUBLE PRECISION,
    low DOUBLE PRECISION,
    high DOUBLE PRECISION,
    interval INTEGER,
    open_interest NUMERIC(38, 18),
    open_interest_usd NUMERIC(38, 18),
    funding_rate NUMERIC(38, 18)
);

-- Создание индекса для часто используемого поля symbol (опционально)
CREATE INDEX idx_symbol_info_symbol ON symbol_info(symbol);

-- Создание индекса для даты создания (опционально)
CREATE INDEX idx_symbol_info_create_date ON symbol_info(create_date);