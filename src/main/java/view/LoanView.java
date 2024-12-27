package view;

import controller.*;
import com.library.model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LoanView extends JPanel {
    private final LoanController loanController;
    private final BookController bookController;
    private final UserController userController;

    private final JTable loanTable;
    private final DefaultTableModel tableModel;
    private final JComboBox<String> filterCombo;
    private final JTextField searchField;
    private final DateTimeFormatter dateFormatter;

    public LoanView(LoanController loanController, BookController bookController,
                    UserController userController) {
        this.loanController = loanController;
        this.bookController = bookController;
        this.userController = userController;
        this.dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Initialize components
        String[] columns = {
                "Loan ID", "Book Title", "Member Name", "Loan Date",
                "Due Date", "Return Date", "Penalty", "Status"
        };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        loanTable = new JTable(tableModel);
        filterCombo = new JComboBox<>(new String[]{
                "All Loans", "Active Loans", "Overdue Loans", "Returned"
        });
        searchField = new JTextField(20);

        initializeUI();
        refreshLoanTable();
    }

    private void initializeUI() {
        // Top Panel - Search and Filter
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchField);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> performSearch());
        topPanel.add(searchButton);
        //live search
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                performSearch();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                performSearch();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                performSearch();
            }
        });

        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(new JLabel("Filter:"));
        topPanel.add(filterCombo);
        filterCombo.addActionListener(e -> refreshLoanTable());

        add(topPanel, BorderLayout.NORTH);

        // Center Panel - Loan Table
        configureLoanTable();
        JScrollPane scrollPane = new JScrollPane(loanTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Panel - Action Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton createLoanButton = new JButton("Create New Loan");
        JButton returnBookButton = new JButton("Return Book");
        JButton viewDetailsButton = new JButton("View Details");

        createLoanButton.addActionListener(e -> showCreateLoanDialog());
        returnBookButton.addActionListener(e -> handleBookReturn());
        viewDetailsButton.addActionListener(e -> showLoanDetails());

        buttonPanel.add(createLoanButton);
        buttonPanel.add(returnBookButton);
        buttonPanel.add(viewDetailsButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void configureLoanTable() {
        loanTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        loanTable.getColumnModel().getColumn(0).setPreferredWidth(100); // Loan ID
        loanTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Book Title
        loanTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Member Name
        loanTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Loan Date
        loanTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Due Date
        loanTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Return Date
        loanTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // Penalty
        loanTable.getColumnModel().getColumn(7).setPreferredWidth(80);  // Status
    }

    private void refreshLoanTable() {
        tableModel.setRowCount(0);
        List<Loan> loans;

        String filter = (String) filterCombo.getSelectedItem();
        switch (filter) {
            case "Active Loans":
                loans = loanController.getActiveLoans();
                break;
            case "Overdue Loans":
                loans = loanController.getOverdueLoans();
                break;
            case "Returned":
                loans = loanController.getAllLoans().stream()
                        .filter(loan -> loan.getReturnDate() != null)
                        .collect(java.util.stream.Collectors.toList());
                break;
            default:
                loans = loanController.getAllLoans();
        }

        for (Loan loan : loans) {
            addLoanToTable(loan);
        }
    }
//"Loan ID", "Book Title", "Member Name", "Loan Date", "Due Date", "Return Date", "Penalty", "Status"
    private void addLoanToTable(Loan loan) {
        String status = determineStatus(loan);
        Object[] row = {
                loan.getId(),
                loan.getBook().getTitle(),
                loan.getUser().getUsername(),
                loan.getLoanDate().format(dateFormatter),
                loan.getDueDate().format(dateFormatter),
                loan.getReturnDate() != null ? loan.getReturnDate().format(dateFormatter) : "-",
                String.format("$%.2f", loan.getPenalty()),
                status
        };
        tableModel.addRow(row);
    }

    private String determineStatus(Loan loan) {
        if (loan.getReturnDate() != null) {
            return "Returned";
        }
        return LocalDate.now().isAfter(loan.getDueDate()) ? "Overdue" : "Active";
    }

    private void performSearch() {
        String searchTerm = searchField.getText().toLowerCase().trim();
        if (searchTerm.isEmpty()) {
            refreshLoanTable();
            return;
        }

        tableModel.setRowCount(0);
        loanController.getAllLoans().stream()
                .filter(loan -> {
                    try {
                        return (loan.getBook() != null && loan.getBook().getTitle() != null
                                && loan.getBook().getTitle().toLowerCase().contains(searchTerm))
                                || (loan.getUser() != null && loan.getUser().getUsername() != null
                                && loan.getUser().getUsername().toLowerCase().contains(searchTerm))
                                || (loan.getId() != null && loan.getId().toLowerCase().contains(searchTerm));
                    } catch (NullPointerException e) {
                        return false;
                    }
                })
                .forEach(this::addLoanToTable);
    }

    private void showCreateLoanDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Create New Loan", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Get available books and members
        List<Book> availableBooks = bookController.getAvailableBooks();
        List<User> members = userController.getAllUsers().stream()
                .filter(user -> user.getRole() == User.UserRole.MEMBER)
                .collect(java.util.stream.Collectors.toList());

        // Create combo boxes
        JComboBox<Book> bookCombo = new JComboBox<>(
                availableBooks.toArray(new Book[0]));
        JComboBox<User> memberCombo = new JComboBox<>(
                members.toArray(new User[0]));

        // Add components
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Select Book:"), gbc);
        gbc.gridx = 1;
        dialog.add(bookCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Select Member:"), gbc);
        gbc.gridx = 1;
        dialog.add(memberCombo, gbc);

        // Create Loan button
        JButton createButton = new JButton("Create Loan");
        createButton.addActionListener(e -> {
            try {
                Book selectedBook = (Book) bookCombo.getSelectedItem();
                User selectedMember = (User) memberCombo.getSelectedItem();

                if (selectedBook != null && selectedMember != null) {
                    loanController.createLoan(selectedBook.getId(), selectedMember.getId());
                    refreshLoanTable();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "Please select both a book and a member",
                            "Input Required",
                            JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Error creating loan: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        dialog.add(createButton, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void handleBookReturn() {
        int selectedRow = loanTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a loan to return",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String loanId = (String) tableModel.getValueAt(selectedRow, 0);
        String status = (String) tableModel.getValueAt(selectedRow, 7);

        if ("Returned".equals(status)) {
            JOptionPane.showMessageDialog(this,
                    "This book has already been returned",
                    "Already Returned",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to return this book?",
                "Confirm Return",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                loanController.returnLoan(loanId);
                refreshLoanTable();
                JOptionPane.showMessageDialog(this,
                        "Book returned successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error returning book: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showLoanDetails() {
        int selectedRow = loanTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a loan to view details",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get loan details from the selected row
        String loanId = (String) tableModel.getValueAt(selectedRow, 0);
        String bookTitle = (String) tableModel.getValueAt(selectedRow, 1);
        String memberName = (String) tableModel.getValueAt(selectedRow, 2);
        String loanDate = (String) tableModel.getValueAt(selectedRow, 3);
        String dueDate = (String) tableModel.getValueAt(selectedRow, 4);
        String returnDate = (String) tableModel.getValueAt(selectedRow, 5);
        String penalty = (String) tableModel.getValueAt(selectedRow, 6);
        String status = (String) tableModel.getValueAt(selectedRow, 7);

        // Create and show details dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Loan Details", true);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel detailsPanel = new JPanel(new GridLayout(8, 2, 5, 5));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        addDetailField(detailsPanel, "Loan ID:", loanId);
        addDetailField(detailsPanel, "Book:", bookTitle);
        addDetailField(detailsPanel, "Member:", memberName);
        addDetailField(detailsPanel, "Loan Date:", loanDate);
        addDetailField(detailsPanel, "Due Date:", dueDate);
        addDetailField(detailsPanel, "Return Date:", returnDate.isEmpty() ? "Not returned" : returnDate);
        addDetailField(detailsPanel, "Penalty:", penalty);
        addDetailField(detailsPanel, "Status:", status);

        dialog.add(detailsPanel, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void addDetailField(JPanel panel, String label, String value) {
        panel.add(new JLabel(label, SwingConstants.RIGHT));
        panel.add(new JLabel(value, SwingConstants.LEFT));
    }



}