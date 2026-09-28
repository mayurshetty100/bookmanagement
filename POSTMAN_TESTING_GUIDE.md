# Book Management API - Postman Testing Guide

This guide explains how to start the project and test every API operation using Postman in VS Code or the Postman desktop application.

## 1. Required software

Before testing, make sure these are available:

- Java 17 or later
- MySQL Server
- MySQL Workbench
- VS Code with the Postman extension, or the Postman desktop application

The database must be created before starting the application:

```text
Database: bookstore_db
Host: localhost
Port: 3306
Username: root
```

The sample database script is available at:

```text
database/bookstore_seed.sql
```

## 2. Verify the MySQL database

Open MySQL Workbench and run:

```sql
USE bookstore_db;
SELECT * FROM book;
```

You should see the sample books:

```text
Clean Code
Effective Java
The Pragmatic Programmer
Head First Java
Design Patterns
```

If the table does not exist, open and execute:

```text
C:\flutter1\bookmanagement\database\bookstore_seed.sql
```

## 3. Configure the database password

The application reads the MySQL password from the `DB_PASSWORD` environment variable.

Open PowerShell in VS Code and run:

```powershell
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "your_mysql_password"
```

Replace `your_mysql_password` with the password used for the `Local instance MySQL80` connection in MySQL Workbench.

If your MySQL root account has no password, use:

```powershell
$env:DB_PASSWORD = ""
```

These environment variables apply only to the current PowerShell terminal. Set them again if you open a new terminal.

## 4. Start the Spring Boot project

Open the project folder in VS Code:

```powershell
cd C:\flutter1\bookmanagement
```

Run the automated tests first:

```powershell
.\mvnw.cmd test
```

Expected result:

```text
Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Start the application:

```powershell
.\mvnw.cmd spring-boot:run
```

Wait for this message:

```text
Started BookmanagementApplication
```

Keep this terminal open. The API runs at:

```text
http://localhost:8080
```

Open a second VS Code terminal for other commands, or use the Postman extension.

## 5. Open Postman in VS Code

If the Postman extension is installed:

1. Open the Postman panel from the VS Code Activity Bar.
2. Create a new request.
3. Select the HTTP method.
4. Enter the URL.
5. Select **Body > raw > JSON** for `POST` and `PUT` requests.
6. Paste the JSON body.
7. Click **Send**.
8. Check the status code and response body.

If the Postman extension is not available, install it from the VS Code Extensions panel or use the Postman desktop application. The request details in this guide are the same.

## 6. Base URL

Use this base URL for every request:

```text
http://localhost:8080
```

The API resource is:

```text
/books
```

## 7. Recommended testing order

Run the requests in this order:

1. Get all books
2. Create a book
3. Get the created book by ID
4. Update the created book
5. Find books by author
6. Find books by publisher
7. Find books by price range
8. Test an invalid price range
9. Delete the created book
10. Confirm that the deleted book returns `404 Not Found`

The create response supplies the ID needed by the get, update, and delete requests.

## 8. Test 1 - Get all books

### Request

```text
Method: GET
URL: http://localhost:8080/books
```

No request body is required.

### Expected response

Status:

```text
200 OK
```

Example response:

```json
[
  {
    "id": 1,
    "title": "Clean Code",
    "author": "Robert C. Martin",
    "publisher": "Prentice Hall",
    "price": 45.5,
    "publicationYear": 2008
  },
  {
    "id": 2,
    "title": "Effective Java",
    "author": "Joshua Bloch",
    "publisher": "Addison-Wesley",
    "price": 55.0,
    "publicationYear": 2018
  }
]
```

The exact IDs and order can differ.

## 9. Test 2 - Create a book

### Request

```text
Method: POST
URL: http://localhost:8080/books
```

Headers:

```text
Content-Type: application/json
```

Body:

```json
{
  "title": "Spring in Action",
  "author": "Craig Walls",
  "publisher": "Manning",
  "price": 52.5,
  "publicationYear": 2022
}
```

### Expected response

Status:

```text
201 Created
```

Example:

```json
{
  "id": 6,
  "title": "Spring in Action",
  "author": "Craig Walls",
  "publisher": "Manning",
  "price": 52.5,
  "publicationYear": 2022
}
```

Record the returned `id`. In the examples below, replace `{id}` with this value.

## 10. Test 3 - Get one book

### Request

```text
Method: GET
URL: http://localhost:8080/books/{id}
```

Example for ID 6:

```text
http://localhost:8080/books/6
```

### Expected response

Status:

```text
200 OK
```

The response contains the book with the requested ID.

## 11. Test 4 - Update a book

### Request

```text
Method: PUT
URL: http://localhost:8080/books/{id}
```

Example:

```text
http://localhost:8080/books/6
```

Headers:

```text
Content-Type: application/json
```

Body:

```json
{
  "title": "Spring in Action - Updated",
  "author": "Craig Walls",
  "publisher": "Manning Publications",
  "price": 59.99,
  "publicationYear": 2023
}
```

### Expected response

Status:

```text
200 OK
```

The response contains the updated values and keeps the same ID.

## 12. Test 5 - Find books by author

### Request

```text
Method: GET
URL: http://localhost:8080/books/author/Craig%20Walls
```

The space in `Craig Walls` is encoded as `%20`.

### Expected response

Status:

```text
200 OK
```

The response is an array of books whose author is `Craig Walls`.

## 13. Test 6 - Find books by publisher

### Request

```text
Method: GET
URL: http://localhost:8080/books/publisher/Manning%20Publications
```

### Expected response

Status:

```text
200 OK
```

The response is an array of books whose publisher is `Manning Publications`.

## 14. Test 7 - Find books by price range

### Request

```text
Method: GET
URL: http://localhost:8080/books/price-range?min=40&max=60
```

No request body is required.

### Expected response

Status:

```text
200 OK
```

The response contains books priced from `40` through `60`, including both boundaries.

## 15. Test 8 - Invalid price range

This confirms the controller validation.

### Request

```text
Method: GET
URL: http://localhost:8080/books/price-range?min=100&max=20
```

### Expected response

Status:

```text
400 Bad Request
```

The request is rejected because the minimum price is greater than the maximum price.

## 16. Test 9 - Delete a book

### Request

```text
Method: DELETE
URL: http://localhost:8080/books/{id}
```

Example:

```text
http://localhost:8080/books/6
```

No request body is required.

### Expected response

Status:

```text
204 No Content
```

The response body is empty.

## 17. Test 10 - Confirm deleted book

### Request

```text
Method: GET
URL: http://localhost:8080/books/{id}
```

Use the ID deleted in the previous step.

### Expected response

Status:

```text
404 Not Found
```

## 18. Additional not-found tests

These requests verify the missing-record behavior without changing data:

### Missing GET

```text
GET http://localhost:8080/books/99999
```

Expected: `404 Not Found`

### Missing PUT

```text
PUT http://localhost:8080/books/99999
```

Body:

```json
{
  "title": "Missing Book",
  "author": "Unknown Author",
  "publisher": "Unknown Publisher",
  "price": 10.0,
  "publicationYear": 2024
}
```

Expected: `404 Not Found`

### Missing DELETE

```text
DELETE http://localhost:8080/books/99999
```

Expected: `404 Not Found`

## 19. Suggested Postman collection names

Create a collection named:

```text
Book Management API
```

Add these requests:

```text
01 - Get All Books
02 - Create Book
03 - Get Book By ID
04 - Update Book
05 - Find By Author
06 - Find By Publisher
07 - Find By Price Range
08 - Invalid Price Range
09 - Delete Book
10 - Confirm Deleted Book
11 - Missing Book
```

## 20. Screenshot checklist for the report

Take screenshots where the request method, complete URL, status code, and response are visible.

1. MySQL Workbench showing the `bookstore_db` schema.
2. MySQL Workbench showing `SELECT * FROM book;` with sample data.
3. VS Code terminal showing `BUILD SUCCESS` after tests.
4. VS Code terminal showing `Started BookmanagementApplication`.
5. `GET /books` with `200 OK`.
6. `POST /books` with `201 Created` and the JSON request body.
7. `GET /books/{id}` with `200 OK`.
8. `PUT /books/{id}` with `200 OK` and changed values.
9. `GET /books/author/{author}` with filtered data.
10. `GET /books/publisher/{publisher}` with filtered data.
11. `GET /books/price-range` with filtered data.
12. Invalid price range with `400 Bad Request`.
13. `DELETE /books/{id}` with `204 No Content`.
14. Request for the deleted ID with `404 Not Found`.

Do not include your MySQL password or other private credentials in screenshots.

## 21. Troubleshooting

### Connection refused

Make sure the MySQL80 service is running and MySQL Workbench can connect to `localhost:3306`.

### Access denied for root

Set the correct password in the same PowerShell terminal before starting Spring Boot:

```powershell
$env:DB_PASSWORD = "your_mysql_password"
.\mvnw.cmd spring-boot:run
```

### Whitelabel error page or 404

Check that:

1. Spring Boot is running.
2. The URL starts with `http://localhost:8080`.
3. The endpoint path is exactly `/books`.
4. The HTTP method is correct.

### Empty list response

Run this in MySQL Workbench:

```sql
USE bookstore_db;
SELECT * FROM book;
```

Also confirm that the application is using `bookstore_db`, not another schema.
