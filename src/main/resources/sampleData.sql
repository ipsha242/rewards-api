-- Clear any existing records to keep the startup clean
DELETE FROM TRANSACTION;


-- John
INSERT INTO TRANSACTION(customer_id, customer_name, amount, transaction_date)
VALUES(1, 'John', 120.0, '2026-03-15');

INSERT INTO TRANSACTION(customer_id, customer_name, amount, transaction_date)
VALUES(1, 'John', 75.0, '2026-04-10');

INSERT INTO TRANSACTION(customer_id, customer_name, amount, transaction_date)
VALUES(1, 'John', 220.0, '2026-05-05');

-- David
INSERT INTO TRANSACTION(customer_id, customer_name, amount, transaction_date)
VALUES(2, 'David', 45.0, '2026-03-08');

INSERT INTO TRANSACTION(customer_id, customer_name, amount, transaction_date)
VALUES(2, 'David', 180.0, '2026-05-22');

-- Lucas
INSERT INTO TRANSACTION(customer_id, customer_name, amount, transaction_date)
VALUES(3, 'Lucas', 250.0, '2026-05-02');

-- Sarah
INSERT INTO TRANSACTION(customer_id, customer_name, amount, transaction_date)
VALUES(4, 'Sarah', 150.0, '2026-03-20');

INSERT INTO TRANSACTION(customer_id, customer_name, amount, transaction_date)
VALUES(4, 'Sarah', 90.0, '2026-04-25');

-- Sharon
INSERT INTO TRANSACTION(customer_id, customer_name, amount, transaction_date)
VALUES(5, 'Sharon', 130.0, '2026-03-06');

INSERT INTO TRANSACTION(customer_id, customer_name, amount, transaction_date)
VALUES(5, 'Sharon', 70.0, '2026-04-14');

INSERT INTO TRANSACTION(customer_id, customer_name, amount, transaction_date)
VALUES(5, 'Sharon', 300.0, '2026-05-28');
