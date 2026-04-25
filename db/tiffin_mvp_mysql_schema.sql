-- =============================================================================
-- MealMatch MVP Tiffin Database Schema (MySQL)
-- =============================================================================

CREATE DATABASE IF NOT EXISTS mealmatch CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE mealmatch;

CREATE TABLE IF NOT EXISTS users (
    id           VARCHAR(36)  NOT NULL DEFAULT (UUID()),
    name         VARCHAR(255) NOT NULL,
    email        VARCHAR(255) NOT NULL UNIQUE,
    phone        VARCHAR(20),
    dietary_tags TEXT,
    location     VARCHAR(255),
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    password_hash VARCHAR(255) NOT NULL,
    role         VARCHAR(20)  NOT NULL DEFAULT 'USER',
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS providers (
    id           VARCHAR(36)  NOT NULL DEFAULT (UUID()),
    name         VARCHAR(255) NOT NULL,
    email        VARCHAR(255) NOT NULL UNIQUE,
    phone        VARCHAR(20),
    location     VARCHAR(255),
    cuisine_type VARCHAR(100),
    rating       DOUBLE       NOT NULL DEFAULT 0.0,
    is_active    TINYINT(1)   NOT NULL DEFAULT 1,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS menu_items (
    id           VARCHAR(36)    NOT NULL DEFAULT (UUID()),
    provider_id  VARCHAR(36)    NOT NULL,
    name         VARCHAR(255)   NOT NULL,
    meal_type    VARCHAR(50),
    dietary_tags TEXT,
    price        DECIMAL(10,2)  NOT NULL,
    is_available TINYINT(1)     NOT NULL DEFAULT 1,
    PRIMARY KEY (id),
    CONSTRAINT fk_menu_item_provider FOREIGN KEY (provider_id) REFERENCES providers(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS subscriptions (
    id               VARCHAR(36)  NOT NULL DEFAULT (UUID()),
    user_id          VARCHAR(36)  NOT NULL,
    provider_id      VARCHAR(36)  NOT NULL,
    menu_item_id     VARCHAR(36)  NOT NULL,
    days_of_week     VARCHAR(100),
    delivery_time    VARCHAR(10),
    delivery_address TEXT,
    status           VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    start_date       DATE,
    end_date         DATE,
    PRIMARY KEY (id),
    CONSTRAINT fk_sub_user     FOREIGN KEY (user_id)      REFERENCES users(id),
    CONSTRAINT fk_sub_provider FOREIGN KEY (provider_id)  REFERENCES providers(id),
    CONSTRAINT fk_sub_item     FOREIGN KEY (menu_item_id) REFERENCES menu_items(id)
);

CREATE TABLE IF NOT EXISTS reviews (
    id          VARCHAR(36) NOT NULL DEFAULT (UUID()),
    user_id     VARCHAR(36) NOT NULL,
    provider_id VARCHAR(36) NOT NULL,
    rating      INT         NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment     TEXT,
    created_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_review_user     FOREIGN KEY (user_id)     REFERENCES users(id),
    CONSTRAINT fk_review_provider FOREIGN KEY (provider_id) REFERENCES providers(id)
);
