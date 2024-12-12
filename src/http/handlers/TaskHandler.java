package http.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.NotFoundException;
import task.Task;
import taskmanager.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;

    public TaskHandler(TaskManager taskManager) {
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
                taskManager.removeTaskById(Integer.parseInt(id[2]));
                sendText(exchange, "task deleted");
            } catch (NotFoundException nfe) {
                sendNotFound(exchange, nfe.getMessage());
            }
        }
    }

    private void handleGet(HttpExchange exchange, String[] id) throws IOException {
        String response;
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
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        try (InputStream inputStream = exchange.getRequestBody()) {
            String taskData = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            Task taskFromRequest = gson.fromJson(taskData, Task.class);
            if (taskFromRequest != null) {
                if (taskFromRequest.getId() == 0) {
                    int newTaskId = taskManager.addTask(taskFromRequest);
                    if (newTaskId != 0) {
                        sendCreated(exchange, "New task id " + newTaskId);
                    } else {
                        sendHasInteractions(exchange, "Task intersects with other task");
                    }
                } else {
                    try {
                        taskManager.updateTask(taskFromRequest);
                        sendCreated(exchange, "task updated");
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
