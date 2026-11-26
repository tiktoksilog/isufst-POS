-- =====================================================================
--  ISUFST POS - Canteen Point of Sale
--  Database script for MySQL Workbench (MySQL 8.x)
--  How to use: open this file in MySQL Workbench, then click
--  "Execute (All or Selection)" (the lightning bolt icon).
-- =====================================================================

DROP DATABASE IF EXISTS isufst_pos_db;
CREATE DATABASE isufst_pos_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE isufst_pos_db;

-- ---------------------------------------------------------------------
-- USERS (passwords are stored as SHA-256 hashes)
-- ---------------------------------------------------------------------
CREATE TABLE users (
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password_hash CHAR(64)     NOT NULL,
    full_name     VARCHAR(100) NOT NULL,
    role          ENUM('ADMIN', 'CASHIER') NOT NULL DEFAULT 'CASHIER',
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------------------
-- CATEGORIES
-- ---------------------------------------------------------------------
CREATE TABLE categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50) NOT NULL UNIQUE
);

-- ---------------------------------------------------------------------
-- PRODUCTS
-- ---------------------------------------------------------------------
CREATE TABLE products (
    product_id  INT AUTO_INCREMENT PRIMARY KEY,
    category_id INT            NOT NULL,
    name        VARCHAR(100)   NOT NULL,
    price       DECIMAL(10, 2) NOT NULL CHECK (price >= 0),
    stock       INT            NOT NULL DEFAULT 0 CHECK (stock >= 0),
    is_active   TINYINT(1)     NOT NULL DEFAULT 1,
    CONSTRAINT fk_products_category FOREIGN KEY (category_id)
        REFERENCES categories (category_id)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

-- ---------------------------------------------------------------------
-- SALES (one row per transaction / receipt)
-- ---------------------------------------------------------------------
CREATE TABLE sales (
    sale_id       INT AUTO_INCREMENT PRIMARY KEY,
    user_id       INT            NOT NULL,
    sale_date     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total_amount  DECIMAL(10, 2) NOT NULL,
    amount_paid   DECIMAL(10, 2) NOT NULL,
    change_amount DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_sales_user FOREIGN KEY (user_id)
        REFERENCES users (user_id)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

-- ---------------------------------------------------------------------
-- SALE ITEMS (line items of each transaction)
-- ---------------------------------------------------------------------
CREATE TABLE sale_items (
    sale_item_id INT AUTO_INCREMENT PRIMARY KEY,
    sale_id      INT            NOT NULL,
    product_id   INT            NOT NULL,
    quantity     INT            NOT NULL CHECK (quantity > 0),
    unit_price   DECIMAL(10, 2) NOT NULL,
    subtotal     DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_items_sale FOREIGN KEY (sale_id)
        REFERENCES sales (sale_id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_items_product FOREIGN KEY (product_id)
        REFERENCES products (product_id)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE INDEX idx_sales_date ON sales (sale_date);

-- =====================================================================
--  SAMPLE DATA
-- =====================================================================

-- Default accounts:
--   admin   / admin123    (can manage products and view sales)
--   cashier / cashier123  (can only sell)
INSERT INTO users (username, password_hash, full_name, role) VALUES
('admin',   SHA2('admin123', 256),   'Canteen Administrator', 'ADMIN'),
('cashier', SHA2('cashier123', 256), 'Juan Dela Cruz',        'CASHIER');

INSERT INTO categories (category_id, name) VALUES
(1, 'Rice Meals'),
(2, 'Silog Meals'),
(3, 'Noodles'),
(4, 'Merienda'),
(5, 'Street Food'),
(6, 'Bread & Pastries'),
(7, 'Chips & Biscuits'),
(8, 'Desserts'),
(9, 'Drinks');

INSERT INTO products (category_id, name, price, stock) VALUES
-- Rice Meals
(1, 'Chicken Adobo with Rice',        75.00, 40),
(1, 'Pork Adobo with Rice',           80.00, 40),
(1, 'Pork Sinigang with Rice',        85.00, 30),
(1, 'Chicken Curry with Rice',        75.00, 30),
(1, 'Pork Menudo with Rice',          75.00, 30),
(1, 'Fried Chicken with Rice',        80.00, 40),
(1, 'Lumpiang Shanghai with Rice',    65.00, 35),
(1, 'Bicol Express with Rice',        80.00, 25),
(1, 'Pinakbet with Rice',             60.00, 25),
(1, 'Ginisang Monggo with Rice',      55.00, 30),
(1, 'Plain Rice',                     15.00, 150),
-- Silog Meals
(2, 'Tapsilog',                       85.00, 30),
(2, 'Longsilog',                      75.00, 30),
(2, 'Tocilog',                        75.00, 30),
(2, 'Hotsilog',                       65.00, 30),
(2, 'Cornsilog',                      65.00, 25),
(2, 'Bangsilog',                      90.00, 20),
-- Noodles
(3, 'Pancit Canton',                  45.00, 30),
(3, 'Pancit Bihon',                   45.00, 30),
(3, 'Pancit Palabok',                 50.00, 25),
(3, 'Lomi',                           55.00, 20),
(3, 'Instant Noodles (Cup)',          30.00, 50),
-- Merienda
(4, 'Turon',                          15.00, 50),
(4, 'Banana Cue',                     20.00, 40),
(4, 'Camote Cue',                     15.00, 40),
(4, 'Lumpiang Gulay',                 15.00, 40),
(4, 'Puto',                           10.00, 60),
(4, 'Kutsinta',                       10.00, 60),
(4, 'Palitaw',                        10.00, 40),
(4, 'Ginataang Bilo-Bilo',            30.00, 20),
-- Street Food
(5, 'Fishball (10 pcs)',              20.00, 60),
(5, 'Kikiam (5 pcs)',                 20.00, 60),
(5, 'Squidball (5 pcs)',              20.00, 60),
(5, 'Kwek-Kwek (3 pcs)',              25.00, 40),
(5, 'Siomai (4 pcs)',                 30.00, 50),
(5, 'Siopao Asado',                   35.00, 30),
(5, 'Hotdog on Stick',                25.00, 40),
-- Bread & Pastries
(6, 'Pandesal',                        5.00, 100),
(6, 'Ensaymada',                      20.00, 30),
(6, 'Spanish Bread',                  10.00, 40),
(6, 'Cheese Bread',                   10.00, 40),
(6, 'Hopia Monggo',                   15.00, 40),
(6, 'Egg Pie (slice)',                25.00, 24),
(6, 'Banana Cake (slice)',            20.00, 24),
-- Chips & Biscuits
(7, 'SkyFlakes Crackers',             10.00, 80),
(7, 'Piattos',                        20.00, 50),
(7, 'Nova',                           20.00, 50),
(7, 'Chippy',                         15.00, 50),
(7, 'Boy Bawang',                     15.00, 50),
(7, 'Fita Crackers',                  10.00, 60),
-- Desserts
(8, 'Halo-Halo',                      50.00, 25),
(8, 'Leche Flan',                     30.00, 20),
(8, 'Buko Pandan',                    30.00, 20),
(8, 'Maja Blanca',                    20.00, 20),
(8, 'Ice Candy',                      10.00, 60),
(8, 'Mais con Yelo',                  40.00, 20),
-- Drinks
(9, 'Bottled Water 500ml',            15.00, 100),
(9, 'Coke Mismo',                     20.00, 60),
(9, 'Royal Mismo',                    20.00, 60),
(9, 'Sprite Mismo',                   20.00, 60),
(9, 'C2 Green Tea',                   25.00, 50),
(9, 'Zesto Juice',                    12.00, 60),
(9, 'Buko Juice',                     25.00, 30),
(9, 'Sago''t Gulaman',                20.00, 40),
(9, 'Iced Coffee',                    30.00, 30),
(9, 'Hot 3-in-1 Coffee',              15.00, 50),
(9, 'Hot Milo',                       15.00, 50);

-- Quick check
SELECT c.name AS category, COUNT(p.product_id) AS products
FROM categories c LEFT JOIN products p ON p.category_id = c.category_id
GROUP BY c.category_id, c.name ORDER BY c.category_id;
