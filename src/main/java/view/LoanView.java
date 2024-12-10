package view;

import com.library.model.Book;
import com.library.model.Loan;
import com.library.model.User;
import controller.LoanController;

import java.io.File;
import java.util.List;
import java.time.LocalDateTime;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class LoanView extends JPanel {
    private JTable loanTable;
    private DefaultTableModel tableModel;
    private LoanController controller;
    private JComboBox<Book> bookComboBox;
    private JComboBox<User> userComboBox;

    public LoanView() {
        initializeComponents();
    }

    private void initializeComponents() {
        // Main layout
        setLayout(new BorderLayout(10, 10));

        // Loan table
        String[] columns = {"ID", "Book Title", "User Name", "Loan Date", "Due Date", "Return Date", "Penalties", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        loanTable = new JTable(tableModel);
        loanTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        loanTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        loanTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Book Title
        loanTable.getColumnModel().getColumn(2).setPreferredWidth(150); // User Name
        loanTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Loan Date
        loanTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Due Date
        loanTable.getColumnModel().getColumn(5).setPreferredWidth(120); // Return Date
        loanTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // Penalties
        loanTable.getColumnModel().getColumn(7).setPreferredWidth(80);  // Status

        // Top panel for filters
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        JComboBox<String> statusFilter = new JComboBox<>(new String[]{"All", "Active", "Returned", "Overdue"});

        filterPanel.add(new JLabel("Search:"));
        filterPanel.add(searchField);
        filterPanel.add(searchButton);
        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(statusFilter);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton createLoanButton = new JButton("Create New Loan");
        JButton returnBookButton = new JButton("Return Book");
        JButton viewDetailsButton = new JButton("View Details");
        JButton exportButton = new JButton("Export to CSV");

        buttonPanel.add(createLoanButton);
        buttonPanel.add(returnBookButton);
        buttonPanel.add(viewDetailsButton);
        buttonPanel.add(exportButton);

        // Add action listeners
        createLoanButton.addActionListener(e -> showCreateLoanDialog());
        returnBookButton.addActionListener(e -> returnSelectedBook());
        viewDetailsButton.addActionListener(e -> showLoanDetails());
        exportButton.addActionListener(e -> exportToCSV());
        searchButton.addActionListener(e -> searchLoans(searchField.getText()));
        statusFilter.addActionListener(e -> filterByStatus((String) statusFilter.getSelectedItem()));

        // Layout
        add(filterPanel, BorderLayout.NORTH);
        add(new JScrollPane(loanTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Initialize comboboxes for later use
        bookComboBox = new JComboBox<>();
        userComboBox = new JComboBox<>();
    }

    public void setController(LoanController controller) {
        this.controller = controller;
    }

    public void refreshLoanList(List<Loan> loans) {
        tableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Loan loan : loans) {
            String status = determineStatus(loan);
            String returnDate = loan.getReturnDate() != null ?
                    loan.getReturnDate().format(formatter) : "-";
            String penalties = loan.getPenalties() > 0 ?
                    String.format("$%.2f", loan.getPenalties()) : "-";

            tableModel.addRow(new Object[]{
                    loan.getId(),
                    loan.getBook().getTitle(),
                    loan.getUser().getName(),
                    loan.getLoanDate().format(formatter),
                    loan.getDueDate().format(formatter),
                    returnDate,
                    penalties,
                    status
            });
        }
    }

    private String determineStatus(Loan loan) {
        if (loan.getReturnDate() != null) {
            return "Returned";
        } else if (LocalDateTime.now().isAfter(loan.getDueDate())) {
            return "Overdue";
        } else {
            return "Active";
        }
    }

    public void updateBookList(List<Book> availableBooks) {
        bookComboBox.removeAllItems();
        for (Book book : availableBooks) {
            if (book.isAvailable()) {
                bookComboBox.addItem(book);
            }
        }
    }

    public void updateUserList(List<User> users) {
        userComboBox.removeAllItems();
        for (User user : users) {
            userComboBox.addItem(user);
        }
    }

    private void showCreateLoanDialog() {
        if (bookComboBox.getItemCount() == 0 || userComboBox.getItemCount() == 0) {
            showError("No available books or users found");
            return;
        }

        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.add(new JLabel("Select Book:"));
        panel.add(bookComboBox);
        panel.add(new JLabel("Select User:"));
        panel.add(userComboBox);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Create New Loan",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            Book selectedBook = (Book) bookComboBox.getSelectedItem();
            User selectedUser = (User) userComboBox.getSelectedItem();

            if (selectedBook != null && selectedUser != null) {
                controller.createLoan(selectedBook, selectedUser);
            }
        }
    }

    private void returnSelectedBook() {
        int selectedRow = loanTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a loan to return");
            return;
        }

        String status = (String) loanTable.getValueAt(selectedRow, 7);
        if ("Returned".equals(status)) {
            showError("This book has already been returned");
            return;
        }

        int loanId = (int) loanTable.getValueAt(selectedRow, 0);
        String bookTitle = (String) loanTable.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Confirm return of book: " + bookTitle + "?",
                "Confirm Return",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            controller.returnBook(loanId);
        }
    }

    private void showLoanDetails() {
        int selectedRow = loanTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a loan to view details");
            return;
        }

        StringBuilder details = new StringBuilder();
        details.append("Loan Details:\n\n");
        details.append("Loan ID: ").append(loanTable.getValueAt(selectedRow, 0)).append("\n");
        details.append("Book: ").append(loanTable.getValueAt(selectedRow, 1)).append("\n");
        details.append("Borrower: ").append(loanTable.getValueAt(selectedRow, 2)).append("\n");
        details.append("Loan Date: ").append(loanTable.getValueAt(selectedRow, 3)).append("\n");
        details.append("Due Date: ").append(loanTable.getValueAt(selectedRow, 4)).append("\n");
        details.append("Return Date: ").append(loanTable.getValueAt(selectedRow, 5)).append("\n");
        details.append("Penalties: ").append(loanTable.getValueAt(selectedRow, 6)).append("\n");
        details.append("Status: ").append(loanTable.getValueAt(selectedRow, 7)).append("\n");

        JTextArea textArea = new JTextArea(details.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        JOptionPane.showMessageDialog(
                this,
                scrollPane,
                "Loan Details",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void searchLoans(String query) {
        controller.searchLoans(query);
    }

    private void filterByStatus(String status) {
        controller.filterLoansByStatus(status);
    }

    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Loan Report");
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new File(file.getParentFile(), file.getName() + ".csv");
            }
            controller.exportLoansToCSV(file);
        }
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}