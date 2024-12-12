package http.server;

import com.sun.net.httpserver.HttpServer;
import formatters.TaskFormatter;
import http.handlers.*;
import taskmanager.FileBackedTaskManager;
import taskmanager.TaskManager;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    public HttpServer httpServer;
    public TaskManager taskManager;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler(taskManager));
        httpServer.createContext("/epics", new EpicHandler(taskManager));
        httpServer.createContext("/subtasks", new SubtaskHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));
    }

    public static void main(String[] args) throws IOException {
        File managerFile = new File("dbfile.csv");
        TaskManager taskManager;
        if (managerFile.exists()) {
            taskManager = TaskFormatter.loadFromFile(managerFile);
        } else {
            taskManager = new FileBackedTaskManager(managerFile);
        }
        HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }
}
