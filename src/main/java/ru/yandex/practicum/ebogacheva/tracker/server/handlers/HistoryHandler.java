package ru.yandex.practicum.ebogacheva.tracker.server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.ebogacheva.tracker.managers.TaskManager;
import ru.yandex.practicum.ebogacheva.tracker.server.adapters.DurationAdapter;
import ru.yandex.practicum.ebogacheva.tracker.server.adapters.LocalDateTimeAdapter;
import ru.yandex.practicum.ebogacheva.tracker.server.ResponseCode;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

public class HistoryHandler implements HttpHandler {

    private final TaskManager taskManager;
    private final Gson gson;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        gsonBuilder.serializeNulls();
        this.gson = gsonBuilder.create();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            final int code;
            String response = null;
            final String path = httpExchange.getRequestURI().getPath();
            final String method = httpExchange.getRequestMethod();

            if (method.equals("GET")) {
                if (Pattern.matches("^/tasks/history/?$", path)) {
                    code = ResponseCode.OK_200.getCode();
                    response = gson.toJson(taskManager.getHistory());
                } else {
                    code = ResponseCode.BAD_REQUEST_400.getCode();
                }
            } else {
                code = ResponseCode.NOT_ALLOWED_405.getCode();
            }
            httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=" + StandardCharsets.UTF_8);
            httpExchange.sendResponseHeaders(code, 0);
            if (response != null) {
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        } finally {
            httpExchange.close();
        }
    }

}
