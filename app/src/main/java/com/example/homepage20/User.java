package com.example.homepage20;

public class User {
    private String name;
    private String username; // Unique identifier for each user

    public User(String name, String username) {
        this.name = name;
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }
}
