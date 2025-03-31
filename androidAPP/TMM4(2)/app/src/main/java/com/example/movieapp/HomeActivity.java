package com.example.movieapp;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        // locate buttons by id
        Button signInButton = findViewById(R.id.signInButton);
        Button signUpButton = findViewById(R.id.signUpButton);

        // sign in define
        signInButton.setOnClickListener(view -> {
            // // move to log in screen
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        //Sign Up define
        signUpButton.setOnClickListener(view -> {
            // move to register screen
            Intent intent = new Intent(HomeActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}
