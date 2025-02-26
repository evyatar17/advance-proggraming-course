#ifndef CALCULATE_SIMILARITY_H
#define CALCULATE_SIMILARITY_H

#include <unordered_set>

// Declaration of function to calculate similarity between two users based on movie sets
int calculate_similarity(const std::unordered_set<int>& user1_movies, const std::unordered_set<int>& user2_movies);

#endif
