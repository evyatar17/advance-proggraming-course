#include <iostream>
#include <sys/socket.h>
#include <netinet/in.h>
#include <unistd.h>
#include <stdexcept>
#include <functional>
#include <sstream>
#include <cstring>
#include <algorithm>

#include "../headers/server.h"
#include "../headers/PATCH.h"
#include "../headers/POST.h"
#include "../headers/GET.h"
#include "../headers/delete.h"

ServerHandler::ServerHandler(int port, size_t threadCount) : threadPool(threadCount) {
    serverSocket = socket(AF_INET, SOCK_STREAM, 0);
    if (serverSocket == -1) {
        throw std::runtime_error("Socket creation failed");
    }

    int optval = 1;
    if (setsockopt(serverSocket, SOL_SOCKET, SO_REUSEADDR, &optval, sizeof(optval)) == -1) {
        throw std::runtime_error("Failed to set socket options");
    }

    sockaddr_in serverAddr = {};
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_addr.s_addr = INADDR_ANY;
    serverAddr.sin_port = htons(port);

    if (bind(serverSocket, (struct sockaddr*)&serverAddr, sizeof(serverAddr)) < 0) {
        throw std::runtime_error("Binding failed");
    }

    if (listen(serverSocket, 5) < 0) {
        throw std::runtime_error("Listen failed");
    }
}

void ServerHandler::start() {
    while (true) {
        int clientSocket = accept(serverSocket, nullptr, nullptr);
        if (clientSocket < 0) {
            std::cerr << "Accept failed" << std::endl;
            continue;
        }
        threadPool.enqueueTask([this, clientSocket] { handleClient(clientSocket); });
    }
}

std::string ServerHandler::preprocessInput(const std::string& input) {
    std::string processed = input;

    processed.erase(std::remove(processed.begin(), processed.end(), '\n'), processed.end());
    
    std::istringstream stream(processed);
    std::string command;
    stream >> command;
    
    std::string arguments = processed.substr(command.length());
    arguments.erase(0, arguments.find_first_not_of(" \t"));
    
    return arguments;
}

void ServerHandler::handleClient(int clientSocket) {
    char buffer[1024] = {};
    // Remove the while(true) loop - handle one request per connection
    memset(buffer, 0, sizeof(buffer));
    ssize_t bytesReceived = recv(clientSocket, buffer, sizeof(buffer) - 1, 0);

    if (bytesReceived > 0) {
        buffer[bytesReceived] = '\0';
        std::string input(buffer);
        std::istringstream stream(input);
        std::string command;
        stream >> command;

        std::string httpResponse;
        std::ostringstream response;
        std::streambuf* oldCout = std::cout.rdbuf(response.rdbuf());

        try {
            if (command == "POST") {
                commands::POST postCommand;
                postCommand.execute(preprocessInput(input));
            } else if (command == "PATCH") {
                commands::PATCH patchCommand;
                patchCommand.execute(preprocessInput(input));
            } else if (command == "GET") {
                commands::GET getCommand;
                getCommand.execute(preprocessInput(input));
            } else if (command == "DELETE") {
                commands::DELETE deleteCommand;
                deleteCommand.execute(preprocessInput(input));
            } else {
                std::cout << "400 Bad Request";
            }
        } catch (const std::exception& e) {
            std::cout << "500 Internal Server Error: " << e.what();
        }

        std::cout.rdbuf(oldCout);
        std::string capturedResponse = response.str();
        
        // Determine the correct HTTP status code based on the response
        std::string statusLine;
        if (capturedResponse.find("400") == 0) {
            statusLine = "HTTP/1.1 400 Bad Request";
        } else if (capturedResponse.find("500") == 0) {
            statusLine = "HTTP/1.1 500 Internal Server Error";
        } else if (capturedResponse.find("201") == 0) {
            statusLine = "HTTP/1.1 201 Created";
        } else {
            statusLine = "HTTP/1.1 200 OK";
        }

        httpResponse = statusLine + "\r\n"
                   "Content-Type: text/plain\r\n"
                   "Content-Length: " + std::to_string(capturedResponse.size()) + "\r\n"
                   "\r\n" +
                   capturedResponse;

        ssize_t bytesSent = send(clientSocket, httpResponse.c_str(), httpResponse.size(), 0);
        if (bytesSent == -1) {
            std::cerr << "Error sending response: " << strerror(errno) << std::endl;
        }
    } else if (bytesReceived < 0) {
        std::cerr << "Receive error: " << strerror(errno) << std::endl;
    }
    
    // Always close the connection after handling the request
    close(clientSocket);
}

ServerHandler::~ServerHandler() {
    close(serverSocket);
}

int main(int argc, char* argv[]) {
    if (argc != 3) {
        std::cerr << "Usage: " << argv[0] << " <port> <thread_count>" << std::endl;
        return 1;
    }

    int port = std::stoi(argv[1]);
    size_t threadCount = std::stoul(argv[2]);

    try {
        ServerHandler server(port, threadCount);
        server.start();
    } catch (const std::exception& e) {
        std::cerr << "Server error: " << e.what() << std::endl;
        return 1;
    }

    return 0;
}