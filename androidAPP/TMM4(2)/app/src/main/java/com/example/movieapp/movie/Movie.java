package com.example.movieapp.movie;

public class Movie {
    private String _id;
    private String title;
    private String director;
    private int releaseYear;
    private String description;
    private Category category;
    private float rating;
    private String posterUrl;

    // Constructor
    public Movie() {}

    // Getters and Setters
    public String getId() { return _id; }
    public void setId(String id) { this._id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDirector() { return director; }
    public void setDirector(String director) { this.director = director; }

    public int getReleaseYear() { return releaseYear; }
    public void setReleaseYear(int releaseYear) { this.releaseYear = releaseYear; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }
}

