DROP USER IF EXISTS 'portfolio_app'@'%';
DROP DATABASE IF EXISTS portfolio;

CREATE DATABASE portfolio;

USE portfolio;

CREATE USER IF NOT EXISTS 'portfolio_app'@'%' IDENTIFIED BY 'password';

GRANT INSERT, UPDATE, DELETE, SELECT ON portfolio.* TO 'portfolio_app'@'%';

CREATE TABLE portfolio_user(
    id                        BIGINT            NOT NULL  PRIMARY KEY  AUTO_INCREMENT,
    username                  VARCHAR(50)       NOT NULL,
    password                  VARCHAR(72)       NOT NULL,
    failed_password_attempts  TINYINT UNSIGNED  NOT NULL  DEFAULT 0,
    locked_on                 DATETIME,
    authorized_until          DATE,
    enabled                   BOOLEAN           NOT NULL  DEFAULT true,
    admin                     BOOLEAN           NOT NULL,
    UNIQUE (username)
);

CREATE TABLE login_attempt(
    id              BIGINT                      NOT NULL  PRIMARY KEY  AUTO_INCREMENT,
    user_id         BIGINT                      NOT NULL,
    outcome         ENUM('SUCCESS', 'FAILURE')  NOT NULL,
    failure_reason  ENUM('ACCOUNT_DISABLED', 'ACCOUNT_EXPIRED', 'ACCOUNT_LOCKED', 'BAD_CREDENTIALS'),
    timeStamp       DATETIME                    NOT NULL,
    FOREIGN KEY (user_id) REFERENCES portfolio_user(id)
);