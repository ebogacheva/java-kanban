import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.ebogacheva.tracker.model.Task;
import ru.yandex.practicum.ebogacheva.tracker.server.HttpTaskServer;
import ru.yandex.practicum.ebogacheva.tracker.model.Epic;
import ru.yandex.practicum.ebogacheva.tracker.server.KVServer;
import ru.yandex.practicum.ebogacheva.tracker.server.ResponseCode;
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

    @BeforeEach
    void resetServer() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request;
        try {
            request = HttpRequest.newBuilder()
                    .uri(URI.create(PATH + "tasks/task/"))
                    .DELETE()
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());

            request = HttpRequest.newBuilder()
                    .uri(URI.create(PATH + "tasks/epic/"))
                    .DELETE()
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder()
                    .uri(URI.create(PATH + "tasks/subtask/"))
                    .DELETE()
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getTasksTest() {
        HttpClient client = HttpClient.newHttpClient();
        Task task1 = new Task("Task 1", "Description 1", 60);
        HttpRequest request1 = HttpRequest
                .newBuilder()
                .uri(URI.create(PATH + "tasks/task"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .build();
        try {
            HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
            Task task1Returned = gson.fromJson(response1.body(), Task.class);
            int id = task1Returned.getId();
            task1.setId(id);
            assertEquals(task1, task1Returned);

            HttpRequest request3 = HttpRequest.newBuilder()
                    .uri(URI.create(PATH + "tasks/task"))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request3, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            JsonArray arrayTasks = new JsonParser().parse(response.body()).getAsJsonArray();
            assertEquals(1, arrayTasks.size());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getEpicsTest() {
        HttpClient client = HttpClient.newHttpClient();
        Epic epic1 = new Epic("Epic 1", "Description 1");
        Epic epic2 = new Epic("Epic 2", "Description 2");
        HttpRequest request1 = HttpRequest
                .newBuilder()
                .uri(URI.create(PATH + "tasks/epic"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic1)))
                .build();
        HttpRequest request2 = HttpRequest
                .newBuilder()
                .uri(URI.create(PATH + "tasks/epic"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic2)))
                .build();
        try {
            client.send(request1, HttpResponse.BodyHandlers.ofString());
            client.send(request2, HttpResponse.BodyHandlers.ofString());
            HttpRequest request3 = HttpRequest.newBuilder()
                    .uri(URI.create(PATH + "tasks/epic"))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request3, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            JsonArray arrayEpics = new JsonParser().parse(response.body()).getAsJsonArray();
            assertEquals(2, arrayEpics.size());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

//    @Test
//    void getSubtasksTest() {
//        HttpClient client = HttpClient.newHttpClient();
//        URI uri = URI.create(PATH + "tasks/epic/");
//        Epic epic = new Epic("Title Epic 1", "Description Epic 1");
//
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(uri)
//                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
//                .build();
//        try {
//            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//            assertEquals(ResponseCode.CREATED_201.getCode(), response.statusCode());
//            if (response.statusCode() == ResponseCode.CREATED_201.getCode()) {
//                int epicId = Integer.parseInt(response.body().split("=")[1]);
//                epic.setId(epicId);
//                SubTask subTask = new SubTask("Title SubTask 1", "Description SubTask 1",
//                        Status.NEW, epic.getId(), Instant.now(), 4);
//                url = URI.create(BASE_PATH + "tasks/subtask/");
//
//                request = HttpRequest
//                        .newBuilder()
//                        .uri(url)
//                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask)))
//                        .build();
//
//                client.send(request, HttpResponse.BodyHandlers.ofString());
//                request = HttpRequest.newBuilder().uri(url).GET().build();
//                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//                assertEquals(StatusCode.CODE_200.getCode(), response.statusCode());
//                JsonArray arrayTasks = parseString(response.body()).getAsJsonArray();
//                assertEquals(1, arrayTasks.size());
//            }
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        }
//    }


}
