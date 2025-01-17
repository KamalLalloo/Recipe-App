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

public class RequestsFragment extends Fragment {

    private RecyclerView recyclerView;
    private RequestsAdapter adapter;
    OkHttpClient client;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        client = new OkHttpClient();
        View view = inflater.inflate(R.layout.fragment_requests, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.requests_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize Requests Adapter with empty data
        List<User> users = new ArrayList<>();
        adapter = new RequestsAdapter(users, this);
        recyclerView.setAdapter(adapter);

        // Fetch requests
        getRequests();

        return view;
    }

    private void getRequests() {
        String url = "https://lamp.ms.wits.ac.za/home/s2656158/R_Req.php";

        RequestBody formBody = new FormBody.Builder()
                .add("USERNAME", Login.uname)
                .build();

        Request request = new Request.Builder().url(url).post(formBody).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(() ->
                        Toast.makeText(getActivity(), "Failed to fetch requests", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String jsonResponse = response.body().string();
                getActivity().runOnUiThread(() -> {
                    try {
                        List<User> users = parseUsers(jsonResponse);
                        adapter.updateUsers(users);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Failed to parse requests", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    private List<User> parseUsers(String jsonResponse) throws JSONException {
        List<User> users = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(jsonResponse);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject item = jsonArray.getJSONObject(i);
            String name = item.getString("Name");
            String username = item.getString("Username");
            users.add(new User(name, username));
        }
        return users;
    }

    public void acceptRequest(String userId) {
        String url = "https://lamp.ms.wits.ac.za/home/s2656158/Accept_F.php";

        RequestBody formBody = new FormBody.Builder()
                .add("USERNAME1", userId)
                .add("USERNAME2", Login.uname)
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
                        Toast.makeText(getActivity(), "Friend request accepted!", Toast.LENGTH_SHORT).show());
            }
        });
    }

    public void declineRequest(String userId) {
        String url = "https://lamp.ms.wits.ac.za/home/s2656158/Decline_F.php";

        RequestBody formBody = new FormBody.Builder()
                .add("USERNAME1", userId)
                .add("USERNAME2", Login.uname)
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
                        Toast.makeText(getActivity(), "Friend request declined!", Toast.LENGTH_SHORT).show());
            }
        });
    }
}

