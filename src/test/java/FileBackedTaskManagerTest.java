import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.ebogacheva.tracker.Managers;
import ru.yandex.practicum.ebogacheva.tracker.managers.FileBackedTaskManager;
import ru.yandex.practicum.ebogacheva.tracker.managers.TaskManager;
import ru.yandex.practicum.ebogacheva.tracker.model.Epic;
import ru.yandex.practicum.ebogacheva.tracker.model.Status;
import ru.yandex.practicum.ebogacheva.tracker.model.Subtask;
import ru.yandex.practicum.ebogacheva.tracker.model.Task;
import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<TaskManager> {

    @BeforeEach
    public void initiateTaskManager() {
        taskManager = Managers.getFileBackedManager("test1.txt");
    }

    @Test
    void loadFromAndToFile() {
        TestObjectsProvider.addThreeTasksToManager(taskManager);
        TestObjectsProvider.addThreeSubtasksToManager(taskManager);
        taskManager.getEpics();
        File file1 = new File("test1.txt");
        FileBackedTaskManager taskManagerNEW = FileBackedTaskManager.loadFromFile(file1);
        File file2 = new File("test2.txt");
        taskManagerNEW.save(file2);
        List<String> test1Data = TestObjectsProvider.getStringsFromFile(file1);
        List<String> test2Data = TestObjectsProvider.getStringsFromFile(file2);
        assertEquals(test1Data, test2Data);
        Assertions.assertEquals(taskManager.getTasks(), taskManagerNEW.getTasks());
        Assertions.assertEquals(taskManager.getSubtasks(), taskManagerNEW.getSubtasks());
        Assertions.assertEquals(taskManager.getEpics(), taskManagerNEW.getEpics());
        Assertions.assertEquals(taskManager.getPrioritizedTasks(), taskManagerNEW.getPrioritizedTasks());
        Assertions.assertEquals(taskManager.getHistory(), taskManagerNEW.getHistory());
    }

    @Test
    void saveHistoryToString() {
        TestObjectsProvider.addThreeTasksToManager(taskManager);
        TestObjectsProvider.addThreeSubtasksToManager(taskManager);
        List<Epic> epics = taskManager.getEpics();
        String historyExpected = epics.get(0).getId() + "," + epics.get(1).getId();
        String actualHistory = FileBackedTaskManager.historyToString(taskManager.getHistory());
        assertEquals(historyExpected, actualHistory);
    }

    @Test
    void getHistoryFromString() {
        List<Integer> historyExpected1 = List.of(1,2,3,4,5);
        String historyInString1= "1,2,3,4,5";
        List<Integer> historyActual1 = FileBackedTaskManager.historyFromString(historyInString1);
        assertEquals(historyExpected1, historyActual1);

        List<Integer> historyExpected2 = List.of();
        String historyInString2 = System.lineSeparator();
        List<Integer> historyActual2 = FileBackedTaskManager.historyFromString(historyInString2);
        assertEquals(historyExpected2, historyActual2);
    }

    @Test
    void taskToFileString() {
        Task task1 = new Task(1, "title", "description", Status.NEW, 0, null);
        String task1Expected = "1,TASK,title,NEW,description,PT0S,null";
        String task1Actual = task1.toFileString();
        Epic epic1 = new Epic(2, "title", "description", Status.NEW, 0, null, List.of(4));
        String epic1Expected = "2,EPIC,title,NEW,description,PT0S,null,[4]";
        String epic1Actual = epic1.toFileString();
        Subtask subtask1 = new Subtask(3, "title", "description", Status.NEW, 60, null, 2);
        String subtask1Expected = "3,SUBTASK,title,NEW,description,PT1H,null,2";
        String subtask1Actual = subtask1.toFileString();
        assertEquals(subtask1Expected, subtask1Actual);
        assertEquals(epic1Expected, epic1Actual);
        assertEquals(task1Expected, task1Actual);
    }

    @Test
    void getTaskDateTimeFromString() {
        String input = "02.02.2002 10:02";
        LocalDateTime expected = LocalDateTime.of(2002, Month.FEBRUARY, 2, 10, 2);
        LocalDateTime actual = FileBackedTaskManager.getTaskDateTimeFromString(input);
        assertEquals(expected, actual);
    }

    @Test
    void getTasksBackedInFile() {
        List<Task> expected = TestObjectsProvider.addThreeTasksToManager(taskManager);
        taskManager.getTasks();
        List<Task> historyFromManager = taskManager
                .getHistory()
                .stream()
                .sorted(Comparator.comparingInt(Task::getId))
                .collect(Collectors.toList());
        assertEquals(expected, historyFromManager);
        FileBackedTaskManager taskManagerNEW = TestObjectsProvider.getFileBackedManager();
        List<Task> historyFromFile = taskManagerNEW
                .getHistory()
                .stream()
                .sorted(Comparator.comparingInt(Task::getId))
                .collect(Collectors.toList());
        assertEquals(expected, historyFromFile);
    }

    @Test
    void getSubtasksBackedInFile() {
        List<Subtask> expected = TestObjectsProvider.addThreeSubtasksToManager(taskManager);
        taskManager.getSubtasks();
        List<Task> historyFromManager = taskManager.getHistory();
        List<Subtask> actual1 = new ArrayList<>();
        if (!historyFromManager.isEmpty()) {
            for (Task task : historyFromManager) {
                actual1.add((Subtask) task);
            }
        }
        for (Subtask subtask : expected) {
            assertTrue(actual1.contains(subtask));
        }
        FileBackedTaskManager taskManagerNEW = TestObjectsProvider.getFileBackedManager();
        List<Task> historyFromFile = taskManagerNEW.getHistory();
        List<Subtask> actual2 = new ArrayList<>();
        if (!historyFromManager.isEmpty()) {
            for (Task task : historyFromFile) {
                actual2.add((Subtask) task);
            }
        }
        for (Subtask subtask : expected) {
            assertTrue(actual2.contains(subtask));
        }
    }

    @Test
    void getEpicsBackedInFile() {
        List<Epic> expected = TestObjectsProvider.addThreeEpicsToManager(taskManager);
        taskManager.getEpics();
        List<Task> historyFromManager = taskManager.getHistory();
        List<Epic> actual1 = new ArrayList<>();
        if (!historyFromManager.isEmpty()) {
            for (Task task : historyFromManager) {
                actual1.add((Epic) task);
            }
        }
        for (Epic epic : expected) {
            assertTrue(actual1.contains(epic));
        }
        FileBackedTaskManager taskManagerNEW = TestObjectsProvider.getFileBackedManager();
        List<Task> historyFromFile = taskManagerNEW.getHistory();
        List<Epic> actual2 = new ArrayList<>();
        if (!historyFromManager.isEmpty()) {
            for (Task task : historyFromFile) {
                actual2.add((Epic) task);
            }
        }
        for (Epic epic : expected) {
            assertTrue(actual2.contains(epic));
        }
    }

    @Test
    void deleteAllTasksBackedInFile() {
        List<Task> expected = TestObjectsProvider.addThreeTasksToManager(taskManager);
        taskManager.getTasks();
        List<Task> historyFromManger = taskManager.getHistory();
        for (Task task : expected) {
            assertTrue(historyFromManger.contains(task));
        }
        taskManager.deleteAllTasks();
        FileBackedTaskManager taskManagerNEW = TestObjectsProvider.getFileBackedManager();
        List<Task> historyFromFile = taskManagerNEW.getHistory();
        assertEquals(0, historyFromFile.size());
        assertEquals(0, taskManagerNEW.getTasks().size());
    }

    @Test
    void deleteAllSubtasksBackedInFile() {
        List<Subtask> expected = TestObjectsProvider.addThreeSubtasksToManager(taskManager);
        taskManager.getSubtasks();
        List<Task> historyFromManger = taskManager.getHistory();
        assertEquals(expected.size(), historyFromManger.size());
        taskManager.deleteAllSubtasks();
        FileBackedTaskManager taskManagerNEW = TestObjectsProvider.getFileBackedManager();
        List<Task> historyFromFile = taskManagerNEW.getHistory();
        assertEquals(0, historyFromFile.size());
        assertEquals(0, taskManagerNEW.getSubtasks().size());
    }

    @Test
    void deleteAllEpicsBackedInFile() {
        List<Subtask> expected = TestObjectsProvider.addThreeSubtasksToManager(taskManager);
        taskManager.getSubtask(expected.get(0).getId());
        taskManager.deleteAllEpics();
        FileBackedTaskManager taskManagerNEW = TestObjectsProvider.getFileBackedManager();
        List<Task> historyFromFile = taskManagerNEW.getHistory();
        assertEquals(0, historyFromFile.size());

        List<Epic> epics = taskManagerNEW.getEpics();
        assertEquals(0, epics.size());
    }

    @Test
    void getTaskBackedInFile() {
        List<Task> tasks = TestObjectsProvider.addThreeTasksToManager(taskManager);
        Task task = taskManager.getTask(tasks.get(0).getId());
        FileBackedTaskManager taskManagerNEW = TestObjectsProvider.getFileBackedManager();
        List<Task> historyFromFile = taskManagerNEW.getHistory();
        assertEquals(task.getId(), historyFromFile.get(0).getId());

        Task task1 = taskManager.getTask(1111);
        assertNull(task1);
    }

    @Test
    void getEpicBackedInFile() {
        List<Epic> epics = TestObjectsProvider.addThreeEpicsToManager(taskManager);
        Epic epic = taskManager.getEpic(epics.get(0).getId());
        FileBackedTaskManager taskManagerNEW = TestObjectsProvider.getFileBackedManager();
        List<Task> historyFromFile = taskManagerNEW.getHistory();
        assertEquals(epic.getId(), historyFromFile.get(0).getId());

        Epic epic1 = taskManager.getEpic(1111);
        assertNull(epic1);
    }

    @Test
    void getSubtaskBackedInFile() {
        List<Subtask> subtasks = TestObjectsProvider.addThreeSubtasksToManager(taskManager);
        Subtask subtask = taskManager.getSubtask(subtasks.get(0).getId());
        FileBackedTaskManager taskManagerNEW = TestObjectsProvider.getFileBackedManager();
        List<Task> historyFromFile = taskManagerNEW.getHistory();
        assertEquals(subtask.getId(), historyFromFile.get(0).getId());

        Subtask subtask1 = taskManager.getSubtask(1111);
        assertNull(subtask1);
    }

    @Test
    void deleteTaskByIdBackedInFile() {
        List<Task> tasks = TestObjectsProvider.addThreeTasksToManager(taskManager);
        int deletedTaskId = tasks.get(0).getId();
        taskManager.deleteTaskById(deletedTaskId);
        FileBackedTaskManager taskManagerNEW = TestObjectsProvider.getFileBackedManager();
        Task taskNew = taskManagerNEW.getTask(deletedTaskId);
        assertNull(taskNew);

        taskManager.deleteTaskById(1111);
        Assertions.assertEquals(2, taskManager.getTasks().size());
        taskManager.deleteTaskById(-10);
        Assertions.assertEquals(2, taskManager.getTasks().size());
    }

    @Test
    void deleteSubtaskById() {
        List<Subtask> subtasks = TestObjectsProvider.addThreeSubtasksToManager(taskManager);
        int deletedSubtaskId = subtasks.get(0).getId();
        taskManager.deleteSubtaskById(deletedSubtaskId);
        FileBackedTaskManager taskManagerNEW = TestObjectsProvider.getFileBackedManager();
        Subtask subtaskNew = taskManagerNEW.getSubtask(deletedSubtaskId);
        assertNull(subtaskNew);

        taskManager.deleteSubtaskById(1111);
        Assertions.assertEquals(2, taskManager.getSubtasks().size());
        taskManager.deleteSubtaskById(-10);
        Assertions.assertEquals(2, taskManager.getSubtasks().size());
    }

    @Test
    void deleteEpicByIdBackedInFile() {
         TestObjectsProvider.addThreeSubtasksToManager(taskManager);
        List<Epic> epics = taskManager.getEpics();
        Epic epicToDelete = epics.get(0);
        List<Integer> subtasksToDelete = epicToDelete.getSubIds();
        int deletedEpicId = epicToDelete.getId();
        taskManager.deleteEpicById(deletedEpicId);
        FileBackedTaskManager taskManagerNEW = TestObjectsProvider.getFileBackedManager();
        Epic epicNew = taskManagerNEW.getEpic(deletedEpicId);
        assertNull(epicNew);
        assertNull(taskManagerNEW.getSubtask(subtasksToDelete.get(0)));

        int epicsCount = taskManagerNEW.getEpics().size();
        taskManagerNEW.deleteEpicById(10000);
        int epicsCountNew = taskManagerNEW.getEpics().size();
        assertEquals(epicsCount, epicsCountNew);
    }

    @Test
    void createTaskBackedInFile() {
        Task task = TestObjectsProvider.addTask(1, taskManager);
        FileBackedTaskManager taskManagerNEW = TestObjectsProvider.getFileBackedManager();
        Task taskNew = taskManagerNEW.getTask(task.getId());
        assertEquals(task, taskNew);

    }

    @Test
    void createSubtaskBackedInFile() {
        Epic epic = TestObjectsProvider.addEpic(1, taskManager);
        Subtask subtask = TestObjectsProvider.addSubtask(1, epic.getId(), taskManager);
        FileBackedTaskManager taskManagerNEW = TestObjectsProvider.getFileBackedManager();
        Subtask subtaskNew = taskManagerNEW.getSubtask(subtask.getId());
        assertEquals(subtask, subtaskNew);
    }

    @Test
    void createEpicBackedInFile() {
        Epic epic = TestObjectsProvider.addEpic(1, taskManager);
        FileBackedTaskManager taskManagerNEW = TestObjectsProvider.getFileBackedManager();
        Epic epicNew = taskManagerNEW.getEpic(epic.getId());
        assertEquals(epic, epicNew);
    }

    @Test
    void getEpicSubtasksBackedInFile() {
        TestObjectsProvider.addThreeSubtasksToManager(taskManager);
        Epic epic = taskManager.getEpics().get(0);
        int size = epic.getSubIds().size();
        FileBackedTaskManager taskManagerNEW = TestObjectsProvider.getFileBackedManager();
        List<Subtask> epicSubs = taskManagerNEW.getEpicSubtasks(epic);
        assertEquals(size, epicSubs.size());
    }

    @Test
    void updateTaskBackedInFile() {
        Task task = TestObjectsProvider.addTask(1, taskManager);
        assertNull(task.getStartDateTime());
        task.setStartDateTime(TestObjectsProvider.getDateTimeForTesting());
        taskManager.updateTask(task);
        FileBackedTaskManager taskManagerNEW = TestObjectsProvider.getFileBackedManager();
        Task taskNew = taskManagerNEW.getTask(task.getId());
        assertNotEquals(task.getStartDateTime(), taskNew.getStartDateTime());
    }

    @Test
    void updateSubtaskBackedInFile() {
        Epic epic = TestObjectsProvider.addEpic(1, taskManager);
        Subtask subtask = TestObjectsProvider.addSubtask(1, epic.getId(), taskManager);
        Duration durationOld = subtask.getDuration();
        subtask.setDuration(Duration.ofMinutes(1000));
        taskManager.updateSubtask(subtask);
        Duration durationNew = taskManager.getSubtask(subtask.getId()).getDuration();
        assertNotEquals(durationOld, durationNew);
        Assertions.assertEquals(taskManager.getEpic(epic.getId()).getDuration(), durationNew);
    }

    @Test
    void updateEpicStatusBackedInFile() {
        Epic epic1 = TestObjectsProvider.addEpic(1, taskManager);
        Subtask subtask1 = TestObjectsProvider.addSubtask(1, epic1.getId(), taskManager);
        Subtask subtask2 = TestObjectsProvider.addSubtask(2, epic1.getId(), taskManager);
        assertSame(epic1.getStatus(), Status.NEW);
        subtask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);
        epic1 = taskManager.getEpic(epic1.getId());
        assertSame(epic1.getStatus(), Status.IN_PROGRESS);
        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask2);
        FileBackedTaskManager taskManagerNEW = TestObjectsProvider.getFileBackedManager();
        Epic epic1New = taskManagerNEW.getEpic(epic1.getId());
        assertSame(epic1New.getStatus(), Status.DONE);

        taskManager.deleteAllEpics();
        Epic epic2 = TestObjectsProvider.addEpic(2, taskManager);
        assertSame(epic2.getStatus(), Status.NEW);
        epic2.setStatus(Status.DONE);
        taskManager.updateEpic(epic2);
        FileBackedTaskManager taskManagerNEW2 = TestObjectsProvider.getFileBackedManager();
        Epic epic2New = taskManagerNEW2.getEpic(epic2.getId());
        assertSame(epic2New.getStatus(), Status.NEW);
    }

    @Test
    void getHistoryBackedInFile() {
        List<Task> tasks = TestObjectsProvider.addThreeTasksToManager(taskManager);
        int id = tasks.get(0).getId();
        taskManager.getTask(id);
        FileBackedTaskManager taskManagerNEW = TestObjectsProvider.getFileBackedManager();
        List<Task> history = taskManagerNEW.getHistory();
        assertEquals(id, history.get(0).getId());
        taskManager.deleteTaskById(id);
        FileBackedTaskManager taskManagerNEW2 = TestObjectsProvider.getFileBackedManager();
        List<Task> history2 = taskManagerNEW2.getHistory();
        assertTrue(history2.isEmpty());
    }
}