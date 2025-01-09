CREATE TABLE products
(
    product_id   BIGSERIAL PRIMARY KEY,
    product_name VARCHAR(255)   NOT NULL,
    price        DECIMAL(10, 2) NOT NULL,
    quantity     INTEGER        NOT NULL,
    order_id     BIGINT         NOT NULL REFERENCES orders (order_id)
);