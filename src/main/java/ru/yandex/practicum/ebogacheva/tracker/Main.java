package ru.yandex.practicum.ebogacheva.tracker;

import ru.yandex.practicum.ebogacheva.tracker.managers.TaskManager;
import ru.yandex.practicum.ebogacheva.tracker.model.Task;
import ru.yandex.practicum.ebogacheva.tracker.server.HttpTaskServer;
import ru.yandex.practicum.ebogacheva.tracker.server.KVServer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        KVServer server = new KVServer();
        server.start();
        TaskManager taskManager = Managers.getHttpTaskManager("http://localhost:" + KVServer.PORT);
        Task task1 = new Task("Task 1",
                "Description 1",
                45,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
        taskManager.createTask(task1);
        System.out.println(taskManager.getTask(task1.getId()));
    }
}
