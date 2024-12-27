package controller;

import com.library.model.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.*;
import java.time.LocalDate;
import java.io.*;

public class LoanController {
    private final Map<String, Loan> loans;
    private final BookController bookController;
    private final UserController userController;
    private static final String LOANS_CSV = "loans.csv";

    public LoanController(BookController bookController, UserController userController) {
        this.loans = new HashMap<>();
        this.bookController = bookController;
        this.userController = userController;
        loadLoans();
    }

    private void loadLoans() {
        try (BufferedReader reader = new BufferedReader(new FileReader(LOANS_CSV))) {
            String line;
            reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                Book book = bookController.getBook(parts[1]);
                User user = userController.getUser(parts[2]);

                if (book != null && user != null) {
                    Loan loan = new Loan(parts[0], book, user);
                    loans.put(loan.getId(), loan);
                    user.addLoan(loan);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading loans: " + e.getMessage());
        }
    }

    private void saveLoans() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOANS_CSV))) {
            writer.println("id,bookId,userId,loanDate,dueDate,returnDate,penalty");
            for (Loan loan : loans.values()) {
                writer.printf("%s,%s,%s,%s,%s,%s,%.2f%n",
                        loan.getId(),
                        loan.getBook().getId(),
                        loan.getUser().getId(),
                        loan.getLoanDate(),
                        loan.getDueDate(),
                        loan.getReturnDate() != null ? loan.getReturnDate() : "",
                        loan.getPenalty()
                );
            }
        } catch (IOException e) {
            System.err.println("Error saving loans: " + e.getMessage());
        }
    }

    public Loan createLoan(String bookId, String userId) {
        Book book = bookController.getBook(bookId);
        User user = userController.getUser(userId);

        System.out.println("quantity:" + book.getQuantity());

        if (book == null || user == null) {
            throw new IllegalArgumentException("Book or user not found");
        }

        if (book.getQuantity() <= 0) {
            throw new IllegalStateException("Book is not available");
        }

        String id = UUID.randomUUID().toString();
        Loan loan = new Loan(id, book, user);
        loans.put(id, loan);
        user.addLoan(loan);
        book.setQuantity(book.getQuantity() - 1);
        bookController.updateBook(book);
        saveLoans();
        return loan;
    }

    public void returnLoan(String loanId) {
        Loan loan = loans.get(loanId);
        if (loan == null) {
            throw new IllegalArgumentException("Loan not found");
        }

        if (loan.getReturnDate() != null) {
            throw new IllegalStateException("Book already returned");
        }

        loan.returnBook();
        Book book = loan.getBook();
        book.setQuantity(book.getQuantity() + 1);

        saveLoans();
    }

    public List<Loan> getActiveLoans() {
        return loans.values().stream()
                .filter(loan -> loan.getReturnDate() == null)
                .collect(Collectors.toList());
    }

    public List<Loan> getOverdueLoans() {
        LocalDate today = LocalDate.now();
        return loans.values().stream()
                .filter(loan ->
                        loan.getReturnDate() == null &&
                                loan.getDueDate().isBefore(today))
                .collect(Collectors.toList());
    }

    public List<Loan> getUserLoans(String userId) {
        return loans.values().stream()
                .filter(loan -> loan.getUser().getId().equals(userId))
                .collect(Collectors.toList());
    }

    public double calculateTotalPenalties(String userId) {
        return getUserLoans(userId).stream()
                .mapToDouble(Loan::getPenalty)
                .sum();
    }

    public List<Loan> getAllLoans() {
        List<Loan> loans = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("loans.csv"))) {
            String line;
            reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String id = parts[0];
                Book book = bookController.getBook(parts[1]); // bookId
                User user = userController.getUser(parts[2]); // userId
                LocalDate loanDate = LocalDate.parse(parts[3]);
                LocalDate dueDate = LocalDate.parse(parts[4]);
                LocalDate returnDate = parts[5].isEmpty() ? null : LocalDate.parse(parts[5]);
                double penalty = Double.parseDouble(parts[6]);

                Loan loan = new Loan(id, book, user, loanDate, dueDate, returnDate, penalty);
                // Set additional properties
                loans.add(loan);
            }
        } catch (IOException e) {
            System.err.println("Error reading loans: " + e.getMessage());
        }
        return loans;
    }
}