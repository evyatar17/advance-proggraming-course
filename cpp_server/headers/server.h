#ifndef SERVER_H
#define SERVER_H

#include <vector>
#include <thread>
#include <string>
#include "threadpool.h"

class ServerHandler {
private:
    int server_socket;
    std::vector<std::thread> client_threads;

    // Utility function to preprocess input
    std::string preprocessInput(const std::string& input);

    // Handle individual client connection
    void handleClient(int client_socket);

    int serverSocket;

    ThreadPool threadPool;

public:
    // Constructor that takes a port number
    explicit ServerHandler(int port, size_t threadCount);

    // Start the server and accept connections
    void start();

    // Destructor to clean up resources
    ~ServerHandler();
};

#endif // SERVER_H