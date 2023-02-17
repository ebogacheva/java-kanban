import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.ebogacheva.tracker.Managers;
import ru.yandex.practicum.ebogacheva.tracker.managers.HttpTaskManager;
import ru.yandex.practicum.ebogacheva.tracker.model.Task;
import ru.yandex.practicum.ebogacheva.tracker.server.KVServer;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

        Task task1FromManager = taskManager.getTask(task1.getId());
        Task task2FromManager = taskManager.getTask(task2.getId());
        assertEquals(task1, task1FromManager);
        assertEquals(task2, task2FromManager);
    }

}
