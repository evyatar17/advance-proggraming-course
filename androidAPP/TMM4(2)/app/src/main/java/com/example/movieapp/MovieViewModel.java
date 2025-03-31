package com.example.movieapp;
import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
public class MovieViewModel extends AndroidViewModel {
    private final MovieRepository repository;
    private final LiveData<MovieEntity> movieLiveData;

    public MovieViewModel(Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application);
        MovieDao movieDao = db.movieDao();
        MovieApi movieApi = ApiClient.getClient();
        repository = new MovieRepository(movieDao, movieApi);
        movieLiveData = repository.getMovieLiveData();
    }
    public LiveData<MovieEntity> getMovieLiveData() {
        return movieLiveData;
    }
    public void fetchMovie(int id) {
        repository.fetchMovie(id);
    }
    public void addMovie(MovieEntity movie) {
        repository.addMovie(movie);
    }
    public void deleteMovie(int movieId) {
        repository.deleteMovie(movieId);
    }
    public void updateMovie(MovieEntity movie) {
        repository.updateMovie(movie);
    }
}