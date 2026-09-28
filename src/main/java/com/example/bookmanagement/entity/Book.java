package com.example.bookmanagement.entity;

// Marks this class as a JPA entity that is stored in a database table.
import jakarta.persistence.Entity;
// Generates database IDs automatically.
import jakarta.persistence.GeneratedValue;
// Selects the database identity-column strategy for ID generation.
import jakarta.persistence.GenerationType;
// Marks the primary-key field.
import jakarta.persistence.Id;

// Represents one book record in the database.
@Entity
public class Book {

    // The unique identifier for a book.
    @Id
    // The database generates this value when a new book is inserted.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The book's title.
    private String title;
    // The book's author.
    private String author;
    // The company or person that published the book.
    private String publisher;
    // The book's price.
    private double price;
    // The year in which the book was published.
    private int publicationYear;

    // Returns the database identifier.
    public Long getId() {
        return id;
    }

    // Sets the database identifier; new books normally leave this null.
    public void setId(Long id) {
        this.id = id;
    }

    // Returns the title.
    public String getTitle() {
        return title;
    }

    // Stores the title.
    public void setTitle(String title) {
        this.title = title;
    }

    // Returns the author.
    public String getAuthor() {
        return author;
    }

    // Stores the author.
    public void setAuthor(String author) {
        this.author = author;
    }

    // Returns the publisher.
    public String getPublisher() {
        return publisher;
    }

    // Stores the publisher.
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    // Returns the price.
    public double getPrice() {
        return price;
    }

    // Stores the price.
    public void setPrice(double price) {
        this.price = price;
    }

    // Returns the publication year.
    public int getPublicationYear() {
        return publicationYear;
    }

    // Stores the publication year.
    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }
}
