package ru.yandex.practicum.ebogacheva.tracker;

import ru.yandex.practicum.ebogacheva.tracker.managers.TaskManager;
import ru.yandex.practicum.ebogacheva.tracker.model.Task;
import ru.yandex.practicum.ebogacheva.tracker.model.TaskManagerConstants;
import ru.yandex.practicum.ebogacheva.tracker.server.KVServer;

import java.io.IOException;
import java.time.LocalDateTime;
public class Main {

    public static void main(String[] args) throws IOException {
        KVServer server = new KVServer();
        server.start();
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Task 1",
                "Description 1",
                45,
                LocalDateTime.now().format(TaskManagerConstants.DATE_TIME_FORMATTER));
        taskManager.createTask(task1);
        System.out.println(taskManager.getTask(task1.getId()));
    }
}
