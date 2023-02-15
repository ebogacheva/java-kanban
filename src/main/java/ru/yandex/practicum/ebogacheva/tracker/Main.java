package ru.yandex.practicum.ebogacheva.tracker;

import ru.yandex.practicum.ebogacheva.tracker.server.HttpTaskServer;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        HttpTaskServer httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();

    }
}
