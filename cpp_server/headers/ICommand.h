#ifndef ICOMMAND_H
#define ICOMMAND_H

#include <string>

namespace commands {

    class ICommand {
    public:
        virtual ~ICommand() = default;
        virtual void execute(std::string input) = 0; // Now accepts input as a string argument
    };

} // namespace commands

#endif // ICOMMAND_H
