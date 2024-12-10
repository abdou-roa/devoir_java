package controller;

import com.library.model.User;
import view.UserView;

import java.util.ArrayList;
import java.util.List;


public class UserController {
    private UserView view;
    private List<User> users;

    public UserController(UserView view) {
        this.view = view;
        this.users = new ArrayList<>();
    }

    public void addUser(String name, String email, String password, User.UserRole role) {
        User user = new User(generateId(), name, email, password, role);
        users.add(user);
        view.refreshUserList(users);
    }

    public void updateUser(int id, String name, String email, User.UserRole role) {
        users.stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .ifPresent(user -> {
                    user.setName(name);
                    user.setEmail(email);
                    user.setRole(role);
                });
        view.refreshUserList(users);
    }

    public void deleteUser(int id) {
        users.removeIf(u -> u.getId() == id);
        view.refreshUserList(users);
    }

    private int generateId() {
        return users.size() + 1;
    }
}
