USE torino_pizza;

DELIMITER $

DROP PROCEDURE IF EXISTS listCustomers$
CREATE PROCEDURE listCustomers ()
BEGIN
	SELECT 
	c.customer_id,
    CONCAT(c.first_name, ' ', c.last_name) AS full_name,
    CONCAT(c.address, ', ', c.city, ', ', c.zip_code) as full_address,
    c.phone,
    c.email
	FROM customer c;
END$

DROP PROCEDURE IF EXISTS searchCustomersFuzzy$
CREATE PROCEDURE searchCustomersFuzzy (
	IN name_fragment VARCHAR(50)
)
BEGIN
	SELECT 
	c.customer_id,
    CONCAT(c.first_name, ' ', c.last_name) AS full_name,
    CONCAT(c.address, ', ', c.city, ', ', c.zip_code) as full_address,
    c.phone,
    c.email
	FROM customer c
    WHERE first_name LIKE CONCAT('%', name_fragment, '%')
    OR last_name LIKE CONCAT('%', name_fragment, '%');
END$

DROP PROCEDURE IF EXISTS findCustomersByField$
CREATE PROCEDURE findCustomersByField (
IN p_field VARCHAR(50),
IN p_value VARCHAR(100)
)
BEGIN
	SET @sql = CONCAT(
		'SELECT ',
        'c.customer_id, ',
		'CONCAT(c.first_name, '' '', c.last_name) AS full_name,',
		'c.address, c.phone, c.email ',
        'FROM customer c ',
        'WHERE ', p_field, ' = ?'
    );
    
    PREPARE stmt FROM @sql;
    SET @pv = p_value;
    EXECUTE stmt USING @pv;
    DEALLOCATE PREPARE stmt;
END$

DROP PROCEDURE IF EXISTS listOrders$
CREATE PROCEDURE listOrders ()
BEGIN
	SELECT 
		o.order_id,
        CONCAT(c.first_name, ' ', c.last_name) AS customer_name,
		o.order_date,
        FORMAT(o.order_price, 2) as order_price,
		COUNT(oi.order_item_id) AS pizza_count
	FROM `order` o
	JOIN customer c ON o.customer_id = c.customer_id
	JOIN order_item oi ON o.order_id = oi.order_id
	LEFT JOIN pizza p ON p.pizza_id = oi.pizza_id
	GROUP BY
		o.order_id,
		o.order_date,
        o.order_price,
		customer_name;
END$

DROP PROCEDURE IF EXISTS getOrderItemDetails$
CREATE PROCEDURE getOrderItemDetails (
	IN target_order_id INT
)
BEGIN
	SELECT 
		o.order_id,
        CONCAT(c.first_name, ' ', c.last_name) AS customer_name,
		o.order_date,
        FORMAT(o.order_price, 2) as order_price,
        p.pizza_name,
		GROUP_CONCAT(
			CONCAT(oic.action, ' ', ing.ingredient_name)
            SEPARATOR ', '
		) AS customization
        
	FROM `order` o
	JOIN customer c ON o.customer_id = c.customer_id
	JOIN order_item oi ON o.order_id = oi.order_id
	LEFT JOIN pizza p ON p.pizza_id = oi.pizza_id
    
    LEFT JOIN order_item_customization oic
		ON oi.order_item_id = oic.order_item_id
	LEFT JOIN ingredient ing
		ON oic.ingredient_id = ing.ingredient_id
    
    WHERE o.order_id = target_order_id
    
	GROUP BY oi.order_item_id
    ORDER BY oi.order_item_id;
END$

DROP PROCEDURE IF EXISTS getPizzaDetails$
CREATE PROCEDURE getPizzaDetails (
	IN target_pizza_id INT
)
BEGIN
	SELECT 
		p.pizza_id,
        p.pizza_name,
		FORMAT(p.pizza_price, 2) as pizza_price,
		GROUP_CONCAT(i.ingredient_id SEPARATOR ', ') AS ingredient_id_list
        
	FROM pizza p
	LEFT JOIN pizza_ingredient pi ON pi.pizza_id = p.pizza_id 
	LEFT JOIN ingredient i ON i.ingredient_id = pi.ingredient_id
    
    WHERE p.pizza_id = target_pizza_id
    
	GROUP BY 
		p.pizza_id,
        p.pizza_name,
        p.pizza_price;
END$

DROP PROCEDURE IF EXISTS getIngredientDetails$
CREATE PROCEDURE getIngredientDetails (
	IN target_ingredient_id INT
)
BEGIN
	SELECT 
		i.ingredient_id,
        i.ingredient_name,
        i.ingredient_desc,
        i.ingredient_price
        
	FROM ingredient i
    
    WHERE i.ingredient_id = target_ingredient_id;
END$

DROP PROCEDURE IF EXISTS getOrderDeliveryDetails$
CREATE PROCEDURE getOrderDeliveryDetails (
	IN target_order_id INT
)
BEGIN
	SELECT 
		d.delivery_id,
		d.order_id,
        CONCAT(co.first_name, ' ', co.last_name) AS courier_name,
        d.delivery_date,
        CONCAT(d.delivery_address, ', ', d.delivery_city, ', ', d.delivery_zip_code) as full_delivery_address,
        d.status,
        CONCAT(c.first_name, ' ', c.last_name) AS customer_name,
		o.order_date,
        FORMAT(o.order_price, 2) as order_price
        
	FROM delivery d
    JOIN `order` o ON o.order_id = d.order_id
	JOIN customer c ON c.customer_id = o.customer_id
    JOIN courier co ON co.courier_id = d.courier_id
    WHERE d.order_id = target_order_id;
END$

DROP PROCEDURE IF EXISTS findOrdersByDate$
CREATE PROCEDURE findOrdersByDate (
    IN from_date DATE,
    IN to_date DATE
)
BEGIN
    SELECT 
        o.order_id,
        CONCAT(c.first_name, ' ', c.last_name) AS customer_name,
        o.order_date,
        FORMAT(o.order_price, 2) as order_price,
        GROUP_CONCAT(p.pizza_name SEPARATOR ', ') AS pizzas
    FROM `order` o
    JOIN customer c ON o.customer_id = c.customer_id
    JOIN order_item oi ON o.order_id = oi.order_id
    JOIN pizza p ON p.pizza_id = oi.pizza_id
    WHERE o.order_date >= from_date
      AND o.order_date < to_date
    GROUP BY
        o.order_id,
        o.order_date,
        o.order_price,
        customer_name;
END$

DROP PROCEDURE IF EXISTS findOrdersByDateTime$
CREATE PROCEDURE findOrdersByDateTime (
	IN from_date DATETIME,
    IN to_date DATETIME
)
BEGIN
	SELECT 
		o.order_id,
        CONCAT(c.first_name, ' ', c.last_name) AS customer_name,
		o.order_date,
        FORMAT(o.order_price, 2) as order_price,
		GROUP_CONCAT(p.pizza_name SEPARATOR ', ') AS pizzas
	FROM `order` o
	JOIN customer c ON o.customer_id = c.customer_id
	JOIN order_item oi ON o.order_id = oi.order_id
	JOIN pizza p ON p.pizza_id = oi.pizza_id
    WHERE o.order_date BETWEEN from_date AND to_date
	GROUP BY
		o.order_id,
		o.order_date,
        o.order_price,
		customer_name;
END$

DROP PROCEDURE IF EXISTS findPizzasByIngredient $
CREATE PROCEDURE findPizzasByIngredient (
	IN ingredient_id INT
)
BEGIN
	SELECT 
		p.pizza_id,
		p.pizza_name,
        p.pizza_price,
		GROUP_CONCAT(i.ingredient_name SEPARATOR ', ') AS ingredients
	FROM pizza p
	JOIN pizza_ingredient pi ON p.pizza_id = pi.pizza_id
	JOIN ingredient i ON pi.ingredient_id = i.ingredient_id
    WHERE p.pizza_id IN (
		SELECT pi2.pizza_id
        FROM pizza_ingredient pi2
        JOIN ingredient i2 ON pi2.ingredient_id = i2.ingredient_id
        WHERE i2.ingredient_id = ingredient_id
    )
	GROUP BY p.pizza_id, p.pizza_name, p.pizza_price;
END$

DROP PROCEDURE IF EXISTS findOrdersByCustomerName$
CREATE PROCEDURE findOrdersByCustomerName (
	IN name_filter VARCHAR(50)
)
BEGIN
	SELECT 
		o.order_id,
        CONCAT(c.first_name, ' ', c.last_name) AS customer_name,
		o.order_date,
        FORMAT(o.order_price, 2) as order_price,
		GROUP_CONCAT(p.pizza_name SEPARATOR ', ') AS pizzas
	FROM `order` o
	JOIN customer c ON o.customer_id = c.customer_id
	JOIN order_item oi ON o.order_id = oi.order_id
	JOIN pizza p ON p.pizza_id = oi.pizza_id
    WHERE c.first_name = name_filter OR c.last_name = name_filter
	GROUP BY
		o.order_id,
		o.order_date,
		c.customer_id,
		customer_name;
END$

DROP PROCEDURE IF EXISTS findPopularIngredients$
CREATE PROCEDURE findPopularIngredients (
    IN min_count INT
)
BEGIN
    SELECT
        i.ingredient_id,
        i.ingredient_name,
        COUNT(*) AS used_in
    FROM ingredient i
    JOIN pizza_ingredient pi ON i.ingredient_id = pi.ingredient_id
    GROUP BY i.ingredient_id
    HAVING COUNT(*) >= min_count;
END$

DROP PROCEDURE IF EXISTS listMenu$
CREATE PROCEDURE listMenu ()
BEGIN
    SELECT
		p.pizza_id,
		p.pizza_name,
        FORMAT(p.pizza_price, 2) as pizza_price,
        GROUP_CONCAT(i.ingredient_id SEPARATOR ', ') AS ingredients
    FROM pizza p
    JOIN pizza_ingredient pi ON pi.pizza_id = p.pizza_id
    JOIN ingredient i ON i.ingredient_id = pi.ingredient_id
    GROUP BY p.pizza_id;
END$

DROP PROCEDURE IF EXISTS listDisplayMenu$
CREATE PROCEDURE listDisplayMenu ()
BEGIN
    SELECT
		p.pizza_id,
		p.pizza_name,
        GROUP_CONCAT(i.ingredient_name SEPARATOR ', ') AS ingredients,
        FORMAT(p.pizza_price, 2) as pizza_price
    FROM pizza p
    JOIN pizza_ingredient pi ON pi.pizza_id = p.pizza_id
    JOIN ingredient i ON i.ingredient_id = pi.ingredient_id
    GROUP BY p.pizza_id;
END$

DROP PROCEDURE IF EXISTS listIngredients$
CREATE PROCEDURE listIngredients ()
BEGIN
    SELECT
		i.ingredient_id,
        i.ingredient_name,
		FORMAT(i.ingredient_price, 2) as ingredient_price,
        i.ingredient_desc
    FROM ingredient i;
END$

DROP PROCEDURE IF EXISTS listStock$
CREATE PROCEDURE listStock ()
BEGIN
    SELECT
		inv.ingredient_id,
        i.ingredient_name,
        CONCAT(inv.current_stock, ' ', inv.unit) as amount
    FROM inventory inv
    JOIN ingredient i ON i.ingredient_id = inv.ingredient_id;
END$


DROP PROCEDURE IF EXISTS changePizzaName$
CREATE PROCEDURE changePizzaName (
    IN p_pizza_id INT,
    IN p_new_pizza_name VARCHAR(50)
)
BEGIN
    UPDATE pizza
    SET pizza_name = p_new_pizza_name
    WHERE pizza_id = p_pizza_id;
END$

DROP PROCEDURE IF EXISTS changePizzaPrice$
CREATE PROCEDURE changePizzaPrice (
    IN p_pizza_id INT,
    IN p_new_pizza_price DOUBLE
)
BEGIN
    UPDATE pizza
    SET pizza_price = p_new_pizza_price
    WHERE pizza_id = p_pizza_id;
END$

DROP PROCEDURE IF EXISTS addPizzaIngredient$
CREATE PROCEDURE addPizzaIngredient (
    IN p_pizza_id INT,
    IN p_ingredient_id INT
)
BEGIN
    INSERT IGNORE INTO pizza_ingredient (pizza_id, ingredient_id)
    VALUES (p_pizza_id, p_ingredient_id);
END$

DROP PROCEDURE IF EXISTS removePizzaIngredient$
CREATE PROCEDURE removePizzaIngredient (
    IN p_pizza_id INT,
    IN p_ingredient_id INT
)
BEGIN
    DELETE FROM pizza_ingredient pi
    WHERE pi.pizza_id = p_pizza_id
      AND ingredient_id = p_ingredient_id;
END$

DROP PROCEDURE IF EXISTS removePizza$
CREATE PROCEDURE removePizza (
    IN p_pizza_id INT
)
BEGIN
    DELETE FROM pizza_ingredient pi
    WHERE pi.pizza_id = p_pizza_id;
    
    DELETE FROM pizza p
    WHERE p.pizza_id = p_pizza_id;
END$

DROP PROCEDURE IF EXISTS createPizza$
CREATE PROCEDURE createPizza (
    IN p_name VARCHAR(50),
    IN p_price DOUBLE(5, 1),
    OUT new_id INT
)
BEGIN
	INSERT INTO pizza (pizza_name, pizza_price) VALUES
    (p_name, p_price);
    
    SET new_id = LAST_INSERT_ID();
END$

DELIMITER ;
