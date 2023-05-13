CREATE TABLE account (id integer auto_increment PRIMARY KEY, user_id VARCHAR(255), first_name VARCHAR(255), last_name VARCHAR(255), routing_number VARCHAR(255), nin VARCHAR(255), account_number VARCHAR(255), constraint uq1 unique (user_id));

CREATE TABLE transaction (id integer auto_increment PRIMARY KEY, user_id VARCHAR(255), account_id integer, amount NUMERIC(20, 2), fee NUMERIC(20, 2), currency  VARCHAR(255),  wallet_transaction_id integer,
    creation_date date, update_date date, type VARCHAR(255), payment_info_id VARCHAR(255), status VARCHAR(255));