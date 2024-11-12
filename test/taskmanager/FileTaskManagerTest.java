package taskmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class FileTaskManagerTest {

    private TaskManager taskManager;
    private File file;

    @BeforeEach
    void beforeEach() throws IOException {
        file = File.createTempFile("test", "csv");
        taskManager = new FileBackedTaskManager(file);
    }

    @Test
    void addTaskAndGetTaskByIdFromFile() {
        Task task = new Task("title", "description", TaskStatus.NEW);
        int taskId = taskManager.addTask(task);
        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        TaskManager newTaskManagerFromFile = FileBackedTaskManager.loadFromFile(file);
        Task restoredTask = newTaskManagerFromFile.getTaskById(taskId);
        assertEquals(restoredTask, savedTask, "Восстановленная задача и исходная задача не совпадают.");
    }

    @Test
    void addSubtaskAndEpicAndRestoreFromFile() {
        Epic epic = new Epic("epicTitle", "epicDescription");
        int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("title", "description", TaskStatus.NEW, epicId);
        int subtaskId = taskManager.addSubtask(subtask);
        final Subtask savedSubtask = taskManager.getSubtaskById(subtaskId);
        final Epic savedEpic = taskManager.getEpicById(epicId);

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");
        TaskManager newTaskManagerFromFile = FileBackedTaskManager.loadFromFile(file);
        Epic restoredEpic = newTaskManagerFromFile.getEpicById(savedEpic.getId());
        assertEquals(restoredEpic, savedEpic, "Восстановленный эпик и исходный не совпадают.");
        Subtask restoredSubtask = newTaskManagerFromFile.getSubtaskById(savedSubtask.getId());
        assertEquals(restoredSubtask, savedSubtask, "Восстановленная подзадача и исходная не совпадают.");
    }

    @Test
    void addSubtaskAndEpicAndRemoveIt() {
        Epic epic = new Epic("epicTitle", "epicDescription");
        int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("title", "description", TaskStatus.NEW, epicId);
        int subtaskId = taskManager.addSubtask(subtask);
        final Subtask savedSubtask = taskManager.getSubtaskById(subtaskId);
        final Epic savedEpic = taskManager.getEpicById(epicId);

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");
        taskManager.removeEpicById(epicId);
        TaskManager newTaskManagerFromFile = FileBackedTaskManager.loadFromFile(file);
        Epic restoredEpic = newTaskManagerFromFile.getEpicById(savedEpic.getId());
        assertNull(restoredEpic, "Эпик найден.");

        Subtask restoredSubtask = newTaskManagerFromFile.getSubtaskById(savedSubtask.getId());
        assertNull(restoredSubtask, "Подзадача найдена.");
    }
}
