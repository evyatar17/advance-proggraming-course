package com.example.movieapp;

import org.json.JSONArray;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface MovieApi {
        @POST("/api/users/register")
        Call<RegisterResponse> registerUser(@Body RegisterRequest request);
        @POST("/api/users/login")
        Call<LoginResponse> loginUser(@Body LoginRequest request);

        @GET("/api/movies/{id}")
        Call<MovieEntity> getMovieById(@Path("id") int id);
        @POST("/api/movies/add")
        Call<MovieEntity> addMovie(@Body MovieEntity movie);

        @PUT("/api/movies/{id}")
        Call<MovieEntity> editMovie(@Path("id") int id, @Body MovieEntity movie);

        @DELETE("/api/movies/{id}")
        Call<Void> deleteMovie(@Path("id") int id);

        @POST("/api/categories")
        Call<CategoriesEntity> addCategory(@Body CategoriesEntity category);

        @PUT("/api/categories/{id}")
        Call<CategoriesEntity> editCategory(@Path("id") int id, @Body CategoriesEntity category);

        @DELETE("/api/categories/{id}")
        Call<Void> deleteCategory(@Path("id") int id);
        @GET("/api/movies")
        Call<List<Movie>> getMovies();

}
