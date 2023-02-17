import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.ebogacheva.tracker.Managers;
import ru.yandex.practicum.ebogacheva.tracker.managers.HttpTaskManager;
import ru.yandex.practicum.ebogacheva.tracker.model.Epic;
import ru.yandex.practicum.ebogacheva.tracker.model.Subtask;
import ru.yandex.practicum.ebogacheva.tracker.model.Task;
import ru.yandex.practicum.ebogacheva.tracker.server.KVServer;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {

    private KVServer kvServer;
    private final String KVServer_URL = "http://localhost:8078";

    @BeforeEach
    void initiateTaskManager() {
        try {
            kvServer = new KVServer();
            kvServer.start();
            taskManager = Managers.getHttpTaskManager(KVServer_URL);
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    @AfterEach
    void stopServer() {
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        taskManager.deleteAllSubtasks();
        kvServer.stop();
    }

    @Test
    void loadTasksTest() {
        Task task1 = TestObjectsProvider.addTaskShifted(1, 140, this.taskManager);
        Task task2 = TestObjectsProvider.addTaskShifted(2, 1800, this.taskManager);
        List<Task> tasksFromManager = taskManager.getTasks();
        List<Task> tasksCreated = List.of(task1, task2);
        assertEquals(tasksCreated.size(), tasksFromManager.size());

        Task task2FromManager = taskManager.getTask(task2.getId());
        Task task1FromManager = taskManager.getTask(task1.getId());
        List<Task> historyExpected = List.of(task2FromManager, task1FromManager);
        List<Task> historyActual = taskManager.getHistory();
        assertEquals(task1, task1FromManager);
        assertEquals(task2, task2FromManager);
        assertEquals(historyExpected, historyActual);

        taskManager.loadFromKey();
        Task task1FromServer = taskManager.getTask(task1.getId());
        assertEquals(task1, task1FromServer);
    }

    @Test
    void loadTEpicsTest() {
        Epic epic1 = TestObjectsProvider.addEpic(1, this.taskManager);
        Epic epic2 = TestObjectsProvider.addEpic(2, this.taskManager);
        List<Epic> epicsFromManager = taskManager.getEpics();
        List<Epic> epicsCreated = List.of(epic1, epic2);
        assertEquals(epicsCreated.size(), epicsFromManager.size());

        Epic epic2FromManager = taskManager.getEpic(epic2.getId());
        Epic epic1FromManager = taskManager.getEpic(epic1.getId());
        List<Task> historyExpected = List.of(epic2FromManager, epic1FromManager);
        List<Task> historyActual = taskManager.getHistory();
        assertEquals(epic1, epic1FromManager);
        assertEquals(epic2, epic2FromManager);
        assertEquals(historyExpected, historyActual);

        taskManager.loadFromKey();
        Task task1FromServer = taskManager.getEpic(epic1.getId());
        assertEquals(epic1, task1FromServer);
    }

    @Test
    void loadSubtasksTest() {
        Epic epic1 = TestObjectsProvider.addEpic(1, this.taskManager);
        Epic epic2 = TestObjectsProvider.addEpic(2, this.taskManager);
        Subtask subtask1 = TestObjectsProvider.addSubtaskShifted(1, epic1.getId(), 45, this.taskManager);
        Subtask subtask2 = TestObjectsProvider.addSubtaskShifted(2, epic1.getId(), 500, this.taskManager);

        Subtask subtask2FromManager = taskManager.getSubtask(subtask2.getId());
        Subtask subtask1FromManager = taskManager.getSubtask(subtask1.getId());
        Epic epic2FromManager = taskManager.getEpic(epic2.getId());
        List<Task> historyExpected = List.of(subtask2FromManager, subtask1FromManager, epic2FromManager);
        List<Task> historyActual = taskManager.getHistory();
        assertEquals(subtask1, subtask1FromManager);
        assertEquals(subtask2, subtask2FromManager);
        assertEquals(historyExpected, historyActual);

        taskManager.loadFromKey();
        Subtask subtask1FromServer = taskManager.getSubtask(subtask1.getId());
        assertEquals(subtask1, subtask1FromServer);
    }
}
