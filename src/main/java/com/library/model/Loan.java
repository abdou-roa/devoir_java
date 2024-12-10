package com.library.model;

import java.time.LocalDateTime;

public class Loan {
    private int id;
    private Book book;
    private User user;
    private LocalDateTime loanDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private double penalties;

    public Loan(int id, Book book, User user) {
        this.id = id;
        this.book = book;
        this.user = user;
        this.loanDate = LocalDateTime.now();
        this.dueDate = loanDate.plusDays(14);
        this.penalties = 0.0;
    }

    // Getters and setters
    public int getId() { return id; }
    public Book getBook() { return book; }
    public User getUser() { return user; }
    public LocalDateTime getLoanDate() { return loanDate; }
    public LocalDateTime getDueDate() { return dueDate; }
    public LocalDateTime getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDateTime date) { this.returnDate = date; }
    public double getPenalties() { return penalties; }
    public void setPenalties(double penalties) { this.penalties = penalties; }
}
