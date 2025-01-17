package com.example.homepage20;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ImageButton;
import android.widget.Toast;
import android.view.View;



import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;

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

public class Comments extends AppCompatActivity {
    private ImageButton likeButton;
    private boolean isLiked = false;
    private String recipeID;
    private OkHttpClient client;
    private Handler handler = new Handler(Looper.getMainLooper());
    RecyclerView recyclerView;
    CommentsAdapter adapter;
    private List<Comment> commentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        ImageView backButton = findViewById(R.id.goback);

        EdgeToEdge.enable(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        recipeID = getIntent().getStringExtra("RECIPE_ID");
        client = new OkHttpClient();

        recyclerView = findViewById(R.id.commentsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CommentsAdapter(commentList);
        recyclerView.setAdapter(adapter);
        fetchComments();
        EditText userComment = findViewById(R.id.user_comment);
        Button postButton = findViewById(R.id.post);
        postButton.setOnClickListener(v -> {
            String commentText = userComment.getText().toString().trim();
            if (!commentText.isEmpty()) {
                postComment(commentText);
            }
        });
        likeButton = findViewById(R.id.like_button);
        likeButton.setOnClickListener(v -> {
            if (isLiked) {
                likeButton.setImageResource(R.drawable.ic_like);
                Toast.makeText(Comments.this, "You unliked this recipe.", Toast.LENGTH_SHORT).show();
                isLiked = false;
            } else {
                likeButton.setImageResource(R.drawable.ic_liked);
                Toast.makeText(Comments.this, "You liked this recipe!.", Toast.LENGTH_SHORT).show();
                isLiked = true;
            }
        });
        backButton.setOnClickListener(v -> {
            finish(); // Closes the current activity and returns to the previous one in the stack
        });

    }



    private void fetchComments() {
        String commentsURL = "https://lamp.ms.wits.ac.za/home/s2656158/R_Com.php";
        RequestBody requestBody = new FormBody.Builder()
                .add("RECIPE_ID", recipeID)
                .build();
        Request request = new Request.Builder().url(commentsURL).post(requestBody).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.body() == null) {
                    return;
                }

                String json = response.body().string();
                handler.post(() -> {
                    try {
                        if (!json.equals("failed")) {
                            JSONArray all = new JSONArray(json);
                            commentList.clear();
                            for (int i = 0; i < all.length(); i++) {
                                JSONObject item = all.getJSONObject(i);
                                String username = item.getString("Username");
                                String comment = item.getString("Comments");
                                commentList.add(new Comment(username, comment));
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(Comments.this, "No comments found.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    private void postComment(String commentText) {
        String postCommentURL = "https://lamp.ms.wits.ac.za/home/s2656158/A_Com.php";
        RequestBody requestBody = new FormBody.Builder()
                .add("RECIPE_ID", recipeID)
                .add("USERNAME", Login.uname)
                .add("COMMENTS", commentText)
                .build();
        Request request = new Request.Builder().url(postCommentURL).post(requestBody).build();
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
                        if (json.equals("success")) {
                            fetchComments(); // Refresh comments
                            EditText userComment = findViewById(R.id.user_comment);
                            userComment.setText(""); // Clear the EditText after successful post
                            Toast.makeText(Comments.this, "Comment posted!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Comments.this, "Failed to post comment.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

}
