package com.library.model;

public class Book {
    private int id;
    private String title;
    private String author;
    private int publicationYear;
    private String genre;
    private boolean isAvailable;

    public Book(int id, String title, String author, int publicationYear, String genre) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.genre = genre;
        this.isAvailable = true;
    }

    // Getters and setters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public int getPublicationYear() { return publicationYear; }
    public void setPublicationYear(int year) { this.publicationYear = year; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { this.isAvailable = available; }
}
