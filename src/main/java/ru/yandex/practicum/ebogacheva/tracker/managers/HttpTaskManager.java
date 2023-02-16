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
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .serializeNulls()
            .create();

    private enum Keys {
        TASKS("tasks"),
        SUBTASKS("subtasks"),
        EPICS("epics"),
        HISTORY("history");
        private final String key;
        Keys(String key) {
            this.key = key;
        }
        public String getKey() {
            return key;
        }
    }

    public HttpTaskManager(String kvServerUrl) throws IOException, InterruptedException {
        super(kvServerUrl);
        this.kvTaskClient = new KVTaskClient(kvServerUrl);
        loadFromKey();
    }

    @Override
    public void save() {
        kvTaskClient.put(Keys.TASKS.getKey(), gson.toJson(this.tasks.values()));
        kvTaskClient.put(Keys.SUBTASKS.getKey(), gson.toJson(this.subtasks.values()));
        kvTaskClient.put(Keys.EPICS.getKey(), gson.toJson(this.epics));
        kvTaskClient.put(Keys.HISTORY.getKey(),
                        gson.toJson(this.getHistory().stream()
                        .map(Task::getId)
                        .collect(Collectors.toList())));
    }

    public void loadFromKey() {
        load(Keys.TASKS);
        load(Keys.EPICS);
        load(Keys.SUBTASKS);

        String loaded = kvTaskClient.load(Keys.HISTORY.getKey());
        JsonElement jsonLoaded = new JsonParser().parse(loaded);
        if (jsonLoaded.isJsonNull()) {
            return;
        }
        JsonArray jsonLoadedArray = jsonLoaded.getAsJsonArray();
        for (JsonElement jsonTaskId : jsonLoadedArray) {
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

    private void load(Keys key) {
        String loaded = kvTaskClient.load(key.getKey());
        JsonElement jsonLoaded = new JsonParser().parse(loaded);
        if (jsonLoaded.isJsonNull()) {
            return;
        }
        JsonArray jsonLoadedArray = jsonLoaded.getAsJsonArray();
        for (JsonElement jsonTask : jsonLoadedArray) {
            switch (key) {
                case TASKS:
                    Task task = gson.fromJson(jsonTask, Task.class);
                    this.tasks.put(task.getId(), task);
                    this.sortedTasks.add(task);
                    break;
                case EPICS:
                    Epic epic = gson.fromJson(jsonTask, Epic.class);
                    this.epics.put(epic.getId(), epic);
                    break;
                case SUBTASKS:
                    Subtask subtask = gson.fromJson(jsonTask, Subtask.class);
                    this.subtasks.put(subtask.getId(), subtask);
                    this.sortedTasks.add(subtask);
                    break;
            }
        }
    }

}
