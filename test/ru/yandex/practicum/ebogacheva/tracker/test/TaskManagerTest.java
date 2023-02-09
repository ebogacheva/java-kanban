package ru.yandex.practicum.ebogacheva.tracker.test;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.ebogacheva.tracker.managers.TaskManager;
import ru.yandex.practicum.ebogacheva.tracker.model.Epic;
import ru.yandex.practicum.ebogacheva.tracker.model.Status;
import ru.yandex.practicum.ebogacheva.tracker.model.Subtask;
import ru.yandex.practicum.ebogacheva.tracker.model.Task;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<TaskManagerType extends TaskManager> {

    TaskManagerType taskManager;

    @Test
    void createTaskRegularCase() {
        final Task task = TestObjectsProvider.addTask(1, taskManager);
        final int id = task.getId();
        final Task actualTask = taskManager.getTask(id);
        assertNotNull(actualTask, "Задача не найдена.");
        assertEquals(task, actualTask, "Задачи не совпадают.");
    }

    @Test
    void createTaskNullObject() {
        assertThrows(NullPointerException.class, () -> taskManager.createTask(null));
    }

    @Test
    void createTaskWithExistingId(){
        final Task existingTask = TestObjectsProvider.addTask(1, taskManager);
        final int existingTaskId = existingTask.getId();
        final Task newTask = new Task(existingTaskId,
                "Новая задача с уже существующим id",
                "Описание задачи",
                Status.NEW,
                60,
                TestObjectsProvider.getDateTimeForTesting());
        taskManager.createTask(newTask);
        final int newTaskId = newTask.getId();
        assertNotEquals(existingTaskId, newTaskId);
        assertEquals(2, taskManager.getTasks().size());
    }

    @Test
    void createSubtaskRegularCase() {
        final Epic epic = TestObjectsProvider.addEpic(1, taskManager);
        final int epicId = epic.getId();
        final Subtask subtask = TestObjectsProvider.addSubtask(1, epicId, taskManager);
        final int subId = subtask.getId();
        final Subtask actualSubtask = taskManager.getSubtask(subId);
        assertNotNull(actualSubtask, "Подзадача не найдена.");
        assertEquals(subtask, actualSubtask, "Подзадачи не совпадают.");
    }

    @Test
    void createSubtaskNullObject() {
        assertThrows(NullPointerException.class, () -> taskManager.createSubtask(null));
    }

    @Test
    void createSubtaskWithInvalidEpicId() {
        final Subtask subtask = TestObjectsProvider.getSubtaskForTesting(1, 11111);
        assertThrows(NullPointerException.class, () -> taskManager.createSubtask(subtask));
    }

    @Test
    void createSubtaskWithExistingId() {
        final Epic epic1 = TestObjectsProvider.addEpic(1, taskManager);
        final Subtask existingSubtask = TestObjectsProvider.addSubtask(1, epic1.getId(), taskManager);
        final int existingSubtaskId = existingSubtask.getId();
        final Subtask newSubtask = new Subtask(existingSubtaskId,
                "Новая подзадача с уже существующим id",
                "Описание подзадачи",
                Status.NEW,
                60,
                TestObjectsProvider.getDateTimeForTesting(),
                epic1.getId());
        taskManager.createSubtask(newSubtask);
        final int newSubtaskId = newSubtask.getId();
        assertNotEquals(existingSubtaskId, newSubtaskId);
        assertEquals(2, taskManager.getSubtasks().size());
        assertEquals(2, taskManager.getEpic(epic1.getId()).getSubIds().size());
        assertTrue(taskManager.getEpic(epic1.getId()).getSubIds().contains(newSubtaskId));
    }

    @Test
    void createEpicRegularCase() {
        final Epic epic = TestObjectsProvider.addEpic(1, taskManager);
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
    void createEpicNullObject() {
        assertThrows(NullPointerException.class, () -> taskManager.createEpic(null));
    }

    @Test
    void createEpicWithExistingId() {
        final Epic existingEpic = TestObjectsProvider.addEpic(1, taskManager);
        int existingEpicId = existingEpic.getId();
        final Epic newEpic = new Epic(existingEpicId,
                "Новый эпик с уже существующим id",
                "Описание эпика.",
                Status.NEW,
                60,
                TestObjectsProvider.getDateTimeForTesting(),
                List.of());
        taskManager.createEpic(newEpic);
        final int newEpicId =newEpic.getId();
        assertNotEquals(existingEpicId, newEpicId);
        assertEquals(2, taskManager.getEpics().size());
        assertTrue(taskManager.getEpics().contains(newEpic));
    }

    @Test
    void getTaskRegularCase() {
        final Task task1 = TestObjectsProvider.getTaskForTesting(1);
        task1.setDuration(Duration.ofMinutes(50));
        taskManager.createTask(task1);
        int id1 = task1.getId();
        final Task actualTask1 = taskManager.getTask(id1);

        assertNotNull(actualTask1, "Задача не найдена.");
        assertEquals(task1, actualTask1, "Задачи не совпадают.");
    }

    @Test
    void getTaskNotExistingId() {
        final Task task1 = TestObjectsProvider.addTask(1, taskManager);
        assertNull(taskManager.getTask(0), "Найдена несуществующая задача.");
        assertNull(taskManager.getTask(99999), "Найдена несуществующая задача.");
        assertEquals(task1, taskManager.getTask(task1.getId()));
    }

    @Test
    void getTaskNotSameObjects() {
        final Task task1 = TestObjectsProvider.getTaskForTesting(1);
        task1.setDuration(Duration.ofMinutes(50));
        taskManager.createTask(task1);
        int id1 = task1.getId();
        final Task actualTask1 = taskManager.getTask(id1);

        assertNotSame(task1, actualTask1);
    }

    @Test
    void getEpicRegularCase() {
        final Epic epic1 = TestObjectsProvider.getEpicForTesting(1);
        taskManager.createEpic(epic1);
        int id1 = epic1.getId();
        final Epic actualEpic1 = taskManager.getEpic(id1);

        assertNotNull(actualEpic1, "Эпик не найден.");
        assertEquals(epic1, actualEpic1, "Эпики не совпадают.");
    }

    @Test
    void getEpicNotExistingId() {
        int idNotExists = 999;
        final Epic actualEpic1 = taskManager.getEpic(idNotExists);
        assertNull(actualEpic1, "Найден несуществующий эпик");
    }

    @Test
    void getEpicNotSameObjects() {
        final Epic epic1 = TestObjectsProvider.getEpicForTesting(1);
        taskManager.createEpic(epic1);
        int id1 = epic1.getId();
        final Epic actualEpic1 = taskManager.getEpic(id1);

        assertNotSame(epic1, actualEpic1);
    }

    @Test
    void getSubtaskRegularCase() {
        final Epic epic1 = TestObjectsProvider.getEpicForTesting(1);
        taskManager.createEpic(epic1);
        final Subtask subtask1 = TestObjectsProvider.getSubtaskForTesting(1, epic1.getId());
        taskManager.createSubtask(subtask1);
        int id1 = subtask1.getId();
        final Subtask actualSubtask1 = taskManager.getSubtask(id1);

        assertNotNull(actualSubtask1, "Подзадача не найдена.");
        assertEquals(subtask1, actualSubtask1, "Подзадачи не совпадают.");
    }

    @Test
    void getSubtaskNotSameObjects() {
        final Epic epic1 = TestObjectsProvider.getEpicForTesting(1);
        taskManager.createEpic(epic1);
        final Subtask subtask1 = TestObjectsProvider.getSubtaskForTesting(1, epic1.getId());
        taskManager.createSubtask(subtask1);
        int id1 = subtask1.getId();
        final Subtask actualSubtask1 = taskManager.getSubtask(id1);

        assertNotSame(subtask1 , actualSubtask1);
    }

    @Test
    void getSubtaskNotExistingId() {
        int idNotExists = 999;
        final Subtask actualSubtask = taskManager.getSubtask(idNotExists);
        assertNull(actualSubtask, "Найдена несуществующая подзадача.");
    }

    @Test
    void getEpicSubtasks() {
        final Epic epic1 = TestObjectsProvider.addEpic(1, taskManager);
        final Epic epic2 = TestObjectsProvider.addEpic(2, taskManager);
        final Subtask subtask1 = TestObjectsProvider.addSubtask(1, epic1.getId(), taskManager);
        final Subtask subtask2 = TestObjectsProvider.addSubtask(2, epic1.getId(), taskManager);
        final Subtask subtask3 = TestObjectsProvider.addSubtask(3, epic2.getId(), taskManager);
        final List<Subtask> expected = List.of(subtask1, subtask2);
        final Epic actualEpic1 = taskManager.getEpic(epic1.getId());
        final List<Subtask> actual = taskManager.getEpicSubtasks(actualEpic1);
        assertEquals(expected, actual);
        assertNotSame(subtask1, actual.get(0));
    }

    @Test
    void getPrioritizedTasksGeneral() {
        final Task task1 = TestObjectsProvider.addTaskShifted(1, 62, taskManager);
        final Task task2 = TestObjectsProvider.addTaskShifted(2, 15, taskManager);
        final Epic epic1 = TestObjectsProvider.addEpic(1, taskManager);
        final Subtask subtask1 = TestObjectsProvider.addSubtaskShifted(1, epic1.getId(), 124, taskManager);
        final Subtask subtask2 = TestObjectsProvider.addSubtask(2, epic1.getId(), taskManager);
        final Task task3 = TestObjectsProvider.addTask(3, taskManager);

        final List<Task> expected = List.of(task1, subtask1, task2, subtask2, task3);
        final List<Task> actual = taskManager.getPrioritizedTasks();
        assertEquals(expected, actual);
    }

    @Test
    void getPrioritizedTasksAllDatesNull() {
        final List<Task> expected = TestObjectsProvider.addThreeTasksToManager(taskManager);
        final List<Task> actual = taskManager.getPrioritizedTasks();
        assertEquals(expected, actual);
    }

    @Test
    void getTasks() {
        final List<Task> expected = TestObjectsProvider.addThreeTasksToManager(taskManager);
        final List<Task> actual = taskManager.getTasks();
        for (Task task : expected) {
            assertTrue(actual.contains(task));
        }
        assertEquals(actual, taskManager.getHistory());
        taskManager.deleteAllTasks();
        assertEquals(0, taskManager.getTasks().size());
    }

    @Test
    void getSubtasks() {
        final List<Subtask> expected = TestObjectsProvider.addThreeSubtasksToManager(taskManager);
        final List<Subtask> actual = taskManager.getSubtasks();
        for (Subtask subtask : expected) {
            assertTrue(actual.contains(subtask));
        }
        assertEquals(actual, taskManager.getHistory());
        taskManager.deleteAllSubtasks();
        assertEquals(0, taskManager.getSubtasks().size());
    }

    @Test
    void getEpics() {
        final List<Epic> expected = TestObjectsProvider.addThreeEpicsToManager(taskManager);
        final Subtask subtask1 = TestObjectsProvider.addSubtask(1, expected.get(0).getId(), taskManager);
        final List<Epic> actual = taskManager.getEpics();
        for (Epic epic : expected) {
            assertTrue(actual.contains(epic));
        }
        assertEquals(actual, taskManager.getHistory());
        taskManager.deleteAllEpics();
        assertEquals(0, taskManager.getEpics().size());
        assertEquals(0, taskManager.getSubtasks().size());
    }

    @Test
    void updateTask() {
        Task task1 = TestObjectsProvider.addTask(1, taskManager);
        Status oldStatus = task1.getStatus();
        task1.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task1);
        Status newStatus = taskManager.getTask(task1.getId()).getStatus();
        assertNotEquals(oldStatus, newStatus);
    }

    @Test
    void updateSubtask() {
        final Epic epic1 = TestObjectsProvider.addEpic(1, taskManager);
        final Subtask subtask1 = TestObjectsProvider.addSubtask(1, epic1.getId(), taskManager);
        final Status oldStatus = subtask1.getStatus();
        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        Status newStatus = taskManager.getSubtask(subtask1.getId()).getStatus();
        assertNotEquals(oldStatus, newStatus);
        assertEquals(taskManager.getEpic(epic1.getId()).getStatus(), Status.DONE);
    }

    // Тут subtask1 используется:
    // Я создаю новый эпик, создаю subtask1 в этом эпике.
    // Потом удаляю все подзадачи у эпика и обновляю эпик в менеджере.
    // После этого проверяю, что эпик в менеджере изменился = считаю подзадачи эпика до и после удаления всех подзадач.
    @Test
    void updateEpic() {
        final Epic epic1 = TestObjectsProvider.addEpic(1, taskManager);
        final Subtask subtask1 = TestObjectsProvider.addSubtask(1, epic1.getId(), taskManager);
        final int countOfSubtasksOld = taskManager.getEpic(epic1.getId()).getSubIds().size();
        epic1.clearSubTasks();
        taskManager.updateEpic(epic1);
        final int countOfSubtasksNew = taskManager.getEpic(epic1.getId()).getSubIds().size();
        assertNotEquals(countOfSubtasksOld, countOfSubtasksNew);
    }

    @Test
    void deleteTaskById() {
        final List<Task> expected = TestObjectsProvider.addThreeTasksToManager(taskManager);
        int deletedTaskId = expected.get(0).getId();
        taskManager.deleteTaskById(deletedTaskId);
        final List<Task> actual = taskManager.getTasks();
        assertEquals(expected.size() - 1, actual.size());
        for (Task task : actual) {
            assertNotEquals(task.getId(), deletedTaskId);
        }
    }

    @Test
    void deleteSubtaskById() {
        final List<Subtask> expected = TestObjectsProvider.addThreeSubtasksToManager(taskManager);
        final int deletedSubtaskId = expected.get(0).getId();
        taskManager.deleteSubtaskById(deletedSubtaskId);
        final List<Subtask> actual = taskManager.getSubtasks();
        assertEquals(expected.size() - 1, actual.size());
        for (Subtask subtask : actual) {
            assertNotEquals(subtask.getId(), deletedSubtaskId);
        }
    }

    @Test
    void deleteEpicById() {
        final List<Epic> expectedEpics = TestObjectsProvider.addThreeEpicsToManager(taskManager);
        final int deletedEpicId = expectedEpics.get(0).getId();
        Subtask subtask1 = TestObjectsProvider.addSubtask(1, deletedEpicId, taskManager);
        final List<Subtask> expectedSubtasks = taskManager.getSubtasks();
        taskManager.deleteEpicById(deletedEpicId);
        final List<Epic> actualEpics = taskManager.getEpics();
        final List<Subtask> actualSubtask = taskManager.getSubtasks();
        assertEquals(expectedEpics.size() - 1, actualEpics.size());
        assertEquals(expectedSubtasks.size() - 1, actualSubtask.size());
        for (Epic epic : actualEpics) {
            assertNotEquals(epic.getId(), deletedEpicId);
        }
    }

    @Test
    void deleteAllTasks() {
        final List<Task> expectedTasks = TestObjectsProvider.addThreeTasksToManager(taskManager);
        taskManager.deleteAllTasks();
        final List<Task> actualTasks = taskManager.getTasks();
        assertEquals(0, actualTasks.size());
        assertNotEquals(expectedTasks.size(), actualTasks.size());
    }

    @Test
    void deleteAllSubtasks() {
        final List<Subtask> expectedSubtasks = TestObjectsProvider.addThreeSubtasksToManager(taskManager);
        taskManager.deleteAllSubtasks();
        final List<Subtask> actualSubtasks = taskManager.getSubtasks();
        assertEquals(0, actualSubtasks.size());
        assertNotEquals(expectedSubtasks.size(), actualSubtasks.size());
    }

    @Test
    void deleteAllEpics() {
        final List<Epic> expectedEpics = TestObjectsProvider.addThreeEpicsToManager(taskManager);
        Subtask subtask1 = TestObjectsProvider.addSubtask(1, expectedEpics.get(0).getId(), taskManager);
        final List<Subtask> expectedSubtasks = taskManager.getSubtasks();
        taskManager.deleteAllEpics();
        final List<Epic> actualEpics = taskManager.getEpics();
        final List<Subtask> actualSubtasks = taskManager.getSubtasks();
        assertEquals(0, actualEpics.size());
        assertEquals(0, actualSubtasks.size());
        assertNotEquals(expectedSubtasks.size(), actualSubtasks.size());
        assertNotEquals(expectedEpics.size(), actualEpics.size());
    }

    @Test
    void getHistory() {
        final Task task1 = TestObjectsProvider.addTask(1, taskManager);
        final Epic epic1 = TestObjectsProvider.addEpic(1, taskManager);
        final Subtask subtask1 = TestObjectsProvider.addSubtask(1, epic1.getId(), taskManager);
        final Subtask subtask2 = TestObjectsProvider.addSubtask(2, epic1.getId(), taskManager);
        final Epic epic2 = TestObjectsProvider.addEpic(2, taskManager);
        final Task task2 = TestObjectsProvider.addTask(2, taskManager);
        taskManager.getEpic(epic1.getId());
        taskManager.getTask(task2.getId());
        taskManager.getSubtask(subtask1.getId());
        taskManager.getSubtask(subtask2.getId());
        taskManager.getTask(task2.getId());
        List<Task> expectedHistory1 = List.of(epic1, subtask1, subtask2, task2);
        List<Task> actualHistory1 = taskManager.getHistory();
        assertEquals(expectedHistory1, actualHistory1);

        taskManager.deleteTaskById(task2.getId());
        List<Task> expectedHistory2 = List.of(epic1, subtask1, subtask2);
        List<Task> actualHistory2 = taskManager.getHistory();
        assertEquals(expectedHistory2, actualHistory2);
    }

    @Test
    void getHistoryEmpty() {
        final Epic epic1 = TestObjectsProvider.addEpic(1, taskManager);
        TestObjectsProvider.addSubtask(1, epic1.getId(), taskManager);
        assertEquals(0, taskManager.getHistory().size());
    }

    @Test
    public void addNewEpic() {
        Epic epic = TestObjectsProvider.addEpic(1, taskManager);
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
        Epic epic = TestObjectsProvider.addEpic(1, taskManager);
        int id = epic.getId();
        Subtask subtask1 = TestObjectsProvider.addSubtask(1, id, taskManager);
        Subtask subtask2 = TestObjectsProvider.addSubtask(2, id, taskManager);
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
        Epic epic = TestObjectsProvider.addEpic(1, taskManager);
        int id = epic.getId();

        Subtask subtask1 = TestObjectsProvider.addSubtask(1, id, taskManager);
        Subtask subtask2 = TestObjectsProvider.addSubtask(2, id, taskManager);
        subtask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);
        Status actualStatus1 = taskManager.getEpic(id).getStatus();
        Status expectedStatus1 = Status.IN_PROGRESS;
        subtask1.setStatus(Status.NEW);
        taskManager.updateSubtask(subtask1);
        Status actualStatus2 = taskManager.getEpic(id).getStatus();
        Status expectedStatus2 = Status.NEW;

        assertEquals(expectedStatus1, actualStatus1,"Статус эпика некорректный.");
        assertEquals(expectedStatus2, actualStatus2, "Статус эпика некорректный.");
    }

    @Test
    public void getEpicStatusWithDoneSubtasksOnly() {
        Epic epic = TestObjectsProvider.addEpic(1, taskManager);
        int id = epic.getId();

        Subtask subtask1 = TestObjectsProvider.addSubtask(1, id, taskManager);
        Subtask subtask2 = TestObjectsProvider.addSubtask(2, id, taskManager);
        Status actualStatus1 = taskManager.getEpic(id).getStatus();
        Status expectedStatus1 = Status.NEW;

        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);
        Status actualStatus2 = taskManager.getEpic(id).getStatus();
        Status expectedStatus2 = Status.DONE;

        assertEquals(expectedStatus1, actualStatus1, "Статус эпика некорректный.");
        assertEquals(expectedStatus2, actualStatus2, "Статус эпика некорректный.");
    }

    @Test
    public void getEpicStatusWithNewAndDoneSubtasks() {
        Epic epic = TestObjectsProvider.addEpic(1, taskManager);
        int id = epic.getId();
        Subtask subtask1 = TestObjectsProvider.addSubtask(1, id, taskManager);
        Subtask subtask2 = TestObjectsProvider.addSubtask(2, id, taskManager);
        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        Status actualStatus1 = taskManager.getEpic(id).getStatus();
        Status expectedStatus1 = Status.IN_PROGRESS;
        assertEquals(expectedStatus1, actualStatus1, "Статус эпика некорректный.");
    }

    @Test
    public void getEpicStatusWithInProgressSubtasks() {
        Epic epic = TestObjectsProvider.addEpic(1, taskManager);
        int id = epic.getId();
        Subtask subtask1 = TestObjectsProvider.addSubtask(1, id, taskManager);
        Subtask subtask2 = TestObjectsProvider.addSubtask(2, id, taskManager);
        subtask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);
        Status actualStatus1 = taskManager.getEpic(id).getStatus();
        Status expectedStatus1 = Status.IN_PROGRESS;
        assertEquals(expectedStatus1, actualStatus1, "Статус эпика некорректный.");
    }
}