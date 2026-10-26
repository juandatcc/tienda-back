-- Crear tabla payment_transaction en Oracle
CREATE TABLE payment_transaction (
    id VARCHAR2(36) PRIMARY KEY,  -- UUID generado por la aplicación
    reference VARCHAR2(100) UNIQUE,
    amount NUMBER(38,2),
    currency VARCHAR2(10),
    status VARCHAR2(30),
    buyer_email VARCHAR2(200),
    pse_transaction_id VARCHAR2(200),
    pse_bank_code VARCHAR2(50),
    redirect_url VARCHAR2(1000),
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IDX_PAYMENT_REF ON payment_transaction(reference);

