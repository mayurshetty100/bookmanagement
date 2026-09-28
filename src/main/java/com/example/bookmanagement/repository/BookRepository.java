package com.example.bookmanagement.repository;

// Imports the entity managed by this repository.
import com.example.bookmanagement.entity.Book;
// Provides standard CRUD operations through Spring Data JPA.
import org.springframework.data.jpa.repository.JpaRepository;

// Imports the collection type returned by finder methods.
import java.util.List;

// Spring creates an implementation of this interface at runtime.
public interface BookRepository extends JpaRepository<Book, Long> {

    // Finds every book whose author exactly matches the supplied value.
    List<Book> findByAuthor(String author);

    // Finds every book whose publisher exactly matches the supplied value.
    List<Book> findByPublisher(String publisher);

    // Finds books whose price is between the two inclusive boundary values.
    List<Book> findByPriceBetween(double min, double max);
}
