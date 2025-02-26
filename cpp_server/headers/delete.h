#ifndef DELETE_H
#define DELETE_H
#include "ICommand.h"
#include <vector>
#include <string>

namespace commands {

class DELETE : public ICommand {
private:
    std::string databasePath;

public:
    // Constructor
    DELETE() = default;

    // Function to execute the DELETE command with arguments
    void execute(std::string input) override;

    void setDatabasePath(const std::string &path);

    std::string getDatabasePath() const;

    // Function to validate input format
    bool isValidInput(const std::string& input);

    // Helper function to check if a string is a number
    bool isNumber(const std::string& str);
};

} // namespace commands

#endif 
