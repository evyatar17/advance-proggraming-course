package com.example.movieapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.movieapp.movie.ViewMovieActivity;
import com.google.android.material.navigation.NavigationView;

public class MenuActivity extends AppCompatActivity {
    protected DrawerLayout drawerLayout;
    protected NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    // call the function for every activity with menu, handle the menu implement
    protected void setupMenu() {

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_View);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(
                    new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                            int itemId = item.getItemId();
                            if (itemId == R.id.nav_home) {
                                navigateTo(SignedHomeActivity.class);
                            } else if (itemId == R.id.nav_settings) {
                                toggleTheme();
                            } else if (itemId == R.id.manager) {
                                navigateTo(ViewMovieActivity.class);
                            } else if (itemId == R.id.log_out) {
                                logout();
                            }

                            drawerLayout.closeDrawer(GravityCompat.END);
                            return true;
                        }
                    });
        }
    }
    // (Light/Dark Mode) change the theme
    private void toggleTheme() {
        SharedPreferences preferences = getSharedPreferences("theme_prefs", MODE_PRIVATE);
        boolean isDarkMode = preferences.getBoolean("is_dark_mode", false);
        boolean newMode = !isDarkMode;

        preferences.edit().putBoolean("is_dark_mode", newMode).apply();

        AppCompatDelegate.setDefaultNightMode(
                newMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        recreate();
    }

    private void navigateTo(Class<?> targetActivity) {
        Intent intent = new Intent(this, targetActivity);
        startActivity(intent);
        finish();
    }

    // get out of the user's account
    private void logout() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
