CREATE TABLE orders
(
    order_id      BIGSERIAL PRIMARY KEY,
    customer_name VARCHAR(255)   NOT NULL,
    status        VARCHAR(64)    NOT NULL,
    total_price   DECIMAL(10, 2) NOT NULL,
    is_active     BOOLEAN        NOT NULL
);