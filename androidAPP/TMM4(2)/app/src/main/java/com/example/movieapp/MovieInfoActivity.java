package com.example.movieapp;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;


public class MovieInfoActivity extends AppCompatActivity {
    private MovieViewModel movieViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_info_screen);

        // הפניות ל-Views
        TextView titleTextView = findViewById(R.id.movieTitle);
        TextView descriptionTextView = findViewById(R.id.movieDescription);
        TextView directorTextView = findViewById(R.id.movieDirector);
        TextView yearTextView = findViewById(R.id.movieYear);
        TextView ratingTextView = findViewById(R.id.movieRating);
        ImageView movieImageView = findViewById(R.id.movieImage);

        // קבלת ה-ID מה-Intent
        Intent intent = getIntent();
        int movieId = intent.getIntExtra("MOVIE_ID", -1);

        // restart ViewModel
        movieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);


        movieViewModel.getMovieLiveData().observe(this, movie -> {
            if (movie != null) {
                titleTextView.setText(movie.getTitle());
                descriptionTextView.setText(movie.getDescription());
                directorTextView.setText("Director: " + movie.getDirector());
                yearTextView.setText("Year: " + movie.getYear());
                ratingTextView.setText("Rating: " + movie.getRating());

                // using Glide for image handling
                Glide.with(this)
                        .load(movie.getImage())
                        .into(movieImageView);
            }
        });
        // load movie's data
        movieViewModel.fetchMovie(movieId);

        findViewById(R.id.WatchButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String ID = Integer.toString(movieId);
                Intent intent = new Intent(MovieInfoActivity.this, WatchActivity.class);
                intent.putExtra("MOVIE_ID", ID);
                startActivity(intent);
            }
        });
    }
}
