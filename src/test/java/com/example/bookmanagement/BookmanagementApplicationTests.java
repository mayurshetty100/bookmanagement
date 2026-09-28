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

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class BookmanagementApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private BookRepository bookRepository;

	@BeforeEach
	void clearBooks() {
		bookRepository.deleteAll();
	}

	@Test
	void createsAndRetrievesBook() throws Exception {
		String bookJson = """
				{"title":"Clean Code","author":"Robert C. Martin","publisher":"Prentice Hall","price":45.5,"publicationYear":2008}
				""";

		mockMvc.perform(post("/books")
						.contentType(MediaType.APPLICATION_JSON)
						.content(bookJson))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.title").value("Clean Code"));

		mockMvc.perform(get("/books"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].author").value("Robert C. Martin"));
	}

	@Test
	void updatesAndDeletesBook() throws Exception {
		Book book = new Book();
		book.setTitle("Original");
		book.setAuthor("Author");
		book.setPublisher("Publisher");
		book.setPrice(20);
		book.setPublicationYear(2020);
		Long id = bookRepository.save(book).getId();

		mockMvc.perform(put("/books/{id}", id)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"title":"Updated","author":"New Author","publisher":"New Publisher","price":25,"publicationYear":2021}
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.title").value("Updated"))
				.andExpect(jsonPath("$.price").value(25));

		mockMvc.perform(delete("/books/{id}", id))
				.andExpect(status().isNoContent());

		mockMvc.perform(get("/books/{id}", id))
				.andExpect(status().isNotFound());
	}

	@Test
	void filtersBooksAndRejectsInvalidPriceRange() throws Exception {
		Book affordable = new Book();
		affordable.setTitle("Affordable");
		affordable.setAuthor("Shared Author");
		affordable.setPublisher("Publisher A");
		affordable.setPrice(10);
		affordable.setPublicationYear(2020);
		bookRepository.save(affordable);

		Book expensive = new Book();
		expensive.setTitle("Expensive");
		expensive.setAuthor("Shared Author");
		expensive.setPublisher("Publisher B");
		expensive.setPrice(100);
		expensive.setPublicationYear(2021);
		bookRepository.save(expensive);

		mockMvc.perform(get("/books/author/Shared Author"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)));

		mockMvc.perform(get("/books/publisher/Publisher A"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].title", is("Affordable")));

		mockMvc.perform(get("/books/price-range").param("min", "5").param("max", "20"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].price").value(10));

		mockMvc.perform(get("/books/price-range").param("min", "20").param("max", "5"))
				.andExpect(status().isBadRequest());
	}

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
