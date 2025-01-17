package com.example.homepage20;

public class Recipes {
    private String recipeName;
    private String username;

    public Recipes(String recipeName, String username) {
        this.recipeName = recipeName;
        this.username = username;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public String getUsername() {
        return username;
    }
}