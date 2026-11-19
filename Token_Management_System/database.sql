-- =====================================================
-- Token Management System - database setup script
-- Run this once in MySQL (phpMyAdmin > SQL tab, or:
--   mysql -u root -p < database.sql
-- =====================================================

CREATE DATABASE IF NOT EXISTS token_db;
USE token_db;

-- ----------------------------------------------------
-- users : everyone who logs in (role decides the menu)
-- ----------------------------------------------------
CREATE TABLE users (
  user_id       INT AUTO_INCREMENT PRIMARY KEY,
  full_name     VARCHAR(60)  NOT NULL,
  username      VARCHAR(30)  NOT NULL UNIQUE,
  password_hash CHAR(64)     NOT NULL,          -- sha256 of the password
  role          ENUM('USER','ADMIN') NOT NULL DEFAULT 'USER',
  phone         VARCHAR(15),
  created_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- ----------------------------------------------------
-- services : the counters issue tokens for these
-- code is the letter used in the token number (A-001)
-- ----------------------------------------------------
CREATE TABLE services (
  service_id  INT AUTO_INCREMENT PRIMARY KEY,
  name        VARCHAR(50) NOT NULL,
  code        CHAR(1)     NOT NULL,
  avg_minutes INT         NOT NULL DEFAULT 5,  -- rough time per customer
  active      TINYINT(1)  NOT NULL DEFAULT 1
);

-- ----------------------------------------------------
-- counters : service desks, each one handles one service
-- ----------------------------------------------------
CREATE TABLE counters (
  counter_id    INT AUTO_INCREMENT PRIMARY KEY,
  name          VARCHAR(30) NOT NULL,
  operator_name VARCHAR(50),
  service_id    INT,
  status        ENUM('CLOSED','OPEN') NOT NULL DEFAULT 'CLOSED',
  FOREIGN KEY (service_id) REFERENCES services(service_id)
);

-- ----------------------------------------------------
-- tokens : one row per token issued
-- ----------------------------------------------------
CREATE TABLE tokens (
  token_id   INT AUTO_INCREMENT PRIMARY KEY,
  token_no   VARCHAR(10) NOT NULL,               -- e.g. A-001
  user_id    INT         NOT NULL,
  service_id INT         NOT NULL,
  counter_id INT         NULL,
  status     ENUM('WAITING','CALLED','SERVED','CANCELLED','NOSHOW')
             NOT NULL DEFAULT 'WAITING',
  issued_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  called_at  DATETIME    NULL,
  done_at    DATETIME    NULL,
  FOREIGN KEY (user_id)    REFERENCES users(user_id),
  FOREIGN KEY (service_id) REFERENCES services(service_id),
  FOREIGN KEY (counter_id) REFERENCES counters(counter_id)
);

-- ----------------------------------------------------
-- sample data
-- default logins:  admin / admin123   and   user / user123
-- ----------------------------------------------------
INSERT INTO users (full_name, username, password_hash, role, phone) VALUES
('System Admin', 'admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'ADMIN', '9800000001'),
('John  Walker', 'user',  'e606e38b0d8c19b24cf0ee3808183162ea7cd63ff7912dbb22b5e803286b4446', 'USER',  '9800000002');

INSERT INTO services (name, code, avg_minutes, active) VALUES
('New Account Opening', 'A', 8, 1),
('Cash Deposit',        'B', 3, 1),
('Loan Enquiry',        'C', 12, 1),
('Passbook Update',     'D', 4, 1);

INSERT INTO counters (name, operator_name, service_id, status) VALUES
('Counter 1', 'Meena K.',   1, 'OPEN'),
('Counter 2', 'Ravi S.',    2, 'OPEN'),
('Counter 3', 'Anita P.',   3, 'CLOSED'),
('Counter 4', 'Deepak R.',  4, 'CLOSED');
