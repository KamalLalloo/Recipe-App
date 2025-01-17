package com.example.homepage20;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class RecipeFragment extends Fragment {
    static public Recipe addRecipes;
    private String recipeID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recipe, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView recipeName = view.findViewById(R.id.RecipeName);
        TextView userName = view.findViewById(R.id.UserName);
        TextView ingredients = view.findViewById(R.id.Ingredients);
        TextView instructions = view.findViewById(R.id.Instructions);
        ImageView backButton = view.findViewById(R.id.goBack);
        ImageView commentButton = view.findViewById(R.id.comment);

        Recipe recipe = (Recipe) getArguments().getSerializable("recipe");

        if (recipe != null) {
            recipeName.setText(recipe.getRecipeName());
            userName.setText("By " + recipe.getUsername());
            ingredients.setText(recipe.getIngredients());
            instructions.setText(recipe.getInstructions());
            recipeID = recipe.getRecipeID();
            Button btnAddToMealPlan = view.findViewById(R.id.add_mealplan);
            btnAddToMealPlan.setOnClickListener(v -> {
                addRecipes = recipe;
                Intent intent = new Intent(getActivity(), Meal_Planner.class);
                startActivity(intent);
            });

            commentButton.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), Comments.class);
                intent.putExtra("RECIPE_ID", recipeID);
                startActivity(intent);
            });
        } else {
            recipeName.setText("Recipe not available");
        }

        backButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    public static RecipeFragment newInstance(Recipe recipe) {
        RecipeFragment fragment = new RecipeFragment();
        Bundle args = new Bundle();
        args.putSerializable("recipe", recipe);
        fragment.setArguments(args);
        return fragment;
    }
}
