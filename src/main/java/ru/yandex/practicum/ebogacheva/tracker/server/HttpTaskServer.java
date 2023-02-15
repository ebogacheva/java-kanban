package ru.yandex.practicum.ebogacheva.tracker.server;

import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.ebogacheva.tracker.Managers;
import ru.yandex.practicum.ebogacheva.tracker.managers.FileBackedTaskManager;
import ru.yandex.practicum.ebogacheva.tracker.managers.HttpTaskManager;
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

    public HttpTaskServer() throws IOException, InterruptedException {
        this.httpServer = HttpServer.create();
        this.httpServer.bind(new InetSocketAddress(PORT), 0);
        TaskManager taskManager = Managers.getHttpTaskManager("http://localhost:8078");
        httpServer.createContext("/tasks", new PrioritizedTasksHandler(taskManager));
        httpServer.createContext("/tasks/history", new HistoryHandler(taskManager));
        httpServer.createContext("/tasks/task", new TaskHandler(taskManager));
        httpServer.createContext("/tasks/subtask", new SubtaskHandler(taskManager));
        httpServer.createContext("/tasks/epic", new EpicHandler(taskManager));
    }

    public void start() {
        System.out.println("HTTP-cервер запущен на " + PORT + " порту!");
        this.httpServer.start();
    }

    public void stop() {
        this.httpServer.stop(1);
        System.out.println("HTTP-cервер остановлен на " + PORT + " порту!");
    }
}
