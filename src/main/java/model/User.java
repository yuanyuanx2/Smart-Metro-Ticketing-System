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
                String password, UserRole role)
    {

        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // 3. Getter methods - provide controlled access to user information
    // purposely didn't include getPassword()
    // to avoid other classes retrieve user's Password
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
}