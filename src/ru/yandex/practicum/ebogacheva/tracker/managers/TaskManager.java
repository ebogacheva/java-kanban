package ru.yandex.practicum.ebogacheva.tracker.managers;

import ru.yandex.practicum.ebogacheva.tracker.model.Epic;
import ru.yandex.practicum.ebogacheva.tracker.model.Subtask;
import ru.yandex.practicum.ebogacheva.tracker.model.Task;

import java.util.List;

public interface TaskManager {
    void createTask(Task task);
    void createSubtask(Subtask subtask);
    void createEpic(Epic epic);

    Task getTask(int id);
    Epic getEpic(int id);
    Subtask getSubtask(int id);

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
