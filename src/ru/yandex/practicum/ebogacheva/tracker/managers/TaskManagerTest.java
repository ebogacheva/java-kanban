package ru.yandex.practicum.ebogacheva.tracker.managers;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.ebogacheva.tracker.model.Epic;
import ru.yandex.practicum.ebogacheva.tracker.model.Subtask;
import ru.yandex.practicum.ebogacheva.tracker.model.Task;
import ru.yandex.practicum.ebogacheva.tracker.model.TestDataProvider;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    T taskManager;

    @Test
    void createTask() {
        Task task = addTask(1);
        int id = task.getId();
        final Task actualTask = taskManager.getTask(id);
        assertNotNull(actualTask, "Задача не найдена.");
        assertEquals(task, actualTask, "Задачи не совпадают.");
    }

    @Test
    void createSubtask() {
        Epic epic = addEpic(1);
        int epicId = epic.getId();
        Subtask subtask = addSubtask(1, epicId);
        int subId = subtask.getId();
        final Subtask actualSubtask = taskManager.getSubtask(subId);
        assertNotNull(actualSubtask, "Подзадача не найдена.");
        assertEquals(subtask, actualSubtask, "Подзадачи не совпадают.");
    }

    @Test
    void createEpic() {
        Epic epic = addEpic(1);
        int id = epic.getId();
        final Epic actualEpic = taskManager.getEpic(id);
        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(actualEpic, "Эпик не найден.");
        assertEquals(epic, actualEpic, "Эпики не совпадают.");
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    void getTask() {
    }

    @Test
    void getEpic() {
    }

    @Test
    void getSubtask() {
    }

    @Test
    void getEpicSubtasks() {
    }

    @Test
    void getTasks() {
    }

    @Test
    void getSubtasks() {
    }

    @Test
    void getEpics() {
    }

    @Test
    void updateTask() {
    }

    @Test
    void updateSubtask() {
    }

    @Test
    void updateEpic() {
    }

    @Test
    void deleteTaskById() {
    }

    @Test
    void deleteSubtaskById() {
    }

    @Test
    void deleteEpicById() {
    }

    @Test
    void deleteAllTasks() {
    }

    @Test
    void deleteAllSubtasks() {
    }

    @Test
    void deleteAllEpics() {
    }

    @Test
    void getHistory() {
    }

    private Epic addEpic(int number) {
        Epic epic = (Epic) TestDataProvider.getEpicForTesting(number);
        taskManager.createEpic(epic);
        return epic;
    }

    private Subtask addSubtask(int number, int id) {
        Subtask subtask = (Subtask) TestDataProvider.getSubtaskForTesting(number, id);
        taskManager.createSubtask(subtask);
        return subtask;
    }

    private Task addTask(int number) {
        Task task = TestDataProvider.getTaskForTesting(number);
        taskManager.createTask(task);
        return task;
    }
}