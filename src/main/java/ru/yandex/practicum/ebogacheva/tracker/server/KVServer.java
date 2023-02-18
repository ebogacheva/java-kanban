package ru.yandex.practicum.ebogacheva.tracker.server;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

/**
 * Постман: https://www.getpostman.com/collections/a83b61d9e1c81c10575c
 */
public class KVServer {
    public static final int DEFAULT_PORT = 8078;
    private final String NOT_AUTHORIZED_REQUEST_MESSAGE = "Запрос не авторизован, " +
            "нужен параметр в query API_TOKEN со значением API-ключа";
    private final String EMPTY_KEY_MESSAGE = "Ключ key пустой. key указывается в пути: /save/{key}";
    private final String SUCCESS_UPDATE_MESSAGE = "Значение для ключа успешно отправлено! key =";
    private final String LOAD_PATH = "/load";
    private final String REGISTER_PATH = "/register";
    private final String SAVE_PATH = "/save";
    private final int port;
    private final String apiToken;
    private final HttpServer server;
    private final Map<String, String> data = new HashMap<>();

    public KVServer() throws IOException {
        this(DEFAULT_PORT);
    }

    public KVServer(int port) throws IOException {
        this.port = port;
        this.apiToken = generateApiToken();
        this.server = HttpServer.create(new InetSocketAddress("localhost", port), 0);
        this.server.createContext(REGISTER_PATH, this::register);
        this.server.createContext(SAVE_PATH, this::save);
        this.server.createContext(LOAD_PATH, this::load);
    }

    private void load(HttpExchange httpExchange) throws IOException {
        try {
            System.out.println(System.lineSeparator() + LOAD_PATH);
            if (hasNoAuth(httpExchange)) {
                System.out.println(NOT_AUTHORIZED_REQUEST_MESSAGE);
                httpExchange.sendResponseHeaders(ResponseCode.FORBIDDEN_403.getCode(), 0);
                return;
            }
            if ("GET".equals(httpExchange.getRequestMethod())) {
                String key = httpExchange.getRequestURI().getPath().substring(LOAD_PATH.length());
                if (key.isEmpty()) {
                    System.out.println(EMPTY_KEY_MESSAGE);
                    httpExchange.sendResponseHeaders(ResponseCode.BAD_REQUEST_400.getCode(), 0);
                    return;
                }
                if (data.get(key) == null) {
                    System.out.println("Нет данных для ключа " + key);
                    httpExchange.sendResponseHeaders(ResponseCode.NOT_FOUND_404.getCode(), 0);
                    return;
                }
                String response = data.get(key);
                sendText(httpExchange, response);
                System.out.println(SUCCESS_UPDATE_MESSAGE + key);
                httpExchange.sendResponseHeaders(ResponseCode.OK_200.getCode(), 0);
            } else {
                System.out.println(LOAD_PATH + " ждёт GET-запрос, а получил: " + httpExchange.getRequestMethod());
                httpExchange.sendResponseHeaders(ResponseCode.NOT_ALLOWED_405.getCode(), 0);
            }
        } finally {
            httpExchange.close();
        }
    }

    private void save(HttpExchange httpExchange) throws IOException {
        try {
            System.out.println(System.lineSeparator() + SAVE_PATH);
            if (hasNoAuth(httpExchange)) {
                System.out.println(NOT_AUTHORIZED_REQUEST_MESSAGE);
                httpExchange.sendResponseHeaders(ResponseCode.FORBIDDEN_403.getCode(), 0);
                return;
            }
            if ("POST".equals(httpExchange.getRequestMethod())) {
                String key = httpExchange.getRequestURI().getPath().substring(SAVE_PATH.length());
                if (key.isEmpty()) {
                    System.out.println(EMPTY_KEY_MESSAGE);
                    httpExchange.sendResponseHeaders(ResponseCode.BAD_REQUEST_400.getCode(), 0);
                    return;
                }
                String value = readText(httpExchange);
                if (value.isEmpty()) {
                    System.out.println("Value для сохранения пустой. value указывается в теле запроса");
                    httpExchange.sendResponseHeaders(ResponseCode.BAD_REQUEST_400.getCode(), 0);
                    return;
                }
                data.put(key, value);
                System.out.println(SUCCESS_UPDATE_MESSAGE + key);
                httpExchange.sendResponseHeaders(ResponseCode.OK_200.getCode(), 0);
            } else {
                System.out.println(SAVE_PATH +" ждёт POST-запрос, а получил: " + httpExchange.getRequestMethod());
                httpExchange.sendResponseHeaders(ResponseCode.NOT_ALLOWED_405.getCode(), 0);
            }
        } finally {
            httpExchange.close();
        }
    }

    private void register(HttpExchange httpExchange) throws IOException {
        try {
            System.out.println(System.lineSeparator() + REGISTER_PATH);
            if ("GET".equals(httpExchange.getRequestMethod())) {
                sendText(httpExchange, apiToken);
            } else {
                System.out.println(REGISTER_PATH +" ждёт GET-запрос, а получил " + httpExchange.getRequestMethod());
                httpExchange.sendResponseHeaders(ResponseCode.NOT_ALLOWED_405.getCode(), 0);
            }
        } finally {
            httpExchange.close();
        }
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + this.port);
        System.out.println("Открой в браузере http://localhost:" + this.port + "/");
        System.out.println("API_TOKEN: " + this.apiToken);
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    private String generateApiToken() {
        return "" + System.currentTimeMillis();
    }

    protected boolean hasNoAuth(HttpExchange httpExchange) {
        String rawQuery = httpExchange.getRequestURI().getRawQuery();
        return rawQuery == null || (!rawQuery.contains("API_TOKEN=" + apiToken) && !rawQuery.contains("API_TOKEN=DEBUG"));
    }

    protected String readText(HttpExchange httpExchange) throws IOException {
        return new String(httpExchange.getRequestBody().readAllBytes(), UTF_8);
    }

    protected void sendText(HttpExchange httpExchange, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json");
        httpExchange.sendResponseHeaders(ResponseCode.OK_200.getCode(), resp.length);
        httpExchange.getResponseBody().write(resp);
    }
}