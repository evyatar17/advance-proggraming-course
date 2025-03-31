package com.example.movieapp;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
public class ManagerActivity extends MenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manager_screen);
        setupMenu();
        findViewById(R.id.menu_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else {
                    drawerLayout.openDrawer(GravityCompat.END);
                }
            }
        });
        findViewById(R.id.btn_add_movie).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerActivity.this, AddMovieActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.btn_edit_movie).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerActivity.this, EditMovieActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_delete_movie).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerActivity.this, DeleteMovieActivity.class);
                startActivity(intent);
            }
        });
    }
}