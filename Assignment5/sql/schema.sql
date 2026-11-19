-- Run this in MySQL Workbench / mysql CLI ONLY if you use MySQL (DbConfig.USE_H2 = false).
-- The Java code also creates the tables automatically if they do not exist.
CREATE DATABASE IF NOT EXISTS busdb;
USE busdb;

CREATE TABLE IF NOT EXISTS bus_pass (
    pass_id  VARCHAR(12) PRIMARY KEY,
    reg_no   VARCHAR(15) NOT NULL,
    name     VARCHAR(60) NOT NULL,
    route_no INT         NOT NULL,
    amount   DOUBLE      NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
    id  VARCHAR(20) PRIMARY KEY,
    pwd VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS student (
    reg_no VARCHAR(15) PRIMARY KEY,
    name   VARCHAR(60),
    dept   VARCHAR(30),
    cgpa   DOUBLE
);

CREATE TABLE IF NOT EXISTS wallet (
    wallet_id VARCHAR(20) PRIMARY KEY,
    balance   DOUBLE NOT NULL
);
