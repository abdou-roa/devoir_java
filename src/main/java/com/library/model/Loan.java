package com.library.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;

public class Loan {
    private final String id;
    private final Book book;
    private final User user;
    private final LocalDate loanDate;
    private final LocalDate dueDate;
    private LocalDate returnDate;
    private double penalty;

    private static final double PENALTY_RATE_PER_DAY = 1.0; // $1 per day
    private static final int STANDARD_LOAN_DAYS = 14;


    public Loan(String id, Book book, User user) {
        validateLoan(book, user);
        this.id = id;
        this.book = book;
        this.user = user;
        this.loanDate = LocalDate.now();
        this.dueDate = loanDate.plusDays(STANDARD_LOAN_DAYS);
        this.penalty = 0.0;
    }

    // Constructor for existing loans (from storage/CSV)
    public Loan(String id, Book book, User user, LocalDate loanDate,
                LocalDate dueDate, LocalDate returnDate, double penalty) {
        validateLoan(book, user);
        this.id = id;
        this.book = book;
        this.user = user;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.penalty = penalty;
    }

    private void validateLoan(Book book, User user) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (!book.isAvailable()) {
            throw new IllegalArgumentException("Book is not available");
        }
        if (user.getRole() != User.UserRole.MEMBER) {
            throw new IllegalArgumentException("Only members can borrow books");
        }
    }

    public void returnBook() {
        if (returnDate != null) {
            throw new IllegalStateException("Book already returned");
        }
        returnDate = LocalDate.now();
        calculatePenalty();
    }

    private void calculatePenalty() {
        if (returnDate.isAfter(dueDate)) {
            long daysLate = ChronoUnit.DAYS.between(dueDate, returnDate);
            penalty = daysLate * PENALTY_RATE_PER_DAY;
        }
    }

    // Getters
    public String getId() {
        return id;
    }

    public Book getBook() {
        return book;
    }

    public User getUser() {
        return user;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public double getPenalty() {
        return penalty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Loan loan = (Loan) o;
        return Objects.equals(id, loan.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Loan{" +
                "id='" + id + '\'' +
                ", book=" + book.getTitle() +
                ", user=" + user.getUsername() +
                ", loanDate=" + loanDate +
                ", dueDate=" + dueDate +
                ", returnDate=" + returnDate +
                ", penalty=" + penalty +
                '}';
    }

}