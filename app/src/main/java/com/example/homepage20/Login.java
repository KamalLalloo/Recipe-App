package com.example.homepage20;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Login extends AppCompatActivity{
    public static String uname;
    OkHttpClient client;
    String insertURL = "https://lamp.ms.wits.ac.za/home/s2661262/insert.php";
    String validateURL = "https://lamp.ms.wits.ac.za/home/s2661262/validate.php";
    EditText username; EditText password; TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        client = new OkHttpClient();
        textView = findViewById(R.id.Logintext);
        username = (EditText)findViewById(R.id.l_username);
        password = (EditText)findViewById(R.id.l_password);
        TextView btn = findViewById(R.id.Signuptext);
        Button signBtn = findViewById(R.id.btnLogin);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {startActivity(new Intent(Login.this,Register.class));
            }
        });
        signBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isValid();

            }
        });
    }

    public void isValid(){
        String usern = username.getText().toString().trim();
        String pass = password.getText().toString().trim();

        if (usern.isEmpty()) {
            username.setError("Username is required");
            return; // Stop further execution if no username is entered
        }

        if (pass.isEmpty()) {
            password.setError("Password is required");
            return; // Stop further execution if no password is entered
        }
        String user = username.getText().toString();
        RequestBody requestBody = new FormBody.Builder()
                .add("USERNAME", user).build();
        Request request = new Request.Builder().url(validateURL).post(requestBody).build();

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
                            String input = response.body().string();
                            if (!(input.contains("failed"))){
                                JSONObject item = new JSONObject(input);
                                String actualPass = item.getString("Password");
                                if (password.getText().toString().equals(actualPass)){
                                    Login.uname = user;
                                    startActivity(new Intent(Login.this,MainActivity.class));
                                }else {
                                    password.setError("Incorrect Password");
                                }
                            }else{
                                username.setError("Username not found");
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        });
    }



}