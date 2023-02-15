package ru.yandex.practicum.ebogacheva.tracker.server;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class KVTaskClient {

    private String API_TOKEN;
    private final String serverURL;

    public KVTaskClient(String serverURL) throws IOException, InterruptedException {
        this.serverURL = serverURL;
        register();
    }

    private void register() throws IOException, InterruptedException {
        URI uri = URI.create(this.serverURL + "/register");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .header("Content-Type", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response = httpClient.send(request, handler);
        if (response.statusCode() != ResponseCode.OK_200.getCode()) {
            System.out.println("Не удалось зарегистрироваться и получить код доступа, ошибка :" + response.statusCode());
        }
        this.API_TOKEN = response.body();
    }

    public void put(String key, String json) {
        URI uri = URI.create(this.serverURL + "/save/" + key + "?API_TOKEN=" + this.API_TOKEN);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8);
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpResponse<String> response = httpClient.send(httpRequest, handler);
            if (response.statusCode() != ResponseCode.OK_200.getCode()) {
                System.out.println("Не удалось сохранить данные, ошибка :" + response.statusCode());
            }
        } catch (IOException | InterruptedException ignored) {
        }
    }

    public String load(String key) throws IOException, InterruptedException {
        URI uri = URI.create(this.serverURL + "/load/" + key + "?API_TOKEN=" + this.API_TOKEN);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Content-Type", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response = httpClient.send(request, handler);
        if (response.statusCode() != ResponseCode.OK_200.getCode()) {
            System.out.println("Не удалось получить данные, оОшибка :" + response.statusCode());
        }
        return response.body();
    }

}
