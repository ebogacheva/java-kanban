import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.ebogacheva.tracker.Managers;
import ru.yandex.practicum.ebogacheva.tracker.managers.TaskManager;

public class InMemoryTaskManagerTest extends TaskManagerTest<TaskManager> {

    @BeforeEach
    public void initiateTaskManager() {
        taskManager = Managers.getDefault();
    }
}
