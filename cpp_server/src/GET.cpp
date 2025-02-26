#include "../headers/GET.h"
#include "../headers/recommendation.h"
#include "../headers/config.h"
#include <iostream>
#include <fstream>
#include <sstream>
#include <string> 
#include <regex>
#include <climits>

namespace commands {

void GET::readFileToMap(const std::string& textFile, std::unordered_map<int, std::unordered_set<int>>& userMovies) {
    std::ifstream file(textFile); // open file
    // error in loading file
    if (!file) {
        return;
    }
    // read text line by line
    std::string line;
    while (std::getline(file, line)) { 
        std::istringstream iss(line);
        int userId;
        char colon;
        if (!(iss >> userId >> colon) || colon != ':') {
            continue;
        }
        std::unordered_set<int> movieIds;
        int movieId;

        while (iss >> movieId) {
            movieIds.insert(movieId); // add movies ID in loop to each user ID
        }
        userMovies[userId] = movieIds; // add user and his movies to map
    }
 
    file.close(); // exit file
}

// check if input of user is legal
bool GET::legalInput(const std::string& input, int& user_id, int& movie_id) {
    std::regex pattern(R"(^\s*(\d+)\s+(\d+)\s*$)");
    std::smatch matches;

    if (!std::regex_match(input, matches, pattern)) {
        return false; // wrong input
    }

    try {
        // convert input to integer
        user_id = std::stoi(matches[1].str());
        movie_id = std::stoi(matches[2].str());

        // check numbers are in range
        if (user_id <= 0 || movie_id <= 0 || user_id > INT_MAX || movie_id > INT_MAX) {
            return false;
        }
    } catch (const std::exception&) {
        return false; 
    }

    return true; 
}

// namespace commands

void GET::execute(std::string input){
 int user_id, movie_id ;
 std::unordered_map<int, std::unordered_set<int>> userMovies;
 // input is legal 
 if(legalInput(input, user_id, movie_id)){
  readFileToMap(getDatabasePath(), userMovies);
// send data next
 recommendation( user_id,  movie_id, userMovies); 
 }
 //invalid input
 else{
    std::cout <<"400 Bad Request\n";
 }

 return;
}

} // namespace commands