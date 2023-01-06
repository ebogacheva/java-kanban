package ru.yandex.practicum.ebogacheva.tracker;

import ru.yandex.practicum.ebogacheva.tracker.task_managers.TaskManager;
import ru.yandex.practicum.ebogacheva.tracker.tasks.Epic;
import ru.yandex.practicum.ebogacheva.tracker.tasks.Subtask;
import ru.yandex.practicum.ebogacheva.tracker.tasks.Task;

import java.util.List;

public class TestUtils {

    static void printAllTasks(TaskManager manager) {
        printTasks(manager.getTasks(), manager.getSubtasks(), manager.getEpics());
    }

    static void printTitle(String title) {
        System.out.println("-----: " + title);
    }

    private static void printTasks(List<Task> tasks, List<Subtask> subtasks, List<Epic> epics) {
        printTitle("Список всех задач");
        tasks.forEach(System.out::println);
        subtasks.forEach(System.out::println);
        epics.forEach(System.out::println);
    }

    static void printManagerHistory(TaskManager manager) {
        List<Task> history = manager.getHistory();
        printTitle("История просмотра задач");
        history.forEach(System.out::println);
    }

}
