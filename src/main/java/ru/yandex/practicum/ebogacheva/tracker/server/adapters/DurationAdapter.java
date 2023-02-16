package ru.yandex.practicum.ebogacheva.tracker.server.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
        if (duration == Duration.ZERO) {
            jsonWriter.value(0);
        } else {
            jsonWriter.value(duration.toMinutes());
        }
    }

    @Override
    public Duration read(JsonReader jsonReader) throws IOException {
        int next = jsonReader.nextInt();
        if (next == 0) {
            return Duration.ZERO;
        }
        return Duration.ofMinutes(next);
    }
}
