package view;
import com.library.model.Book;
import controller.BookController;

import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class BookView extends JPanel {
    private JTextField searchField;
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private BookController controller;

    public BookView() {
        initializeComponents();
    }

    private void initializeComponents() {
        // Search panel
        JPanel searchPanel = new JPanel();
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> controller.searchBooks(searchField.getText()));
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Book table
        String[] columns = {"ID", "Title", "Author", "Year", "Genre", "Available"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        bookTable = new JTable(tableModel);
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Buttons panel
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Book");
        JButton editButton = new JButton("Edit Book");
        JButton deleteButton = new JButton("Delete Book");
        addButton.addActionListener(e -> showAddBookDialog());
        editButton.addActionListener(e -> showEditBookDialog());
        deleteButton.addActionListener(e -> deleteSelectedBook());
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        // Layout
        setLayout(new BorderLayout());
        add(searchPanel, BorderLayout.NORTH);
        add(new JScrollPane(bookTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void setController(BookController controller) {
        this.controller = controller;
    }

    public void displaySearchResults(List<Book> books) {
        tableModel.setRowCount(0);
        for (Book book : books) {
            tableModel.addRow(new Object[]{
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublicationYear(),
                    book.getGenre(),
                    book.isAvailable() ? "Available" : "Checked Out"
            });
        }
    }

    public void refreshBookList(List<Book> books) {
        displaySearchResults(books);
    }

    private void showAddBookDialog() {
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField yearField = new JTextField();
        JTextField genreField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Author:"));
        panel.add(authorField);
        panel.add(new JLabel("Year:"));
        panel.add(yearField);
        panel.add(new JLabel("Genre:"));
        panel.add(genreField);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Add New Book", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                controller.addBook(
                        titleField.getText(),
                        authorField.getText(),
                        Integer.parseInt(yearField.getText()),
                        genreField.getText()
                );
            } catch (NumberFormatException e) {
                showError("Invalid year format");
            }
        }
    }

    private void showEditBookDialog() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a book to edit");
            return;
        }

        int bookId = (int) bookTable.getValueAt(selectedRow, 0);
        String currentTitle = (String) bookTable.getValueAt(selectedRow, 1);
        String currentAuthor = (String) bookTable.getValueAt(selectedRow, 2);
        int currentYear = (int) bookTable.getValueAt(selectedRow, 3);
        String currentGenre = (String) bookTable.getValueAt(selectedRow, 4);

        JTextField titleField = new JTextField(currentTitle);
        JTextField authorField = new JTextField(currentAuthor);
        JTextField yearField = new JTextField(String.valueOf(currentYear));
        JTextField genreField = new JTextField(currentGenre);

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Author:"));
        panel.add(authorField);
        panel.add(new JLabel("Year:"));
        panel.add(yearField);
        panel.add(new JLabel("Genre:"));
        panel.add(genreField);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Edit Book", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                controller.updateBook(
                        bookId,
                        titleField.getText(),
                        authorField.getText(),
                        Integer.parseInt(yearField.getText()),
                        genreField.getText()
                );
            } catch (NumberFormatException e) {
                showError("Invalid year format");
            }
        }
    }

    private void deleteSelectedBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a book to delete");
            return;
        }

        int bookId = (int) bookTable.getValueAt(selectedRow, 0);
        String bookTitle = (String) bookTable.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete the book: " + bookTitle + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            controller.deleteBook(bookId);
        }
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}