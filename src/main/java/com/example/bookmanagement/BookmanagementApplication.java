package com.example.bookmanagement;

// Imports Spring Boot's application launcher.
import org.springframework.boot.SpringApplication;
// Imports the annotation that enables component scanning and auto-configuration.
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Marks this class as the main Spring Boot configuration and application entry point.
@SpringBootApplication
public class BookmanagementApplication {

	// Java starts the application by calling this method.
	public static void main(String[] args) {
		// Creates the Spring application context and starts the embedded web server.
		SpringApplication.run(BookmanagementApplication.class, args);
	}

}
