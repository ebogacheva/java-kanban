package ru.yandex.practicum.ebogacheva.tracker.server;

public class ResponseData {

    private final int code;
    private final String response;

    public ResponseData(int code, String response) {
        this.code = code;
        this.response = response;
    }

    public int getCode() {
        return code;
    }

    public String getResponse() {
        return response;
    }
}
