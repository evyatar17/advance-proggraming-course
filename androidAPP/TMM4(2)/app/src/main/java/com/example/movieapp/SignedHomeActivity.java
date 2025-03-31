package com.example.movieapp;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignedHomeActivity extends MenuActivity {
    private VideoView videoView;
    private MovieApi apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signed_in_home);
        setupMenu();
        videoView = findViewById(R.id.videoView);

        // אתחול Retrofit
        apiService = ApiClient.getClient();

        // קריאה ל-API ומשיכת רשימת הסרטים
        //fetchMoviesAndPlayRandom();
        fetchMovies();


        findViewById(R.id.menu_button).setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END);
            } else {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });
    }

    private void fetchMovies() {
        Call<List<Movie>> call = apiService.getMovies();

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Movie>> call, @NonNull Response<List<Movie>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> movies = response.body();
                    Movie movie = movies.get(0);
                    playVideo(movie.getVideoUrl());


                } else {
                    Log.e("Failed", "Response failed: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Movie>> call, @NonNull Throwable t) {
                Log.e("onFailure", "Network Error: " + t.getMessage());
            }
        });
    }

    private void playVideo(String videoUrl) {
        Uri uri = Uri.parse(videoUrl);
        videoView.setVideoURI(uri);

        // כאשר הסרט מוכן, מתחילים לנגן אותו אוטומטית
        videoView.setOnPreparedListener(mp -> videoView.start());
    }
}