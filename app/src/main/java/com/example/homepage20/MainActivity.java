package com.example.homepage20;

import android.os.Bundle;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private BottomSheetDialog bottomSheetDialog;
    private TextView textView;
    private RecyclerView recyclerView;
    private RecipeAdapter adapter;
    private ScrollView scrollView;
    private List<Recipe> recipesList = new ArrayList<>();
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        client = new OkHttpClient();
        // Apply window insets to layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize ScrollView
        scrollView = findViewById(R.id.main_scrollview);

        // Initialize RecyclerView and Adapter
        recyclerView = findViewById(R.id.recipes_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //List<Recipe> recipesList = getMockData(); // Generating mock data
        adapter = new RecipeAdapter(this, recipesList);
        recyclerView.setAdapter(adapter);

        // Setup SearchView for recipes
        SearchView searchView = findViewById(R.id.search_recipes);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    scrollView.post(() -> scrollView.smoothScrollTo(0, searchView.getTop()));
                }
            }
        });

        // Setup BottomNavigationView
        BottomNavigationView navView = findViewById(R.id.navigation);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.navigation_home) {
                    // This is the current activity, so do nothing or refresh
                } else if (id == R.id.navigation_meal_adder) {
                    startActivity(new Intent(MainActivity.this, MealAdder.class));
                } else if (id == R.id.navigation_grocery) {
                    startActivity(new Intent(MainActivity.this, GroceryActivity.class));
                } else if (id == R.id.navigation_community) {
                    startActivity(new Intent(MainActivity.this, CommunityActivity.class));
                }
                return true;
            }
        });

        // Setup ImageView to trigger BottomSheetDialog
        ImageView menuImageView = findViewById(R.id.imageView);
        menuImageView.setOnClickListener(v -> showBottomSheetDialog());
        textView = findViewById(R.id.helloUser);
        textView.setText("Hello, " + Login.uname);  // Assuming 'Login' is a class that holds the user name

        fetchRecipes();
    }



    private void fetchRecipes() {
        String url = "https://lamp.ms.wits.ac.za/home/s2656158/R_Rec.php";

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to fetch recipes", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String jsonResponse = response.body().string();
                    runOnUiThread(() -> {
                        try {
                            parseRecipes(jsonResponse);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Failed to parse recipes", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to fetch recipes", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void parseRecipes(String jsonResponse) throws JSONException {
        JSONArray jsonArray = new JSONArray(jsonResponse);
        recipesList.clear();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject item = jsonArray.getJSONObject(i);
            String recipeID = item.getString("RecipeID");
            String recipeName = item.getString("RecipeName");
            String username = item.getString("Username");
            String ingredients = item.getString("Ingredients");
            String instructions = item.getString("Instructions");
            String description = item.getString("Description");
            recipesList.add(new Recipe(recipeID, recipeName, username, ingredients, instructions, description));
        }
        adapter.notifyDataSetChanged();
    }

    private void showBottomSheetDialog() {
        if (bottomSheetDialog == null) {
            bottomSheetDialog = new BottomSheetDialog(this);
            View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_dialog, null);
            bottomSheetDialog.setContentView(sheetView);

            // Setup buttons in the bottom sheet
            Button btnLogout = sheetView.findViewById(R.id.btn_logout);
            btnLogout.setOnClickListener(v -> {
                // Show confirmation dialog before logging out
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Log Out")
                        .setMessage("Are you sure you want to leave?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // User clicked "Yes"
                            bottomSheetDialog.dismiss();  // Dismiss bottom sheet first
                            logoutUser();  // Handle the logout process
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            // User clicked "No", dismiss the dialog
                            dialog.dismiss();
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            });

            Button btnInfo = sheetView.findViewById(R.id.btn_info);
            btnInfo.setOnClickListener(v -> {
                // Handle info
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("Plan and Plate is a Recipe and Meal Planner app developed by 4 computer science students: Harshil, Kamal, Karan and Yabi")
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                        .show();
                bottomSheetDialog.dismiss();
            });
        }
        bottomSheetDialog.show();
    }

    private void logoutUser() {
        // Here you can clear any saved data or session
        // For example, clear SharedPreferences, logout from servers, etc.

        // After log out, redirect to Login Activity
        Intent intent = new Intent(MainActivity.this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();  // Finish MainActivity so user can't go back by pressing back button
    }

}
