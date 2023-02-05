package ru.yandex.practicum.ebogacheva.tracker.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.ebogacheva.tracker.Managers;
import ru.yandex.practicum.ebogacheva.tracker.managers.TaskManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    private TaskManager taskManager;

    @BeforeEach
    public void initiateTaskManager() {
        this.taskManager = Managers.getFileBackedManager("test1.txt");
    }

    @Test
    public void addNewEpic() {
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
    public void getEpicStatusWithEmptySubtaskList() {
        Epic epic = addEpic(1);
        int id = epic.getId();
        Subtask subtask1 = addSubtask(1, id);
        Subtask subtask2 = addSubtask(2, id);
        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask2);

        Status actualStatus1 = taskManager.getEpic(id).getStatus();
        Status expectedStatus1 = Status.IN_PROGRESS;
        taskManager.deleteAllSubtasks();
        Status actualStatus2 = taskManager.getEpic(id).getStatus();
        Status expectedStatus2 = Status.NEW;

        assertEquals(expectedStatus1, actualStatus1, "Статус эпика некорректный.");
        assertEquals(expectedStatus2, actualStatus2, "Статус эпика некорректный.");
    }

    @Test
    public void getEpicStatusWithNewSubtasksOnly() {
        Epic epic = addEpic(1);
        int id = epic.getId();

        Subtask subtask1 = addSubtask(1, id);
        Subtask subtask2 = addSubtask(2, id);
        subtask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);
        Status actualStatus1 = taskManager.getEpic(id).getStatus();
        Status expectedStatus1 = Status.IN_PROGRESS;
        subtask1.setStatus(Status.NEW);
        taskManager.updateSubtask(subtask1);
        Status actualStatus2 = taskManager.getEpic(id).getStatus();
        Status expectedStatus2 = Status.NEW;

        assertEquals(actualStatus1, expectedStatus1, "Статус эпика некорректный.");
        assertEquals(actualStatus2, expectedStatus2, "Статус эпика некорректный.");

    }

    @Test
    public void getEpicStatusWithDoneSubtasksOnly() {
        Epic epic = addEpic(1);
        int id = epic.getId();

        Subtask subtask1 = addSubtask(1, id);
        Subtask subtask2 = addSubtask(2, id);
        Status actualStatus1 = taskManager.getEpic(id).getStatus();
        Status expectedStatus1 = Status.NEW;

        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);
        Status actualStatus2 = taskManager.getEpic(id).getStatus();
        Status expectedStatus2 = Status.DONE;

        assertEquals(actualStatus1, expectedStatus1, "Статус эпика некорректный.");
        assertEquals(actualStatus2, expectedStatus2, "Статус эпика некорректный.");
    }

    @Test
    public void getEpicStatusWithNewAndDoneSubtasks() {
        Epic epic = addEpic(1);
        int id = epic.getId();
        Subtask subtask1 = addSubtask(1, id);
        Subtask subtask2 = addSubtask(2, id);
        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        Status actualStatus1 = taskManager.getEpic(id).getStatus();
        Status expectedStatus1 = Status.IN_PROGRESS;
        assertEquals(actualStatus1, expectedStatus1, "Статус эпика некорректный.");
    }

    @Test
    public void getEpicStatusWithInProgressSubtasks() {
        Epic epic = addEpic(1);
        int id = epic.getId();
        Subtask subtask1 = addSubtask(1, id);
        Subtask subtask2 = addSubtask(2, id);
        subtask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);
        Status actualStatus1 = taskManager.getEpic(id).getStatus();
        Status expectedStatus1 = Status.IN_PROGRESS;
        assertEquals(actualStatus1, expectedStatus1, "Статус эпика некорректный.");
    }


    private Epic addEpic(int number) {
        Epic epic = TestDataProvider.getEpicForTesting(number);
        taskManager.createEpic(epic);
        return epic;
    }

    private Subtask addSubtask(int number, int id) {
        Subtask subtask = (Subtask) TestDataProvider.getSubtaskForTesting(number, id);
        taskManager.createSubtask(subtask);
        return subtask;
    }


}