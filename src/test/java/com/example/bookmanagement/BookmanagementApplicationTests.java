package com.example.bookmanagement;

import com.example.bookmanagement.entity.Book;
import com.example.bookmanagement.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Loads the complete Spring application for integration-style endpoint tests.
@SpringBootTest
// Selects the H2-backed test configuration.
@ActiveProfiles("test")
// Starts MockMvc without opening a real network port.
@AutoConfigureMockMvc
class BookmanagementApplicationTests {

	// Sends simulated HTTP requests to the controller.
	@Autowired
	private MockMvc mockMvc;

	// Provides direct access for arranging test data.
	@Autowired
	private BookRepository bookRepository;

	// Keeps each test independent by deleting data created by a previous test.
	@BeforeEach
	void clearBooks() {
		bookRepository.deleteAll();
	}

	// Verifies that POST creates a record and GET returns it.
	@Test
	void createsAndRetrievesBook() throws Exception {
		// JSON payload sent to the create endpoint.
		String bookJson = """
				{"title":"Clean Code","author":"Robert C. Martin","publisher":"Prentice Hall","price":45.5,"publicationYear":2008}
				""";

		// Send the create request and verify the response status and fields.
		mockMvc.perform(post("/books")
						.contentType(MediaType.APPLICATION_JSON)
						.content(bookJson))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.title").value("Clean Code"));

		// Read all books and verify that the created book is present.
		mockMvc.perform(get("/books"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].author").value("Robert C. Martin"));
	}

	// Verifies update behavior followed by successful deletion.
	@Test
	void updatesAndDeletesBook() throws Exception {
		// Arrange an existing book directly in the test database.
		Book book = new Book();
		book.setTitle("Original");
		book.setAuthor("Author");
		book.setPublisher("Publisher");
		book.setPrice(20);
		book.setPublicationYear(2020);
		Long id = bookRepository.save(book).getId();

		// Update the stored book and verify the changed response values.
		mockMvc.perform(put("/books/{id}", id)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"title":"Updated","author":"New Author","publisher":"New Publisher","price":25,"publicationYear":2021}
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.title").value("Updated"))
				.andExpect(jsonPath("$.price").value(25));

		// Delete the book and expect an empty successful response.
		mockMvc.perform(delete("/books/{id}", id))
				.andExpect(status().isNoContent());

		// Confirm that the deleted record can no longer be found.
		mockMvc.perform(get("/books/{id}", id))
				.andExpect(status().isNotFound());
	}

	// Verifies all filter endpoints and invalid-range validation.
	@Test
	void filtersBooksAndRejectsInvalidPriceRange() throws Exception {
		// Create a lower-priced book for filter assertions.
		Book affordable = new Book();
		affordable.setTitle("Affordable");
		affordable.setAuthor("Shared Author");
		affordable.setPublisher("Publisher A");
		affordable.setPrice(10);
		affordable.setPublicationYear(2020);
		bookRepository.save(affordable);

		// Create a second book outside the tested price range.
		Book expensive = new Book();
		expensive.setTitle("Expensive");
		expensive.setAuthor("Shared Author");
		expensive.setPublisher("Publisher B");
		expensive.setPrice(100);
		expensive.setPublicationYear(2021);
		bookRepository.save(expensive);

		// The author filter should return both records.
		mockMvc.perform(get("/books/author/Shared Author"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)));

		// The publisher filter should return only the first record.
		mockMvc.perform(get("/books/publisher/Publisher A"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].title", is("Affordable")));

		// The inclusive price range should return only the affordable book.
		mockMvc.perform(get("/books/price-range").param("min", "5").param("max", "20"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].price").value(10));

		// A minimum greater than the maximum is invalid.
		mockMvc.perform(get("/books/price-range").param("min", "20").param("max", "5"))
				.andExpect(status().isBadRequest());
	}

	// Verifies 404 responses for all operations that target a missing ID.
	@Test
	void returnsNotFoundForMissingBookOperations() throws Exception {
		mockMvc.perform(get("/books/{id}", 999L))
				.andExpect(status().isNotFound());

		mockMvc.perform(put("/books/{id}", 999L)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"title":"Missing","author":"Author","publisher":"Publisher","price":1,"publicationYear":2024}
								"""))
				.andExpect(status().isNotFound());

		mockMvc.perform(delete("/books/{id}", 999L))
				.andExpect(status().isNotFound());
	}

}
