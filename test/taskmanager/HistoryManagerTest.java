package taskmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;
import taskhistory.HistoryManager;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HistoryManagerTest {

    protected TaskManager taskManager;
    protected HistoryManager historyManager;

    @BeforeEach
    void beforeEach() {
        taskManager = new InMemoryTaskManager();
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void getHistory() {
        assertNotNull(historyManager);
        Task task = new Task("title", "description", TaskStatus.NEW);
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void shouldReturnHistory() {
        Epic epic = new Epic("epicTitle", "epicDescription");
        int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("title", "description", TaskStatus.NEW, epicId);
        int subtaskId = taskManager.addSubtask(subtask);
        Subtask subtask2 = new Subtask("title2", "description2", TaskStatus.NEW, epicId);
        int subtask2Id = taskManager.addSubtask(subtask2);
        Task task = new Task("title", "description", TaskStatus.NEW);
        int taskId = taskManager.addTask(task);
        Task savedTask = taskManager.getTaskById(taskId);
        Subtask getSubtask = taskManager.getSubtaskById(subtaskId);
        Epic getEpic = taskManager.getEpicById(epicId);
        Subtask getSubtask2 = taskManager.getSubtaskById(subtask2Id);
        List<Task> expectedHistory = new ArrayList<>();
        expectedHistory.add(savedTask);
        expectedHistory.add(getSubtask);
        expectedHistory.add(getEpic);
        expectedHistory.add(getSubtask2);

        List<Task> history = taskManager.getHistory();
        assertEquals(4, history.size(), "Количество элементов в истории не совпадает");
        assertEquals(expectedHistory, history, "Последовательность элементов в истории не совпадает");
    }

    @Test
    void shouldReturnTaskHistoryWithoutRepeats() {
        Task task = new Task("title", "description", TaskStatus.NEW);
        Task task2 = new Task("title2", "description2", TaskStatus.NEW);
        taskManager.addTask(task);
        taskManager.addTask(task2);
        final Task savedTask = taskManager.getTaskById(task.getId());
        final Task savedTask2 = taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task.getId());
        List<Task> history = taskManager.getHistory();
        List<Task> expectedHistory = new ArrayList<>();
        expectedHistory.add(savedTask2);
        expectedHistory.add(savedTask);
        assertEquals(2, history.size(), "Количество элементов в истории не совпадает");
        assertEquals(expectedHistory, history, "Последовательность элементов в истории не совпадает");
    }

    @Test
    void shouldNotReturnHistory() {
        Task task = new Task("title", "description", TaskStatus.NEW);
        Task task2 = new Task("title2", "description2", TaskStatus.NEW);
        taskManager.addTask(task);
        taskManager.addTask(task2);
        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.removeTasks();
        List<Task> history = taskManager.getHistory();
        assertEquals(0, history.size(), "Количество элементов в истории не совпадает");
    }

    @Test
    void shouldReturnSubtaskAndEpicHistoryWithoutRepeats() {
        Epic epic = new Epic("epicTitle", "epicDescription");
        int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("title", "description", TaskStatus.NEW, epicId);
        int subtaskId = taskManager.addSubtask(subtask);
        final Subtask savedSubtask = taskManager.getSubtaskById(subtaskId);
        final Epic savedEpic = taskManager.getEpicById(epicId);
        List<Task> history = taskManager.getHistory();
        List<Task> expectedHistory = new ArrayList<>();
        expectedHistory.add(savedSubtask);
        expectedHistory.add(savedEpic);
        assertEquals(2, history.size(), "Количество элементов в истории не совпадает");
        assertEquals(expectedHistory, history, "Последовательность элементов в истории не совпадает");

        taskManager.getSubtaskById(subtaskId);
        List<Task> history2 = taskManager.getHistory();
        List<Task> expectedHistory2 = new ArrayList<>();
        expectedHistory2.add(savedEpic);
        expectedHistory2.add(savedSubtask);
        assertEquals(2, history2.size(), "Количество элементов в истории не совпадает");
        assertEquals(expectedHistory2, history2, "Последовательность элементов в истории не совпадает");
    }

    @Test
    void shouldNotReturnEpicSubtasksHistory() {
        Epic epic = new Epic("epicTitle", "epicDescription");
        int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("title", "description", TaskStatus.NEW, epicId);
        Subtask subtask2 = new Subtask("title2", "description2", TaskStatus.NEW, epicId);
        int subtaskId = taskManager.addSubtask(subtask);
        int subtaskId2 = taskManager.addSubtask(subtask2);
        taskManager.getSubtaskById(subtaskId);
        taskManager.getEpicById(epicId);
        taskManager.getSubtaskById(subtaskId2);
        List<Task> history = taskManager.getHistory();
        assertEquals(3, history.size(), "Количество элементов в истории не совпадает");
        taskManager.removeSubtasks();
        List<Task> history2 = taskManager.getHistory();
        assertEquals(1, history2.size(), "Количество элементов в истории не совпадает");
        taskManager.removeEpics();
        List<Task> history3 = taskManager.getHistory();
        assertEquals(0, history3.size(), "Количество элементов в истории не совпадает");
    }
}
