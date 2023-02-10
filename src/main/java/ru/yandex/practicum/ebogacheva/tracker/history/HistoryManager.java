package ru.yandex.practicum.ebogacheva.tracker.history;

import ru.yandex.practicum.ebogacheva.tracker.model.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);
    void remove(int id);
    List<Task> getHistory();

}
