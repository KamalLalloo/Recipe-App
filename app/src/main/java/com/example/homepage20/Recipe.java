package com.example.homepage20;

import java.io.Serializable;

public class Recipe implements Serializable {
    private String recipeID;
    private String recipeName;
    private String username;
    private String ingredients;
    private String instructions;
    private String description;
    private String cookingTime;

    public Recipe(String recipeID, String recipeName, String username, String ingredients, String instructions,String description) {
        this.recipeID = recipeID;
        this.recipeName = recipeName;
        this.username = username;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.description = description;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public String getUsername() {
        return username;
    }

    public String getIngredients() {
        return ingredients;
    }

    public String getInstructions() {
        return instructions;
    }

    public String getDescription() {
        return description;
    }

    public String getRecipeID() {
        return recipeID;
    }

    public String getCookingTime() {
        return cookingTime;
    }
}
