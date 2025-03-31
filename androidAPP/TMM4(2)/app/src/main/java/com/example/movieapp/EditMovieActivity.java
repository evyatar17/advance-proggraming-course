package com.example.movieapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.Arrays;
import java.util.List;

public class EditMovieActivity extends AppCompatActivity {
    private EditText etTitle, etDescription, etYear, etDirector, etRating, etImage, etCategories,
            etPath;
    private Button btnUpdate;
    private MovieViewModel movieViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_movie);
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etYear = findViewById(R.id.etYear);
        etDirector = findViewById(R.id.etDirector);
        etRating = findViewById(R.id.etRating);
        etImage = findViewById(R.id.etImage);
        etCategories = findViewById(R.id.etCategories);
        btnUpdate = findViewById(R.id.btnSubmit);
        etPath = findViewById(R.id.etPath);
        movieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);

        // get the input from the manager
        etTitle.setText(getIntent().getStringExtra("MOVIE_TITLE"));
        etDescription.setText(getIntent().getStringExtra("MOVIE_DESCRIPTION"));
        etYear.setText(String.valueOf(getIntent().getIntExtra("MOVIE_YEAR", 0)));
        etDirector.setText(getIntent().getStringExtra("MOVIE_DIRECTOR"));
        etRating.setText(String.valueOf(getIntent().getDoubleExtra("MOVIE_RATING", 0.0)));
        etImage.setText(getIntent().getStringExtra("MOVIE_IMAGE"));
        etPath.setText(getIntent().getStringExtra("MOVIE_PATH"));
        etCategories.setText(getIntent().getStringExtra("MOVIE_CATEGORIES"));

        btnUpdate.setOnClickListener(view -> updateMovie());
    }

    private void updateMovie() {
        String title = etTitle.getText().toString();
        String description = etDescription.getText().toString();
        String year = etYear.getText().toString();
        String director = etDirector.getText().toString();
        String rating = etRating.getText().toString();
        String image = etImage.getText().toString();
        String categories = etCategories.getText().toString();
        String path = etPath.getText().toString();
        if (title.isEmpty()) {
            Toast.makeText(this, "Title is required!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (path.isEmpty()) {
            Toast.makeText(this, "Path is required!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (description.isEmpty()) {
            description = "" ;
        }
        if (image.isEmpty()) {
            image = "" ;
        }
        if (director.isEmpty()) {
            director = "" ;
        }
        List<String> Categories = Arrays.asList(categories.split("\\s*,\\s*"));
        int Year = year.isEmpty() ? 0 : Integer.parseInt(year);
        double Rating = rating.isEmpty() ? 0 : Double.parseDouble(rating);
        MovieEntity movie = new MovieEntity( title, description,
                Year, director,Rating
                , image, path, categories);

        movieViewModel.updateMovie(movie);
        Toast.makeText(this, "Movie updated successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
