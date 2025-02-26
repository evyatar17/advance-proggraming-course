#include "../headers/delete.h"
#include <fstream>
#include <sstream>
#include <string>
#include <vector>
#include <iostream>
#include <algorithm>
#include <climits>
#include "../headers/config.h"
#include "delete.h"
#define DEFAULT_DATABASE_PATH "/app/Data/database.txt" 
namespace commands {

void DELETE::setDatabasePath(const std::string& path) {
    databasePath = path;
}
 
std::string DELETE::getDatabasePath() const {
    return databasePath.empty() ? DEFAULT_DATABASE_PATH : databasePath;
}


// Function to check if the input is valid for deletion
bool DELETE::isValidInput(const std::string& input) {
    if (input.empty()) {
        return false;
    }

    std::istringstream stream(input);
    std::string userIdStr;
    stream >> userIdStr;

    if (!isNumber(userIdStr)) {
        return false;
    }

    long userId = std::stol(userIdStr);
    if (userId <= 0 || userId > INT_MAX) {
        return false;
    }

    std::string movie;
    while (stream >> movie) {
        if (!isNumber(movie)) {
            return false;
        }
    }

    return true; // Valid if at least one movie is provided
}

// Helper function to check if a string is a number
bool DELETE::isNumber(const std::string& str) {
    return !str.empty() && std::all_of(str.begin(), str.end(), ::isdigit);
}


// Function to delete movies from a user in the database
void DELETE::execute(std::string input) { 
    // inbalid input check
    if (!isValidInput(input)) {
        std::cout << "400 Bad Request\n";
        return; 
    }

    // work with stream 
    std::istringstream stream(input);
    std::string userIdStr;
    stream >> userIdStr;

    int userId = std::stoi(userIdStr);
    
    std::vector<std::string> moviesToDelete;
    std::string movie;
    while (stream >> movie) {
        moviesToDelete.push_back(movie);
    }

    std::ifstream dbRead(getDatabasePath());
    std::string line;
    std::vector<std::string> allLines;
    bool userFlag = false, allMoviesExist = true;

    // read and save in temporary file
    while (std::getline(dbRead, line)) {
        std::istringstream lineStream(line);
        std::string existingUserIdStr;
        std::getline(lineStream, existingUserIdStr, ':');

        int existingUserId = std::stoi(existingUserIdStr);
        if (existingUserId == userId) {
            userFlag = true;
            std::string existingMovies;
            std::getline(lineStream, existingMovies);
            std::istringstream moviesStream(existingMovies);
            std::string existingMovie;
            std::vector<std::string> userMovies;

            // read user's movies
            while (moviesStream >> existingMovie) {
                userMovies.push_back(existingMovie);
            }

            // check if all movies to delete is in user's movies
            for (const auto& movieToDelete : moviesToDelete) {
                if (std::find(userMovies.begin(), userMovies.end(), movieToDelete) == userMovies.end()) {
                    allMoviesExist = false;
                    break;
                }
            }

            // error some movies not found 
            if (!allMoviesExist) {
                std::cout << "404 Not Found\n";
                return;
            }

            // delete the movies
            std::vector<std::string> updatedMovies;
            for (const auto& userMovie : userMovies) {
                if (std::find(moviesToDelete.begin(), moviesToDelete.end(), userMovie) == moviesToDelete.end()) {
                    updatedMovies.push_back(userMovie);
                }
            }

            // restore the line
            line = existingUserIdStr + ":";
            for (const auto& mov : updatedMovies) {
                line += " " + mov;
            }
        }

        allLines.push_back(line);
    }
    // close database
    dbRead.close();

    // rewriting to the database
    std::ofstream dbWrite(getDatabasePath());
    for (const auto& line : allLines) {
        dbWrite << line << "\n";
    }
    dbWrite.close();

    if (!userFlag) {
        std::cout << "404 Not Found\n";
        return;
    }

    std::cout << "204 No Content\n";
}
}
