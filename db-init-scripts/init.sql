CREATE DATABASE IF NOT EXISTS shop;
CREATE USER IF NOT EXISTS 'shop_admin'@'%' IDENTIFIED BY 'passpass';
GRANT ALL PRIVILEGES ON shop.* TO 'shop_admin'@'%';
FLUSH PRIVILEGES;
