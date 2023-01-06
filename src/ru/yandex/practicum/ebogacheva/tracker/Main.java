package ru.yandex.practicum.ebogacheva.tracker;

import ru.yandex.practicum.ebogacheva.tracker.history.InMemoryHistoryManager;
import ru.yandex.practicum.ebogacheva.tracker.task_managers.TaskManager;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();

        // Тестирование Трекера задач (Спринт 3,4)
        // InMemoryTaskManagerTests.testInMemoryTaskManager(taskManager);

        // Тестирование CustomLinkedList и InMemoryHistoryManager
        InMemoryHistoryManagerTests.testInMemoryHistoryManager(taskManager);
    }


}
