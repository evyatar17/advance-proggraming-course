package com.example.movieapp;

import com.google.gson.annotations.SerializedName;

public class Movie {
    @SerializedName("_id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("releaseYear")
    private String releaseYear;

    @SerializedName("posterUrl")
    private String posterUrl;

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getReleaseDate() { return releaseYear; }
    public String getVideoUrl() { return posterUrl; }
}
