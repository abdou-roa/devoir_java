package controller;


import com.library.model.*;
import java.util.*;
import java.time.LocalDate;
import java.io.*;
import java.util.stream.Collectors;

import view.BookView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
public class BookController {
    private final Map<String, Book> books;
    private static final String BOOKS_CSV = "books.csv";

    public BookController() {
        this.books = new HashMap<>();
        loadBooks();
    }

    private void loadBooks() {
        try (BufferedReader reader = new BufferedReader(new FileReader(BOOKS_CSV))) {
            String line;
            reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                Book book = new Book(
                        parts[0], // id
                        parts[1], // title
                        parts[2], // author
                        Integer.parseInt(parts[3]), // publicationYear
                        parts[4], // genre
                        Integer.parseInt(parts[5]) // quantity
                );
                books.put(book.getId(), book);
            }
        } catch (IOException e) {
            System.err.println("Error loading books: " + e.getMessage());
        }
    }

    private void saveBooks() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(BOOKS_CSV))) {
            writer.println("id,title,author,publicationYear,genre,quantity");
            for (Book book : books.values()) {
                writer.printf("%s,%s,%s,%d,%s,%d%n",
                        book.getId(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getPublicationYear(),
                        book.getGenre(),
                        book.getQuantity()
                );
            }
        } catch (IOException e) {
            System.err.println("Error saving books: " + e.getMessage());
        }
    }

    public Book addBook(String title, String author, int publicationYear,
                        String genre, int quantity) {
        String id = UUID.randomUUID().toString();
        Book book = new Book(id, title, author, publicationYear, genre, quantity);
        books.put(id, book);
        saveBooks();
        return book;
    }

    public void updateBook(Book book) {
        if (!books.containsKey(book.getId())) {
            throw new IllegalArgumentException("Book not found");
        }
        books.put(book.getId(), book);
        saveBooks();
    }

    public void deleteBook(String id) {
        if (!books.containsKey(id)) {
            throw new IllegalArgumentException("Book not found");
        }
        books.remove(id);
        saveBooks();
    }

    public Book getBook(String id) {
        return books.get(id);
    }

    public List<Book> getAllBooks() {
        return new ArrayList<>(books.values());
    }

    public List<Book> searchBooks(String query) {
        String lowerQuery = query.toLowerCase();
        return books.values().stream()
                .filter(book ->
                        book.getTitle().toLowerCase().contains(lowerQuery) ||
                                book.getAuthor().toLowerCase().contains(lowerQuery) ||
                                book.getGenre().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
    }

    public List<Book> getAvailableBooks() {
        return books.values().stream()
                .filter(Book::isAvailable)
                .collect(Collectors.toList());
    }
}