package httpTaskServer;

import exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import task.Task;
import task.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTasksTest extends HttpTaskServerTest {
    @Test
    void getAllTasks() throws IOException, InterruptedException {
        Task task = new Task("title", "description", TaskStatus.NEW);
        Task task2 = new Task("title2", "description", TaskStatus.NEW, 20, LocalDateTime.now());
        Task task3 = new Task("title3", "description", TaskStatus.NEW);
        taskManager.addTask(task);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List taskList = gson.fromJson(response.body(), List.class);

        assertEquals(3, taskList.size(), "Некорректное количество задач");
    }

    @Test
    void getTaskById() throws IOException, InterruptedException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy H:m");
        Task task = new Task("title", "description", TaskStatus.NEW, 10, LocalDateTime.now());
        Task task2 = new Task("title2", "description", TaskStatus.NEW, 20, LocalDateTime.now().plusHours(2));
        Task task3 = new Task("title3", "description", TaskStatus.NEW);
        taskManager.addTask(task);
        int taskId = taskManager.addTask(task2);
        taskManager.addTask(task3);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + taskId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task taskResponse = gson.fromJson(response.body(), Task.class);
        assertEquals(200, response.statusCode());
        assertEquals(task2.getTitle(), taskResponse.getTitle(), "Некорректное название задачи");
        assertEquals(task2.getDescription(), taskResponse.getDescription(), "Некорректное описание задачи");
        assertEquals(taskId, taskResponse.getId(), "Некорректный id задачи");
        assertEquals(task2.getDuration(), taskResponse.getDuration(), "Некорректная длительность задачи");
        assertEquals(task2.getStatus(), taskResponse.getStatus(), "Некорректный статус задачи");
        assertEquals(task2.getStartTime().format(dtf), taskResponse.getStartTime().format(dtf), "Некорректная дата старта задачи");

        URI urlNotFound = URI.create("http://localhost:8080/tasks/10");
        HttpRequest requestNotFound = HttpRequest.newBuilder().uri(urlNotFound).GET().build();
        HttpResponse<String> responseNotFound = client.send(requestNotFound, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, responseNotFound.statusCode());
    }

    @Test
    public void addTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, 5, LocalDateTime.now());
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        Collection<Task> tasksFromManager = taskManager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.stream().toList().get(0).getTitle(), "Некорректное имя задачи");
    }

    @Test
    public void updateTask() throws IOException, InterruptedException {

        Task task2 = new Task("title2", "description", TaskStatus.NEW, 20, LocalDateTime.now().plusHours(2));
        int taskId = taskManager.addTask(task2);
        Task task = new Task(taskId, "Test 2", "Testing task 2",
                TaskStatus.IN_PROGRESS, 20, LocalDateTime.now().plusHours(2));
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        Task taskFromManager = taskManager.getTaskById(taskId);

        assertNotNull(taskFromManager, "Задача не найдена");
        assertEquals("Test 2", taskFromManager.getTitle(), "Некорректное имя задачи");
        assertEquals("Testing task 2", taskFromManager.getDescription(), "Некорректное описание задачи");
        assertEquals(TaskStatus.IN_PROGRESS, taskFromManager.getStatus(), "Некорректный статус задачи");

        Task taskNotFound = new Task(35, "Test 35", "Testing task 35",
                TaskStatus.IN_PROGRESS, 20, LocalDateTime.now().plusHours(2));
        // конвертируем её в JSON
        String taskJsonNotFound = gson.toJson(taskNotFound);
        HttpRequest requestNotFound = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJsonNotFound)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> responseNotFound = client.send(requestNotFound, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, responseNotFound.statusCode());
    }

    @Test
    void deleteTaskById() throws IOException, InterruptedException {
        Task task2 = new Task("title2", "description", TaskStatus.NEW, 20, LocalDateTime.now().plusHours(2));
        int taskId = taskManager.addTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + taskId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertThrows(NotFoundException.class,
                () -> taskManager.getTaskById(taskId),
                "Задача найдена.");

        URI urlNotFound = URI.create("http://localhost:8080/tasks/10");
        HttpRequest requestNotFound = HttpRequest.newBuilder().uri(urlNotFound).DELETE().build();
        HttpResponse<String> responseNotFound = client.send(requestNotFound, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, responseNotFound.statusCode());
    }

}
