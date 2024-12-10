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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpEpicTest extends HttpTaskServerTest {

    @Test
    void getAllEpics() throws IOException, InterruptedException {
        Epic epic = new Epic("title", "description");
        Epic epic2 = new Epic("title2", "description");
        Epic epic3 = new Epic("title3", "description");
        taskManager.addEpic(epic);
        taskManager.addEpic(epic2);
        taskManager.addEpic(epic3);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List epicsList = gson.fromJson(response.body(), List.class);

        assertEquals(3, epicsList.size(), "Некорректное количество эпиков");
    }

    @Test
    void getEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("title2", "description");
        int taskId = taskManager.addEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + taskId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Epic taskResponse = gson.fromJson(response.body(), Epic.class);
        assertEquals(200, response.statusCode());
        assertEquals(epic.getTitle(), taskResponse.getTitle(), "Некорректное название эпика");
        assertEquals(epic.getDescription(), taskResponse.getDescription(), "Некорректное описание эпика");
        assertEquals(taskId, taskResponse.getId(), "Некорректный id эпика");

        URI urlNotFound = URI.create("http://localhost:8080/epics/10");
        HttpRequest requestNotFound = HttpRequest.newBuilder().uri(urlNotFound).GET().build();
        HttpResponse<String> responseNotFound = client.send(requestNotFound, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, responseNotFound.statusCode());
    }

    @Test
    public void addEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 2", "Testing task 2");
        String epicJson = gson.toJson(epic);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        Collection<Epic> tasksFromManager = taskManager.getAllEpics();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.stream().toList().get(0).getTitle(), "Некорректное имя задачи");
    }

    @Test
    public void updateEpic() throws IOException, InterruptedException {

        Epic epic2 = new Epic("title2", "description");
        int epicId = taskManager.addEpic(epic2);
        Epic epic = new Epic(epicId, "Test 2", "Testing task 2", new ArrayList<>());
        // конвертируем её в JSON
        String taskJson = gson.toJson(epic);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        Epic fromManager = taskManager.getEpicById(epicId);

        assertNotNull(fromManager, "Эпик не найден");
        assertEquals("Test 2", fromManager.getTitle(), "Некорректное имя эпика");
        assertEquals("Testing task 2", fromManager.getDescription(), "Некорректное описание эпика");

        Epic epicNotFound = new Epic(35, "Test 35", "Testing task 35", new ArrayList<>());
        // конвертируем её в JSON
        String epicJsonNotFound = gson.toJson(epicNotFound);
        HttpRequest requestNotFound = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJsonNotFound)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> responseNotFound = client.send(requestNotFound, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, responseNotFound.statusCode());
    }

    @Test
    void deleteEpicById() throws IOException, InterruptedException {
        Epic epic2 = new Epic("title2", "description");
        int epicId = taskManager.addEpic(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epicId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertThrows(NotFoundException.class,
                () -> taskManager.getEpicById(epicId),
                "Эпик найден.");

        URI urlNotFound = URI.create("http://localhost:8080/epics/10");
        HttpRequest requestNotFound = HttpRequest.newBuilder().uri(urlNotFound).DELETE().build();
        HttpResponse<String> responseNotFound = client.send(requestNotFound, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, responseNotFound.statusCode());
    }

    @Test
    void getEpicSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("title", "description");
        int epicid = taskManager.addEpic(epic);
        Subtask subtask2 = new Subtask("title2", "description", TaskStatus.NEW, epicid);
        Subtask subtask3 = new Subtask("title3", "description", TaskStatus.NEW, epicid);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epicid + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List subtasksResponse = gson.fromJson(response.body(), List.class);
        assertEquals(200, response.statusCode());
        assertNotNull(subtasksResponse, "Подзадачи не возвращаются");
        assertEquals(2, subtasksResponse.size(), "Некорректное количество подзадач");
    }
}
