#include "../headers/threadpool.h"

ThreadPool::ThreadPool(size_t numThreads) : stop(false) {
    for (size_t i = 0; i < numThreads; ++i) {
        workers.emplace_back([this] { workerThread(); });
    }
}

ThreadPool::~ThreadPool() {
    stop.store(true);
    condition.notify_all();
    for (std::thread &worker : workers) {
        if (worker.joinable()) {
            worker.join();
        }
    }
}

void ThreadPool::enqueueTask(const std::function<void()>& task) {
    {
        std::unique_lock<std::mutex> lock(queueMutex);
        tasks.emplace(task);
    }
    condition.notify_one();
}

void ThreadPool::workerThread() {
    while (!stop.load()) {
        std::function<void()> task;
        {
            std::unique_lock<std::mutex> lock(queueMutex);
            condition.wait(lock, [this] { return stop.load() || !tasks.empty(); });
            if (stop.load() && tasks.empty()) {
                return;
            }
            task = std::move(tasks.front());
            tasks.pop();
        }
        task();
    }
}
