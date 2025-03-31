package com.example.movieapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class DeleteMovieActivity extends AppCompatActivity {
    private EditText etMovieId;
    private Button btnDelete;
    private MovieViewModel movieViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_movie);

        etMovieId = findViewById(R.id.etId);
        btnDelete = findViewById(R.id.btnSubmit);
        movieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);

        btnDelete.setOnClickListener(view -> deleteMovie());
    }

    private void deleteMovie() {
        String movieIdStr = etMovieId.getText().toString();
        if (movieIdStr.isEmpty()) {
            Toast.makeText(this, "Please enter a Movie ID", Toast.LENGTH_SHORT).show();
            return;
        }

        int movieId = Integer.parseInt(movieIdStr);
        movieViewModel.deleteMovie(movieId);
        Toast.makeText(this, "Movie deleted successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
