package ru.yandex.practicum.ebogacheva.tracker.model;

public class TestDataProvider {

    public static Task getSubtaskForTesting(int number, int epicNumber) {
        return new Subtask("Test addNewSubtask #" + number, "Test addNewSubtask #" + number + " description", 60, epicNumber);
    }

    public static Epic getEpicForTesting(int number) {
        return new Epic("Test addNewEpic #" + number, "Test addNewEpic #" + number + " description");
    }

    public static Task getTaskForTesting(int number) {
        return new Task("Test addNewTask #" + number, "Test addNewTask #" + number + " description", 60);
    }

}
