package ru.yandex.practicum.ebogacheva.tracker.server;

import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.ebogacheva.tracker.managers.FileBackedTaskManager;
import ru.yandex.practicum.ebogacheva.tracker.managers.TaskManager;
import ru.yandex.practicum.ebogacheva.tracker.server.handlers.EpicHandler;
import ru.yandex.practicum.ebogacheva.tracker.server.handlers.HistoryHandler;
import ru.yandex.practicum.ebogacheva.tracker.server.handlers.PrioritizedTasksHandler;
import ru.yandex.practicum.ebogacheva.tracker.server.handlers.SubtaskHandler;
import ru.yandex.practicum.ebogacheva.tracker.server.handlers.TaskHandler;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    private static final int PORT = 8080;
    private final HttpServer httpServer;

    public HttpTaskServer() throws IOException {
        this.httpServer = HttpServer.create();
        this.httpServer.bind(new InetSocketAddress(PORT), 0);
        TaskManager taskManager = FileBackedTaskManager.loadFromFile(new File("test-httpServer.txt"));
        httpServer.createContext("/tasks", new PrioritizedTasksHandler(taskManager)); // getPrioritizedTasks
        httpServer.createContext("/tasks/history", new HistoryHandler(taskManager)); // getHistory
        httpServer.createContext("/tasks/task", new TaskHandler(taskManager)); // tasks-methods
        httpServer.createContext("/tasks/subtask", new SubtaskHandler(taskManager)); // subtask-methods
        httpServer.createContext("/tasks/epic", new EpicHandler(taskManager)); //epic-methods
    }

    public void start() {
        this.httpServer.start();
    }

    public void stop() {
        this.httpServer.stop(0);
    }
}
