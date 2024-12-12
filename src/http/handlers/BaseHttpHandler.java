package http.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import http.handlers.adapters.DurationAdapter;
import http.handlers.adapters.LocalDateTimeAdapter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class BaseHttpHandler {

    protected GsonBuilder gsonBuilder = new GsonBuilder();
    protected Gson gson;

    BaseHttpHandler() {
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gson = gsonBuilder.create();
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        baseResponse(h, text, 200, true);
    }

    protected void sendCreated(HttpExchange h, String text) throws IOException {
        baseResponse(h, text, 201, true);
    }

    protected void sendNotFound(HttpExchange h, String text) throws IOException {
        baseResponse(h, text, 404, true);
    }

    protected void sendHasInteractions(HttpExchange h, String text) throws IOException {
        baseResponse(h, text, 406, true);
    }

    protected void sendError(HttpExchange h, String text) throws IOException {
        baseResponse(h, text, 500, true);
    }

    protected void sendBadRequest(HttpExchange h) throws IOException {
        baseResponse(h, "", 400, false);
    }

    private void baseResponse(HttpExchange h, String text, int code, boolean hasBody) throws IOException {
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        if (hasBody) {
            byte[] resp = text.getBytes(StandardCharsets.UTF_8);
            h.sendResponseHeaders(code, resp.length);
            h.getResponseBody().write(resp);
        } else {
            h.sendResponseHeaders(code, 0);
        }
        h.close();
    }
}