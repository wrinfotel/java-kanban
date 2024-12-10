package httpTaskServer;

import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpPrioritizedTest extends HttpTaskServerTest {
    @Test
    void getPrioritizedTasks() throws IOException, InterruptedException {
        Task task = new Task("title", "description", TaskStatus.NEW, 10, LocalDateTime.now());
        Task task2 = new Task("title2", "description", TaskStatus.NEW, 20, LocalDateTime.now().plusHours(2));
        Task task3 = new Task("title3", "description", TaskStatus.NEW);
        taskManager.addTask(task);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        Epic epic = new Epic("title", "description");
        int epicid = taskManager.addEpic(epic);
        Subtask subtask2 = new Subtask("title2", "description", TaskStatus.NEW, epicid, 5, LocalDateTime.now().plusHours(1));
        Subtask subtask3 = new Subtask("title3", "description", TaskStatus.NEW, epicid);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);
        HttpClient client = HttpClient.newHttpClient();
        URI urlHistory = URI.create("http://localhost:8080/prioritized");
        HttpRequest requestHistory = HttpRequest.newBuilder().uri(urlHistory).GET().build();
        HttpResponse<String> responseHistory = client.send(requestHistory, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseHistory.statusCode());
        List prioritizedList = gson.fromJson(responseHistory.body(), List.class);
        assertEquals(3, prioritizedList.size(), "Некорректное количество отсортированных задач");
    }
}
