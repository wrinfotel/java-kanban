package http.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.NotFoundException;
import http.handlers.adapters.DurationAdapter;
import http.handlers.adapters.LocalDateTimeAdapter;
import task.Epic;
import task.Subtask;
import taskmanager.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;

    public EpicHandler(TaskManager taskManager) {
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
                    String epicData = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    Epic epicFromRequest = gson.fromJson(epicData, Epic.class);
                    if (epicFromRequest != null) {
                        epicFromRequest = createByConstructor(epicFromRequest);
                        if (epicFromRequest.getId() == 0) {
                            int newEpicId = taskManager.addEpic(epicFromRequest);
                            sendSuccessChaged(exchange, "Epic id: " + newEpicId);
                        } else {
                            try {
                                taskManager.updateEpic(epicFromRequest);
                                sendSuccessChaged(exchange, "epic updated");
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
                    Collection<Epic> epics = taskManager.getAllEpics();
                    response = gson.toJson(epics);
                    sendText(exchange, response);
                } else {
                    try {
                        Epic epic = taskManager.getEpicById(Integer.parseInt(id[2]));
                        if (id.length == 4 && id[3].equals("subtasks")) {
                            Collection<Subtask> epicSubtasks = taskManager.getAllEpicSubtasks(epic);
                            response = gson.toJson(epicSubtasks);
                        } else {
                            response = gson.toJson(epic);
                        }
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
                        taskManager.removeEpicById(Integer.parseInt(id[2]));
                        sendText(exchange, "epic deleted");
                    } catch (NotFoundException nfe) {
                        sendNotFound(exchange, nfe.getMessage());
                    }
                }
                break;
            default:
                sendBadRequest(exchange);
        }
    }

    private Epic createByConstructor(Epic epicFromRequest) {
        if (epicFromRequest.getId() == 0) {
            return new Epic(epicFromRequest.getTitle(), epicFromRequest.getDescription());
        } else {
            if (epicFromRequest.getSubtasks() != null) {
                return new Epic(epicFromRequest.getId(), epicFromRequest.getTitle(), epicFromRequest.getDescription(), epicFromRequest.getSubtasks());
            }
            return new Epic(epicFromRequest.getId(), epicFromRequest.getTitle(), epicFromRequest.getDescription(), new ArrayList<>());
        }
    }
}
