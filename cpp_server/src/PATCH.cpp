#include "../headers/PATCH.h"
#include <fstream>
#include <sstream>
#include <string>
#include <vector>
#include <iostream>
#include <algorithm>
#include <climits>
#include "../headers/config.h"

namespace commands {

// Function to check if the input is valid
bool PATCH::isValidInput(const std::string& input) {
    // Reject inputs with any whitespace characters other than spaces
    if (input.find_first_of("\t\n\r\f\v") != std::string::npos) {
        return false;
    }
    // Ensure there's at least one user ID and one movie ID
    if (input.empty()) {
        return false;
    }

    // Use a stringstream to break the input into parts
    std::istringstream stream(input);
    std::string userIdStr;
    stream >> userIdStr; // Read the user ID

    // If the user ID is not a valid number, return false
    if (!isNumber(userIdStr)) {
        return false;
    }

    long userId = std::stol(userIdStr);
    // Ensure the user ID is positive and not too big
    if (userId <= 0 || userId > INT_MAX) {
        return false;
    }

    std::string movie;
    bool movieFound = false;
    // Read the remaining parts (movies) from the stream
    long movcheck;
    while (stream >> movie) {
        if (!isNumber(movie)) {
        return false;
        }
        movcheck = std::stol(movie);
        if (movcheck > INT_MAX || movcheck < 0) {
        return false;
        }
        movieFound = true; //its a valid digit
    }

    return movieFound; // At least one movie should be found
}

// Helper function to check if a string is a number
bool PATCH::isNumber(const std::string& str) {
    // Check if the string is empty or contains any whitespace
    if (str.empty() || str.find_first_of(" \t\n\r\f\v") != std::string::npos) {
        return false;
    }

    // Ensure all characters are digits
    return std::all_of(str.begin(), str.end(), ::isdigit);
}


// Function to add a user and movies to the database
void PATCH::execute(std::string input) {

    if (!isValidInput(input)) {
        std::cout << "400 Bad Request" << std::endl;
        return;
    }
    std::istringstream stream(input);
    std::string userIdStr;
    stream >> userIdStr; // Read user ID
    
    int userId = std::stoi(userIdStr); // Convert to integer

    // Read the movies from the input stream into a vector
    std::vector<std::string> movies;
    std::string movie;
    while (stream >> movie) {
        movies.push_back(movie);
    }

    // Open the database file to append the new data
    std::ifstream dbRead(getDatabasePath());
    std::string line;
    bool userFound = false;
    std::vector<std::string> allLines;

    // Read the current content of the database to check if the user already exists
    while (std::getline(dbRead, line)) {
        std::istringstream lineStream(line);
        std::string existingUserIdStr;
        std::getline(lineStream, existingUserIdStr, ':'); // Extract the user ID

        int existingUserId = std::stoi(existingUserIdStr); // Convert to int

        if (existingUserId == userId) {
            userFound = true;
            std::string existingMovies;
            std::getline(lineStream, existingMovies);
            
            // Merge the new movies with the existing ones, ensuring no duplicates
            std::istringstream moviesStream(existingMovies);
            std::string existingMovie;
            std::vector<std::string> updatedMovies;
            
            // First, add existing movies to the vector
            while (moviesStream >> existingMovie) {
                updatedMovies.push_back(existingMovie);
            }
            
            // Then, add new movies only if they don't already exist
            for (const auto& newMovie : movies) {
                if (std::find(updatedMovies.begin(), updatedMovies.end(), newMovie) == updatedMovies.end()) {
                    updatedMovies.push_back(newMovie);
                }
            }
            
            // Reconstruct the line with updated movies
            line = existingUserIdStr + ":";
            for (const auto& mov : updatedMovies) {
                line += " " + mov;
            }
        }

        allLines.push_back(line); // Store all lines for later writing
    }
    dbRead.close();

    // If user is not found, return
    if (!userFound) {
        std::cout << "404 Not Found" << std::endl;
        return;
    }

    // Write the updated data back to the database
    std::ofstream dbWrite(getDatabasePath());
    for (const auto& line : allLines) {
        dbWrite << line << "\n"; // Write each line to the file
    }
    std::cout << "204 No Content" << std::endl;
    dbWrite.close();
}

} // namespace commands
