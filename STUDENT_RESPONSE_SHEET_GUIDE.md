# Spring Boot CRUD Response Sheet Guide

## Purpose of this guide

The supplied response sheet uses `Student` and `/students` examples. This project is
slightly different: it manages `Book` records through the `/books` endpoints. Use
the explanations below to prepare your own answers from the code you actually wrote.
Do not copy this guide word for word. Read the referenced files, run the commands,
and rewrite the answers in your own normal writing style.

This guide is a preparation document. It tells you what to understand, what to
write, and what evidence to collect for the report.

---

## 1. First, identify your project correctly

Write these details at the top of your own response sheet:

- Application: Book Management REST API
- Base URL: `http://localhost:8080`
- Main resource: `Book`
- Main endpoint: `/books`
- Database: MySQL database `bookstore_db`
- Table: `book`
- Test database: H2 in-memory database `bookstore_test`
- Java version: 17
- Build tool: Maven Wrapper

The main Java files you should open while preparing are:

- `src/main/java/com/example/bookmanagement/entity/Book.java`
- `src/main/java/com/example/bookmanagement/repository/BookRepository.java`
- `src/main/java/com/example/bookmanagement/controller/BookController.java`
- `src/main/resources/application.properties`

Do not include your MySQL password in the report, screenshots, README, or GitHub
repository. It is supplied through the `DB_PASSWORD` environment variable.

---

## 2. How to run the project before answering

### Step 1: Start MySQL

Open MySQL Workbench and connect to your local MySQL 8 server. Confirm that the
`bookstore_db` schema exists. If the database or sample rows are missing, open
`database/bookstore_seed.sql`, execute it, and verify the data:

```sql
USE bookstore_db;
SELECT DATABASE();
SELECT * FROM book;
```

The query should show `bookstore_db` as the selected database and should return the
sample book rows.

### Step 2: Open a terminal in the project folder

In the VS Code terminal, run the following command. Replace the placeholder with
your own local MySQL password. Do not save the password in a source file:

```powershell
$env:DB_PASSWORD="YOUR_MYSQL_PASSWORD"
.\mvnw.cmd spring-boot:run
```

Expected log evidence includes:

```text
Started BookmanagementApplication
Tomcat started on port 8080
```

The exact log wording can vary with the Spring Boot version. The important points
are that the application starts without an error, uses port `8080`, and connects
to `bookstore_db`.

### Step 3: Run the automated tests in another terminal

Keep the application terminal open and run:

```powershell
.\mvnw.cmd test
```

Expected result:

```text
Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

The number of tests can change if the test file is extended. Record the result
you actually see instead of changing it to match this example.

---

## 3. Section A – Core Concepts

### Question 1: Role of `@RestController`

Base your answer on `BookController.java`.

Points to include in your own wording:

1. `@RestController` tells Spring that `BookController` receives HTTP requests.
2. Its return values are written as response data, normally JSON.
3. The class is the HTTP entry point for the book operations.
4. `@RequestMapping("/books")` adds the common `/books` path.
5. For example, `getAllBooks()` handles `GET /books`.

A useful code location to show is:

```java
@RestController
@RequestMapping("/books")
public class BookController {
```

Explain the idea in your own way. Do not only define the annotation from a
website.

### Question 2: Difference between `@PathVariable`, `@RequestParam`, and `@RequestBody`

Use examples from this project:

| Annotation | Where the value is found | Project example |
|---|---|---|
| `@PathVariable` | A value inside the URL path | `@GetMapping("/{id}")` with `@PathVariable Long id` |
| `@RequestParam` | A query-string value after `?` | `/books/price-range?min=10&max=50` |
| `@RequestBody` | JSON sent inside the request body | `createBook(@RequestBody Book book)` |

Make sure your explanation includes a real example:

- `GET /books/5` supplies `5` as a path variable.
- `GET /books/price-range?min=10&max=50` supplies `10` and `50` as request
  parameters.
- A `POST /books` JSON object is converted into a `Book` object through the
  request body.

### Question 3: Why GET, POST, PUT, and DELETE are different

Relate each HTTP method to this application:

- `GET /books` reads all books without creating or changing a row.
- `POST /books` creates a new book and returns `201 Created`.
- `PUT /books/{id}` changes the fields of an existing book.
- `DELETE /books/{id}` removes a book and returns `204 No Content` when successful.

Mention that the URL and method together tell the controller which operation is
being requested. Also mention that a missing ID returns `404 Not Found` for the
single-record update and delete operations.

### Question 4: Service layer and Repository layer

This is an important project-specific point: **this version of the project does
not contain a separate Service class**. The controller directly uses
`BookRepository`.

Write honestly:

- In a layered application, the Service layer normally contains business rules
  and coordinates repository operations.
- The Repository layer handles persistence and database access.
- In this project, `BookRepository` extends `JpaRepository<Book, Long>`, so Spring
  supplies standard methods such as `findAll`, `findById`, `save`, `existsById`,
  and `deleteById`.
- The custom methods `findByAuthor`, `findByPublisher`, and
  `findByPriceBetween` are derived from method names.
- The current controller calls this repository directly, so a separate Service
  layer has not been added.

Do not claim that a `BookService` class exists when it does not.

---

## 4. Section B – Trace the request flow

The sheet says `GET /students/101`. For this project, trace a real request such
as:

```text
GET http://localhost:8080/books/1
```

Use the following order in your explanation:

1. Postman sends an HTTP GET request to the running Spring Boot server.
2. `BookController` matches the request using `@GetMapping("/{id}")`.
3. Spring reads `1` from the URL and passes it into `@PathVariable Long id`.
4. The controller calls `bookRepository.findById(id)`.
5. Spring Data JPA sends the database query to MySQL.
6. MySQL searches the `book` table in `bookstore_db`.
7. If the row exists, the controller returns `ResponseEntity.ok(book)`.
8. Spring converts the `Book` object to JSON and sends HTTP `200 OK` to Postman.
9. If no row exists, the `Optional` is empty and the controller returns
   `404 Not Found`.

Because this code has no service class, do not insert a fictional service step
between the controller and repository. You can add one sentence saying that the
controller currently performs the coordination directly.

Evidence to collect:

- Screenshot of `GET /books/1` in Postman with `200 OK`.
- Screenshot of `SELECT * FROM bookstore_db.book;` in MySQL Workbench.
- Optional screenshot of the Spring Boot terminal showing the request and SQL.

---

## 5. Section C – Understand your own code

### 6(a) Create URL and method

Write:

```text
Method: POST
URL: http://localhost:8080/books
```

Use this JSON body when testing:

```json
{
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "publisher": "Prentice Hall",
  "price": 42.5,
  "publicationYear": 2008
}
```

Set Postman to **Body → raw → JSON**. The body must contain only the JSON object.
Do not paste the word `json` above it and do not paste Markdown backticks.

Expected result: `201 Created`, with an `id` generated by MySQL.

### 6(b) Controller method for read by ID

Find this method in `BookController.java`:

```java
@GetMapping("/{id}")
public ResponseEntity<Book> getBookById(@PathVariable Long id) {
```

The annotation receiving the ID is `@PathVariable`. The ID comes from a URL such
as `/books/1`, not from the JSON body.

### 6(c) Repository method for all records

The method is:

```java
bookRepository.findAll()
```

It is inherited from `JpaRepository`; it is not manually written in
`BookRepository.java`. The controller uses it inside `getAllBooks()`.

### 6(d) When a record does not exist

For `GET`, `PUT`, and `DELETE` by ID, the controller checks the requested ID.
The result is `404 Not Found` when no matching row exists.

Test with an ID that is not present, for example:

```text
GET http://localhost:8080/books/99999
```

Record the status returned by your own run.

---

## 6. Section D – Debugging and modification

### Question 7: Correct the annotation mistake

The sheet shows:

```java
@GetMapping("/students/{id}")
public Student getStudent(@RequestBody int id) {
    return service.getStudent(id);
}
```

The problem is that the ID is part of the URL path, but the method tries to read
it from the request body. A GET request normally does not use a JSON body for
this purpose.

The corrected pattern is:

```java
@GetMapping("/students/{id}")
public Student getStudent(@PathVariable int id) {
    return service.getStudent(id);
}
```

For your project, the equivalent real method is
`getBookById(@PathVariable Long id)`.

### Question 8: Add a filtered route

The sheet asks for a branch filter. Your equivalent filter is already present:

```text
GET /books/author/{author}
```

For example:

```text
GET http://localhost:8080/books/author/Robert C. Martin
```

The project contains these three related pieces:

1. Controller route:

   ```java
   @GetMapping("/author/{author}")
   public List<Book> getBooksByAuthor(@PathVariable String author) {
       return bookRepository.findByAuthor(author);
   }
   ```

2. Repository method:

   ```java
   List<Book> findByAuthor(String author);
   ```

3. Database query: Spring Data derives the query from the method name and
   returns books whose author matches.

If your lecturer specifically requires a new branch example, explain that the
same pattern would be used: add a controller route, add a matching repository
method such as `findByBranch`, and call it through the application layer. Do not
say that this project has a `branch` column.

### Question 9: How POST JSON becomes a database row

Use the actual book request:

```json
{
  "title": "The Pragmatic Programmer",
  "author": "Andrew Hunt",
  "publisher": "Addison-Wesley",
  "price": 45.0,
  "publicationYear": 1999
}
```

Explain the sequence:

1. Postman sends the JSON to `POST /books`.
2. `@RequestBody Book book` tells Spring to deserialize the JSON into a Java
   `Book` object.
3. Spring uses the setters and matching field names such as `title`, `author`,
   and `price`.
4. The controller sets the ID to `null`, allowing the database to generate it.
5. `bookRepository.save(book)` uses JPA/Hibernate to issue an insert.
6. MySQL stores the row in `bookstore_db.book`.
7. The saved object, including its generated ID, is returned as JSON with
   `201 Created`.

---

## 7. Postman practical evidence

Run the requests in this order so later requests can use an ID returned by POST:

| Order | Method | URL | Expected result |
|---:|---|---|---|
| 1 | GET | `http://localhost:8080/books` | `200 OK` and a JSON array |
| 2 | POST | `http://localhost:8080/books` | `201 Created` and a generated ID |
| 3 | GET | `http://localhost:8080/books/{newId}` | `200 OK` |
| 4 | PUT | `http://localhost:8080/books/{newId}` | `200 OK` and updated fields |
| 5 | GET | `http://localhost:8080/books/author/{author}` | `200 OK` |
| 6 | GET | `http://localhost:8080/books/publisher/{publisher}` | `200 OK` |
| 7 | GET | `http://localhost:8080/books/price-range?min=10&max=50` | `200 OK` |
| 8 | GET | `http://localhost:8080/books/price-range?min=80&max=10` | `400 Bad Request` |
| 9 | DELETE | `http://localhost:8080/books/{newId}` | `204 No Content` |
| 10 | GET | `http://localhost:8080/books/{newId}` | `404 Not Found` |

For POST and PUT, use the `Content-Type: application/json` header. For GET and
DELETE, no request body is needed.

---

## 8. Screenshot checklist for the report

Take only clear screenshots that show the relevant result and the URL or SQL
being executed. Suggested screenshots:

1. VS Code project tree showing the controller, entity, repository, tests, and
   database script.
2. `Book.java` showing the entity fields and ID generation.
3. `BookRepository.java` showing the derived finder methods.
4. `BookController.java` showing the CRUD mappings.
5. MySQL Workbench showing the `bookstore_db` schema and `book` table.
6. MySQL query showing `SELECT * FROM bookstore_db.book;`.
7. Spring Boot terminal showing successful startup on port `8080`.
8. Maven test terminal showing a successful build.
9. Postman `GET /books` response.
10. Postman `POST /books` response showing `201 Created`.
11. Postman `PUT /books/{id}` response showing changed data.
12. Postman author, publisher, and price-range filter responses.
13. Postman invalid range response showing `400 Bad Request`.
14. Postman delete response showing `204 No Content`.
15. Postman missing/deleted ID response showing `404 Not Found`.

Before submitting, check every screenshot for accidental passwords, unrelated
browser tabs, or private personal information.

---

## 9. Final preparation checklist

- [ ] I replaced the generic `Student` examples with my actual `Book` project.
- [ ] I can explain `@RestController`, `@PathVariable`, `@RequestParam`, and
  `@RequestBody` without reading a definition.
- [ ] I understand that this project currently calls the repository directly and
  does not contain a separate service class.
- [ ] I ran the application and recorded the actual startup result.
- [ ] I ran `.\mvnw.cmd test` and recorded the actual test result.
- [ ] I tested POST, GET, PUT, filters, invalid range, and DELETE in Postman.
- [ ] I verified at least one result in MySQL Workbench.
- [ ] I used my own wording in the final response sheet.
- [ ] I did not include the MySQL password anywhere.

