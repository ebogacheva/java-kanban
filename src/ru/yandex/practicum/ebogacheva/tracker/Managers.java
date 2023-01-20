package ru.yandex.practicum.ebogacheva.tracker;

import ru.yandex.practicum.ebogacheva.tracker.history.HistoryManager;
import ru.yandex.practicum.ebogacheva.tracker.history.InMemoryHistoryManager;
import ru.yandex.practicum.ebogacheva.tracker.task_managers.FileBackedTaskManager;
import ru.yandex.practicum.ebogacheva.tracker.task_managers.InMemoryTaskManager;
import ru.yandex.practicum.ebogacheva.tracker.task_managers.TaskManager;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getFileBackedManager(String fileName) {
        return new FileBackedTaskManager(fileName);
    }

}
