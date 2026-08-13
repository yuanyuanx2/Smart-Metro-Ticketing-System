package model;

import enums.UserRole;

public abstract class User {

    // 1. Attributes - store the common information shared by all users
    private String userId;
    private String name;
    private String email;
    private String password;
    private UserRole role;

    // 2. Constructor - initialize a User object with the required information
    public User(String userId, String name, String email,
                String password, UserRole role) {

        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // 3. Getter methods - provide controlled access to user information
    // Password getter is purposely omitted to prevent other classes
    // from directly retrieving the user's password
    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public UserRole getRole() {
        return role;
    }

    // 4. Login method - verify the email and password entered by the user
    public boolean login(String email, String password) {
        return this.email.equals(email) &&
                this.password.equals(password);
    }

    // 5. View profile - display the user's basic profile information
    public void viewProfile() {
        System.out.println("User ID : " + userId);
        System.out.println("Name    : " + name);
        System.out.println("Email   : " + email);
        System.out.println("Role    : " + role);
    }

    // 6. Edit profile - update the user's name and email
    public void editProfile(String name, String email) {
        this.name = name;
        this.email = email;
    }
}