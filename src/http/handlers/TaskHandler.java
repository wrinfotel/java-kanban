package http.handlers;

import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.NotFoundException;
import http.handlers.adapters.DurationAdapter;
import http.handlers.adapters.LocalDateTimeAdapter;
import task.Task;
import taskmanager.TaskManager;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;

    public TaskHandler(TaskManager taskManager) {
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
                    String taskData = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    Task taskFromRequest = gson.fromJson(taskData, Task.class);
                    if (taskFromRequest != null) {
                        if (taskFromRequest.getId() == 0) {
                            int newTaskId = taskManager.addTask(taskFromRequest);
                            if (newTaskId != 0) {
                                sendSuccessChaged(exchange, "New task id " + newTaskId);
                            } else {
                                sendHasInteractions(exchange, "Task intersects with other task");
                            }
                        } else {
                            try {
                                taskManager.updateTask(taskFromRequest);
                                sendSuccessChaged(exchange, "task updated");
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
                    response = gson.toJson(taskManager.getAllTasks());
                    sendText(exchange, response);
                } else {
                    try {
                        Task task = taskManager.getTaskById(Integer.parseInt(id[2]));
                        response = gson.toJson(task);
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
                        taskManager.removeTaskById(Integer.parseInt(id[2]));
                        sendText(exchange, "task deleted");
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
