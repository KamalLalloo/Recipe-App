package com.example.homepage20;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

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

public class GroceryActivity extends AppCompatActivity {
    public List<String> groList = new ArrayList<>();
    public List<String> mealList = new ArrayList<>();
    public String hasRec = "yes";
    public String hasMeal = "yes";
    OkHttpClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        client = new OkHttpClient();
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_grocery);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fragment_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (savedInstanceState == null) {
            GroceryListFragment groceryListFragment = new GroceryListFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, groceryListFragment);
            transaction.commit();
        }

        BottomNavigationView navView = findViewById(R.id.navigation);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.navigation_home) {
                    startActivity(new Intent(GroceryActivity.this, MainActivity.class));
                } else if (id == R.id.navigation_meal_adder) {
                    startActivity(new Intent(GroceryActivity.this, MealAdder.class));
                } else if (id == R.id.navigation_grocery) {
                    // This is the current activity, do nothing or refresh
                } else if (id == R.id.navigation_community) {
                    startActivity(new Intent(GroceryActivity.this, CommunityActivity.class));
                }
                return true;
            }
        });

        navView.setSelectedItemId(R.id.navigation_grocery); // Highlight the current item

        // Load only the GroceryListFragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new GroceryListFragment())
                .commit();

        groList.clear();
        mealList.clear();
        fillGroList();
        fillMeal();
        Log.d("GroceryActivity", "Activity created and fragment added.");
    }

    public OkHttpClient getClient() {
        return client;
    }
    public void fillGroList(){
        String groURL = "https://lamp.ms.wits.ac.za/home/s2656158/R_GroD.php";
        RequestBody requestBody = new FormBody.Builder()
                .add("USERNAME", Login.uname).build();
        Request request = new Request.Builder().url(groURL).post(requestBody).build();
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
                            String json = response.body().string(); // either success or failed
                            if (!json.equals("failed")){
                                JSONArray all = new JSONArray(json);
                                for (int i = 0; i < all.length(); i++) {
                                    JSONObject item = all.getJSONObject(i);
                                    String ings = item.getString("Ingredients");
                                    groList.add(ings);
                                }
                            }else{
                                hasRec = "no";
                            }

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        });
    }

    public void fillMeal(){
        String groURL = "https://lamp.ms.wits.ac.za/home/s2656158/Cur_Sel.php";
        RequestBody requestBody = new FormBody.Builder()
                .add("USERNAME", Login.uname).build();
        Request request = new Request.Builder().url(groURL).post(requestBody).build();
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
                            String json = response.body().string(); // either success or failed
                            if (!json.equals("failed")){
                                JSONArray all = new JSONArray(json);
                                for (int i = 0; i < all.length(); i++) {
                                    JSONObject item = all.getJSONObject(i);
                                    String name = item.getString("RecipeName");
                                    String mealT = item.getString("MealType");
                                    mealList.add(name + " for " + mealT);
                                }
                            }else{
                                hasMeal = "no";
                            }

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        });
    }
}
