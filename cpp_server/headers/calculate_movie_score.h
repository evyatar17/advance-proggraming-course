#ifndef CALCULATE_MOVIE_SCORE_H
#define CALCULATE_MOVIE_SCORE_H

#include <unordered_map>
#include <unordered_set>

// Declaration of function to calculate movie scores based on user similarity
std::unordered_map<int, int> calculate_movie_scores(
    int user_id, int movie_id,
    const std::unordered_map<int, std::unordered_set<int>>& user_movies
);

#endif
