package ru.yandex.practicum.ebogacheva.sprint3;

import java.util.List;

public interface TaskManager {
    Task createTask(Task task);
    Subtask createSubtask(Subtask subtask);
    Epic createEpic(Epic epic);

    Task getTask(int id);

    List<Subtask> getEpicSubtasks(Epic epic);

    List<Task> getTasks();
    List<Subtask> getSubtasks();
    List<Epic> getEpics();

    void updateTask(Task task);
    void updateSubtask(Subtask subtask);
    void updateEpic(Epic epic);

    void deleteTaskById(int id);
    void deleteSubtaskById(int id);
    void deleteEpicById(int id);

    void deleteAllTasks();
    void deleteAllSubtasks();
    void deleteAllEpics();

    List<Task> getHistory();
}
