#ifndef CONFIG_H
#define CONFIG_H

#include <string>
#include <cstdlib>

inline const std::string getDatabasePath() {
    const char* dbEnv = std::getenv("DATABASE_PATH");
    return dbEnv ? dbEnv : "/app/Data/database.txt";
}

#endif // CONFIG_H
