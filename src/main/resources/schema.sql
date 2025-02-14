-- category
CREATE SEQUENCE category_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE category
(
    id        INT DEFAULT NEXTVAL('category_seq') PRIMARY KEY,
    name      VARCHAR(40) UNIQUE NOT NULL,
    parent_id INT                NULL
);

CREATE INDEX idx_category_name ON category (name);

-- customer
CREATE SEQUENCE customer_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE customer
(
    id          INT       DEFAULT NEXTVAL('customer_seq') PRIMARY KEY,
    name        VARCHAR(40) UNIQUE NOT NULL,
    code        VARCHAR(6) UNIQUE  NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_archived BOOLEAN   DEFAULT FALSE
);

CREATE INDEX idx_customer_code ON customer (code);

-- image
CREATE SEQUENCE image_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE image
(
    id          INT       DEFAULT NEXTVAL('image_seq') PRIMARY KEY,
    key         VARCHAR(8) UNIQUE NOT NULL,
    product_id  INT               NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_archived BOOLEAN   DEFAULT FALSE
);

CREATE INDEX idx_image_key ON image (key);

-- product
CREATE SEQUENCE product_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE product
(
    id           INT       DEFAULT NEXTVAL('product_seq') PRIMARY KEY,
    name         VARCHAR(40)       NOT NULL,
    description  TEXT              NOT NULL,
    price        INT               NOT NULL,
    count        INT               NOT NULL,
    is_activated BOOLEAN   DEFAULT FALSE,
    category_id  INT               NOT NULL,
    store_id     INT               NOT NULL,
    code         VARCHAR(6) UNIQUE NOT NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_archived  BOOLEAN   DEFAULT FALSE
);

CREATE INDEX idx_product_code ON product (code);

-- review
CREATE SEQUENCE review_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE review
(
    id          INT       DEFAULT NEXTVAL('review_seq') PRIMARY KEY,
    score       INT               NOT NULL,
    contents    TEXT              NOT NULL,
    product_id  INT               NOT NULL,
    code        VARCHAR(6) UNIQUE NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_archived BOOLEAN   DEFAULT FALSE
);

CREATE INDEX idx_review_code ON review (code);

-- search_history
CREATE SEQUENCE search_history_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE search_history
(
    id          INT       DEFAULT NEXTVAL('search_history_seq') PRIMARY KEY,
    contents    TEXT NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    customer_id INT  NOT NULL
);

-- store
CREATE SEQUENCE store_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE store
(
    id            INT       DEFAULT NEXTVAL('store_seq') PRIMARY KEY,
    name          VARCHAR(40) UNIQUE NOT NULL,
    description   TEXT               NOT NULL,
    refund_info   TEXT               NOT NULL,
    delivery_info TEXT               NOT NULL,
    code          VARCHAR(6) UNIQUE  NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_archived   BOOLEAN   DEFAULT FALSE
);

CREATE INDEX idx_store_code ON store (code);

-- store_history
CREATE SEQUENCE store_history_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE store_history
(
    id         INT       DEFAULT NEXTVAL('store_history_seq') PRIMARY KEY,
    contents   TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    store_id   INT  NOT NULL
);

-- Add foreign keys separately
ALTER TABLE category
    ADD CONSTRAINT fk_category_parent FOREIGN KEY (parent_id) REFERENCES category (id) ON DELETE SET NULL;
ALTER TABLE image
    ADD CONSTRAINT fk_image_product FOREIGN KEY (product_id) REFERENCES product (id) ON DELETE RESTRICT;
ALTER TABLE product
    ADD CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES category (id) ON DELETE RESTRICT;
ALTER TABLE product
    ADD CONSTRAINT fk_product_store FOREIGN KEY (store_id) REFERENCES store (id) ON DELETE RESTRICT;
ALTER TABLE review
    ADD CONSTRAINT fk_review_product FOREIGN KEY (product_id) REFERENCES product (id) ON DELETE RESTRICT;
ALTER TABLE search_history
    ADD CONSTRAINT fk_search_history_customer FOREIGN KEY (customer_id) REFERENCES customer (id) ON DELETE RESTRICT;
ALTER TABLE store_history
    ADD CONSTRAINT fk_store_history_customer FOREIGN KEY (store_id) REFERENCES store (id) ON DELETE RESTRICT;
