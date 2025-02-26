#include <unordered_map>
#include <unordered_set>
#include "../headers/calculate_similarity.h"
#include <iostream>

    std::unordered_map<int, int> calculate_movie_scores(
    int user_id, int movie_id,
    const std::unordered_map<int, std::unordered_set<int>>& user_movies) 
{
    std::unordered_map<int, int> movie_scores;
    //  movies that "input user" saw we need to know what movies we shouldn't recommend
    const auto& user_watched_movies = user_movies.at(user_id);
    for (const auto& pair : user_movies) {
        int other_user_id = pair.first; // key: user ID when the user is not the user_id
        const std::unordered_set<int>& other_movies = pair.second; // value: set of movies of last user
        if (other_user_id == user_id){ 
        continue;
        }
        if(!(pair.second.count(movie_id))) continue;
        // calculate the similarity 
         int similarity = calculate_similarity(user_watched_movies, other_movies);
         
        for (int other_movie : other_movies) {
            if (!user_watched_movies.count(other_movie)) {
                movie_scores[other_movie] += similarity;
            }
        }
    }
    // according to example the movie_id that came in input won't be recommend
    movie_scores.erase(movie_id);
    
    // delete movie with score 0 according to what we understood from
    // forum in moodle if relevance = 0 movie won't be recommend
    for (auto current = movie_scores.begin(); current != movie_scores.end();) {
        if (current->second == 0) {
            current = movie_scores.erase(current); // delete movie
        } else {
            ++current; // continue to next movie
        }
    }
    return movie_scores;
}