package http.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import task.Task;
import taskmanager.TaskManager;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;

    public HistoryHandler(TaskManager taskManager) {
        super();
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if (method.equals("GET")) {
            List<Task> tasks = taskManager.getHistory();
            String response = gson.toJson(tasks);
            sendText(exchange, response);
        } else {
            sendBadRequest(exchange);
        }
    }
}

