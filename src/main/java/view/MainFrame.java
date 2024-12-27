package view;

import controller.*;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    // Layouts
    private final CardLayout cardLayout;
    private final JPanel mainPanel;

    // Controllers
    private final LoginController loginController;
    private final BookController bookController;
    private final UserController userController;
    private final LoanController loanController;

    // Views
    private MenuView menuView;
    private BookView bookView;
    private UserView userView;
    private LoanView loanView;
    private LoginView loginView;
    private JMenuBar menuBar;
    private JMenuItem usersItem;
    private JMenuItem loansItem;
    private JMenuItem booksItem;
    private JMenuItem homeItem;
    private JMenuItem loginItem;
    private JMenuItem logoutItem;

    public MainFrame(LoginController loginController, BookController bookController,
                     UserController userController, LoanController loanController) {
        this.loginController = loginController;
        this.bookController = bookController;
        this.userController = userController;
        this.loanController = loanController;

        // Initialize layout components
        this.cardLayout = new CardLayout();
        this.mainPanel = new JPanel(cardLayout);

        // Setup frame properties
        setTitle("Library Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Initialize components
        initializeViews();
        initializeMenuBar();

        // Add main panel to frame
        add(mainPanel);
    }

    private void initializeViews() {
        // Create views
        menuView = new MenuView(this);
        bookView = new BookView(bookController);
        userView = new UserView(userController, loginController);
        loanView = new LoanView(loanController, bookController, userController);
        loginView = new LoginView(this, loginController);

        // Add views to card layout
        mainPanel.add(menuView, "MENU");
        mainPanel.add(bookView, "BOOKS");
        mainPanel.add(userView, "USERS");
        mainPanel.add(loanView, "LOANS");
        mainPanel.add(loginView, "LOGIN");

        // Show initial view
        showCard("MENU");
    }

    private void initializeMenuBar() {
        menuBar = new JMenuBar();

        // Create regular menu items (no drop-downs)
        homeItem = new JMenuItem("Home");
        homeItem.addActionListener(e -> showCard("MENU"));

        booksItem = new JMenuItem("Books");
        booksItem.addActionListener(e -> showCard("BOOKS"));

        usersItem = new JMenuItem("Users");
        usersItem.addActionListener(e -> showCard("USERS"));

        loansItem = new JMenuItem("Loans");
        loansItem.addActionListener(e -> showCard("LOANS"));

        loginItem = new JMenuItem("Login");
        loginItem.addActionListener(e -> showCard("LOGIN"));

        logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> {
            loginController.logout();
            showCard("MENU");
            updateMenuVisibility(false);
        });

        // Add all items directly to the menu bar
        menuBar.add(homeItem);
        menuBar.add(booksItem);
        menuBar.add(usersItem);
        menuBar.add(loansItem);
        menuBar.add(loginItem);
        menuBar.add(logoutItem);

        // Initially show only login
        updateMenuVisibility(false);

        setJMenuBar(menuBar);
    }

    public void updateMenuVisibility(boolean isLoggedIn) {
        boolean isLibrarian = isLoggedIn && loginController.isLibrarian();
        boolean isAdmin = isLoggedIn && loginController.isAdmin();

        // Items visible when logged in
        homeItem.setVisible(isLoggedIn);
        booksItem.setVisible(isLoggedIn);
        usersItem.setVisible(isLibrarian || isAdmin);
        loansItem.setVisible(isLibrarian || isAdmin);
        logoutItem.setVisible(isLoggedIn);

        // Login item only visible when not logged in
        loginItem.setVisible(!isLoggedIn);

        repaint();
    }

    public void showCard(String cardName) {
        // Check permissions for restricted areas
        if (cardName.equals("USERS") || cardName.equals("LOANS")) {
            if (!loginController.isLibrarian() && !loginController.isAdmin()) {
                JOptionPane.showMessageDialog(this,
                        "Only librarians & admins can access this section",
                        "Access Denied",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        cardLayout.show(mainPanel, cardName);
    }

    public LoginController getLoginController() {
        return loginController;
    }
}