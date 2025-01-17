package com.example.homepage20;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

public class GroceryListFragment extends Fragment {
    private OkHttpClient client;
    private List<String> groList = new ArrayList<>();
    private Handler handler = new Handler(Looper.getMainLooper());
    private TextView displayGrocery;
    private TextView mealPlan;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_grocery_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        client = new OkHttpClient();
        displayGrocery = view.findViewById(R.id.displayGrocery);
        mealPlan = view.findViewById(R.id.mealplan);

        fillGroList();
        fillMealList();
    }

    private void fillGroList() {
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

                String json = response.body().string();
                handler.post(() -> {
                    try {
                        if (!json.equals("failed")) {
                            JSONArray all = new JSONArray(json);
                            String groceries = "";
                            for (int i = 0; i < all.length(); i++) {
                                JSONObject item = all.getJSONObject(i);
                                String ings = item.getString("Ingredients");
                                groceries += ings + "\n";
                            }
                            displayGrocery.setText(groceries);
                        } else {
                            displayGrocery.setText("No groceries for the next 7 days!");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    private void fillMealList() {
        String mealURL = "https://lamp.ms.wits.ac.za/home/s2656158/Cur_Sel.php";
        RequestBody requestBody = new FormBody.Builder()
                .add("USERNAME", Login.uname).build();
        Request request = new Request.Builder().url(mealURL).post(requestBody).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {


                String json = response.body().string();
                handler.post(() -> {
                    try {
                        if (!json.equals("failed")) {
                            JSONArray all = new JSONArray(json);
                            String meals = "";
                            for (int i = 0; i < all.length(); i++) {
                                JSONObject item = all.getJSONObject(i);
                                String name = item.getString("RecipeName");
                                String meal = item.getString("MealType");
                                meals += name + ", for " + meal + "\n";
                            }
                            mealPlan.setText(meals);
                        } else {
                            mealPlan.setText("No meal plan for today.");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }
}
