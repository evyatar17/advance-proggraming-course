package com.example.movieapp.movie;

public class Category {
    private String _id;
    private String name;
    private String description;

    // Constructor
    public Category() {}

    // Getters and Setters
    public String getId() { return _id; }
    public void setId(String id) { this._id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return name;
    }
}

