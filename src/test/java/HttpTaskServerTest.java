import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.ebogacheva.tracker.Managers;
import ru.yandex.practicum.ebogacheva.tracker.managers.FileBackedTaskManager;
import ru.yandex.practicum.ebogacheva.tracker.managers.TaskManager;
import ru.yandex.practicum.ebogacheva.tracker.model.Subtask;
import ru.yandex.practicum.ebogacheva.tracker.model.Task;
import ru.yandex.practicum.ebogacheva.tracker.server.HttpTaskServer;
import ru.yandex.practicum.ebogacheva.tracker.model.Epic;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class HttpTaskServerTest {

    private HttpTaskServer httpServer;

    @BeforeAll
    public static void createNewDataFile() {
        TestObjectsProvider.createFileWithTestData();
    }

    @Test
    public void initiateHttpTaskServer() throws IOException {
        this.httpServer = new HttpTaskServer();
        this.httpServer.start();
    }

}
