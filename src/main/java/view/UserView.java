package view;

import com.library.model.User;
import controller.UserController;

import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class UserView extends JPanel {
    private JTable userTable;
    private DefaultTableModel tableModel;
    private UserController controller;

    public UserView() {
        initializeComponents();
    }

    private void initializeComponents() {
        // User table
        String[] columns = {"ID", "Name", "Email", "Role"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        userTable = new JTable(tableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Buttons panel
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add User");
        JButton editButton = new JButton("Edit User");
        JButton deleteButton = new JButton("Delete User");
        addButton.addActionListener(e -> showAddUserDialog());
        editButton.addActionListener(e -> showEditUserDialog());
        deleteButton.addActionListener(e -> deleteSelectedUser());
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        // Layout
        setLayout(new BorderLayout());
        add(new JScrollPane(userTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void setController(UserController controller) {
        this.controller = controller;
    }

    public void refreshUserList(List<User> users) {
        tableModel.setRowCount(0);
        for (User user : users) {
            tableModel.addRow(new Object[]{
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole()
            });
        }
    }

    private void showAddUserDialog() {
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JComboBox<User.UserRole> roleComboBox = new JComboBox<>(User.UserRole.values());

        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Role:"));
        panel.add(roleComboBox);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Add New User", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String password = new String(passwordField.getPassword());
            if (validateFields(nameField.getText(), emailField.getText(), password)) {
                controller.addUser(
                        nameField.getText(),
                        emailField.getText(),
                        password,
                        (User.UserRole) roleComboBox.getSelectedItem()
                );
            }
        }
    }

    private void showEditUserDialog() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a user to edit");
            return;
        }

        int userId = (int) userTable.getValueAt(selectedRow, 0);
        String currentName = (String) userTable.getValueAt(selectedRow, 1);
        String currentEmail = (String) userTable.getValueAt(selectedRow, 2);
        User.UserRole currentRole = (User.UserRole) userTable.getValueAt(selectedRow, 3);

        JTextField nameField = new JTextField(currentName);
        JTextField emailField = new JTextField(currentEmail);
        JComboBox<User.UserRole> roleComboBox = new JComboBox<>(User.UserRole.values());
        roleComboBox.setSelectedItem(currentRole);

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Role:"));
        panel.add(roleComboBox);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Edit User", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            if (validateFields(nameField.getText(), emailField.getText(), null)) {
                controller.updateUser(
                        userId,
                        nameField.getText(),
                        emailField.getText(),
                        (User.UserRole) roleComboBox.getSelectedItem()
                );
            }
        }
    }

    private void deleteSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a user to delete");
            return;
        }

        int userId = (int) userTable.getValueAt(selectedRow, 0);
        String userName = (String) userTable.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete the user: " + userName + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            controller.deleteUser(userId);
        }
    }

    private boolean validateFields(String name, String email, String password) {
        if (name == null || name.trim().isEmpty()) {
            showError("Name cannot be empty");
            return false;
        }
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("Invalid email format");
            return false;
        }
        if (password != null && password.trim().isEmpty()) {
            showError("Password cannot be empty");
            return false;
        }
        return true;
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