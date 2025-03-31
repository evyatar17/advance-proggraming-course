package com.example.movieapp.movie;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewMovieActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private List<Movie> movieList;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_view_movie);

        recyclerView = findViewById(R.id.recyclerViewMovies);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        movieList = new ArrayList<>();
        movieAdapter = new MovieAdapter(this, movieList);
        recyclerView.setAdapter(movieAdapter);

        apiService = ApiClient.getClient().create(ApiService.class);

        FloatingActionButton fab = findViewById(R.id.fabAddMovie);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(ViewMovieActivity.this, AddEditMovieActivity.class);
            startActivity(intent);
        });

        loadMovies();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMovies(); // Refresh data when returning to activity
    }

    private void loadMovies() {
        Call<List<Movie>> call = apiService.getMovies();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                if (response.isSuccessful()) {
                    movieList.clear();
                    movieList.addAll(response.body());
                    movieAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
                // Handle error
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_categories) {
            Intent intent = new Intent(ViewMovieActivity.this, CategoryListActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
