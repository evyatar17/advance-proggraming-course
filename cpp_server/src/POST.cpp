#include "../headers/POST.h"
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
bool POST::isValidInput(const std::string& input) {
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
bool POST::isNumber(const std::string& str) {
    // Check if the string is empty or contains any whitespace
    if (str.empty() || str.find_first_of(" \t\n\r\f\v") != std::string::npos) {
        return false;
    }

    // Ensure all characters are digits
    return std::all_of(str.begin(), str.end(), ::isdigit);
}


// Function to add a user and movies to the database
void POST::execute(std::string input) {

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
    std::vector<std::string> allLines;
    std::ifstream dbRead(getDatabasePath());
    if (dbRead.is_open()) {  // Added file check
        std::string line;
        while (std::getline(dbRead, line)) {
            std::istringstream lineStream(line);
            std::string existingUserIdStr;
            std::getline(lineStream, existingUserIdStr, ':');
        
            // Added whitespace trimming
            existingUserIdStr.erase(0, existingUserIdStr.find_first_not_of(" "));
            existingUserIdStr.erase(existingUserIdStr.find_last_not_of(" ") + 1);
        
            if (!existingUserIdStr.empty()) {  // Added empty line check
                int existingUserId = std::stoi(existingUserIdStr);
                if (existingUserId == userId) {
                    std::cout << "400 Bad Request" << std::endl;
                    dbRead.close();
                    return;
                }
                allLines.push_back(line);
            }
        }
        dbRead.close();
    }

    // Otherwise since user was not found, add them to the database
    std::string newLine = userIdStr + ":";
    for (const auto& movie : movies) {
        newLine += " " + movie;
    }
    allLines.push_back(newLine); // Add the new user with movies

    // Write the updated data back to the database
    std::ofstream dbWrite(getDatabasePath());
    if (dbWrite.is_open()) {  // Added file check
        for (size_t i = 0; i < allLines.size(); ++i) {
            dbWrite << allLines[i];
            if (i < allLines.size() - 1) {  // Only add newline between lines
                dbWrite << "\n";
            }
        }
        dbWrite.close();
        std::cout << "201 Created" << std::endl;
    }else {
        std::cout << "400 Bad Request" << std::endl;
    }   
    }

} // namespace commands
