package com.example.homepage20;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
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

public class AddFriendsFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private SearchView searchView;
    private String json;
    OkHttpClient client;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        client = new OkHttpClient();
        View view = inflater.inflate(R.layout.fragment_add_friends, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.users_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize User Adapter with empty data
        List<User> users = new ArrayList<>();
        adapter = new UserAdapter(getContext(), users, this);
        recyclerView.setAdapter(adapter);

        // Fetch users
        getRAUsers();

        // Setup the SearchView to filter users
        searchView = view.findViewById(R.id.search_users);
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

        return view;
    }

    private static List<User> parseUsers(String jsonResponse) throws JSONException {
        List<User> users = new ArrayList<>();

        JSONArray jsonArray = new JSONArray(jsonResponse);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject item = jsonArray.getJSONObject(i);
            String name = item.getString("Name");
            String username = item.getString("Username");

            User user = new User(name, username);
            users.add(user);
        }

        return users;
    }

    private void getRAUsers() {
        String url = "https://lamp.ms.wits.ac.za/home/s2656158/RA_Fre.php";

        RequestBody formBody = new FormBody.Builder()
                .add("USERNAME", Login.uname)
                .build();

        Request request = new Request.Builder().url(url).post(formBody).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(() ->
                        Toast.makeText(getActivity(), "Failed to fetch users", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String jsonResponse = response.body().string();
                    getActivity().runOnUiThread(() -> {
                        try {
                            List<User> users = parseUsers(jsonResponse);
                            adapter.updateUsers(users);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Failed to parse users", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getActivity(), "Failed to fetch users", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    public void addFriend(String userId) {
        String url = "https://lamp.ms.wits.ac.za/home/s2656158/A_Fre.php";

        RequestBody formBody = new FormBody.Builder()
                .add("USERNAME1", Login.uname) // Replace with actual current user ID
                .add("USERNAME2", userId)
                .build();

        Request request = new Request.Builder().url(url).post(formBody).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                getActivity().runOnUiThread(() ->
                        Toast.makeText(getActivity(), "Friend request sent!", Toast.LENGTH_SHORT).show());
            }
        });
    }
}
