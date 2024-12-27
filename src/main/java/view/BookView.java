package view;

import controller.BookController;
import com.library.model.Book;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.Timer;

import java.util.List;

public class BookView extends JPanel {
    private final BookController bookController;
    private final JTable bookTable;
    private final DefaultTableModel tableModel;
    private final JTextField searchField;
    private final JComboBox<String> filterCombo;
    private final JButton addButton;
    private final JButton editButton;
    private final JButton deleteButton;

    public BookView(BookController bookController) {
        this.bookController = bookController;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Initialize components
        String[] columns = {"ID", "Title", "Author", "Year", "Genre", "Quantity", "Available"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bookTable = new JTable(tableModel);
        searchField = new JTextField(20);
        filterCombo = new JComboBox<>(new String[]{"All Books", "Available Books", "Out of Stock"});
        addButton = new JButton("Add Book");
        editButton = new JButton("Edit Book");
        deleteButton = new JButton("Delete Book");

        initializeUI();
        setupSearchField();
        addListeners();
        refreshBookTable();
    }

    private void initializeUI() {
        // Search Panel (Top)
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Filter:"));
        searchPanel.add(filterCombo);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> performSearch());
        searchPanel.add(searchButton);
        add(searchPanel, BorderLayout.NORTH);

        // Table Panel (Center)
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        bookTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Title
        bookTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Author
        bookTable.getColumnModel().getColumn(3).setPreferredWidth(70);  // Year
        bookTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Genre
        bookTable.getColumnModel().getColumn(5).setPreferredWidth(70);  // Quantity
        bookTable.getColumnModel().getColumn(6).setPreferredWidth(70);  // Available

        JScrollPane scrollPane = new JScrollPane(bookTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        add(scrollPane, BorderLayout.CENTER);

        // Button Panel (Bottom)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addListeners() {
        addButton.addActionListener(e -> showAddBookDialog());
        editButton.addActionListener(e -> showEditBookDialog());
        deleteButton.addActionListener(e -> deleteSelectedBook());
        filterCombo.addActionListener(e -> refreshBookTable());

        searchField.addActionListener(e -> performSearch());
    }

    private void refreshBookTable() {
        tableModel.setRowCount(0);
        List<Book> books;

        String filter = (String) filterCombo.getSelectedItem();
        if ("Available Books".equals(filter)) {
            books = bookController.getAvailableBooks();
        } else if ("Out of Stock".equals(filter)) {
            books = bookController.getAllBooks();
            books.removeIf(Book::isAvailable);
        } else {
            books = bookController.getAllBooks();
        }

        for (Book book : books) {
            Object[] row = {
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublicationYear(),
                    book.getGenre(),
                    book.getQuantity(),
                    book.isAvailable() ? "Yes" : "No"
            };
            tableModel.addRow(row);
        }
    }

    private Timer searchTimer;

    private void setupSearchField() {
        // Create a timer with a 300ms delay
        searchTimer = new Timer(300, e -> performSearch());
        searchTimer.setRepeats(false);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                searchTimer.restart();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                searchTimer.restart();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                searchTimer.restart();
            }
        });
    }

    private void performSearch() {
        // Use SwingUtilities.invokeLater to prevent potential concurrency issues
        SwingUtilities.invokeLater(() -> {
            String searchTerm = searchField.getText().trim();
            if (!searchTerm.isEmpty()) {
                tableModel.setRowCount(0);
                List<Book> books = bookController.searchBooks(searchTerm);
                for (Book book : books) {
                    Object[] row = {
                            book.getId(),
                            book.getTitle(),
                            book.getAuthor(),
                            book.getPublicationYear(),
                            book.getGenre(),
                            book.getQuantity(),
                            book.isAvailable() ? "Yes" : "No"
                    };
                    tableModel.addRow(row);
                }
            } else {
                refreshBookTable();
            }
        });
    }

    private void showAddBookDialog() {
        String[] genres = {"Fiction", "Non-Fiction", "Science Fiction", "Fantasy", "Mystery", "Romance", "Horror"};
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Book", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = createDialogConstraints();

        // Create input fields
        JTextField titleField = new JTextField(20);
        JTextField authorField = new JTextField(20);
        JTextField yearField = new JTextField(20);
        JComboBox<String> genreComboBox = new JComboBox<>(genres);
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 0, 999, 1));

        // Add components to dialog
        addDialogComponent(dialog, "Title:", titleField, gbc, 0);
        addDialogComponent(dialog, "Author:", authorField, gbc, 1);
        addDialogComponent(dialog, "Year:", yearField, gbc, 2);
        addDialogComponent(dialog, "Genre:", genreComboBox, gbc, 3);
        addDialogComponent(dialog, "Quantity:", quantitySpinner, gbc, 4);

        // Add save button
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {
                int year = Integer.parseInt(yearField.getText());
                int quantity = (Integer) quantitySpinner.getValue();

                bookController.addBook(
                        titleField.getText(),
                        authorField.getText(),
                        year,
                        (String) genreComboBox.getSelectedItem(),
                        quantity
                );

                refreshBookTable();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Please enter valid numbers for year and quantity.",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog,
                        ex.getMessage(),
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy++;
        dialog.add(saveButton, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showEditBookDialog() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a book to edit",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String bookId = (String) tableModel.getValueAt(selectedRow, 0);
        Book book = bookController.getBook(bookId);
        if (book == null) {
            JOptionPane.showMessageDialog(this,
                    "Book not found",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Book", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = createDialogConstraints();

        // Create input fields with existing values
        JTextField titleField = new JTextField(book.getTitle(), 20);
        JTextField authorField = new JTextField(book.getAuthor(), 20);
        JTextField yearField = new JTextField(String.valueOf(book.getPublicationYear()), 20);
        JTextField genreField = new JTextField(book.getGenre(), 20);
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(book.getQuantity(), 0, 999, 1));

        // Add components to dialog
        addDialogComponent(dialog, "Title:", titleField, gbc, 0);
        addDialogComponent(dialog, "Author:", authorField, gbc, 1);
        addDialogComponent(dialog, "Year:", yearField, gbc, 2);
        addDialogComponent(dialog, "Genre:", genreField, gbc, 3);
        addDialogComponent(dialog, "Quantity:", quantitySpinner, gbc, 4);

        // Add save button
        JButton saveButton = new JButton("Save Changes");
        saveButton.addActionListener(e -> {
            try {
                book.setTitle(titleField.getText());
                book.setAuthor(authorField.getText());
                book.setPublicationYear(Integer.parseInt(yearField.getText()));
                book.setGenre(genreField.getText());
                book.setQuantity((Integer) quantitySpinner.getValue());

                bookController.updateBook(book);
                refreshBookTable();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Please enter valid numbers for year and quantity.",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog,
                        ex.getMessage(),
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy++;
        dialog.add(saveButton, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deleteSelectedBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a book to delete",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String bookId = (String) tableModel.getValueAt(selectedRow, 0);
        String bookTitle = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete the book: " + bookTitle + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                bookController.deleteBook(bookId);
                refreshBookTable();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error deleting book: " + e.getMessage(),
                        "Delete Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private GridBagConstraints createDialogConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        return gbc;
    }

    private void addDialogComponent(JDialog dialog, String label, JComponent component,
                                    GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        dialog.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        dialog.add(component, gbc);
    }
}