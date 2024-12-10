package service;

import controller.BookController;
import controller.UserController;
import controller.LoanController;
import view.BookView;
import view.UserView;
import view.LoanView;

import javax.swing.*;
import java.awt.*;

public class LibraryManagementSystem {
    public static void main(String[] args) {
        // Set Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create the main frame
        JFrame mainFrame = new JFrame("Library Management System");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1000, 700);

        // Create views
        BookView bookView = new BookView();
        UserView userView = new UserView();
        LoanView loanView = new LoanView();

        // Create controllers
        BookController bookController = new BookController(bookView);
        UserController userController = new UserController(userView);
        LoanController loanController = new LoanController(loanView);

        // Set controllers to views
        bookView.setController(bookController);
        userView.setController(userController);
        loanView.setController(loanController);

        // Create tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Books", new ImageIcon(), bookView, "Manage Books");
        tabbedPane.addTab("Users", new ImageIcon(), userView, "Manage Users");
        tabbedPane.addTab("Loans", new ImageIcon(), loanView, "Manage Loans");

        // Add tabbed pane to main frame
        mainFrame.add(tabbedPane);

        // Center the frame on screen
        mainFrame.setLocationRelativeTo(null);

        // Display the main frame
        SwingUtilities.invokeLater(() -> {
            mainFrame.setVisible(true);
        });
    }
}