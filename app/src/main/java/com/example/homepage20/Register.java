package com.example.homepage20;

import com.example.homepage20.Validation;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
public class Register extends AppCompatActivity {
    OkHttpClient client;
    EditText username; EditText password; EditText name; EditText email;
    EditText confirm; TextView textView;
    String validateURL = "https://lamp.ms.wits.ac.za/home/s2661262/validate.php";
    String insertURL = "https://lamp.ms.wits.ac.za/home/s2661262/insert.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        client = new OkHttpClient();
        TextView btn = findViewById(R.id.Login_text);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this,Login.class));
            }
        });
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.Password);
        name = (EditText)findViewById(R.id.Name);
        email = (EditText)findViewById(R.id.email);
        confirm = (EditText)findViewById(R.id.confirm);
        textView = findViewById(R.id.textViewSignUp);
        Button signReg = findViewById(R.id.btnRegister);
        signReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isValid();
            }
        });

    }


    public void doInsert() {
        String Uusername = username.getText().toString().trim();
        String Upassword = password.getText().toString().trim();
        String Uname = name.getText().toString().trim();
        String Uemail = email.getText().toString().trim();
        String Uconfirm = confirm.getText().toString().trim();

        // Check if name is valid
        if (!Validation.isName(Uname)) {
            name.setError("Invalid Name");
            return;
        }

        // Check if email is valid
        if (!Validation.isEmail(Uemail)) {
            email.setError("Invalid Email");
            return;
        }

        // Validate username
        int usernameResult = Validation.validUsername(Uusername);
        if (usernameResult != 2) { // Assuming 2 is the success code from your Validation class
            switch (usernameResult) {
                case 0:
                    username.setError("The username is too short!");
                    break;
                case 1:
                    username.setError("Only letters, numbers, -, _ and . are allowed!");
                    break;
                case 6:
                    username.setError("The username cannot be empty");
                    break;
            }
            return;
        }

        // Validate password
        int passwordResult = Validation.isPassword(Upassword);
        if (passwordResult != 4) { // Assuming 4 is the success code
            switch (passwordResult) {
                case 0:
                    password.setError("Password must be longer than 9 characters!");
                    break;
                case 1:
                    password.setError("First character must be a capital letter!");
                    break;
                case 2:
                    password.setError("Only letters and numbers are allowed!");
                    break;
                case 3:
                    password.setError("Password must have a minimum of 3 digits");
                    break;
                case 6:
                    password.setError("Password cannot be empty");
                    break;
            }
            return;
        }

        // Confirm passwords match
        if (!Upassword.equals(Uconfirm)) {
            confirm.setError("Passwords do not match");
            return;
        }

        // All validations passed, proceed to register user
        RequestBody requestBody = new FormBody.Builder()
                .add("NAME", Uname)
                .add("USERNAME", Uusername)
                .add("PASSWORD", Upassword)
                .add("EMAIL", Uemail)
                .build();
        Request request = new Request.Builder().url(insertURL).post(requestBody).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(Register.this, "Failed to connect to server", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String input = response.body().string();
                runOnUiThread(() -> {
                    if (input.contains("success")) {
                        Toast.makeText(Register.this, "Registration successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Register.this, Login.class));
                        finish();
                    } else {
                        textView.setText("Registration failed: " + input); // Show detailed error from server
                    }
                });
            }
        });
    }

    public void isValid() {
        String user = username.getText().toString().trim();
        if (user.isEmpty()) {
            username.setError("Username cannot be empty");
            return;
        }
        RequestBody requestBody = new FormBody.Builder().add("USERNAME", user).build();
        Request request = new Request.Builder().url(validateURL).post(requestBody).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(Register.this, "Network error, please try again", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String input = response.body().string();
                runOnUiThread(() -> {
                    if (input.contains("failed")) {
                        doInsert();  // Wait for doInsert to complete and check success internally
                    } else {
                        username.setError("Username Already Taken");
                    }
                });
            }
        });
    }

}