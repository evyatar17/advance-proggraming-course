package com.example.movieapp.movie;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.movieapp.R;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private Context context;
    private List<Movie> movieList;

    public MovieAdapter(Context context, List<Movie> movieList) {
        this.context = context;
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.new_item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);

        holder.textTitle.setText(movie.getTitle());
        holder.textDirector.setText(movie.getDirector());
        holder.textYear.setText(String.valueOf(movie.getReleaseYear()));

        if (movie.getCategory() != null) {
            holder.textCategory.setText(movie.getCategory().getName());
        }

        if (movie.getPosterUrl() != null && !movie.getPosterUrl().isEmpty()) {
            Glide.with(context)
                    .load(movie.getPosterUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(holder.imgPoster);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddEditMovieActivity.class);
            intent.putExtra("MOVIE_ID", movie.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPoster;
        TextView textTitle, textDirector, textYear, textCategory;

        MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPoster = itemView.findViewById(R.id.imgMoviePoster);
            textTitle = itemView.findViewById(R.id.textMovieTitle);
            textDirector = itemView.findViewById(R.id.textMovieDirector);
            textYear = itemView.findViewById(R.id.textMovieYear);
            textCategory = itemView.findViewById(R.id.textMovieCategory);
        }
    }
}
