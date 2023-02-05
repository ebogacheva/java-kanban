package ru.yandex.practicum.ebogacheva.tracker.model;

public class TestDataProvider {

    public static Task getTaskForTesting(TaskType type, int number) {
        Task task = null;
        switch (type) {
            case TASK:
                task = createTask(number);
                break;
            case EPIC:
                task = createEpic(number);
                break;
        }
        return task;
    }

    public static Task getTaskForTesting(TaskType type, int number, int epicNumber) {
        return new Subtask("Test addNewSubtask #" + number, "Test addNewSubtask #" + number + " description", epicNumber);
    }

    private static Task createEpic(int number) {
        return new Epic("Test addNewEpic #" + number, "Test addNewEpic #" + number + " description");
    }

    private static Task createTask(int number) {
        return new Task("Test addNewTask #" + number, "Test addNewTask #" + number + " description");
    }

}
