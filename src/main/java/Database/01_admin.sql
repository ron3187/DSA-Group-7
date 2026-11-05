-- =====================================================================
-- 01_admin.sql  -  Self-refreshing database setup for the Fitness Tracker
--
-- Run this FIRST. It is safe to run again and again: it drops the old
-- tables, re-creates them empty, and inserts ONLY the default admin.
--   Default admin login:  admin / admin123
-- (passwords are stored as SHA-256 hashes, never as plain text)
--
-- Then run 02_customers.sql to load the sample data.
-- =====================================================================

CREATE DATABASE IF NOT EXISTS fitness_tracker;
USE fitness_tracker;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS goals;
DROP TABLE IF EXISTS meals;
DROP TABLE IF EXISTS workouts;
DROP TABLE IF EXISTS users;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE users (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    username  VARCHAR(50)  NOT NULL UNIQUE,
    password  VARCHAR(64)  NOT NULL,              -- SHA-256 hex
    name      VARCHAR(100) NOT NULL,
    role      VARCHAR(10)  NOT NULL DEFAULT 'MEMBER'   -- ADMIN or MEMBER
);

CREATE TABLE workouts (
    workout_id      INT AUTO_INCREMENT PRIMARY KEY,
    user_id         INT          NOT NULL,
    name            VARCHAR(100) NOT NULL,
    duration        INT          NOT NULL,         -- minutes
    calories_burned INT          NOT NULL,
    workout_date    DATE         NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE meals (
    meal_id          INT AUTO_INCREMENT PRIMARY KEY,
    user_id          INT          NOT NULL,
    name             VARCHAR(100) NOT NULL,
    calories         INT          NOT NULL,
    protein          INT          NOT NULL DEFAULT 0,   -- grams
    carbs            INT          NOT NULL DEFAULT 0,   -- grams
    fat              INT          NOT NULL DEFAULT 0,   -- grams
    nutritional_info VARCHAR(255) NOT NULL DEFAULT '',
    meal_date        DATE         NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE goals (
    goal_id      INT AUTO_INCREMENT PRIMARY KEY,
    user_id      INT          NOT NULL,
    title        VARCHAR(150) NOT NULL,
    goal_type    VARCHAR(20)  NOT NULL,   -- BURN_CALORIES, WORKOUT_MINUTES, EAT_LIMIT
    target_value INT          NOT NULL,
    start_date   DATE         NOT NULL,
    deadline     DATE         NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- The ONLY row this script inserts: the default admin (id = 1)
INSERT INTO users (id, username, password, name, role)
VALUES (1, 'admin', SHA2('admin123', 256), 'Administrator', 'ADMIN');
