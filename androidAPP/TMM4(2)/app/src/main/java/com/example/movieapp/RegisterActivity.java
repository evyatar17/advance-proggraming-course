package com.example.movieapp;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.movieapp.movie.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailEditText, firstNameEditText, lastNameEditText, usernameEditText, passwordEditText, confirmPasswordEditText;
    private Button postButton;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_screen);

        // Locate fields
        emailEditText = findViewById(R.id.email);
        firstNameEditText = findViewById(R.id.firstName);
        lastNameEditText = findViewById(R.id.lastName);
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirm_password);
        postButton = findViewById(R.id.PostButton);

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        // Post Button Click
        postButton.setOnClickListener(view -> handleRegister());

        findViewById(R.id.home_arrow_button).setOnClickListener(v -> {
            // Go back to home
            Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
            startActivity(intent);
        });
    }
    private void handleRegister() {
        String email = emailEditText.getText().toString().trim();
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password)
                || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Please fill out all fields"
                    , Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        RegisterRequest registerRequest = new RegisterRequest(email, firstName, lastName, username, password);
        Call<RegisterResponse> call = apiService.registerUser(registerRequest);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this,
                            "Registration successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this
                            , LoginActivity.class));
                    finish();
                } else {
                    Log.e("responseFail",response.toString());
                    Toast.makeText(RegisterActivity.this, "Registration failed"+response,
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    }