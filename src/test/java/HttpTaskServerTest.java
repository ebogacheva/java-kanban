import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.ebogacheva.tracker.model.Subtask;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

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
    void resetData() {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request;
        try {
            request = HttpRequest.newBuilder()
                    .uri(URI.create(PATH + "tasks/task/"))
                    .DELETE()
                    .build();
            httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            request = HttpRequest.newBuilder()
                    .uri(URI.create(PATH + "tasks/epic/"))
                    .DELETE()
                    .build();
            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder()
                    .uri(URI.create(PATH + "tasks/subtask/"))
                    .DELETE()
                    .build();
            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void prioritizedTasks() {
        HttpClient client = HttpClient.newHttpClient();
        // GET sorted tasks and subtasks: List<Task> getPrioritizedTasks()
        try {
            Task task1 = TestObjectsProvider.getTaskForTesting(1);
            task1.setStartDateTime(TestObjectsProvider.getDateTimeForTesting());
            HttpRequest request1 = HttpRequest
                    .newBuilder()
                    .uri(URI.create(PATH + "tasks/task"))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                    .build();
            HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
            int id1 = (gson.fromJson(response1.body(), Task.class)).getId();

            Task task2 = TestObjectsProvider.getTaskForTesting(2);
            task2.setStartDateTime(TestObjectsProvider.getDateTimeForTestingPlusMinutes(-10000));
            HttpRequest request2 = HttpRequest
                    .newBuilder()
                    .uri(URI.create(PATH + "tasks/task"))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task2)))
                    .build();
            HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
            int id2 = (gson.fromJson(response2.body(), Task.class)).getId();

            Task task3 = TestObjectsProvider.getTaskForTesting(3);
            HttpRequest request3 = HttpRequest
                    .newBuilder()
                    .uri(URI.create(PATH + "tasks/task"))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task3)))
                    .build();
            HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
            int id3 = (gson.fromJson(response3.body(), Task.class)).getId();

            HttpRequest request4 = HttpRequest.newBuilder()
                    .uri(URI.create(PATH + "tasks/"))
                    .GET()
                    .build();
            HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());
            JsonArray arrayTasks = new JsonParser().parse(response4.body()).getAsJsonArray();

            Task taskSorted1 = gson.fromJson(arrayTasks.get(0), Task.class);
            int idSorted1 = taskSorted1.getId();
            Task taskSorted2 = gson.fromJson(arrayTasks.get(1), Task.class);
            int idSorted2 = taskSorted2.getId();
            Task taskSorted3 = gson.fromJson(arrayTasks.get(2), Task.class);
            int idSorted3 = taskSorted3.getId();
            assertEquals(id2, idSorted1);
            assertEquals(id1, idSorted2);
            assertEquals(id3, idSorted3);
        } catch (IllegalArgumentException ex) {
            System.out.println("Неверный формат URL.");
        } catch (IOException | InterruptedException ex) {
            System.out.println("Ошибка соединения.");
        }
    }

    @Test
    void taskGeneralTest() {
        HttpClient client = HttpClient.newHttpClient();
        try {
            // General: POST new Task = void createTask(Task task)
            Task task1 = TestObjectsProvider.getTaskForTesting(1);
            task1.setStartDateTime(TestObjectsProvider.getDateTimeForTesting());
            HttpRequest request1 = HttpRequest
                    .newBuilder()
                    .uri(URI.create(PATH + "tasks/task"))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                    .build();
            HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
            Task task1Returned = gson.fromJson(response1.body(), Task.class);
            int code1 = response1.statusCode();
            int id = task1Returned.getId();
            assertEquals(ResponseCode.CREATED_201.getCode(), code1);
            assertNotNull(task1Returned);

            // General: GET Task = Task getTask(int id)
            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(URI.create(PATH + "tasks/task/?id=" + id))
                    .GET()
                    .build();
            HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
            Task task1FromServer = gson.fromJson(response2.body(), Task.class);
            assertEquals(task1Returned, task1FromServer);

            // General: GET Tasks = List<Task> getTasks()
            HttpRequest request3 = HttpRequest.newBuilder()
                    .uri(URI.create(PATH + "tasks/task"))
                    .GET()
                    .build();
            HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
            JsonArray arrayTasks3 = new JsonParser().parse(response3.body()).getAsJsonArray();
            assertEquals(1, arrayTasks3.size());

            // General: DELETE Task = void deleteTaskById(int id)
            HttpRequest request4 = HttpRequest.newBuilder()
                    .uri(URI.create(PATH + "tasks/task/?id=" + id))
                    .DELETE()
                    .build();
            HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());
            Task task4FromServer = gson.fromJson(response4.body(), Task.class);
            int code4 = response4.statusCode();
            assertNull(task4FromServer);
            assertEquals(ResponseCode.OK_200.getCode(), code4);

            // General: DELETE Tasks = void deleteAllTasks()
            resetData();
            Task task2 = TestObjectsProvider.getTaskForTesting(2);
            task2.setStartDateTime(TestObjectsProvider.getDateTimeForTesting());
            Task task3 = TestObjectsProvider.getTaskForTesting(3);
            HttpRequest request5 = HttpRequest
                    .newBuilder()
                    .uri(URI.create(PATH + "tasks/task"))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task2)))
                    .build();
            HttpRequest request6 = HttpRequest
                    .newBuilder()
                    .uri(URI.create(PATH + "tasks/task"))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task3)))
                    .build();
            HttpResponse<String> response5 = client.send(request5, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response6 = client.send(request6, HttpResponse.BodyHandlers.ofString());
            Task task2Returned = gson.fromJson(response5.body(), Task.class);
            Task task3Returned = gson.fromJson(response6.body(), Task.class);
            int code5 = response5.statusCode();
            int code6 = response6.statusCode();
            assertEquals(ResponseCode.CREATED_201.getCode(), code5);
            assertEquals(ResponseCode.CREATED_201.getCode(), code6);
            assertNotNull(task2Returned);
            assertNotNull(task3Returned);

            HttpRequest request7 = HttpRequest
                    .newBuilder()
                    .uri(URI.create(PATH + "tasks/task"))
                    .DELETE()
                    .build();
            HttpResponse<String> response7 = client.send(request7, HttpResponse.BodyHandlers.ofString());
            int code7 = response7.statusCode();
            assertEquals(ResponseCode.OK_200.getCode(), code7);

            HttpRequest request8 = HttpRequest.newBuilder()
                    .uri(URI.create(PATH + "tasks/task"))
                    .GET()
                    .build();
            HttpResponse<String> response8 = client.send(request8, HttpResponse.BodyHandlers.ofString());
            int code8 = response8.statusCode();
            JsonArray arrayTasks8 = new JsonParser().parse(response8.body()).getAsJsonArray();
            assertEquals(ResponseCode.OK_200.getCode(), code8);
            assertEquals(0, arrayTasks8.size());
        } catch (IllegalArgumentException ex) {
            System.out.println("Неверный формат URL.");
        } catch (IOException | InterruptedException ex) {
            System.out.println("Ошибка соединения.");
        }
    }

    @Test
    void taskNullTest() {
        HttpClient client = HttpClient.newHttpClient();
        try {
            // Null: POST new Task = void createTask(Task task)
            Task task1 = null;
            HttpRequest request1 = HttpRequest
                    .newBuilder()
                    .uri(URI.create(PATH + "tasks/task"))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                    .build();
            HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
            int code1 = response1.statusCode();
            assertEquals(ResponseCode.BAD_REQUEST_400.getCode(), code1);

            // Task doesn't exist: GET Task = Task getTask(int id)
            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(URI.create(PATH + "tasks/task/?id=" + 999))
                    .GET()
                    .build();
            HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
            int code2 = response2.statusCode();
            assertEquals(ResponseCode.NOT_FOUND_404.getCode(), code2);

            // Empty list of Tasks: GET Tasks = List<Task> getTasks()
            resetData();
            HttpRequest request3 = HttpRequest.newBuilder()
                    .uri(URI.create(PATH + "tasks/task"))
                    .GET()
                    .build();
            HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
            int code3 = response3.statusCode();
            JsonArray arrayTasks = new JsonParser().parse(response3.body()).getAsJsonArray();
            assertEquals(ResponseCode.OK_200.getCode(), code3);
            assertEquals(0, arrayTasks.size());
        } catch (IllegalArgumentException ex) {
            System.out.println("Неверный формат URL.");
        } catch (IOException | InterruptedException ex) {
            System.out.println("Ошибка соединения.");
        }
    }

    @Test
    void epicGeneralTest() {
        HttpClient client = HttpClient.newHttpClient();
        try {
            // General: POST new Epic = void createEpic(Epic epic)
            Epic epic1 = TestObjectsProvider.getEpicForTesting(1);
            HttpRequest request1 = HttpRequest
                    .newBuilder()
                    .uri(URI.create(PATH + "tasks/epic"))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic1)))
                    .build();
            HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
            Epic epic1Returned = gson.fromJson(response1.body(), Epic.class);
            int code1 = response1.statusCode();
            int id = epic1Returned.getId();
            assertEquals(ResponseCode.CREATED_201.getCode(), code1);
            assertNotNull(epic1Returned);

            // General: GET Epic = Epic getEpic(int id)
            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(URI.create(PATH + "tasks/epic/?id=" + id))
                    .GET()
                    .build();
            HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
            Epic epic1FromServer = gson.fromJson(response2.body(), Epic.class);
            int code2 = response2.statusCode();
            assertEquals(epic1Returned, epic1FromServer);
            assertEquals(ResponseCode.OK_200.getCode(), code2);

            // General: GET Epics = List<Epic> getEpics()
            HttpRequest request3 = HttpRequest.newBuilder()
                    .uri(URI.create(PATH + "tasks/epic"))
                    .GET()
                    .build();
            HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
            JsonArray arrayEpics = new JsonParser().parse(response3.body()).getAsJsonArray();
            int code3 = response3.statusCode();
            assertEquals(1, arrayEpics.size());
            assertEquals(ResponseCode.OK_200.getCode(), code3);
            JsonElement jsonElement = arrayEpics.get(0);
            Epic epicFromList = gson.fromJson(jsonElement, Epic.class);
            assertEquals(epic1Returned, epicFromList);

            // General: DELETE Epic = void deleteEpicById(int id)
            HttpRequest request4 = HttpRequest.newBuilder()
                    .uri(URI.create(PATH + "tasks/epic/?id=" + id))
                    .DELETE()
                    .build();
            HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());
            Epic epic4FromServer = gson.fromJson(response4.body(), Epic.class);
            int code4 = response4.statusCode();
            assertNull(epic4FromServer);
            assertEquals(ResponseCode.OK_200.getCode(), code4);

            // General: DELETE Epics = void deleteAllEpics()
            resetData();
            Epic epic2 = TestObjectsProvider.getEpicForTesting(2);
            Epic epic3 = TestObjectsProvider.getEpicForTesting(3);
            HttpRequest request5 = HttpRequest
                    .newBuilder()
                    .uri(URI.create(PATH + "tasks/epic"))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic2)))
                    .build();
            HttpRequest request6 = HttpRequest
                    .newBuilder()
                    .uri(URI.create(PATH + "tasks/epic"))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic3)))
                    .build();
            HttpResponse<String> response5 = client.send(request5, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> response6 = client.send(request6, HttpResponse.BodyHandlers.ofString());
            Epic epic2Returned = gson.fromJson(response5.body(), Epic.class);
            Epic epic3Returned = gson.fromJson(response6.body(), Epic.class);
            int code5 = response5.statusCode();
            int code6 = response6.statusCode();
            assertEquals(ResponseCode.CREATED_201.getCode(), code5);
            assertEquals(ResponseCode.CREATED_201.getCode(), code6);
            assertNotNull(epic2Returned);
            assertNotNull(epic3Returned);

            HttpRequest request7 = HttpRequest
                    .newBuilder()
                    .uri(URI.create(PATH + "tasks/epic"))
                    .DELETE()
                    .build();
            HttpResponse<String> response7 = client.send(request7, HttpResponse.BodyHandlers.ofString());
            int code7 = response7.statusCode();
            assertEquals(ResponseCode.OK_200.getCode(), code7);

            HttpRequest request8 = HttpRequest.newBuilder()
                    .uri(URI.create(PATH + "tasks/epic"))
                    .GET()
                    .build();
            HttpResponse<String> response8 = client.send(request8, HttpResponse.BodyHandlers.ofString());
            int code8 = response8.statusCode();
            JsonArray arrayEpics8 = new JsonParser().parse(response8.body()).getAsJsonArray();
            assertEquals(ResponseCode.OK_200.getCode(), code8);
            assertEquals(0, arrayEpics8.size());

        } catch (IllegalArgumentException ex) {
            System.out.println("Неверный формат URL.");
        } catch (IOException | InterruptedException ex) {
            System.out.println("Ошибка соединения.");
        }
    }

    @Test
    void epicNullTest() {
        HttpClient client = HttpClient.newHttpClient();
        try {
            // Null: POST new Epic = void createEpic(Epic epic)
            Epic epic1 = null;
            HttpRequest request1 = HttpRequest
                    .newBuilder()
                    .uri(URI.create(PATH + "tasks/epic"))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic1)))
                    .build();
            HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
            int code1 = response1.statusCode();
            assertEquals(ResponseCode.BAD_REQUEST_400.getCode(), code1);

            // Epic doesn't exist: GET Epic = Epic getEpic(int id)
            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(URI.create(PATH + "tasks/epic/?id=" + 999))
                    .GET()
                    .build();
            HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
            int code2 = response2.statusCode();
            assertEquals(ResponseCode.NOT_FOUND_404.getCode(), code2);

            // Empty list of Epics: GET Epics = List<Epic> getEpics()
            resetData();
            HttpRequest request3 = HttpRequest.newBuilder()
                    .uri(URI.create(PATH + "tasks/epic"))
                    .GET()
                    .build();
            HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
            int code3 = response3.statusCode();
            JsonArray arrayEpics = new JsonParser().parse(response3.body()).getAsJsonArray();
            assertEquals(ResponseCode.OK_200.getCode(), code3);
            assertEquals(0, arrayEpics.size());
        } catch (IllegalArgumentException ex) {
            System.out.println("Неверный формат URL.");
        } catch (IOException | InterruptedException ex) {
            System.out.println("Ошибка соединения.");
        }
    }

    @Test
    void subtaskGeneralTest() {
        HttpClient client = HttpClient.newHttpClient();
        try {
            // General: POST new Subtask = void createSubtask(Subtask subtask)
            Epic epic1 = TestObjectsProvider.getEpicForTesting(1);
            HttpRequest requestEpic = HttpRequest
                    .newBuilder()
                    .uri(URI.create(PATH + "tasks/epic"))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic1)))
                    .build();
            HttpResponse<String> responseEpic = client.send(requestEpic, HttpResponse.BodyHandlers.ofString());
            Epic epic1Returned = gson.fromJson(responseEpic.body(), Epic.class);
            int epicId = epic1Returned.getId();

            Subtask subtask1 = TestObjectsProvider.getSubtaskForTesting(1, epicId);
            subtask1.setStartDateTime(TestObjectsProvider.getDateTimeForTesting());
            HttpRequest request1 = HttpRequest
                    .newBuilder()
                    .uri(URI.create(PATH + "tasks/subtask"))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask1)))
                    .build();
            HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
            Subtask subtask1Returned = gson.fromJson(response1.body(), Subtask.class);
            int code1 = response1.statusCode();
            int subId = subtask1Returned.getId();
            assertEquals(ResponseCode.CREATED_201.getCode(), code1);
            assertNotNull(subtask1Returned);

            // General: GET Subtask = Subtask getSubtask(int id)
            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(URI.create(PATH + "tasks/subtask/?id=" + subId))
                    .GET()
                    .build();
            HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
            Subtask subtask1FromServer = gson.fromJson(response2.body(), Subtask.class);
            int code2 = response2.statusCode();
            assertEquals(subtask1Returned, subtask1FromServer);
            assertEquals(ResponseCode.OK_200.getCode(), code2);

            // General: GET Subtasks = List<Subtask> getSubtasks()
            HttpRequest request3 = HttpRequest.newBuilder()
                    .uri(URI.create(PATH + "tasks/subtask"))
                    .GET()
                    .build();
            HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
            JsonArray arraySubtasks1 = new JsonParser().parse(response3.body()).getAsJsonArray();
            int code3 = response3.statusCode();
            assertEquals(1, arraySubtasks1.size());
            assertEquals(ResponseCode.OK_200.getCode(), code3);

            // General: GET Subtasks by Epic = List<Subtask> getEpicSubtasks(Epic epic)
            HttpRequest request4 = HttpRequest.newBuilder()
                    .uri(URI.create(PATH + "tasks/subtask/epic/?id=" + epicId))
                    .GET()
                    .build();
            HttpResponse<String> response4 = client.send(request3, HttpResponse.BodyHandlers.ofString());
            JsonArray arraySubtasks2 = new JsonParser().parse(response4.body()).getAsJsonArray();
            JsonElement jsonElement = arraySubtasks2.get(0);
            Subtask subtaskFromServer2 = gson.fromJson(jsonElement, Subtask.class);
            assertEquals(1, arraySubtasks2.size());
            int code4 = response4.statusCode();
            assertEquals(ResponseCode.OK_200.getCode(), code4);
            assertEquals(subtask1Returned, subtaskFromServer2);

            // General: DELETE Subtask = void deleteSubtaskById(int id)
            HttpRequest request5 = HttpRequest.newBuilder()
                    .uri(URI.create(PATH + "tasks/subtask/?id=" + subId))
                    .DELETE()
                    .build();
            HttpResponse<String> response5 = client.send(request5, HttpResponse.BodyHandlers.ofString());
            Subtask subtask5FromServer = gson.fromJson(response5.body(), Subtask.class);
            int code5 = response5.statusCode();
            assertNull(subtask5FromServer);
            assertEquals(ResponseCode.OK_200.getCode(), code5);

            // General: DELETE Subtasks = void deleteAllSubtasks()
            resetData();
            Epic epic2 = TestObjectsProvider.getEpicForTesting(2);
            HttpRequest requestEpic2 = HttpRequest
                    .newBuilder()
                    .uri(URI.create(PATH + "tasks/epic"))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic2)))
                    .build();
            HttpResponse<String> responseEpic2 = client.send(requestEpic2, HttpResponse.BodyHandlers.ofString());
            Epic epic2Returned = gson.fromJson(responseEpic2.body(), Epic.class);
            int epicId2 = epic2Returned.getId();

            Subtask subtask21 = TestObjectsProvider.getSubtaskForTesting(21, epicId2);
            subtask21.setStartDateTime(TestObjectsProvider.getDateTimeForTesting());
            HttpRequest request6 = HttpRequest
                    .newBuilder()
                    .uri(URI.create(PATH + "tasks/subtask"))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask21)))
                    .build();
            HttpResponse<String> response6 = client.send(request6, HttpResponse.BodyHandlers.ofString());
            Subtask subtask21Returned = gson.fromJson(response6.body(), Subtask.class);

            HttpRequest request7 = HttpRequest
                    .newBuilder()
                    .uri(URI.create(PATH + "tasks/subtask/"))
                    .DELETE()
                    .build();
            HttpResponse<String> response7 = client.send(request7, HttpResponse.BodyHandlers.ofString());
            int code7 = response7.statusCode();
            assertEquals(ResponseCode.OK_200.getCode(), code7);

            HttpRequest request8 = HttpRequest.newBuilder()
                    .uri(URI.create(PATH + "tasks/subtask"))
                    .GET()
                    .build();
            HttpResponse<String> response8 = client.send(request8, HttpResponse.BodyHandlers.ofString());
            int code8 = response8.statusCode();
            JsonArray arrayTasks8 = new JsonParser().parse(response8.body()).getAsJsonArray();
            assertEquals(ResponseCode.OK_200.getCode(), code8);
            assertEquals(0, arrayTasks8.size());

        } catch (IllegalArgumentException ex) {
            System.out.println("Неверный формат URL.");
        } catch (IOException | InterruptedException ex) {
            System.out.println("Ошибка соединения.");
        }
    }

    @Test
    void subtaskNullTest() {
        HttpClient client = HttpClient.newHttpClient();
        try {
            // Null: POST new Subtask = void createSubtask(Subtask subtask)
            Epic epic1 = TestObjectsProvider.getEpicForTesting(1);
            HttpRequest requestEpic = HttpRequest
                    .newBuilder()
                    .uri(URI.create(PATH + "tasks/epic"))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic1)))
                    .build();
            HttpResponse<String> responseEpic = client.send(requestEpic, HttpResponse.BodyHandlers.ofString());
            Epic epic1Returned = gson.fromJson(responseEpic.body(), Epic.class);
            int epicId = epic1Returned.getId();

            Subtask subtask1 = null;
            HttpRequest request1 = HttpRequest
                    .newBuilder()
                    .uri(URI.create(PATH + "tasks/subtask"))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask1)))
                    .build();
            HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
            int code1 = response1.statusCode();
            assertEquals(ResponseCode.BAD_REQUEST_400.getCode(), code1);

            // Subtask doesn't exist: GET Subtask = Subtask getSubtask(int id)
            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(URI.create(PATH + "tasks/subtask/?id=" + 999))
                    .GET()
                    .build();
            HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
            int code2 = response2.statusCode();
            assertEquals(ResponseCode.NOT_FOUND_404.getCode(), code2);

            // Empty list of Subtask: GET Subtask = List<Subtask> getSubtasks()
            resetData();
            HttpRequest request3 = HttpRequest.newBuilder()
                    .uri(URI.create(PATH + "tasks/subtask"))
                    .GET()
                    .build();
            HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
            int code3 = response3.statusCode();
            JsonArray arraySubtasks = new JsonParser().parse(response3.body()).getAsJsonArray();
            assertEquals(ResponseCode.OK_200.getCode(), code3);
            assertEquals(0, arraySubtasks.size());
        } catch (IllegalArgumentException ex) {
            System.out.println("Неверный формат URL.");
        } catch (IOException | InterruptedException ex) {
            System.out.println("Ошибка соединения.");
        }
    }

    @Test
    void historyTest() {
        HttpClient client = HttpClient.newHttpClient();
        try {
            Task task1 = TestObjectsProvider.getTaskForTesting(1);
            task1.setStartDateTime(TestObjectsProvider.getDateTimeForTesting());
            HttpRequest request1 = HttpRequest
                    .newBuilder()
                    .uri(URI.create(PATH + "tasks/task"))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                    .build();
            HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
            int id1 = (gson.fromJson(response1.body(), Task.class)).getId();

            Epic epic1 = TestObjectsProvider.getEpicForTesting(1);
            HttpRequest request2 = HttpRequest
                    .newBuilder()
                    .uri(URI.create(PATH + "tasks/epic"))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic1)))
                    .build();
            HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
            Epic epic1Returned = gson.fromJson(response2.body(), Epic.class);
            int id2 = epic1Returned.getId();

            Subtask subtask1 = TestObjectsProvider.getSubtaskForTesting(1, id2);
            HttpRequest request3 = HttpRequest
                    .newBuilder()
                    .uri(URI.create(PATH + "tasks/subtask"))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask1)))
                    .build();
            HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
            Subtask subtask1Returned = gson.fromJson(response3.body(), Subtask.class);
            int id3 = subtask1Returned.getId();

            Task task2 = TestObjectsProvider.getTaskForTesting(2);
            task2.setStartDateTime(TestObjectsProvider.getDateTimeForTesting());
            HttpRequest request4 = HttpRequest
                    .newBuilder()
                    .uri(URI.create(PATH + "tasks/task"))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task2)))
                    .build();
            HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());
            int id4 = (gson.fromJson(response4.body(), Task.class)).getId();

            HttpRequest request5 = HttpRequest.newBuilder()
                    .uri(URI.create(PATH + "tasks/task/?id=" + id4))
                    .GET()
                    .build();
            HttpResponse<String> response5 = client.send(request5, HttpResponse.BodyHandlers.ofString());
            Task task2FromServer = gson.fromJson(response5.body(), Task.class);

            HttpRequest request6 = HttpRequest.newBuilder()
                    .uri(URI.create(PATH + "tasks/subtask/?id=" + id3))
                    .GET()
                    .build();
            HttpResponse<String> response6 = client.send(request6, HttpResponse.BodyHandlers.ofString());
            Subtask subtask1FromServer = gson.fromJson(response6.body(), Subtask.class);

            HttpRequest request7 = HttpRequest.newBuilder()
                    .uri(URI.create(PATH + "tasks/task/?id=" + id1))
                    .GET()
                    .build();
            HttpResponse<String> response7 = client.send(request7, HttpResponse.BodyHandlers.ofString());
            Task task1FromServer = gson.fromJson(response7.body(), Task.class);

            HttpRequest request8 = HttpRequest.newBuilder()
                    .uri(URI.create(PATH + "tasks/epic/?id=" + id2))
                    .GET()
                    .build();
            HttpResponse<String> response8 = client.send(request8, HttpResponse.BodyHandlers.ofString());
            Epic epic1FromServer = gson.fromJson(response8.body(), Epic.class);

            HttpRequest request = HttpRequest
                    .newBuilder()
                    .uri(URI.create(PATH + "tasks/history"))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonArray jsonArray = new JsonParser().parse(response.body()).getAsJsonArray();
            List<Task> expected = List.of(task2FromServer, subtask1FromServer, task1FromServer, epic1FromServer);
            List<Task> actual = new ArrayList<>();
            jsonArray.forEach(element -> actual.add(gson.fromJson(element, Task.class)));
            assertEquals(expected, actual);
        } catch (IllegalArgumentException ex) {
            System.out.println("Неверный формат URL.");
        } catch (IOException | InterruptedException ex) {
            System.out.println("Ошибка соединения.");
        }
    }

}
