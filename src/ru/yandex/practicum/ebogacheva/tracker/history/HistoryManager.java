package ru.yandex.practicum.ebogacheva.tracker.history;

import ru.yandex.practicum.ebogacheva.tracker.tasks.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);
    void remove(int id);
    List<Task> getHistory();
    
}
