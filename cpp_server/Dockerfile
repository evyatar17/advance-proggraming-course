# Use a lightweight base image for C++ development
FROM gcc:12.2.0

# Set the working directory inside the container
WORKDIR /app

# Copy all source files into the container
COPY src/ ./src/
COPY headers/ ./headers/

# Compile the C++ server with all its dependencies
RUN g++ -o server ./src/server.cpp ./src/Threadpool.cpp ./src/calculate_movie_score.cpp ./src/calculate_similarity.cpp ./src/GET.cpp ./src/POST.cpp ./src/PATCH.cpp ./src/DELETE.cpp ./src/recommendation.cpp -I./headers -lpthread

# Expose the port your server will listen on
EXPOSE 8080

# Command to run the C++ server
CMD ["./server", "8080", "4"]
