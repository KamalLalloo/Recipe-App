package com.example.homepage20;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class FeedFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecipesAdapter adapter;
    private List<Recipes> recipesList = new ArrayList<>();
    private OkHttpClient client;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        client = new OkHttpClient();
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize Recipes Adapter
        adapter = new RecipesAdapter(getContext(), recipesList);
        recyclerView.setAdapter(adapter);

        // Fetch recipes
        fetchRecipes();

        return view;
    }

    private void fetchRecipes() {
        String url = "https://lamp.ms.wits.ac.za/home/s2656158/RF_Rec.php";

        RequestBody formBody = new FormBody.Builder()
                .add("USERNAME", Login.uname)
                .build();

        Request request = new Request.Builder().url(url).post(formBody).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(() ->
                        Toast.makeText(getActivity(), "Failed to fetch recipes", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String jsonResponse = response.body().string();
                    getActivity().runOnUiThread(() -> {
                        try {
                            parseRecipes(jsonResponse);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Failed to parse recipes", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getActivity(), "Failed to fetch recipes", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void parseRecipes(String jsonResponse) throws JSONException {
        JSONArray jsonArray = new JSONArray(jsonResponse);
        recipesList.clear();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject item = jsonArray.getJSONObject(i);
            String recipeName = item.getString("RecipeName");
            String username = item.getString("Username");
            recipesList.add(new Recipes(recipeName, username));
        }
        adapter.notifyDataSetChanged();
    }
}
