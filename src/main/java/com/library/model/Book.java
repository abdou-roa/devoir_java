package com.library.model;

import java.util.Objects;

public class Book {
    private final String id;
    private String title;
    private String author;
    private int publicationYear;
    private String genre;
    private int quantity;
    private boolean isAvailable;

    public Book(String id, String title, String author, int publicationYear,
                String genre, int quantity) {
        validateBook(title, author, publicationYear, quantity);

        this.id = id;
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.genre = genre;
        this.quantity = quantity;
        this.isAvailable = quantity > 0;
    }

    private void validateBook(String title, String author, int publicationYear, int quantity) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Author cannot be empty");
        }
        if (publicationYear < 0 || publicationYear > java.time.Year.now().getValue()) {
            throw new IllegalArgumentException("Invalid publication year");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getPublicationYear() { return publicationYear; }
    public String getGenre() { return genre; }
    public int getQuantity() { return quantity; }
    public boolean isAvailable() { return isAvailable; }

    // Setters with validation
    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        this.title = title;
    }

    public void setAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Author cannot be empty");
        }
        this.author = author;
    }

    public void setPublicationYear(int publicationYear) {
        if (publicationYear < 0 || publicationYear > java.time.Year.now().getValue()) {
            throw new IllegalArgumentException("Invalid publication year");
        }
        this.publicationYear = publicationYear;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.quantity = quantity;
        this.isAvailable = quantity > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(id, book.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Book{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", publicationYear=" + publicationYear +
                ", genre='" + genre + '\'' +
                ", quantity=" + quantity +
                ", isAvailable=" + isAvailable +
                '}';
    }
}


