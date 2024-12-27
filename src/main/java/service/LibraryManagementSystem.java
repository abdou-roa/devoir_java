package service;

import controller.*;
import view.*;

import javax.swing.*;
import java.awt.*;

public class LibraryManagementSystem {
    public static void main(String[] args) {
        // Initialize controllers
        BookController bookController = new BookController();
        UserController userController = new UserController();
        userController.createDefaultLibrarian();
        LoginController loginController = new LoginController(userController);
        LoanController loanController = new LoanController(bookController, userController);

        // Create and show main frame using SwingUtilities
        SwingUtilities.invokeLater(() -> {
            try {
                // Set system look and feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                // Create main frame
                MainFrame mainFrame = new MainFrame(
                        loginController,
                        bookController,
                        userController,
                        loanController
                );

                // Configure and display the frame
                mainFrame.setTitle("Library Management System");
                mainFrame.pack();
                mainFrame.setLocationRelativeTo(null); // Center on screen
                mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                mainFrame.setMinimumSize(mainFrame.getSize());
                mainFrame.setVisible(true);

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Error starting application: " + e.getMessage(),
                        "Startup Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}