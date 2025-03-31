package com.example.movieapp.movie;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.movieapp.R;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEditMovieActivity extends AppCompatActivity {
    private EditText editTitle, editDirector, editYear, editDescription, editRating, editPosterUrl;
    private Spinner spinnerCategory;
    private Button btnSave, btnDelete;

    private ApiService apiService;
    private List<Category> categoryList;
    private String movieId;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_add_edit_movie);

        // Initialize views
        editTitle = findViewById(R.id.editMovieTitle);
        editDirector = findViewById(R.id.editMovieDirector);
        editYear = findViewById(R.id.editMovieYear);
        editDescription = findViewById(R.id.editMovieDescription);
        editRating = findViewById(R.id.editMovieRating);
        editPosterUrl = findViewById(R.id.editMoviePosterUrl);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnSave = findViewById(R.id.btnSaveMovie);
        btnDelete = findViewById(R.id.btnDeleteMovie);

        apiService = ApiClient.getClient().create(ApiService.class);
        categoryList = new ArrayList<>();

        // Check if we're editing an existing movie
        movieId = getIntent().getStringExtra("MOVIE_ID");
        isEditMode = movieId != null;

        if (isEditMode) {
            setTitle("Edit Movie");
            btnDelete.setVisibility(View.VISIBLE);
            loadMovie();
        } else {
            setTitle("Add Movie");
            btnDelete.setVisibility(View.GONE);
        }

        // Load categories for spinner
        loadCategories();

        // Set up button click listeners
        btnSave.setOnClickListener(v -> saveMovie());
        btnDelete.setOnClickListener(v -> confirmDelete());

        // Enable back button
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void loadCategories() {
        Call<List<Category>> call = apiService.getCategories();
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful()) {
                    categoryList.clear();
                    categoryList.addAll(response.body());

                    ArrayAdapter<Category> adapter = new ArrayAdapter<>(
                            AddEditMovieActivity.this,
                            android.R.layout.simple_spinner_item,
                            categoryList
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCategory.setAdapter(adapter);

                    // If editing, select the correct category
                    if (isEditMode) {
                        loadMovie();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(AddEditMovieActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMovie() {
        Call<Movie> call = apiService.getMovie(movieId);
        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                if (response.isSuccessful()) {
                    Movie movie = response.body();

                    editTitle.setText(movie.getTitle());
                    editDirector.setText(movie.getDirector());
                    editYear.setText(String.valueOf(movie.getReleaseYear()));
                    editDescription.setText(movie.getDescription());
                    editRating.setText(String.valueOf(movie.getRating()));

                    if (movie.getPosterUrl() != null) {
                        editPosterUrl.setText(movie.getPosterUrl());
                    }

                    // Set the correct category in spinner
                    if (movie.getCategory() != null) {
                        for (int i = 0; i < categoryList.size(); i++) {
                            if (categoryList.get(i).getId().equals(movie.getCategory().getId())) {
                                spinnerCategory.setSelection(i);
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Toast.makeText(AddEditMovieActivity.this, "Failed to load movie", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveMovie() {
        String title = editTitle.getText().toString().trim();
        String director = editDirector.getText().toString().trim();
        String yearStr = editYear.getText().toString().trim();
        String description = editDescription.getText().toString().trim();
        String ratingStr = editRating.getText().toString().trim();
        String posterUrl = editPosterUrl.getText().toString().trim();

        if (title.isEmpty()) {
            editTitle.setError("Title is required");
            return;
        }

        if (yearStr.isEmpty()) {
            editYear.setError("Year is required");
            return;
        }

        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setDirector(director);
        movie.setReleaseYear(Integer.parseInt(yearStr));
        movie.setDescription(description);

        if (!ratingStr.isEmpty()) {
            movie.setRating(Float.parseFloat(ratingStr));
        }

        movie.setPosterUrl(posterUrl);

        // Get selected category
        if (spinnerCategory.getSelectedItem() != null) {
            Category selectedCategory = (Category) spinnerCategory.getSelectedItem();
            movie.setCategory(selectedCategory);
        }

        if (isEditMode) {
            // Update existing movie
            Call<Movie> call = apiService.updateMovie(movieId, movie);
            call.enqueue(new Callback<Movie>() {
                @Override
                public void onResponse(Call<Movie> call, Response<Movie> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AddEditMovieActivity.this, "Movie updated", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddEditMovieActivity.this, "Failed to update movie", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Movie> call, Throwable t) {
                    Toast.makeText(AddEditMovieActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Create new movie
            Call<Movie> call = apiService.createMovie(movie);
            call.enqueue(new Callback<Movie>() {
                @Override
                public void onResponse(Call<Movie> call, Response<Movie> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AddEditMovieActivity.this, "Movie added", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddEditMovieActivity.this, "Failed to add movie", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Movie> call, Throwable t) {
                    Toast.makeText(AddEditMovieActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Movie")
                .setMessage("Are you sure you want to delete this movie?")
                .setPositiveButton("Delete", (dialog, which) -> deleteMovie())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteMovie() {
        Call<Void> call = apiService.deleteMovie(movieId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddEditMovieActivity.this, "Movie deleted", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddEditMovieActivity.this, "Failed to delete movie", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AddEditMovieActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
