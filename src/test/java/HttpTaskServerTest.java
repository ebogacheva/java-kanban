import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
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
import ru.yandex.practicum.ebogacheva.tracker.server.KVServer;
import ru.yandex.practicum.ebogacheva.tracker.server.adapters.DurationAdapter;
import ru.yandex.practicum.ebogacheva.tracker.server.adapters.LocalDateTimeAdapter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskServerTest {
    private static KVServer kvServer;
    private static HttpTaskServer httpTaskServer;
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .serializeNulls()
            .create();
    private final String PATH = "http://localhost:8080/";

    @BeforeAll
    static void startServer() {
        try {
            kvServer = new KVServer();
            kvServer.start();
            httpTaskServer = new HttpTaskServer();
            httpTaskServer.start();
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    @AfterAll
    static void stopServer() {
        kvServer.stop();
        httpTaskServer.stop();
    }

//    @BeforeEach
//    void resetServer() {
//        HttpClient client = HttpClient.newHttpClient();
//        URI uri = URI.create(PATH + "tasks/task/");
//        try {
//            HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
//            client.send(request, HttpResponse.BodyHandlers.ofString());
//            uri = URI.create(PATH + "tasks/epic/");
//            request = HttpRequest.newBuilder().uri(uri).DELETE().build();
//            client.send(request, HttpResponse.BodyHandlers.ofString());
//            uri = URI.create(PATH + "tasks/subtask/");
//            request = HttpRequest.newBuilder().uri(uri).DELETE().build();
//            client.send(request, HttpResponse.BodyHandlers.ofString());
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        }
//    }

    @Test
    public void getTasksTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(PATH + "tasks/task");
        Task task1 = new Task("Task 1",
                "Description 1");
        HttpRequest request = HttpRequest
                .newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder().uri(uri).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            JsonArray arrayTasks = new JsonParser().parse(response.body()).getAsJsonArray();
            assertEquals(1, arrayTasks.size());
        } catch (IOException | InterruptedException e) {
           e.printStackTrace();
        }
    }

}
