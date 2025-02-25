#include <unordered_set>
#include <unordered_map>
#include <algorithm> 
#include <fstream>
#include <vector>
#include "../headers/calculate_movie_score.h"
#include <iostream>
// a compare function for the sorting of the movies. order by score (decreasing) and if 
// score eaqul order by movie id (increasing).
bool compare (const std::pair<int, int>& a, const std::pair<int, int>& b) {
        // if score equal order by movie id (increasing).
        if (a.second == b.second) {
            return a.first < b.first; 
        }
        // order by score (decreasing)
        return a.second > b.second; 
    }

std:: vector<int> get_top_k (const std::unordered_map<int, int> movies ,int k)
{
    std::vector<std::pair<int, int>> movie_list(movies.begin(), movies.end());
    std::sort(movie_list.begin(), movie_list.end(), compare);
    std::vector<int> top_movies ; 
    int size = movie_list.size();
    for (int i = 0; i < k && i < size; ++i) {
    top_movies.push_back(movie_list[i].first);
}
return top_movies ;
}

std::vector<int> recommendation (int user_id, int movie_id, const std::unordered_map<int, std::unordered_set<int>>& user_movies) 
{ 
    std::unordered_map<int, std::unordered_set<int>> copy_user_movies = user_movies;
     // if file not exist
  if (copy_user_movies.empty()){
    // empty vector list 
     std::cout <<"404 Not Found\n";
    return {} ;
  }
    // if user not exist
  if ( copy_user_movies.count(user_id) <= 0){
     std::cout <<"404 Not Found\n";
    return {} ;
  }

  // check if movie_id not exist
 bool found = false;
    // search value in map
    for (const auto& pair : copy_user_movies) {

        // if movie_id in map
        if (pair.second.count(movie_id) > 0  ) { 
            found = true;
            break;
        }
    }
    // movie not exist 
    if (found == false) {
        std::cout <<"404 Not Found\n";
        return {} ;
    }
 // the score of each movie that exist according to the algorythem 
    auto movie_scores = calculate_movie_scores(user_id, movie_id, user_movies);
    // return best 10 recommendation (or less if there are less than 10 movies)
   std::vector<int> x = get_top_k(movie_scores, 10);
   // no recommendation
  if(x.empty()){
    std::cout <<"200 Ok\n";
    std::cout <<"\n";
    return x;
  }
  // if there are recommendation
  std::cout <<"200 Ok\n";
  std::cout <<"\n";
  // print the movie recommended list
    for (int i = 0; i < x.size(); ++i) {
        std::cout << x[i] << " ";
    }
    std::cout << std::endl;
    return x;
}