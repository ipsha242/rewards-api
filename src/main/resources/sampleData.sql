-- Clear any existing records to keep the startup clean
DELETE FROM TRANSACTION;
DELETE FROM CUSTOMER;

-- Customers
INSERT INTO CUSTOMER(customer_id, customer_name)
VALUES (1, 'John');

INSERT INTO CUSTOMER(customer_id, customer_name)
VALUES (2, 'David');

INSERT INTO CUSTOMER(customer_id, customer_name)
VALUES (3, 'Lucas');

INSERT INTO CUSTOMER(customer_id, customer_name)
VALUES (4, 'Sarah');

INSERT INTO CUSTOMER(customer_id, customer_name)
VALUES (5, 'Sharon');

-- John
INSERT INTO TRANSACTION(customer_id, amount, transaction_date)
VALUES(1, 120.0, CURRENT_DATE - 88);

INSERT INTO TRANSACTION(customer_id, amount, transaction_date)
VALUES(1, 75.0, CURRENT_DATE - 62);

INSERT INTO TRANSACTION(customer_id, amount, transaction_date)
VALUES(1, 220.0, CURRENT_DATE - 37);

-- David
INSERT INTO TRANSACTION(customer_id, amount, transaction_date)
VALUES(2, 45.0, CURRENT_DATE - 95);

INSERT INTO TRANSACTION(customer_id, amount, transaction_date)
VALUES(2, 180.0, CURRENT_DATE - 20);

-- Lucas
INSERT INTO TRANSACTION(customer_id, amount, transaction_date)
VALUES(3, 250.0, CURRENT_DATE - 40);

-- Sarah
INSERT INTO TRANSACTION(customer_id, amount, transaction_date)
VALUES(4, 150.0, CURRENT_DATE - 83);

INSERT INTO TRANSACTION(customer_id, amount, transaction_date)
VALUES(4, 90.0, CURRENT_DATE - 47);

-- Sharon
INSERT INTO TRANSACTION(customer_id, amount, transaction_date)
VALUES(5, 130.0, CURRENT_DATE - 70);

INSERT INTO TRANSACTION(customer_id, amount, transaction_date)
VALUES(5, 70.0, CURRENT_DATE - 58);

INSERT INTO TRANSACTION(customer_id, amount, transaction_date)
VALUES(5, 300.0, CURRENT_DATE - 14);