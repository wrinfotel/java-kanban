package http.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.NotFoundException;
import http.handlers.adapters.DurationAdapter;
import http.handlers.adapters.LocalDateTimeAdapter;
import task.Subtask;
import taskmanager.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;

    public SubtaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String response;
        String path = exchange.getRequestURI().getPath();
        String[] id = path.split("/");
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        Gson gson = gsonBuilder.create();
        switch (method) {
            case "POST":
                try (InputStream inputStream = exchange.getRequestBody()) {
                    String subtaskData = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    Subtask subtaskFromRequest = gson.fromJson(subtaskData, Subtask.class);
                    if (subtaskFromRequest != null) {
                        if (subtaskFromRequest.getId() == 0) {
                            int newSubtaskId = taskManager.addSubtask(subtaskFromRequest);
                            if (newSubtaskId != 0) {
                                sendSuccessChaged(exchange, "New subtask id " + newSubtaskId);
                            } else {
                                sendHasInteractions(exchange, "Subtask intersects with other task");
                            }
                        } else {
                            try {
                                taskManager.updateSubtask(subtaskFromRequest);
                                sendSuccessChaged(exchange, "subtask updated");
                            } catch (NotFoundException nfe) {
                                sendNotFound(exchange, nfe.getMessage());
                            }
                        }
                    }
                    sendBadRequest(exchange);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    sendError(exchange, e.getMessage());
                }
                break;
            case "GET":
                if (id.length <= 2) {
                    response = gson.toJson(taskManager.getAllSubtasks());
                    sendText(exchange, response);
                } else {
                    try {
                        Subtask subtask = taskManager.getSubtaskById(Integer.parseInt(id[2]));
                        response = gson.toJson(subtask);
                        sendText(exchange, response);
                    } catch (NotFoundException nfe) {
                        sendNotFound(exchange, nfe.getMessage());
                    }
                }
                break;
            case "DELETE":
                if (id.length <= 2) {
                    sendBadRequest(exchange);
                } else {
                    try {
                        taskManager.removeSubtaskById(Integer.parseInt(id[2]));
                        sendText(exchange, "subtask deleted");
                    } catch (NotFoundException nfe) {
                        sendNotFound(exchange, nfe.getMessage());
                    }
                }
                break;
            default:
                sendBadRequest(exchange);
        }
    }
}
