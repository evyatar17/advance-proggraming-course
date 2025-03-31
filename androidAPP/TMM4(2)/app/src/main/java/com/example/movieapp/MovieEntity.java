package com.example.movieapp;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.List;

@Entity(tableName = "movies")
public class MovieEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String description;
    private int year;
    private String director;
    private double rating;
    private String image;

    private String categories;

    private String videoPath;

    /*@TypeConverters(Converters.class)
    private List<String> categories;*/


    public MovieEntity( String title, String description, int year, String director,
                        double rating, String image, String videoPath, String categories) {
        this.title = title;
        this.description = description;
        this.year = year;
        this.director = director;
        this.rating = rating;
        this.image = image;
        this.videoPath = videoPath;
        this.categories = categories;
    }

    public MovieEntity() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }
    public String getVideoPath() {
    return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }
}