# Book Management REST API

## 1. Project overview

This project is a Spring Boot REST API for managing books. It uses:

- Java 17
- Spring Boot 4.1.1
- Spring Web MVC for HTTP endpoints
- Spring Data JPA for database access
- MySQL for the runtime database
- H2 for automated tests
- Maven Wrapper for repeatable Maven commands

The API supports creating, reading, updating, deleting, and filtering books.

## 2. Project structure

```text
src/
├── main/
│   ├── java/com/example/bookmanagement/
│   │   ├── BookmanagementApplication.java
│   │   ├── controller/BookController.java
│   │   ├── entity/Book.java
│   │   └── repository/BookRepository.java
│   └── resources/application.properties
└── test/
    ├── java/com/example/bookmanagement/BookmanagementApplicationTests.java
    └── resources/application-test.properties
```

### What each layer does

1. **Entity**: `Book` represents one row in the database.
2. **Repository**: `BookRepository` provides database operations through Spring Data JPA.
3. **Controller**: `BookController` receives HTTP requests and returns HTTP responses.
4. **Configuration**: `application.properties` contains the MySQL connection settings.
5. **Tests**: the test class verifies the application and every endpoint using an in-memory H2 database.

## 3. Prerequisites

Install the following before running the project:

- JDK 17 or later
- MySQL Server
- Git
- A terminal such as PowerShell
- Postman or another REST client for manual endpoint testing

Verify Java and Git:

```powershell
java -version
git --version
```

The output should show Java 17 or a newer compatible JDK and a Git version.

## 4. Clone and open the project

Replace the URL with your GitHub repository URL after pushing this project:

```powershell
git clone https://github.com/<your-username>/<your-repository>.git
cd <your-repository>
```

If the project is already on your computer:

```powershell
cd C:\flutter1\bookmanagement
```

## 5. Create the MySQL database

Open MySQL Workbench or the MySQL command-line client and run:

```sql
CREATE DATABASE bookstore_db;
```

The application uses the `bookstore_db` database. The `book` table is created or updated automatically by Hibernate because `spring.jpa.hibernate.ddl-auto=update` is configured.

## 6. Configure database credentials

The default configuration expects:

| Setting | Default |
|---|---|
| Database host | `localhost` |
| Database port | `3306` |
| Database name | `bookstore_db` |
| Database username | `root` |
| Database password | The value configured in `application.properties` |

For a different MySQL installation, use environment variables instead of changing source code:

```powershell
$env:DB_HOST = "localhost"
$env:DB_PORT = "3306"
$env:DB_NAME = "bookstore_db"
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "your-mysql-password"
```

Do not commit real passwords to GitHub. The `.gitignore` file excludes local environment files and other common secret locations.

## 7. Build the project

The Maven Wrapper downloads or uses the correct Maven version automatically.

On Windows PowerShell:

```powershell
.\mvnw.cmd clean compile
```

On macOS or Linux:

```bash
./mvnw clean compile
```

Expected result:

```text
BUILD SUCCESS
```

## 8. Run automated tests

Run all tests:

```powershell
.\mvnw.cmd test
```

The tests use H2, so MySQL does not need to be running for this command.

Expected result:

```text
Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

The test profile is defined in `src/test/resources/application-test.properties`. It creates a temporary in-memory database and removes it after the tests finish.

## 9. Start the application

Start the application with Maven:

```powershell
.\mvnw.cmd spring-boot:run
```

Or build and run the packaged JAR:

```powershell
.\mvnw.cmd -DskipTests package
java -jar target\bookmanagement-0.0.1-SNAPSHOT.jar
```

Expected startup output includes:

```text
Started BookmanagementApplication
```

The API is available at:

```text
http://localhost:8080
```

Keep the application terminal running while sending requests from Postman or another terminal.

## 10. API endpoints

All endpoints use the base path `/books`.

### 10.1 Create a book

```powershell
curl.exe -X POST "http://localhost:8080/books" `
  -H "Content-Type: application/json" `
  -d '{\"title\":\"Clean Code\",\"author\":\"Robert C. Martin\",\"publisher\":\"Prentice Hall\",\"price\":45.5,\"publicationYear\":2008}'
```

Expected status: `201 Created`

Expected response:

```json
{
  "id": 1,
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "publisher": "Prentice Hall",
  "price": 45.5,
  "publicationYear": 2008
}
```

The database generates the `id`.

### 10.2 Get all books

```powershell
curl.exe "http://localhost:8080/books"
```

Expected status: `200 OK`

Expected response shape:

```json
[
  {
    "id": 1,
    "title": "Clean Code",
    "author": "Robert C. Martin",
    "publisher": "Prentice Hall",
    "price": 45.5,
    "publicationYear": 2008
  }
]
```

### 10.3 Get one book by ID

```powershell
curl.exe "http://localhost:8080/books/1"
```

Expected status for an existing book: `200 OK`.

Expected status for a missing book: `404 Not Found`.

### 10.4 Update a book

```powershell
curl.exe -X PUT "http://localhost:8080/books/1" `
  -H "Content-Type: application/json" `
  -d '{\"title\":\"Clean Code - Updated\",\"author\":\"Robert C. Martin\",\"publisher\":\"Prentice Hall\",\"price\":50.0,\"publicationYear\":2008}'
```

Expected status: `200 OK`

The response contains the updated book.

### 10.5 Delete a book

```powershell
curl.exe -X DELETE "http://localhost:8080/books/1"
```

Expected status when deletion succeeds: `204 No Content`.

Expected status when the ID does not exist: `404 Not Found`.

### 10.6 Find books by author

```powershell
curl.exe "http://localhost:8080/books/author/Robert%20C.%20Martin"
```

Expected status: `200 OK`

The response is an array containing books whose author exactly matches the path value.

### 10.7 Find books by publisher

```powershell
curl.exe "http://localhost:8080/books/publisher/Prentice%20Hall"
```

Expected status: `200 OK`

The response is an array containing books whose publisher exactly matches the path value.

### 10.8 Find books by price range

```powershell
curl.exe "http://localhost:8080/books/price-range?min=20&max=60"
```

Expected status: `200 OK`.

The minimum and maximum values are inclusive because the repository uses `findByPriceBetween`.

If `min` is greater than `max`:

```powershell
curl.exe "http://localhost:8080/books/price-range?min=60&max=20"
```

Expected status: `400 Bad Request`.

## 11. Postman setup

For each request:

1. Open Postman.
2. Select the HTTP method.
3. Enter the endpoint URL.
4. For `POST` and `PUT`, select **Body > raw > JSON**.
5. Add the JSON request body shown above.
6. Click **Send**.
7. Record the status code and response body.

Recommended Postman request names:

```text
Create Book
Get All Books
Get Book By ID
Update Book
Delete Book
Find Books By Author
Find Books By Publisher
Find Books By Price Range
Invalid Price Range
Missing Book
```

## 12. Screenshots required for the report

Take clear screenshots that show the request method, complete URL, request body where applicable, HTTP status code, and response body.

### Setup screenshots

1. MySQL Workbench or terminal showing:

   ```sql
   CREATE DATABASE bookstore_db;
   ```

2. MySQL showing the `bookstore_db` schema and the generated `book` table.
3. IDE showing the project structure.
4. Terminal showing:

   ```text
   BUILD SUCCESS
   ```

   after running `.\mvnw.cmd test`.

5. Terminal showing the application startup message:

   ```text
   Started BookmanagementApplication
   ```

### API screenshots

6. `POST /books` showing `201 Created` and the created book.
7. `GET /books` showing the list of books.
8. `GET /books/{id}` showing one book.
9. `PUT /books/{id}` showing `200 OK` and updated values.
10. `DELETE /books/{id}` showing `204 No Content`.
11. `GET /books/author/{author}` showing filtered results.
12. `GET /books/publisher/{publisher}` showing filtered results.
13. `GET /books/price-range?min=20&max=60` showing filtered results.
14. Invalid price range showing `400 Bad Request`.
15. A missing book request showing `404 Not Found`.

### Code screenshots

16. `Book.java` showing the entity fields and JPA annotations.
17. `BookRepository.java` showing the three derived finder methods.
18. `BookController.java` showing the CRUD mappings.
19. Test class showing the automated endpoint tests.

Avoid including passwords, private tokens, or other personal information in report screenshots.

## 13. Useful Git commands

Check the working tree:

```powershell
git status
```

Add files:

```powershell
git add .
```

Review staged files:

```powershell
git diff --cached
```

Create a commit:

```powershell
git commit -m "Add book management REST API documentation"
```

Connect the local repository to GitHub:

```powershell
git remote add origin https://github.com/<your-username>/<your-repository>.git
git branch -M main
git push -u origin main
```

## 14. Important concepts explained

### REST API

A REST API exposes application data through HTTP methods. This project uses `GET` to read, `POST` to create, `PUT` to update, and `DELETE` to remove books.

### JSON

JSON is the text format used for request and response bodies. The Spring Boot web starter automatically converts JSON into Java objects and Java objects back into JSON.

### Entity and JPA

An entity is a Java class mapped to a database table. JPA annotations describe how the class and its fields map to database records.

### Repository

A repository is the data-access layer. `JpaRepository<Book, Long>` supplies standard CRUD methods without requiring SQL for each operation.

### Derived query methods

Spring Data reads method names such as `findByAuthor` and `findByPriceBetween` and creates the corresponding database query automatically.

### Dependency injection

Spring creates the `BookRepository` object and supplies it to `BookController` through the constructor. This avoids manually creating database objects inside the controller.

### HTTP status codes

- `200 OK`: the request succeeded.
- `201 Created`: a new book was created.
- `204 No Content`: the deletion succeeded and there is no response body.
- `400 Bad Request`: the input is invalid.
- `404 Not Found`: the requested book does not exist.

### Profiles

The normal application profile uses MySQL. The `test` profile uses H2 so tests are isolated, repeatable, and do not modify the real database.

### Environment variables

Environment variables allow machine-specific settings such as database passwords to be supplied at runtime without committing them to source control.

## 15. Troubleshooting

### MySQL connection error

Check that:

1. MySQL Server is running.
2. `bookstore_db` exists.
3. The username and password are correct.
4. `DB_HOST` and `DB_PORT` match the MySQL installation.

### Port 8080 is already in use

Stop the process using port 8080, or add this property to `application.properties`:

```properties
server.port=8081
```

Then use `http://localhost:8081` in all requests.

### Tests fail because of MySQL

Run:

```powershell
.\mvnw.cmd test
```

Tests should use the `test` profile and H2. Do not replace the test configuration with production MySQL credentials.

## 16. License and academic use

This project was created as an academic lab assignment. Add the license or academic submission details required by your institution before publishing it publicly.
