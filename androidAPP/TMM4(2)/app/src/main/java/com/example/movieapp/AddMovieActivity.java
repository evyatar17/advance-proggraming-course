package com.example.movieapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import java.util.Arrays;
import java.util.List;

public class AddMovieActivity extends AppCompatActivity {
    private EditText etTitle, etDescription, etYear, etDirector, etRating, etImage, etCategories,
            etPath;
    private Button btnSubmit;
    private MovieViewModel movieViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_movie);
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etYear = findViewById(R.id.etYear);
        etDirector = findViewById(R.id.etDirector);
        etRating = findViewById(R.id.etRating);
        etImage = findViewById(R.id.etImage);
        etCategories = findViewById(R.id.etCategories);
        etPath = findViewById(R.id.etPath);
        btnSubmit = findViewById(R.id.btnSubmit);
        movieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);

        btnSubmit.setOnClickListener(view -> sendPostRequest());
    }

    private void sendPostRequest() {
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
                , image,path,categories);

        movieViewModel.addMovie(movie);

            Toast.makeText(getApplicationContext(), "Movie added successfully!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

