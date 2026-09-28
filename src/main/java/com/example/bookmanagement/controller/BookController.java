package com.example.bookmanagement.controller;

// Imports the object used in request bodies and responses.
import com.example.bookmanagement.entity.Book;
// Imports the data-access component used by this controller.
import com.example.bookmanagement.repository.BookRepository;
// Imports the status code used when creating a resource.
import org.springframework.http.HttpStatus;
// Represents an HTTP response with a status and optional body.
import org.springframework.http.ResponseEntity;
// Imports annotations that map HTTP methods to Java methods.
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// Imports the collection type returned by list endpoints.
import java.util.List;

// Tells Spring that this class handles REST requests and returns JSON.
@RestController
// Adds /books before every endpoint path in this class.
@RequestMapping("/books")
public class BookController {

    // Stores the repository dependency used for all database operations.
    private final BookRepository bookRepository;

    // Spring injects the repository through this constructor.
    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // Handles GET /books and returns every stored book.
    @GetMapping
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // Handles GET /books/{id} and returns one book when it exists.
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        // Optional.map creates a successful response only when a book is found.
        return bookRepository.findById(id)
                .map(ResponseEntity::ok)
                // Missing records are represented by HTTP 404.
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Handles POST /books and creates a new book.
    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        // Ignore a client-supplied ID so the database generates a new one.
        book.setId(null);
        // Return HTTP 201 and the saved record, including its generated ID.
        return ResponseEntity.status(HttpStatus.CREATED).body(bookRepository.save(book));
    }

    // Handles PUT /books/{id} and replaces the editable fields of an existing book.
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book bookDetails) {
        return bookRepository.findById(id)
                .map(book -> {
                    // Copy each editable value from the request to the existing entity.
                    book.setTitle(bookDetails.getTitle());
                    book.setAuthor(bookDetails.getAuthor());
                    book.setPublisher(bookDetails.getPublisher());
                    book.setPrice(bookDetails.getPrice());
                    book.setPublicationYear(bookDetails.getPublicationYear());
                    // Persist the updated entity and return HTTP 200.
                    return ResponseEntity.ok(bookRepository.save(book));
                })
                // Return HTTP 404 when the requested ID does not exist.
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Handles DELETE /books/{id} and removes an existing book.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        // Check first so a missing ID returns 404 instead of looking successful.
        if (!bookRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        // Delete the record after confirming that it exists.
        bookRepository.deleteById(id);
        // A successful deletion has no response body.
        return ResponseEntity.noContent().build();
    }

    // Handles GET /books/author/{author} and filters by author.
    @GetMapping("/author/{author}")
    public List<Book> getBooksByAuthor(@PathVariable String author) {
        return bookRepository.findByAuthor(author);
    }

    // Handles GET /books/publisher/{publisher} and filters by publisher.
    @GetMapping("/publisher/{publisher}")
    public List<Book> getBooksByPublisher(@PathVariable String publisher) {
        return bookRepository.findByPublisher(publisher);
    }

    // Handles GET /books/price-range?min=value&max=value.
    @GetMapping("/price-range")
    public ResponseEntity<List<Book>> getBooksByPriceRange(
            @RequestParam double min,
            @RequestParam double max) {
        // Reject an impossible range before querying the database.
        if (min > max) {
            return ResponseEntity.badRequest().build();
        }
        // Return books whose prices fall within the inclusive range.
        return ResponseEntity.ok(bookRepository.findByPriceBetween(min, max));
    }
}
