package controller;
import java.util.*;
import java.time.LocalDate;
import java.io.*;
import java.util.stream.Collectors;
import com.library.model.*;
public class LoginController {
    private final UserController userController;
    private User currentUser;

    public LoginController(UserController userController) {
        this.userController = userController;
    }

    public boolean login(String username, String password) {
        currentUser = userController.authenticateUser(username, password);
        return currentUser != null;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isLibrarian() {
        return currentUser != null &&
                currentUser.getRole() == User.UserRole.LIBRARIAN;
    }

    public boolean isAdmin() {
        return currentUser != null &&
                currentUser.getRole() == User.UserRole.ADMIN;
    }
}

