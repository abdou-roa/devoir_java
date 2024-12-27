package view;

import controller.LoginController;
import javax.swing.*;
import java.awt.*;

public class LoginView extends JPanel {
    private final MainFrame mainFrame;
    private final LoginController loginController;
    private final JTextField usernameField;
    private final JPasswordField passwordField;

    public LoginView(MainFrame mainFrame, LoginController loginController) {
        this.mainFrame = mainFrame;
        this.loginController = loginController;

        // Initialize components
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);

        setLayout(new BorderLayout());
        initializeUI();
    }

    private void initializeUI() {
        // Create main login panel
        JPanel loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Create title
        JLabel titleLabel = new JLabel("Librarian Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginPanel.add(titleLabel, gbc);

        // Username field
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        loginPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        loginPanel.add(usernameField, gbc);

        // Password field
        gbc.gridx = 0;
        gbc.gridy = 2;
        loginPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        loginPanel.add(passwordField, gbc);

        // Login button
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> handleLogin());
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(loginButton, gbc);

        // Add login panel to center
        add(loginPanel, BorderLayout.CENTER);

        // Add action listener for Enter key
        passwordField.addActionListener(e -> handleLogin());
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (loginController.login(username, password)) {
            // Clear the fields
            usernameField.setText("");
            passwordField.setText("");

            // Update menu visibility
            mainFrame.updateMenuVisibility(true);

            // Show success message
            JOptionPane.showMessageDialog(this,
                    "Login successful!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            // Switch to books view
            mainFrame.showCard("BOOKS");
        } else {
            JOptionPane.showMessageDialog(this,
                    "Invalid username or password",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }
}