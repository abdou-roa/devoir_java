package controller;

import com.library.model.Book;
import com.library.model.Loan;
import com.library.model.User;
import view.LoanView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class LoanController {
    private LoanView view;
    private List<Loan> loans;
    private List<Loan> filteredLoans; // To maintain the current filtered state

    public LoanController(LoanView view) {
        this.view = view;
        this.loans = new ArrayList<>();
        this.filteredLoans = new ArrayList<>();
    }

    public void createLoan(Book book, User user) {
        if (!book.isAvailable()) {
            view.showError("Book is not available");
            return;
        }
        Loan loan = new Loan(generateId(), book, user);
        book.setAvailable(false);
        loans.add(loan);
        user.getLoans().add(loan);
        refreshView();
        view.showSuccess("Loan created successfully");
    }

    public void returnBook(int loanId) {
        loans.stream()
                .filter(l -> l.getId() == loanId)
                .findFirst()
                .ifPresent(loan -> {
                    loan.setReturnDate(LocalDateTime.now());
                    loan.getBook().setAvailable(true);
                    calculatePenalties(loan);
                    refreshView();
                    view.showSuccess("Book returned successfully");
                });
    }

    public void searchLoans(String query) {
        if (query == null || query.trim().isEmpty()) {
            filteredLoans = new ArrayList<>(loans);
        } else {
            query = query.toLowerCase().trim();
            String finalQuery = query;
            filteredLoans = loans.stream()
                    .filter(loan ->
                            loan.getBook().getTitle().toLowerCase().contains(finalQuery) ||
                                    loan.getUser().getName().toLowerCase().contains(finalQuery) ||
                                    String.valueOf(loan.getId()).contains(finalQuery))
                    .collect(Collectors.toList());
        }
        view.refreshLoanList(filteredLoans);
    }

    public void filterLoansByStatus(String status) {
        if (status == null || status.equals("All")) {
            filteredLoans = new ArrayList<>(loans);
        } else {
            filteredLoans = loans.stream()
                    .filter(loan -> {
                        switch (status) {
                            case "Active":
                                return loan.getReturnDate() == null &&
                                        !LocalDateTime.now().isAfter(loan.getDueDate());
                            case "Returned":
                                return loan.getReturnDate() != null;
                            case "Overdue":
                                return loan.getReturnDate() == null &&
                                        LocalDateTime.now().isAfter(loan.getDueDate());
                            default:
                                return true;
                        }
                    })
                    .collect(Collectors.toList());
        }
        view.refreshLoanList(filteredLoans);
    }

    public void exportLoansToCSV(File file) {
        try {
            List<String> lines = new ArrayList<>();

            // Add header
            lines.add("Loan ID,Book Title,User Name,Loan Date,Due Date,Return Date,Penalties,Status");

            // Add data
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            for (Loan loan : filteredLoans) {
                String line = String.format("%d,%s,%s,%s,%s,%s,%.2f,%s",
                        loan.getId(),
                        escapeCsvField(loan.getBook().getTitle()),
                        escapeCsvField(loan.getUser().getName()),
                        loan.getLoanDate().format(formatter),
                        loan.getDueDate().format(formatter),
                        loan.getReturnDate() != null ? loan.getReturnDate().format(formatter) : "",
                        loan.getPenalties(),
                        getLoanStatus(loan)
                );
                lines.add(line);
            }

            // Write to file using Files.write
            Files.write(file.toPath(), lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            view.showSuccess("Loans exported successfully");

        } catch (IOException e) {
            view.showError("Error exporting loans: " + e.getMessage());
        }
    }

    private String escapeCsvField(String field) {
        if (field == null) {
            return "";
        }
        // Escape quotes and wrap in quotes if the field contains comma or quotes
        if (field.contains("\"") || field.contains(",")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }

    private String getLoanStatus(Loan loan) {
        if (loan.getReturnDate() != null) {
            return "Returned";
        } else if (LocalDateTime.now().isAfter(loan.getDueDate())) {
            return "Overdue";
        } else {
            return "Active";
        }
    }


    private void calculatePenalties(Loan loan) {
        if (loan.getReturnDate().isAfter(loan.getDueDate())) {
            long daysLate = ChronoUnit.DAYS.between(loan.getDueDate(), loan.getReturnDate());
            loan.setPenalties(daysLate * 1.0); // $1 per day
        }
    }

//    private String getLoanStatus(Loan loan) {
//        if (loan.getReturnDate() != null) {
//            return "Returned";
//        } else if (LocalDateTime.now().isAfter(loan.getDueDate())) {
//            return "Overdue";
//        } else {
//            return "Active";
//        }
//    }

    private void refreshView() {
        // Reapply current filters
        if (filteredLoans.isEmpty()) {
            view.refreshLoanList(loans);
        } else {
            view.refreshLoanList(filteredLoans);
        }
    }

    private int generateId() {
        return loans.size() + 1;
    }
}