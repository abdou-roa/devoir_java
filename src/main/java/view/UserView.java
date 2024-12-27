package view;

import controller.LoginController;
import controller.UserController;
import com.library.model.User;
import com.library.model.User.UserRole;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserView extends JPanel {
    private final UserController userController;

    private final  LoginController loginController;
    private final JTable userTable;
    private final DefaultTableModel tableModel;
    private final JComboBox<String> filterCombo;
    private final JTextField searchField;

    public UserView(UserController userController, LoginController loginController) {
        this.userController = userController;
        this.loginController = loginController;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Initialize components
        String[] columns = {"ID", "Username", "Full Name", "Role", "Active Loans"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(tableModel);
        filterCombo = new JComboBox<>(new String[]{"All Users", "Members", "Librarians"});
        searchField = new JTextField(20);

        initializeUI();
        refreshUserTable();
    }

    private void initializeUI() {
        // Top Panel - Search and Filter
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchField);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> performSearch());
        topPanel.add(searchButton);

        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(new JLabel("Filter:"));
        topPanel.add(filterCombo);
        filterCombo.addActionListener(e -> refreshUserTable());

        add(topPanel, BorderLayout.NORTH);

        // Center Panel - User Table
        configureUserTable();
        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Panel - Action Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Add User");
        JButton editButton = new JButton("Edit User");
        JButton deleteButton = new JButton("Delete User");
        JButton viewDetailsButton = new JButton("View Details");

        addButton.addActionListener(e -> showAddUserDialog());
        editButton.addActionListener(e -> showEditUserDialog());
        deleteButton.addActionListener(e -> deleteSelectedUser());
        viewDetailsButton.addActionListener(e -> showUserDetails());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(viewDetailsButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void configureUserTable() {
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.getColumnModel().getColumn(0).setPreferredWidth(100); // ID
        userTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Username
        userTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Role
        userTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Active Loans
    }

    private void refreshUserTable() {
        tableModel.setRowCount(0);
        List<User> users = userController.getAllUsers();

        String filter = (String) filterCombo.getSelectedItem();
        for (User user : users) {
            if (shouldShowUser(user, filter)) {
                addUserToTable(user);
            }
        }
    }

    private boolean shouldShowUser(User user, String filter) {
        return switch (filter) {
            case "Members" -> user.getRole() == UserRole.MEMBER;
            case "Librarians" -> user.getRole() == UserRole.LIBRARIAN;
            default -> true;
        };
    }

    private void addUserToTable(User user) {
        Object[] row = {
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getRole().toString(),
                user.getLoans().size()
        };
        tableModel.addRow(row);
    }

    private void performSearch() {
        String searchTerm = searchField.getText().toLowerCase().trim();
        if (searchTerm.isEmpty()) {
            refreshUserTable();
            return;
        }

        tableModel.setRowCount(0);
        List<User> users = userController.getAllUsers();
        String filter = (String) filterCombo.getSelectedItem();

        users.stream()
                .filter(user -> shouldShowUser(user, filter))
                .filter(user ->
                        user.getUsername().toLowerCase().contains(searchTerm) ||
                                user.getId().toLowerCase().contains(searchTerm))
                .forEach(this::addUserToTable);
    }

    private void showAddUserDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New User", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Create input fields
        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JTextField fullNameField = new JTextField(20);
        JTextField cinField = new JTextField(20);
        JTextField phoneField = new JTextField(20);
        JTextField addressField = new JTextField(20);

        // Create role combo box with appropriate roles based on current user's role
        JComboBox<UserRole> roleCombo;
        if (loginController.isAdmin()) {
            // Admin can add all types of users
            roleCombo = new JComboBox<>(UserRole.values());
        } else {
            // Librarian can only add members
            roleCombo = new JComboBox<>(new UserRole[]{UserRole.MEMBER});
            roleCombo.setEnabled(false);  // Disable it since there's only one option
        }

        // Add components
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        dialog.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        dialog.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel passwordLabel = new JLabel("Password:");
        dialog.add(passwordLabel, gbc);
        gbc.gridx = 1;
        dialog.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        dialog.add(fullNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(new JLabel("CIN:"), gbc);
        gbc.gridx = 1;
        dialog.add(cinField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        dialog.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        dialog.add(phoneField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        dialog.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        dialog.add(addressField, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        dialog.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        dialog.add(roleCombo, gbc);

        // Handle password field visibility based on role selection
        roleCombo.addActionListener(e -> {
            UserRole selectedRole = (UserRole) roleCombo.getSelectedItem();
            boolean isAdminOrLibrarian = selectedRole == UserRole.LIBRARIAN || selectedRole == UserRole.ADMIN;

            passwordField.setVisible(isAdminOrLibrarian);
            passwordLabel.setVisible(isAdminOrLibrarian);
            passwordField.setText("");

            // Repack the dialog to adjust its size
            dialog.pack();
        });

        // Create User button
        JButton createButton = new JButton("Create User");
        createButton.addActionListener(e -> {
            try {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword());
                String fullName = fullNameField.getText().trim();
                String cin = cinField.getText().trim();
                String phone = phoneField.getText().trim();
                String address = addressField.getText().trim();
                UserRole role = (UserRole) roleCombo.getSelectedItem();

                userController.addUser(username, password, fullName, cin, phone, address, role);
                refreshUserTable();
                dialog.dispose();

                JOptionPane.showMessageDialog(this,
                        "User created successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Error creating user: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridx = 0; gbc.gridy = 7;
        gbc.gridwidth = 2;
        dialog.add(createButton, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showEditUserDialog() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a user to edit",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String userId = (String) tableModel.getValueAt(selectedRow, 0);
        User user = userController.getUser(userId);
        if (user == null) {
            JOptionPane.showMessageDialog(this,
                    "User not found",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Edit User", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Create input fields with existing values
        JTextField usernameField = new JTextField(user.getUsername(), 20);
        JPasswordField passwordField = new JPasswordField(user.getPassword(), 20);
        JComboBox<UserRole> roleCombo = new JComboBox<>(UserRole.values());
        roleCombo.setSelectedItem(user.getRole());

        // Add components
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        dialog.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        dialog.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        dialog.add(roleCombo, gbc);

        // Update button
        JButton updateButton = new JButton("Update User");
        updateButton.addActionListener(e -> {
            try {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                UserRole role = (UserRole) roleCombo.getSelectedItem();

                if (username.trim().isEmpty()) {
                    throw new IllegalArgumentException("Username cannot be empty");
                }

                if (role == UserRole.LIBRARIAN && password.trim().isEmpty()) {
                    throw new IllegalArgumentException("Password required for librarian");
                }

                user.setUsername(username);
                user.setPassword(password);
                user.setRole(role);

                userController.updateUser(user);
                refreshUserTable();
                dialog.dispose();

                JOptionPane.showMessageDialog(this,
                        "User updated successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Error updating user: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        dialog.add(updateButton, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deleteSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a user to delete",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String userId = (String) tableModel.getValueAt(selectedRow, 0);
        String username = (String) tableModel.getValueAt(selectedRow, 1);
        int activeLoans = (int) tableModel.getValueAt(selectedRow, 3);

        if (activeLoans > 0) {
            JOptionPane.showMessageDialog(this,
                    "Cannot delete user with active loans",
                    "Delete Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete user: " + username + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                userController.deleteUser(userId);
                refreshUserTable();
                JOptionPane.showMessageDialog(this,
                        "User deleted successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error deleting user: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showUserDetails() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a user to view details",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String userId = (String) tableModel.getValueAt(selectedRow, 0);
        User user = userController.getUser(userId);
        if (user == null) {
            JOptionPane.showMessageDialog(this,
                    "User not found",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "User Details", true);
        dialog.setLayout(new BorderLayout(10, 10));

        // Details Panel
        JPanel detailsPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        addDetailField(detailsPanel, "User ID:", user.getId());
        addDetailField(detailsPanel, "Username:", user.getUsername());
        addDetailField(detailsPanel, "Role:", user.getRole().toString());
        addDetailField(detailsPanel, "Active Loans:", String.valueOf(user.getLoans().size()));

        dialog.add(detailsPanel, BorderLayout.CENTER);

        // Loan History Panel (if user is a member)
        if (user.getRole() == UserRole.MEMBER && !user.getLoans().isEmpty()) {
            JPanel loanPanel = new JPanel(new BorderLayout());
            loanPanel.setBorder(BorderFactory.createTitledBorder("Loan History"));

            String[] columns = {"Book Title", "Loan Date", "Due Date", "Return Date", "Penalty"};
            DefaultTableModel loanModel = new DefaultTableModel(columns, 0);
            JTable loanTable = new JTable(loanModel);

            for (var loan : user.getLoans()) {
                Object[] row = {
                        loan.getBook().getTitle(),
                        loan.getLoanDate().toString(),
                        loan.getDueDate().toString(),
                        loan.getReturnDate() != null ? loan.getReturnDate().toString() : "Not Returned",
                        String.format("$%.2f", loan.getPenalty())
                };
                loanModel.addRow(row);
            }

            JScrollPane loanScroll = new JScrollPane(loanTable);
            loanScroll.setPreferredSize(new Dimension(600, 150));
            loanPanel.add(loanScroll, BorderLayout.CENTER);
            dialog.add(loanPanel, BorderLayout.SOUTH);
        }

        // Close Button
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