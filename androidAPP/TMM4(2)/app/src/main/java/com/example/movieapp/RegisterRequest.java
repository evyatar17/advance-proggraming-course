package com.example.movieapp;

public class RegisterRequest {
    private String email;
    private String firstName;
    private String lastName;
    private String username;
    private String password;

    public RegisterRequest(String email, String firstName, String lastName, String username, String password) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
    }
}
