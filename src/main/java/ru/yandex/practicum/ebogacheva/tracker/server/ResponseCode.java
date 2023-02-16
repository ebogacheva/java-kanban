package ru.yandex.practicum.ebogacheva.tracker.server;

public enum ResponseCode {
    OK_200(200),
    CREATED_201(201),
    BAD_REQUEST_400(400),
    FORBIDDEN_403(403),
    NOT_FOUND_404(404),
    NOT_ALLOWED_405(405);

    private final int code;

    ResponseCode(int code) {
        this.code = code;
    }
    public int getCode() {
        return code;
    }
}
