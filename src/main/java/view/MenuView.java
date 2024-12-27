package view;

import javax.swing.*;
import java.awt.*;
import controller.LoginController;

public class MenuView extends JPanel {
    private final MainFrame mainFrame;
    private final LoginController loginController;
    private JPanel menuPanel;

    public MenuView(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.loginController = mainFrame.getLoginController();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initializeUI();
    }

    private void initializeUI() {
        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("Library Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Menu Panel
        menuPanel = new JPanel(new GridBagLayout());
        updateMenuOptions();
        add(menuPanel, BorderLayout.CENTER);

        // Footer Panel
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel footerLabel = new JLabel("© 2024 Library Management System");
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        footerPanel.add(footerLabel);
        add(footerPanel, BorderLayout.SOUTH);
    }

    public void updateMenuOptions() {
        menuPanel.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        if (loginController.isLoggedIn() && loginController.isLibrarian()) {
            // Librarian Menu
            addMenuButton("Browse Books", "BOOKS", gbc);
            addMenuButton("Manage Users", "USERS", gbc);
            addMenuButton("Manage Loans", "LOANS", gbc);
            addMenuButton("Logout", e -> {
                loginController.logout();
                updateMenuOptions();
            }, gbc);
        } else if (loginController.isLoggedIn()) {
            // Regular User Menu
            addMenuButton("Browse Books", "BOOKS", gbc);
            addMenuButton("Logout", e -> {
                loginController.logout();
                updateMenuOptions();
            }, gbc);
        } else {
            // Public Menu
//            addMenuButton("Browse Books", "BOOKS", gbc);
            addMenuButton("Librarian Login", "LOGIN", gbc);
        }

        menuPanel.revalidate();
        menuPanel.repaint();
    }

    private void addMenuButton(String text, String cardName, GridBagConstraints gbc) {
        JButton button = createStyledButton(text);
        button.addActionListener(e -> mainFrame.showCard(cardName));
        menuPanel.add(button, gbc);
    }

    private void addMenuButton(String text, java.awt.event.ActionListener listener, GridBagConstraints gbc) {
        JButton button = createStyledButton(text);
        button.addActionListener(listener);
        menuPanel.add(button, gbc);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(200, 40));
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setFocusPainted(false);
        return button;
    }
}