package http.handlers.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
        jsonWriter.value(duration.toMinutes());
    }

    @Override
    public Duration read(JsonReader jsonReader) throws IOException {
        long durationFromRequest = jsonReader.nextLong();
        if (durationFromRequest != 0) {
            return Duration.ofMinutes(durationFromRequest);
        } else {
            return Duration.ZERO;
        }
    }
}
