package com.example.movieapp;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MovieDao {
    @Insert
    void insertMovie(MovieEntity movie);
    @Query("DELETE FROM movies WHERE id = :movieId")
    void deleteMovieById(int movieId);
    @Query("SELECT * FROM movies WHERE ID = :id")
    MovieEntity getMovieById(int id);
    @Update
    void updateMovie(MovieEntity movie);

}
