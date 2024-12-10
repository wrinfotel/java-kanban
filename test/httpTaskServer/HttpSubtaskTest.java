package httpTaskServer;

import exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpSubtaskTest extends HttpTaskServerTest {

    @Test
    void getAllSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("title", "description");
        int epicid = taskManager.addEpic(epic);
        Subtask subtask2 = new Subtask("title2", "description", TaskStatus.NEW, epicid);
        Subtask subtask3 = new Subtask("title3", "description", TaskStatus.NEW, epicid);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List subtasksList = gson.fromJson(response.body(), List.class);

        assertEquals(2, subtasksList.size(), "Некорректное количество подзадач");
    }

    @Test
    void getSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("title", "description");
        int epicid = taskManager.addEpic(epic);
        Subtask subtask2 = new Subtask("title2", "description", TaskStatus.NEW, epicid);
        Subtask subtask3 = new Subtask("title3", "description", TaskStatus.NEW, epicid);
        int subtaskId = taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subtaskId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Subtask subtaskResponse = gson.fromJson(response.body(), Subtask.class);
        assertEquals(200, response.statusCode());
        assertEquals(subtask2.getTitle(), subtaskResponse.getTitle(), "Некорректное название подзадачи");
        assertEquals(subtask2.getDescription(), subtaskResponse.getDescription(), "Некорректное описание подзадачи");
        assertEquals(subtask2.getEpicId(), subtaskResponse.getEpicId(), "Некорректный id эпика");
        assertEquals(subtaskId, subtaskResponse.getId(), "Некорректный id подзадачи");

        URI urlNotFound = URI.create("http://localhost:8080/subtasks/10");
        HttpRequest requestNotFound = HttpRequest.newBuilder().uri(urlNotFound).GET().build();
        HttpResponse<String> responseNotFound = client.send(requestNotFound, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, responseNotFound.statusCode());
    }

    @Test
    public void addSubtask() throws IOException, InterruptedException {

        Epic epic = new Epic("title", "description");
        int epicid = taskManager.addEpic(epic);
        Subtask subtask2 = new Subtask("title2", "description", TaskStatus.NEW, epicid);
        String subtaskJson = gson.toJson(subtask2);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        Collection<Subtask> fromManager = taskManager.getAllSubtasks();

        assertNotNull(fromManager, "Подзадачи не возвращаются");
        assertEquals(1, fromManager.size(), "Некорректное количество подзадач");
        assertEquals("title2", fromManager.stream().toList().get(0).getTitle(), "Некорректное имя подзадачи");
    }

    @Test
    public void updateSubtask() throws IOException, InterruptedException {

        Epic epic = new Epic("title", "description");
        int epicid = taskManager.addEpic(epic);
        Subtask subtask2 = new Subtask("title2", "description", TaskStatus.NEW, epicid);
        int subtaskId = taskManager.addSubtask(subtask2);
        Subtask subtask = new Subtask(subtaskId, "Test 2", "Testing task 2", TaskStatus.IN_PROGRESS, epicid);
        // конвертируем её в JSON
        String taskJson = gson.toJson(subtask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        Subtask fromManager = taskManager.getSubtaskById(subtaskId);

        assertNotNull(fromManager, "Подзадача не найдена");
        assertEquals("Test 2", fromManager.getTitle(), "Некорректное имя подзадачи");
        assertEquals("Testing task 2", fromManager.getDescription(), "Некорректное описание подзадачи");
        assertEquals(TaskStatus.IN_PROGRESS, fromManager.getStatus(), "Некорректный статус подзадачи");

        Subtask subtaskNotFound = new Subtask(35, "Test 35", "Testing task 35", TaskStatus.DONE, 23);
        // конвертируем её в JSON
        String epicJsonNotFound = gson.toJson(subtaskNotFound);
        HttpRequest requestNotFound = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJsonNotFound)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> responseNotFound = client.send(requestNotFound, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, responseNotFound.statusCode());
    }

    @Test
    void deleteSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("title", "description");
        int epicid = taskManager.addEpic(epic);
        Subtask subtask2 = new Subtask("title2", "description", TaskStatus.NEW, epicid);
        int subtaskId = taskManager.addSubtask(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subtaskId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertThrows(NotFoundException.class,
                () -> taskManager.getSubtaskById(subtaskId),
                "Подзадача найдена.");

        URI urlNotFound = URI.create("http://localhost:8080/subtasks/10");
        HttpRequest requestNotFound = HttpRequest.newBuilder().uri(urlNotFound).DELETE().build();
        HttpResponse<String> responseNotFound = client.send(requestNotFound, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, responseNotFound.statusCode());
    }
}
