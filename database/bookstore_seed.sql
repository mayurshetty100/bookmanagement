-- Create the application database if it does not already exist.
CREATE DATABASE IF NOT EXISTS bookstore_db;

-- Select the database for the following table and data statements.
USE bookstore_db;

-- Create the table so sample data can be inserted before the application starts.
-- Hibernate will keep this table aligned with the Book entity.
CREATE TABLE IF NOT EXISTS book (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(255),
    author VARCHAR(255),
    publisher VARCHAR(255),
    price DOUBLE NOT NULL,
    publication_year INT NOT NULL,
    PRIMARY KEY (id)
);

-- Add sample records for Postman demonstrations.
INSERT INTO book (title, author, publisher, price, publication_year)
VALUES
    ('Clean Code', 'Robert C. Martin', 'Prentice Hall', 45.50, 2008),
    ('Effective Java', 'Joshua Bloch', 'Addison-Wesley', 55.00, 2018),
    ('The Pragmatic Programmer', 'David Thomas', 'Addison-Wesley', 49.99, 2019),
    ('Head First Java', 'Kathy Sierra', 'O''Reilly Media', 39.95, 2005),
    ('Design Patterns', 'Erich Gamma', 'Addison-Wesley', 60.00, 1994);
