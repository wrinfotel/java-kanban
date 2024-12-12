package http.handlers.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy H:m");

    @Override
    public void write(final JsonWriter jsonWriter, final LocalDateTime localDate) throws IOException {
        if (localDate != null) {
            jsonWriter.value(localDate.format(dtf));
        } else {
            jsonWriter.value("-");
        }
    }

    @Override
    public LocalDateTime read(final JsonReader jsonReader) throws IOException {
        String startTimeFromRequest = jsonReader.nextString();
        if (!startTimeFromRequest.equals("-")) {
            return LocalDateTime.parse(startTimeFromRequest, dtf);
        } else {
            return null;
        }
    }
}
