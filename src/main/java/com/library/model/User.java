package com.library.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User {
    public enum UserRole {
        MEMBER,
        LIBRARIAN,
        ADMIN
    }

    private final String id;
    private String username;
    private String password;
    private String fullName;
    private String cin;
    private String phoneNumber;
    private String address;
    private UserRole role;
    private List<Loan> loans;

    public User(String id, String username, String password, String fullName, String cin,
                String phoneNumber, String address, UserRole role) {
        validateUser(username, password, fullName, cin, role);
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.cin = cin;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.role = role;
        this.loans = new ArrayList<>();
    }

    private void validateUser(String username, String password, String fullName,
                              String cin, UserRole role) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        // Password required for both librarian and admin
        if ((role == UserRole.LIBRARIAN || role == UserRole.ADMIN)
                && (password == null || password.trim().isEmpty())) {
            throw new IllegalArgumentException("Password required for " + role.toString().toLowerCase());
        }
        if (role == null) {
            throw new IllegalArgumentException("User role cannot be null");
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be empty");
        }
        if (cin == null || cin.trim().isEmpty()) {
            throw new IllegalArgumentException("CIN cannot be empty");
        }
    }

    // Existing getters
    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public UserRole getRole() {
        return role;
    }

    public List<Loan> getLoans() {
        return new ArrayList<>(loans);
    }

    // New getters
    public String getFullName() {
        return fullName;
    }

    public String getCin() {
        return cin;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    // Existing setters
    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        this.username = username;
    }

    public void setPassword(String password) {
        if ((role == UserRole.LIBRARIAN || role == UserRole.ADMIN)
                && (password == null || password.trim().isEmpty())) {
            throw new IllegalArgumentException("Password required for " + role.toString().toLowerCase());
        }
        this.password = password;
    }

    public void setRole(UserRole role) {
        if (role == null) {
            throw new IllegalArgumentException("User role cannot be null");
        }
        this.role = role;
    }

    // New setters
    public void setFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be empty");
        }
        this.fullName = fullName;
    }

    public void setCin(String cin) {
        if (cin == null || cin.trim().isEmpty()) {
            throw new IllegalArgumentException("CIN cannot be empty");
        }
        this.cin = cin;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // Loan management methods
    public void addLoan(Loan loan) {
        if (loan != null) {
            loans.add(loan);
        }
    }

    public void removeLoan(Loan loan) {
        loans.remove(loan);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", cin='" + cin + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'' +
                ", role=" + role +
                ", numberOfLoans=" + loans.size() +
                '}';
    }
}