package com.example.movieapp;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieRepository {
    private static final String TAG = "MOVIE_ADD";
    private final MovieDao movieDao;
    private final MovieApi movieApi;
    private final ExecutorService executorService;
    private final MutableLiveData<MovieEntity> movieLiveData = new MutableLiveData<>();

    public MovieRepository(MovieDao movieDao, MovieApi movieApi) {
        this.movieDao = movieDao;
        this.movieApi = movieApi;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<MovieEntity> getMovieLiveData() {
        return movieLiveData;
    }

    public void fetchMovie(int id) {
        executorService.execute(() -> {
            MovieEntity movie = movieDao.getMovieById(id);
            if (movie != null) {
                movieLiveData.postValue(movie); // send data to UI
            } else {
                fetchFromApi(id);
            }
        });
    }

    private void fetchFromApi(int id) {
        movieApi.getMovieById(id).enqueue(new Callback<MovieEntity>() {
            @Override
            public void onResponse(Call<MovieEntity> call, Response<MovieEntity> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MovieEntity movie = response.body();
                    executorService.execute(() -> movieDao.insertMovie(movie));
                    movieLiveData.postValue(movie);
                    Log.d(TAG, "Fetched movie from API and inserted locally: " + movie.getTitle());
                } else {
                    Log.e(TAG, "API response not successful or empty body for movie id " + id);
                }
            }

            @Override
            public void onFailure(Call<MovieEntity> call, Throwable t) {
                Log.e(TAG, "Failed to fetch movie from API for id " + id, t);
            }
        });
    }

    public void addMovie(MovieEntity movie) {
        movieApi.addMovie(movie).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<MovieEntity> call, @NonNull Response<MovieEntity> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        MovieEntity newMovie = response.body();
                        executorService.execute(() -> {
                            movieDao.insertMovie(newMovie);
                            Log.d(TAG, "Inserted movie from API response: " + newMovie.getTitle());
                        });
                        movieLiveData.postValue(newMovie);
                    } else {
                        Log.d(TAG, "API response successful but body is null, inserting movie locally");
                        executorService.execute(() -> {
                            movieDao.insertMovie(movie);
                            Log.d(TAG, "Inserted movie locally: " + movie.getTitle());
                        });
                        movieLiveData.postValue(movie);
                    }
                } else {
                    Log.e(TAG, "API response not successful: " + response.code());
                    // במקרה של כישלון ב-API – נכניס את הסרט מקומית
                    executorService.execute(() -> {
                        movieDao.insertMovie(movie);
                        Log.d(TAG, "Inserted movie locally after API error: " + movie.getTitle());
                    });
                    movieLiveData.postValue(movie);
                }
            }

            @Override
            public void onFailure(Call<MovieEntity> call, Throwable t) {
                Log.e(TAG, "API call failed for addMovie", t);
                executorService.execute(() -> {
                    movieDao.insertMovie(movie);
                    Log.d(TAG, "Inserted movie locally after API call failure: " + movie.getTitle());
                });
                movieLiveData.postValue(movie);
            }
        });
    }

    public void deleteMovie(int movieId) {
        movieApi.deleteMovie(movieId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    executorService.execute(() -> {
                        movieDao.deleteMovieById(movieId);
                        Log.d(TAG, "Deleted movie with id " + movieId + " locally.");
                    });
                } else {
                    Log.e(TAG, "Failed to delete movie via API, response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "API call failed for deleteMovie", t);
            }
        });
    }

    public void updateMovie(MovieEntity movie) {
        movieApi.editMovie(movie.getId(), movie).enqueue(new Callback<MovieEntity>() {
            @Override
            public void onResponse(Call<MovieEntity> call, Response<MovieEntity> response) {
                if (response.isSuccessful() && response.body() != null) {
                    executorService.execute(() -> {
                        movieDao.updateMovie(response.body());
                        Log.d(TAG, "Updated movie locally: " + response.body().getTitle());
                    });
                } else {
                    Log.e(TAG, "Failed to update movie via API, response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<MovieEntity> call, Throwable t) {
                Log.e(TAG, "API call failed for updateMovie", t);
            }
        });
    }
}
