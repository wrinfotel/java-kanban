package http.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.NotFoundException;
import task.Subtask;
import taskmanager.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;

    public SubtaskHandler(TaskManager taskManager) {
        super();
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] id = path.split("/");
        switch (method) {
            case "POST":
                handlePost(exchange);
                break;
            case "GET":
                handleGet(exchange, id);
                break;
            case "DELETE":
                handleDelete(exchange, id);
                break;
            default:
                sendBadRequest(exchange);
        }
    }

    private void handleDelete(HttpExchange exchange, String[] id) throws IOException {
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
    }

    private void handleGet(HttpExchange exchange, String[] id) throws IOException {
        String response;
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
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        try (InputStream inputStream = exchange.getRequestBody()) {
            String subtaskData = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            Subtask subtaskFromRequest = gson.fromJson(subtaskData, Subtask.class);
            if (subtaskFromRequest != null) {
                if (subtaskFromRequest.getId() == 0) {
                    int newSubtaskId = taskManager.addSubtask(subtaskFromRequest);
                    if (newSubtaskId != 0) {
                        sendCreated(exchange, "New subtask id " + newSubtaskId);
                    } else {
                        sendHasInteractions(exchange, "Subtask intersects with other task");
                    }
                } else {
                    try {
                        taskManager.updateSubtask(subtaskFromRequest);
                        sendCreated(exchange, "subtask updated");
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
    }
}
