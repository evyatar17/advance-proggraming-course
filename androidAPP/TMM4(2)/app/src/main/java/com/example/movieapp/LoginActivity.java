
package com.example.movieapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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

public class LoginActivity extends AppCompatActivity {

   private EditText usernameEditText, passwordEditText;
   private Button loginButton;
   private ApiService apiService;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.login_screen);
      findViewById(R.id.right_arrow_button).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            // move back to home
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
         }
      });
      findViewById(R.id.RegisterButton).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
         }
      });
      // Locate fields
      usernameEditText = findViewById(R.id.username);
      passwordEditText = findViewById(R.id.password);
      loginButton = findViewById(R.id.loginButton);

      // Initialize Retrofit
      Retrofit retrofit = new Retrofit.Builder()
              .baseUrl(ApiClient.BASE_URL)
              .addConverterFactory(GsonConverterFactory.create())
              .build();

      apiService = retrofit.create(ApiService.class);


      // Login Button Click
      loginButton.setOnClickListener(view -> handleLogin());
   }

   private void handleLogin() {
      String username = usernameEditText.getText().toString().trim();
      String password = passwordEditText.getText().toString().trim();

      if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
         Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
         return;
      }

      LoginRequest request = new LoginRequest(username, password);
      apiService.loginUser(request).enqueue(new Callback<>() {
         @Override
         public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
            if (response.isSuccessful() && response.body() != null) {
               saveUserData(response.body().getToken(), response.body().getRole());
               Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
               // מעבר למסך הראשי אחרי התחברות מוצלחת
               Intent intent = new Intent(LoginActivity.this, SignedHomeActivity.class);
               startActivity(intent);
            } else {
               Log.e("loginFailed",response.toString());

               Toast.makeText(LoginActivity.this, "Login failed! Check your credentials", Toast.LENGTH_SHORT).show();
            }
         }

         @Override
         public void onFailure(Call<LoginResponse> call, Throwable t) {
            Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
         }
      });
   }

   private void saveUserData(String token, String role) {
      SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
      SharedPreferences.Editor editor = sharedPreferences.edit();
      editor.putString("jwt_token", token);
      editor.putString("user_role", role);
      editor.apply();
   }
}
