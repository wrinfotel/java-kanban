package taskmanager;

import formatters.TaskFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class FileTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    private File file;

    @BeforeEach
    void beforeEach() throws IOException {
        file = File.createTempFile("test", "csv");
        taskManager = new FileBackedTaskManager(file);
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void addTaskAndGetTaskByIdFromFile() {
        Task task = new Task("title", "description", TaskStatus.NEW);
        int taskId = taskManager.addTask(task);
        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        TaskManager newTaskManagerFromFile = TaskFormatter.loadFromFile(file);
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
        TaskManager newTaskManagerFromFile = TaskFormatter.loadFromFile(file);
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
        TaskManager newTaskManagerFromFile = TaskFormatter.loadFromFile(file);
        Epic restoredEpic = newTaskManagerFromFile.getEpicById(savedEpic.getId());
        assertNull(restoredEpic, "Эпик найден.");

        Subtask restoredSubtask = newTaskManagerFromFile.getSubtaskById(savedSubtask.getId());
        assertNull(restoredSubtask, "Подзадача найдена.");
    }

    @Test
    void epicStatusesTest() {
        Epic epic = new Epic("epicTitle", "epicDescription");
        int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("title", "description", TaskStatus.NEW, epicId, 10, LocalDateTime.now());
        Subtask subtask2 = new Subtask("title2", "description2", TaskStatus.NEW, epicId, 15, LocalDateTime.now().plusHours(1));
        taskManager.addSubtask(subtask);
        taskManager.addSubtask(subtask2);
        // Все подзадачи со статусом NEW
        assertEquals(TaskStatus.NEW, epic.getStatus(), "Статус эпика не NEW - не корректно.");

        Subtask subtask3 = new Subtask(subtask2.getId(), "title2", "description2", TaskStatus.DONE, epicId, 15, LocalDateTime.now().plusHours(1));
        taskManager.updateSubtask(subtask3);
        // Подзадачи со статусами NEW и DONE
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статус эпика не IN_PROGRESS - не корректно.");

        Subtask subtask4 = new Subtask(subtask.getId(), "title", "description", TaskStatus.DONE, epicId, 10, LocalDateTime.now());
        taskManager.updateSubtask(subtask4);
        // Все подзадачи со статусом DONE
        assertEquals(TaskStatus.DONE, epic.getStatus(), "Статус эпика не DONE - не корректно.");

        Subtask subtask5 = new Subtask(subtask.getId(), "title", "description", TaskStatus.IN_PROGRESS, epicId, 10, LocalDateTime.now());
        Subtask subtask6 = new Subtask(subtask2.getId(), "title2", "description2", TaskStatus.IN_PROGRESS, epicId, 15, LocalDateTime.now().plusHours(1));
        taskManager.updateSubtask(subtask5);
        taskManager.updateSubtask(subtask6);
        // Подзадачи со статусом IN_PROGRESS
        assertEquals(epic.getStatus(), TaskStatus.IN_PROGRESS, "Статус эпика не IN_PROGRESS - не корректно.");
    }
}
