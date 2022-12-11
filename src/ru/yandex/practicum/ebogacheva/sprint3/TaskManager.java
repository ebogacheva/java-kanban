package ru.yandex.practicum.ebogacheva.sprint3;

import java.util.List;

public interface TaskManager {

    List<Task> getTasks();
    List<Subtask> getSubtasks();
    List<Epic> getEpics();
    void deleteAllTasks();
    void deleteAllSubtasks();
    void deleteAllEpics();
    Task getTask(int id);
    void deleteTaskById(int id);
    void deleteSubtaskById(int id);
    void deleteEpicById(int id);
    Task createTask(Task task);
    Subtask createSubtask(Subtask subtask);
    Epic createEpic(Epic epic);
    List<Subtask> getEpicSubtasks(Epic epic);
    void updateTask(Task task);
    void updateSubtask(Subtask subtask);
    void updateEpic(Epic epic);
    List<Task> getHistory();
}
