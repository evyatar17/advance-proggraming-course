#ifndef POST_H
#define POST_H

#include "ICommand.h"
#include <vector>
#include <string>

namespace commands {

class POST : public ICommand {
public:
    // Constructor
    POST() = default;

    // Function to execute the Add command with arguments
    void execute(std::string input) override;

    // Function to validate input format
    bool isValidInput(const std::string& input);

    // Helper function to check if a string is a number
    bool isNumber(const std::string& str);
};

} // namespace commands

#endif // POST_H
