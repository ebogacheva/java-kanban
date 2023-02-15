package ru.yandex.practicum.ebogacheva.tracker.managers;

import com.google.gson.*;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import ru.yandex.practicum.ebogacheva.tracker.model.Epic;
import ru.yandex.practicum.ebogacheva.tracker.model.Task;
import ru.yandex.practicum.ebogacheva.tracker.model.Subtask;
import ru.yandex.practicum.ebogacheva.tracker.server.KVTaskClient;
import ru.yandex.practicum.ebogacheva.tracker.server.adapters.DurationAdapter;
import ru.yandex.practicum.ebogacheva.tracker.server.adapters.LocalDateTimeAdapter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTaskManager{

    private final KVTaskClient kvTaskClient;
    private final String kvServerURL;
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .serializeNulls()
            .create();


    public HttpTaskManager(String kvServerURL) throws IOException, InterruptedException {
        super(kvServerURL);
        this.kvServerURL = kvServerURL;
        this.kvTaskClient = new KVTaskClient(this.kvServerURL);
        loadFromKey();
    }

    public void loadFromKey() throws IOException, InterruptedException {
        JsonElement jsonTasks = new JsonParser().parse(kvTaskClient.load("tasks"));
        if (!jsonTasks.isJsonNull()) {
            JsonArray jsonTasksArray = jsonTasks.getAsJsonArray();
            for (JsonElement jsonTask : jsonTasksArray) {
                Task task = gson.fromJson(jsonTask, Task.class);
                this.tasks.put(task.getId(), task);
            }
        }

        JsonElement jsonEpics = new JsonParser().parse(kvTaskClient.load("epics"));
        if (!jsonEpics.isJsonNull()) {
            JsonArray jsonEpicsArray = jsonEpics.getAsJsonArray();
            for (JsonElement jsonEpic : jsonEpicsArray) {
                Epic epic = gson.fromJson(jsonEpic, Epic.class);
                this.epics.put(epic.getId(), epic);
            }
        }

        JsonElement jsonSubtasks = new JsonParser().parse(kvTaskClient.load("subtask"));
        if (!jsonSubtasks.isJsonNull()) {
            JsonArray jsonSubtasksArray = jsonSubtasks.getAsJsonArray();
            for (JsonElement jsonSubtask : jsonSubtasksArray) {
                Subtask subtask = gson.fromJson(jsonSubtask, Subtask.class);
                this.subtasks.put(subtask.getId(), subtask);
            }
        }

        JsonElement jsonSortedTasks = new JsonParser().parse(kvTaskClient.load("sortedTasks"));
        if (!jsonSubtasks.isJsonNull()) {
            JsonArray jsonSortedTasksArray = jsonSortedTasks.getAsJsonArray();
            for (JsonElement jsonSortedTask : jsonSortedTasksArray) {
                Task task = gson.fromJson(jsonSortedTask, Task.class);
                this.sortedTasks.add(task);
            }
        }

        JsonElement jsonHistoryList = new JsonParser().parse(kvTaskClient.load("history"));
        if (!jsonHistoryList.isJsonNull()) {
            JsonArray jsonHistoryArray = jsonHistoryList.getAsJsonArray();
            for (JsonElement jsonTaskId : jsonHistoryArray) {
                int taskId = jsonTaskId.getAsInt();
                if (this.subtasks.containsKey(taskId)) {
                    this.getSubtask(taskId);
                } else if (this.epics.containsKey(taskId)) {
                    this.getEpic(taskId);
                } else if (this.tasks.containsKey(taskId)) {
                    this.getTask(taskId);
                }
            }
        }
    }

    @Override
    public void save() {
        kvTaskClient.put("tasks", gson.toJson(this.tasks.values()));
        kvTaskClient.put("subtask", gson.toJson(this.subtasks.values()));
        kvTaskClient.put("epics", gson.toJson(this.epics));
        kvTaskClient.put("sortedTasks", gson.toJson(this.sortedTasks));
        kvTaskClient.put("history", gson.toJson(this.getHistory()
                .stream()
                .map(Task::getId)
                .collect(Collectors.toList())));
    }

}
