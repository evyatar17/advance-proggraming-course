#ifndef RECOMMEND_H
#define RECOMMEND_H
#include <unordered_map>
#include <unordered_set>
#include "ICommand.h"

namespace commands {
    class GET : public ICommand {
    public:
        // Constructor
        GET() = default;

        // Function to execute the Add command with arguments
        void execute(std::string input) override;

        void readFileToMap(const std::string& textFile, std::unordered_map<int, std::unordered_set<int>>& userMovies);

        bool legalInput(const std::string& input, int& user_id, int& movie_id);

    };
}
#endif