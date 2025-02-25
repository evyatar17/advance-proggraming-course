#include <unordered_set>
int calculate_similarity(const std::unordered_set<int>& user1_movies, const std::unordered_set<int>& user2_movies) {
    int similarity = 0;
    for (int movie : user1_movies) {
        if (user2_movies.count(movie)) {
            similarity++;
        }
    }
    return similarity;
}
