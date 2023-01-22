package ru.yandex.practicum.ebogacheva.tracker.tests;

import ru.yandex.practicum.ebogacheva.tracker.managers.TaskManager;
import ru.yandex.practicum.ebogacheva.tracker.model.Epic;
import ru.yandex.practicum.ebogacheva.tracker.model.Subtask;
import ru.yandex.practicum.ebogacheva.tracker.model.Task;

import java.util.List;

public class TestUtils {

    static void printAllTasks(TaskManager manager) {
        printTasks(manager.getTasks(), manager.getSubtasks(), manager.getEpics());
    }

    static void printTitle(String title) {
        System.out.println("-----: " + title);
    }

    static void printLine(String line) {
        System.out.println("- " +line);
    }

    private static void printTasks(List<Task> tasks, List<Subtask> subtasks, List<Epic> epics) {
        printTitle("Список всех задач");
        tasks.forEach(System.out::println);
        subtasks.forEach(System.out::println);
        epics.forEach(System.out::println);
    }

    static void printManagerHistory(TaskManager manager) {
        List<Task> history = manager.getHistory();
        System.out.println("История просмотра задач");
        history.forEach(System.out::println);
    }

}
