package com.example.homepage20;

import android.os.Bundle;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MealAdder extends AppCompatActivity {
    OkHttpClient client;
    String IngURL = "https://lamp.ms.wits.ac.za/home/s2656158/R_Ing.php";

    String RecURL = "https://lamp.ms.wits.ac.za/home/s2656158/A_Rec.php";
    List<String> spinnerArray = new ArrayList<>();
    ArrayAdapter<String> adapter;
    String ingList = "";
    Spinner unitsSpinner; Spinner ingredientSpinner;
    TextView displayGrocery; TextInputEditText instructions;

    EditText recipeName; EditText sDisc; EditText cTime; EditText quantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        client = new OkHttpClient();
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_meal_adder);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        instructions = findViewById(R.id.instructions);
        recipeName = (EditText)findViewById(R.id.textInputEditText3);
        sDisc = (EditText)findViewById(R.id.short_d);
        cTime = (EditText)findViewById(R.id.cook_time);
        quantity = (EditText)findViewById(R.id.quantity);
        displayGrocery = findViewById(R.id.displayGrocery);
        BottomNavigationView navView = findViewById(R.id.navigation);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.navigation_home) {
                    startActivity(new Intent(MealAdder.this, MainActivity.class));
                } else if (id == R.id.navigation_meal_adder) {
                    // This is the current activity, do nothing or refresh
                } else if (id == R.id.navigation_grocery) {
                    startActivity(new Intent(MealAdder.this, GroceryActivity.class));
                } else if (id == R.id.navigation_community) {
                    startActivity(new Intent(MealAdder.this, CommunityActivity.class));
                }
                return true;
            }
        });
        navView.setSelectedItemId(R.id.navigation_meal_adder); // Highlight the current item
        fillArr();
        Button AddIngredient = findViewById(R.id.AddIngredient);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ingredientSpinner = findViewById(R.id.IngredientSpinner);
        ingredientSpinner.setAdapter(adapter);
        ingredientSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MealAdder.this, "Selected: " + spinnerArray.get(position), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // Initialize units spinner
        unitsSpinner = findViewById(R.id.units);
        ArrayAdapter<CharSequence> unitAdapter = ArrayAdapter.createFromResource(this,
                R.array.units_array, android.R.layout.simple_spinner_item);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitsSpinner.setAdapter(unitAdapter);
        unitsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedUnit = parent.getItemAtPosition(position).toString();
                Toast.makeText(MealAdder.this, "Unit selected: " + parent.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        AddIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Validation.isNum(quantity.getText().toString())){
                    addIngredient();
                }else{
                    quantity.setError("Please Enter a Valid Quantity");
                }

            }
        });
        Button confrimRec = findViewById(R.id.ConfirmRecipeButton);
        confrimRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput()){
                    createRecipe(Login.uname ,recipeName.getText().toString(), ingList,
                            instructions.getText().toString(), sDisc.getText().toString());
                    recipeName.setText(""); instructions.setText(""); sDisc.setText("");
                    displayGrocery.setText(""); ingList ="";
                    cTime.setText(""); quantity.setText("");
                }

            }
        });
    }

    public void fillArr() {
        Request request = new Request.Builder().url(IngURL).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                runOnUiThread(() -> {
                    try {
                        String Ing = response.body().string();
                        JSONArray all = new JSONArray(Ing);
                        for (int i = 0; i < all.length(); i++) {
                            JSONObject item = all.getJSONObject(i);
                            String ing = item.getString("IngredientName");
                            spinnerArray.add(ing);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    public void createRecipe(String username, String RecipeName, String Ingredients, String Instructions, String Description){
        RequestBody requestBody = new FormBody.Builder()
                .add("USERNAME", username).add("RECIPE_NAME", RecipeName)
                .add("INGREDIENTS", Ingredients).add("INSTRUCTIONS", Instructions)
                .add("DESCRIPTION", Description).build();
        Request request = new Request.Builder().url(RecURL).post(requestBody).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String works = response.body().string(); // either success or failed
                            if (works.equals("success")){
                                Toast.makeText(MealAdder.this, "Recipe Posted", Toast.LENGTH_SHORT).show();
                            }

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        });
    }

    public boolean checkInput(){
        if (!(recipeName.getText().toString().equals(""))){
            if(!(sDisc.getText().toString().equals(""))){
                if (Validation.isValidCookingTime(cTime.getText().toString())){
                    return true;
                }else{
                    cTime.setError("Please Enter a Valid Cook Time");
                    return false;
                }
            }else{
                sDisc.setError("Please Enter a Description");
                return false;
            }
        }else{
            recipeName.setError("Please Enter a Recipe Name");
            return false;
        }
    }

    private void addIngredient(){
        String ingredient = ingredientSpinner.getSelectedItem().toString();
        String quantityType = unitsSpinner.getSelectedItem().toString();
        if (!ingList.equals("")) {
            ingList += "\n";
        }
        ingList += ingredient + ", " + quantity.getText().toString() + quantityType;
        displayGrocery.setText(ingList);
    }


}