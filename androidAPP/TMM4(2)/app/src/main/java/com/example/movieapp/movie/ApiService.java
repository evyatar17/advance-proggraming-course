package com.example.movieapp.movie;

import com.example.movieapp.LoginRequest;
import com.example.movieapp.LoginResponse;
import com.example.movieapp.RegisterRequest;
import com.example.movieapp.RegisterResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {
    //Register & Login endpoints
    @POST("users/register")
    Call<RegisterResponse> registerUser(@Body RegisterRequest request);

    @POST("users/login")
    Call<LoginResponse> loginUser(@Body LoginRequest request);

    // Category endpoints
    @GET("categories")
    Call<List<Category>> getCategories();

    @GET("categories/{id}")
    Call<Category> getCategory(@Path("id") String id);

    @POST("categories")
    Call<Category> createCategory(@Body Category category);

    @PUT("categories/{id}")
    Call<Category> updateCategory(@Path("id") String id, @Body Category category);

    @DELETE("categories/{id}")
    Call<Void> deleteCategory(@Path("id") String id);

    // Movie endpoints
    @GET("movies")
    Call<List<Movie>> getMovies();

    @GET("movies/{id}")
    Call<Movie> getMovie(@Path("id") String id);

    @POST("movies")
    Call<Movie> createMovie(@Body Movie movie);

    @PUT("movies/{id}")
    Call<Movie> updateMovie(@Path("id") String id, @Body Movie movie);

    @DELETE("movies/{id}")
    Call<Void> deleteMovie(@Path("id") String id);
}

