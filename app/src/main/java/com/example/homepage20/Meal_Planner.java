package com.example.homepage20;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Meal_Planner extends AppCompatActivity {
    private CalendarView calendarView;
    private RadioGroup radioGroupMeal;
    private Button addMealButton;
    private String selectedMealType;
    private String selectedDate;
    private TextView rText;
    OkHttpClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_meal_planner);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        calendarView = findViewById(R.id.calendarView);
        radioGroupMeal = findViewById(R.id.radioGroupMeal);
        addMealButton = findViewById(R.id.add_meal);
        client = new OkHttpClient();
        rText = findViewById(R.id.r_name);
        rText.setText(RecipeFragment.addRecipes.getRecipeName());

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
        });
        radioGroupMeal.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton radioButton = findViewById(checkedId);
            if (radioButton != null) {
                selectedMealType = radioButton.getText().toString();
            }
        });
        addMealButton.setOnClickListener(v -> {
            if (selectedDate == null || selectedMealType == null) {
                Toast.makeText(Meal_Planner.this, "Please select a date and a meal type", Toast.LENGTH_SHORT).show();
            } else {
                addMealToPlan(selectedDate, selectedMealType);
                finish();
            }
        });
    }

    public void addMealToPlan(String selectedDate, String selectedMealType){
        String addURL = "https://lamp.ms.wits.ac.za/home/s2656158/A_Sel.php";
        RequestBody requestBody = new FormBody.Builder()
                .add("USERNAME", Login.uname).add("RECIPE_ID", RecipeFragment.addRecipes.getRecipeID())
                .add("DATE", selectedDate).add("MEAL_TYPE", selectedMealType).build();
        Request request = new Request.Builder().url(addURL).post(requestBody).build();
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
                                Toast.makeText(Meal_Planner.this, "Recipe Added", Toast.LENGTH_SHORT).show();
                            }

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        });
    }
}