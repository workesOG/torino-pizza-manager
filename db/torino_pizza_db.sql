DROP DATABASE IF EXISTS torino_pizza;
CREATE DATABASE torino_pizza;
USE torino_pizza;

CREATE TABLE customer(
	customer_id INT AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
	last_name VARCHAR(50) NOT NULL,
    address VARCHAR(100),
    zip_code VARCHAR(20),
    city VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(100),
    PRIMARY KEY(customer_id)
);

CREATE TABLE ingredient(
	ingredient_id INT AUTO_INCREMENT,
    ingredient_name VARCHAR(50),
    ingredient_desc VARCHAR(200),
    ingredient_price DECIMAL(3, 1),
    PRIMARY KEY(ingredient_id)
);

CREATE TABLE pizza(
	pizza_id INT AUTO_INCREMENT,
    pizza_name VARCHAR(50),
    pizza_price DECIMAL(5, 1),
    PRIMARY KEY(pizza_id)
);

CREATE TABLE `order`(
	order_id INT AUTO_INCREMENT,
	customer_id INT,
    order_date DATETIME,
    order_price DECIMAL(7, 1),
    PRIMARY KEY(order_id),
    FOREIGN KEY(customer_id) REFERENCES customer(customer_id)
);

CREATE TABLE pizza_ingredient(
	pizza_id INT NOT NULL,
    ingredient_id INT NOT NULL,
    PRIMARY KEY (pizza_id, ingredient_id),
    FOREIGN KEY (pizza_id) REFERENCES pizza(pizza_id),
    FOREIGN KEY (ingredient_id) REFERENCES ingredient(ingredient_id)
);

CREATE TABLE order_item(
    order_item_id INT AUTO_INCREMENT,
    order_id INT NOT NULL,
    pizza_id INT NOT NULL,
    PRIMARY KEY (order_item_id),
    FOREIGN KEY (order_id) REFERENCES `order`(order_id),
    FOREIGN KEY (pizza_id) REFERENCES pizza(pizza_id)
);

CREATE TABLE order_item_customization(
    order_item_customization_id INT AUTO_INCREMENT,
    order_item_id INT NOT NULL,
    ingredient_id INT NOT NULL,
    action ENUM('add', 'remove') NOT NULL,
    PRIMARY KEY(order_item_customization_id),
    FOREIGN KEY(order_item_id) REFERENCES order_item(order_item_id),
    FOREIGN KEY(ingredient_id) REFERENCES ingredient(ingredient_id)
);

CREATE TABLE courier(
	courier_id INT AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    PRIMARY KEY (courier_id)
);

CREATE TABLE delivery (
	delivery_id INT AUTO_INCREMENT,
    order_id INT NOT NULL,
    courier_id INT NOT NULL,
	delivery_date DATETIME NOT NULL,
	delivery_address VARCHAR(100) NOT NULL,
    delivery_zip_code VARCHAR(20) NOT NULL,
    delivery_city VARCHAR(50) NOT NULL,
    status ENUM('pending', 'out-for-delivery', 'delivered', 'error'),
    PRIMARY KEY(delivery_id),
    FOREIGN KEY(order_id) REFERENCES `order`(order_id),
    FOREIGN KEY(courier_id) REFERENCES courier(courier_id)
);

CREATE TABLE inventory (
    ingredient_id INT NOT NULL,
    current_stock DECIMAL(10, 2) NOT NULL,
    unit VARCHAR(10) NOT NULL,
    
    PRIMARY KEY (ingredient_id),
    FOREIGN KEY (ingredient_id) REFERENCES ingredient(ingredient_id)
);

INSERT INTO ingredient (ingredient_name, ingredient_desc, ingredient_price) VALUES
('Tomato Sauce', 'Classic tomato base', 2.50),
('Mozzarella', 'Fresh mozzarella cheese', 3.00),
('Pepperoni', 'Spicy sliced sausage', 4.00),
('Ham', 'Smoked ham slices', 3.50),
('Mushrooms', 'Fresh champignon', 2.00),
('Basil', 'Aromatic fresh basil', 1.00),
('Olives', 'Black Mediterranean olives', 1.50),
('Onion', 'Fresh sliced onion', 1.20);

INSERT INTO pizza (pizza_name, pizza_price) VALUES
('Margherita', 65),
('Pepperoni', 75),
('Fun Guy', 70),
('Ham Deluxe', 80);

INSERT INTO pizza_ingredient (pizza_id, ingredient_id) VALUES
-- Margherita
(1, 1),
(1, 2),
(1, 6),

-- Pepperoni
(2, 1),
(2, 2),
(2, 3),

-- Funghi
(3, 1),
(3, 2),
(3, 5),

-- Ham Deluxe
(4, 1),
(4, 2),
(4, 4),
(4, 7),
(4, 8);

INSERT INTO customer (first_name, last_name, address, zip_code, city, phone, email) VALUES
('Anna', 'Holm', 'Rosenvej 12', '2100', 'København Ø', '+45 31787312', 'anna.holm@example.com'),
('Jens', 'Petersen', 'Bakkevej 42', '8000', 'Aarhus C', '+45 67561231', 'jp@example.com'),
('Sara', 'Møller', 'Enghave Plads 5', '1670', 'København V', '+45 98731427', 'sara.m@example.com'),
('Lars', 'Knudsen', 'Fjordvej 88', '9000', 'Aalborg', '+45 59231238', 'l.knudsen@example.com');

INSERT INTO `order` (customer_id, order_date, order_price) VALUES
(1, '2025-05-10 18:30:00', 75),
(2, '2025-05-11 12:15:00', 145),
(3, '2025-05-11 19:02:00', 65),
(4, '2025-05-12 17:45:00', 155);

INSERT INTO order_item (order_id, pizza_id) VALUES
-- Order 1: Anna
(1, 2),

-- Order 2: Jens
(2, 1),
(2, 4),

-- Order 3: Sara
(3, 1),

-- Order 4: Lars
(4, 2),
(4, 4);

INSERT INTO order_item_customization(order_item_id, ingredient_id, action) VALUES
(1, 3, 'remove'),
(2, 3, 'add'),
(2, 5, 'add');

-- Couriers
INSERT INTO courier (first_name, last_name, phone) VALUES
('Mikkel', 'Hansen', '+45 31234567'),
('Sofie', 'Jensen', '+45 29876543'),
('Emil', 'Nielsen', '+45 30456789');

-- Deliveries
INSERT INTO delivery (order_id, courier_id, delivery_date, delivery_address, delivery_zip_code, delivery_city, status) VALUES
(1, 1, '2025-05-10 19:00:00', 'Rosenvej 12', '2100', 'København Ø', 'delivered'),
(2, 2, '2025-05-11 12:45:00', 'Bakkevej 42', '8000', 'Aarhus C', 'out-for-delivery'),
(3, 3, '2025-05-11 19:30:00', 'Enghave Plads 5', '1670', 'København V', 'delivered'),
(4, 1, '2025-05-12 18:15:00', 'Fjordvej 88', '9000', 'Aalborg', 'pending');

-- Inventory
INSERT INTO inventory (ingredient_id, current_stock, unit) VALUES
(1, 50.0, 'liter'),  -- Tomato Sauce
(2, 20.0, 'kg'),     -- Mozzarella
(3, 10.0, 'kg'),     -- Pepperoni (slices)
(5, 5.0, 'kg');      -- Mushrooms
