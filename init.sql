SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";

DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS posts;
DROP TABLE IF EXISTS comments;

CREATE TABLE users (
id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
email VARCHAR(30) UNIQUE,
username VARCHAR(20) UNIQUE
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

INSERT INTO users (id,username, email) VALUES (1, "John", "john@john.com");
INSERT INTO users (id,username, email) VALUES (2, "John1", "john1@john.com");
INSERT INTO users (id,username, email) VALUES (3, "John2", "john2@john.com");
INSERT INTO users (id,username, email) VALUES (4, "John3", "john3@john.com");

CREATE TABLE posts (
id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
user_id BIGINT(20),
title VARCHAR(50),
body VARCHAR(500),
location VARCHAR(50),
date DATE
)  ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE comments (
id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
user_id BIGINT(20),
post_id BIGINT(20),
body VARCHAR(500)
)  ENGINE=MyISAM DEFAULT CHARSET=latin1;

INSERT INTO comments (id, user_id, post_id, body) VALUES (1, 1, 1, "first comment on the first post!");
INSERT INTO comments (id, user_id, post_id, body) VALUES (2, 1, 1, "second comment on the first post!");
INSERT INTO comments (id, user_id, post_id, body) VALUES (3, 1, 1, "third comment on the first post!");
INSERT INTO comments (id, user_id, post_id, body) VALUES (4, 1, 1, "fourth comment on the first post!");