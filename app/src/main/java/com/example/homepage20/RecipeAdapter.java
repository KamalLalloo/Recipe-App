package com.example.homepage20;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private Context context; // Context to be used for Intents
    private List<Recipe> recipes;
    private List<Recipe> recipesFull; // Full copy of recipes list for filtering
    private LayoutInflater inflater;

    // Constructor
    public RecipeAdapter(Context context, List<Recipe> recipes) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.recipes = recipes;
        this.recipesFull = new ArrayList<>(recipes); // Initialize with all recipes
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recipe_item, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.recipeName.setText(recipe.getRecipeName());
        holder.recipeDescription.setText(recipe.getDescription());

        holder.itemView.setOnClickListener(v -> {
            RecipeFragment recipeFragment = RecipeFragment.newInstance(recipe);
            ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, recipeFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    // ViewHolder class to hold item views
    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView recipeName;
        TextView recipeDescription;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeName = itemView.findViewById(R.id.recipe_name);
            recipeDescription = itemView.findViewById(R.id.recipe_description);
        }
    }

    // Implement Filterable for recipe filtering

    public Filter getFilter() {
        return recipeFilter;
    }

    private Filter recipeFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Recipe> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(recipesFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Recipe item : recipesFull) {
                    if (item.getRecipeName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            recipes.clear();
            recipes.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
