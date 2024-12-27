package controller;

import com.library.model.*;
import view.UserView;
import java.util.*;
import java.time.LocalDate;
import java.io.*;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;

public class UserController {
    private final Map<String, User> users;
    private static final String USERS_CSV = "users.csv";

    public UserController() {
        this.users = new HashMap<>();
        loadUsers();
    }

    private void loadUsers() {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_CSV))) {
            String line;
            reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                User user = new User(
                        parts[0],  // id
                        parts[1],  // username
                        parts[2],  // password
                        parts[3],  // fullName
                        parts[4],  // cin
                        parts[5],  // phoneNumber
                        parts[6],  // address
                        User.UserRole.valueOf(parts[7])  // role
                );
                users.put(user.getId(), user);
            }
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
    }

    private void saveUsers() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_CSV))) {
            writer.println("id,username,password,fullName,cin,phoneNumber,address,role");
            for (User user : users.values()) {
                writer.printf("%s,%s,%s,%s,%s,%s,%s,%s%n",
                        user.getId(),
                        user.getUsername(),
                        user.getPassword() != null ? user.getPassword() : "",
                        user.getFullName(),
                        user.getCin(),
                        user.getPhoneNumber() != null ? user.getPhoneNumber() : "",
                        user.getAddress() != null ? user.getAddress() : "",
                        user.getRole()
                );
            }
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    public User addUser(String username, String password, String fullName,
                        String cin, String phoneNumber, String address, User.UserRole role) {
        String id = UUID.randomUUID().toString();
        User user = new User(id, username, password, fullName, cin, phoneNumber, address, role);
        users.put(id, user);
        saveUsers();
        return user;
    }

    // Existing methods remain the same
    public void updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new IllegalArgumentException("User not found");
        }
        users.put(user.getId(), user);
        saveUsers();
    }

    public void deleteUser(String id) {
        if (!users.containsKey(id)) {
            throw new IllegalArgumentException("User not found");
        }
        users.remove(id);
        saveUsers();
    }

    public User getUser(String id) {
        return users.get(id);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User authenticateUser(String username, String password) {
        return users.values().stream()
                .filter(user -> user.getUsername().equals(username) &&
                        (user.getRole() == User.UserRole.LIBRARIAN ||
                                user.getRole() == User.UserRole.ADMIN) &&
                        user.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    public void createDefaultLibrarian() {
        // Check if there are any librarians or admins
        boolean adminExists = getAllUsers().stream()
                .anyMatch(user -> user.getRole() == User.UserRole.ADMIN);

        // If no admin exists, create default one
        if (!adminExists) {
            addUser("admin", "admin123", "Administrator",
                    "ADMIN1", "0123456789", "Library HQ", User.UserRole.ADMIN);
        }
    }
}