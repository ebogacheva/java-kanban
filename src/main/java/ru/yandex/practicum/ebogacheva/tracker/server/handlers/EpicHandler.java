package ru.yandex.practicum.ebogacheva.tracker.server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.ebogacheva.tracker.managers.TaskManager;
import ru.yandex.practicum.ebogacheva.tracker.model.Epic;
import ru.yandex.practicum.ebogacheva.tracker.server.ResponseCode;
import ru.yandex.practicum.ebogacheva.tracker.server.ResponseData;
import ru.yandex.practicum.ebogacheva.tracker.server.adapters.DurationAdapter;
import ru.yandex.practicum.ebogacheva.tracker.server.adapters.LocalDateTimeAdapter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

public class EpicHandler implements HttpHandler {

    private final TaskManager taskManager;
    private final Gson gson;

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        gsonBuilder.serializeNulls();
        this.gson = gsonBuilder.create();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            final int code;
            final ResponseData responseData;
            final String path = httpExchange.getRequestURI().toString();
            final String method = httpExchange.getRequestMethod();
            final String PATH_WITH_ID = "^/tasks/epic/\\?id=\\d+$";
            final String PATH_WITHOUT_ID = "^/tasks/epic/?$";
            final boolean isCorrectPathWithoutId = Pattern.matches(PATH_WITHOUT_ID, path);
            final boolean isCorrectPathWithId = Pattern.matches(PATH_WITH_ID, path);

            switch (method) {
                case "GET":
                    if (isCorrectPathWithId) {
                        String pathId = getIdFromPath(path);
                        responseData = get(pathId);
                    } else if (isCorrectPathWithoutId) {
                        responseData = get();
                    } else {
                        code = ResponseCode.BAD_REQUEST_400.getCode();
                        responseData = new ResponseData(code, null);
                    }
                    break;
                case "POST":
                    String body = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    responseData = post(body);
                    break;
                case "DELETE":
                    if (isCorrectPathWithId) {
                        String pathId = getIdFromPath(path);
                        responseData = delete(pathId);
                    } else if (isCorrectPathWithoutId) {
                        responseData = delete(null);
                    } else {
                        code = ResponseCode.BAD_REQUEST_400.getCode();
                        responseData = new ResponseData(code, null);
                    }
                    break;
                default:
                    code = ResponseCode.NOT_ALLOWED_405.getCode();
                    responseData = new ResponseData(code, null);
            }
            httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=" + StandardCharsets.UTF_8);
            httpExchange.sendResponseHeaders(responseData.getCode(), 0);
            if (responseData.getResponse() != null) {
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(responseData.getResponse().getBytes());
                }
            }
        } finally {
            httpExchange.close();
        }
    }

    private String getIdFromPath(String path) {
        return path.replaceFirst("/tasks/epic/\\?id=", "");
    }

    private ResponseData get(String pathId) {
        ResponseData responseData;
        try {
            Epic epic = taskManager.getEpic(Integer.parseInt(pathId));
            if (epic == null) {
                responseData = new ResponseData(ResponseCode.NOT_FOUND_404.getCode(), null);
            } else {
                int code = ResponseCode.OK_200.getCode();
                String response = gson.toJson(epic);
                responseData = new ResponseData(code, response);
            }
        } catch (NumberFormatException ex) {
            responseData = new ResponseData(ResponseCode.BAD_REQUEST_400.getCode(), null);
        }
        return responseData;
    }

    private ResponseData get() {
        ResponseData responseData;
        List<Epic> epics = taskManager.getEpics();
        if (epics == null) {
            responseData = new ResponseData(ResponseCode.NOT_FOUND_404.getCode(), null);
        } else {
            int code = ResponseCode.OK_200.getCode();
            String response = gson.toJson(epics);
            responseData = new ResponseData(code, response);
        }
        return responseData;
    }

    private ResponseData post(String body) {
        int code;
        if (body == null) {
            code = ResponseCode.BAD_REQUEST_400.getCode();
            return new ResponseData(code, null);
        }
        Epic epic = gson.fromJson(body, Epic.class);
        if (epic == null) {
            code = ResponseCode.BAD_REQUEST_400.getCode();
        } else {
            int id = epic.getId();
            if (taskManager.getEpic(id) != null) {
                taskManager.updateEpic(epic);
            } else {
                taskManager.createEpic(epic);
            }
            code = ResponseCode.CREATED_201.getCode();
        }
        return new ResponseData(code, null);
    }

    private ResponseData delete(String pathId) {
        ResponseData responseData;
        if(pathId == null) {
            taskManager.deleteAllEpics();
            responseData = new ResponseData(ResponseCode.OK_200.getCode(), null);
        } else {
            try {
                taskManager.deleteEpicById(Integer.parseInt(pathId));
                responseData = new ResponseData(ResponseCode.OK_200.getCode(), null);
            } catch (NumberFormatException ex) {
                responseData = new ResponseData(ResponseCode.BAD_REQUEST_400.getCode(), null);
            }
        }
        return responseData;
    }
}
