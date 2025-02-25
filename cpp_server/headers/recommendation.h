#ifndef RECOMMENDATION_H
#define RECOMMENDATION_H

#include <unordered_map>
#include <unordered_set>
#include <vector>

// Declaration of compare function for sorting movies by score and movie ID
bool compare(const std::pair<int, int>& a, const std::pair<int, int>& b);

// Declaration of function to get top k movies based on scores
std::vector<int> get_top_k(const std::unordered_map<int, int> movies, int k);

// Declaration of function to get movie recommendations for a user
std::vector<int> recommendation(int user_id, int movie_id, const std::unordered_map<int, std::unordered_set<int>>& user_movies);

#endif
