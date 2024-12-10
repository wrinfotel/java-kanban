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

public class HttpHistoryTest extends HttpTaskServerTest {

    @Test
    void getHistory() throws IOException, InterruptedException {
        Epic epic = new Epic("title", "description");
        int epicid = taskManager.addEpic(epic);
        Subtask subtask2 = new Subtask("title2", "description", TaskStatus.NEW, epicid);
        Subtask subtask3 = new Subtask("title3", "description", TaskStatus.NEW, epicid);
        int subtaskId2 = taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);
        Task task = new Task("title", "description", TaskStatus.NEW);
        Task task2 = new Task("title2", "description", TaskStatus.NEW, 20, LocalDateTime.now());
        int taskId = taskManager.addTask(task);
        taskManager.addTask(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subtaskId2);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        URI urlTask = URI.create("http://localhost:8080/tasks/" + taskId);
        HttpRequest requestTask = HttpRequest.newBuilder().uri(urlTask).GET().build();
        HttpResponse<String> responseTask = client.send(requestTask, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseTask.statusCode());

        URI urlHistory = URI.create("http://localhost:8080/history");
        HttpRequest requestHistory = HttpRequest.newBuilder().uri(urlHistory).GET().build();
        HttpResponse<String> responseHistory = client.send(requestHistory, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseHistory.statusCode());
        List historyList = gson.fromJson(responseHistory.body(), List.class);
        assertEquals(2, historyList.size(), "Некорректное количество задач в истории");
    }

}
