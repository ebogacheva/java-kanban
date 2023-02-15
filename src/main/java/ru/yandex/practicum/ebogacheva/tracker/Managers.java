package ru.yandex.practicum.ebogacheva.tracker;

import ru.yandex.practicum.ebogacheva.tracker.history.HistoryManager;
import ru.yandex.practicum.ebogacheva.tracker.history.InMemoryHistoryManager;
import ru.yandex.practicum.ebogacheva.tracker.managers.FileBackedTaskManager;
import ru.yandex.practicum.ebogacheva.tracker.managers.HttpTaskManager;
import ru.yandex.practicum.ebogacheva.tracker.managers.InMemoryTaskManager;
import ru.yandex.practicum.ebogacheva.tracker.managers.TaskManager;
import ru.yandex.practicum.ebogacheva.tracker.server.KVServer;

import java.io.IOException;

public class Managers {

    public static TaskManager getDefault() { return new InMemoryTaskManager(); }
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
    public static TaskManager getFileBackedManager(String fileName) {
        return new FileBackedTaskManager(fileName);
    }

    public static HttpTaskManager getHttpTaskManager(String kvServerURL) throws IOException, InterruptedException {
        return new HttpTaskManager(kvServerURL);
    }

}
