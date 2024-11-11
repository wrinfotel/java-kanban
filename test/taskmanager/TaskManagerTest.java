package taskmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;
import taskhistory.HistoryManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {

    private TaskManager taskManager;
    private HistoryManager historyManager;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void addTaskAndGetTaskById() {
        Task task = new Task("title", "description", TaskStatus.NEW);
        int taskId = taskManager.addTask(task);
        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }


    @Test
    void getAllTasks() {
        Task task = new Task("title", "description", TaskStatus.NEW);
        taskManager.addTask(task);
        Task task2 = new Task("title", "description", TaskStatus.NEW);
        taskManager.addTask(task2);

        final Collection<Task> tasks = taskManager.getAllTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");
    }

    @Test
    void removeTasks() {
        Task task = new Task("title", "description", TaskStatus.NEW);
        taskManager.addTask(task);
        Task task2 = new Task("title", "description", TaskStatus.NEW);
        taskManager.addTask(task2);
        final Collection<Task> tasks = taskManager.getAllTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");
        taskManager.removeTasks();
        assertTrue(tasks.isEmpty(), "Задачи возвращаются.");

    }


    @Test
    void updateTask() {
        Task task = new Task("title", "description", TaskStatus.NEW);
        int taskId = taskManager.addTask(task);
        Task taskUpdate = new Task(taskId, "titleUpdate", "descriptionUpdate", TaskStatus.IN_PROGRESS);
        taskManager.updateTask(taskUpdate);
        assertEquals(task, taskUpdate, "Задачи не совпадают.");
        Task getTask = taskManager.getTaskById(taskId);
        assertEquals(taskUpdate.getTitle(), getTask.getTitle(), "Заголовки не обновились");
        assertEquals(taskUpdate.getDescription(), getTask.getDescription(), "Описание не обновилось");
        assertEquals(taskUpdate.getStatus(), getTask.getStatus(), "Статус не обновился");
    }

    @Test
    void removeTaskById() {
        Task task = new Task("title", "description", TaskStatus.NEW);
        int taskId = taskManager.addTask(task);
        final Task savedTask = taskManager.getTaskById(taskId);
        assertNotNull(savedTask, "Задача не найдена.");
        taskManager.removeTaskById(taskId);
        final Task removedTask = taskManager.getTaskById(taskId);
        assertNull(removedTask, "Задача найдена.");
    }

    @Test
    void addSubtaskAndEpic() {
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
    }

    @Test
    void getAllSubtasksAndRemoveAll() {
        Epic epic = new Epic("epicTitle", "epicDescription");
        int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("title", "description", TaskStatus.NEW, epicId);
        taskManager.addSubtask(subtask);
        Subtask subtask2 = new Subtask("title2", "description2", TaskStatus.NEW, epicId);
        taskManager.addSubtask(subtask2);

        Epic epic2 = new Epic("epicTitle2", "epicDescription2");
        int epicId2 = taskManager.addEpic(epic2);
        Subtask subtask3 = new Subtask("title3", "description3", TaskStatus.NEW, epicId2);
        taskManager.addSubtask(subtask3);

        final Collection<Subtask> subtasks = taskManager.getAllSubtasks();
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(3, subtasks.size(), "Неверное количество подзадач.");

        taskManager.removeSubtasks();
        assertTrue(subtasks.isEmpty(), "Подзадачи возвращаются.");
        assertEquals(0, epic.getSubtasks().size(), "Эпик не должен содержать подзадачи после их удаления");
        assertEquals(0, epic2.getSubtasks().size(), "Эпик не должен содержать подзадачи после их удаления");
    }


    @Test
    void updateSubtask() {
        Epic epic = new Epic("epicTitle", "epicDescription");
        int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("title", "description", TaskStatus.NEW, epicId);
        int subtaskId = taskManager.addSubtask(subtask);
        Subtask subtaskUpdate = new Subtask(subtaskId, "titleUpdate", "descriptionUpdate", TaskStatus.IN_PROGRESS, epicId);
        taskManager.updateSubtask(subtaskUpdate);
        assertEquals(subtask, subtaskUpdate, "Подзадачи не совпадают.");
        Subtask getSubtask = taskManager.getSubtaskById(subtaskId);
        assertEquals(subtaskUpdate.getTitle(), getSubtask.getTitle(), "Заголовки не обновились");
        assertEquals(subtaskUpdate.getDescription(), getSubtask.getDescription(), "Описание не обновилось");
        assertEquals(subtaskUpdate.getStatus(), getSubtask.getStatus(), "Статус не обновился");
    }

    @Test
    void removeSubtaskById() {
        Epic epic = new Epic("epicTitle", "epicDescription");
        int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("title", "description", TaskStatus.NEW, epicId);
        int subtaskId = taskManager.addSubtask(subtask);
        final Subtask savedSubtask = taskManager.getSubtaskById(subtaskId);
        assertNotNull(savedSubtask, "Подзадача не найдена.");
        taskManager.removeSubtaskById(subtaskId);
        final Subtask removedSubtask = taskManager.getSubtaskById(subtaskId);
        assertNull(removedSubtask, "Подзадача найдена.");
    }


    @Test
    void getAllEpicsAndRemove() {
        Epic epic = new Epic("epicTitle", "epicDescription");
        Epic epic2 = new Epic("epicTitle2", "epicDescription2");
        taskManager.addEpic(epic2);
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("title", "description", TaskStatus.NEW, epic.getId());
        taskManager.addSubtask(subtask);
        final Collection<Epic> epics = taskManager.getAllEpics();
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(2, epics.size(), "Неверное количество эпиков.");

        taskManager.removeEpics();
        assertTrue(epics.isEmpty(), "Эпики возвращаются.");
    }

    @Test
    void getEpicById() {
        Epic epic = new Epic("epicTitle", "epicDescription");
        int epicId = taskManager.addEpic(epic);
        final Epic savedEpic = taskManager.getEpicById(epicId);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");
    }

    @Test
    void updateEpic() {
        Epic epic = new Epic("epicTitle", "epicDescription");
        int epicId = taskManager.addEpic(epic);
        Epic epicUpdate = new Epic(epicId, "epicTitle", "epicDescription", epic.getSubtasks());
        taskManager.updateEpic(epicUpdate);
        assertEquals(epic, epicUpdate, "Эпики не совпадают.");
        Epic getEpic = taskManager.getEpicById(epicId);
        assertEquals(epicUpdate.getTitle(), getEpic.getTitle(), "Заголовки не обновились");
        assertEquals(epicUpdate.getDescription(), getEpic.getDescription(), "Описание не обновилось");
        assertEquals(epicUpdate.getStatus(), getEpic.getStatus(), "Статус не обновился");
    }

    @Test
    void removeEpicById() {
        Epic epic = new Epic("epicTitle", "epicDescription");
        int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("title", "description", TaskStatus.NEW, epicId);
        taskManager.addSubtask(subtask);
        final Epic savedEpic = taskManager.getEpicById(epicId);
        assertNotNull(savedEpic, "Эпик не найден.");
        taskManager.removeEpicById(epicId);
        final Epic removedEpic = taskManager.getEpicById(epicId);
        assertNull(removedEpic, "Эпик найден.");

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
    void getAllEpicSubtasks() {
        Epic epic = new Epic("epicTitle", "epicDescription");
        int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("title", "description", TaskStatus.NEW, epicId);
        taskManager.addSubtask(subtask);
        Subtask subtask2 = new Subtask("title2", "description2", TaskStatus.NEW, epicId);
        taskManager.addSubtask(subtask2);
        ArrayList<Subtask> expectedSubtaskList = new ArrayList<>();
        expectedSubtaskList.add(subtask);
        expectedSubtaskList.add(subtask2);
        final Epic savedEpic = taskManager.getEpicById(epicId);
        ArrayList<Subtask> epicSubtasks = taskManager.getAllEpicSubtasks(savedEpic);
        assertEquals(expectedSubtaskList, epicSubtasks, "Набор подзадач не совпадает");
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
    void shouldChangeEpicStatuses() {
        Epic epic = new Epic("epicTitle", "epicDescription");
        int epicId = taskManager.addEpic(epic);
        Epic epicSecond = new Epic("epicTitle2", "epicDescription2");
        int epicIdSecond = taskManager.addEpic(epicSecond);
        Epic getEpic = taskManager.getEpicById(epicId);
        Epic getEpicSecond = taskManager.getEpicById(epicIdSecond);
        assertEquals(TaskStatus.NEW, getEpic.getStatus(), "Статус не совпадает с ожидаемым - NEW");
        Subtask subtask = new Subtask("title", "description", TaskStatus.NEW, epicId);
        int subtaskId = taskManager.addSubtask(subtask);
        Subtask subtask2 = new Subtask("title2", "description2", TaskStatus.NEW, epicId);
        int subtask2Id = taskManager.addSubtask(subtask2);

        assertEquals(TaskStatus.NEW, getEpic.getStatus(), "Статус не совпадает с ожидаемым - NEW");

        Subtask updateSubtask2 = new Subtask(subtask2Id, "title2", "description2", TaskStatus.IN_PROGRESS, epicId);
        taskManager.updateSubtask(updateSubtask2);
        assertEquals(TaskStatus.IN_PROGRESS, getEpic.getStatus(), "Статус не совпадает с ожидаемым - IN_PROGRESS");

        Subtask updateSubtask2ToStatusDone = new Subtask(subtask2Id, "title2", "description2", TaskStatus.DONE, epicId);
        taskManager.updateSubtask(updateSubtask2ToStatusDone);
        assertEquals(TaskStatus.IN_PROGRESS, getEpic.getStatus(), "Статус не совпадает с ожидаемым - IN_PROGRESS");

        Subtask updateSubtaskToStatusDone = new Subtask(subtaskId, "title", "description", TaskStatus.DONE, epicId);
        taskManager.updateSubtask(updateSubtaskToStatusDone);
        assertEquals(TaskStatus.DONE, getEpic.getStatus(), "Статус не совпадает с ожидаемым - DONE");
        assertEquals(TaskStatus.NEW, getEpicSecond.getStatus(), "Статус не совпадает с ожидаемым - NEW");

        Subtask updateSubtaskToAnotherEpic = new Subtask(subtaskId, "title", "description", TaskStatus.DONE, epicIdSecond);
        taskManager.updateSubtask(updateSubtaskToAnotherEpic);
        Subtask updateSubtask2ToAnotherEpic = new Subtask(subtask2Id, "title2", "description2", TaskStatus.DONE, epicIdSecond);
        taskManager.updateSubtask(updateSubtask2ToAnotherEpic);
        assertEquals(TaskStatus.NEW, getEpic.getStatus(), "Статус не совпадает с ожидаемым - DONE");
        assertEquals(TaskStatus.DONE, getEpicSecond.getStatus(), "Статус не совпадает с ожидаемым - NEW");

        taskManager.removeSubtasks();
        assertEquals(TaskStatus.NEW, getEpic.getStatus(), "Статус не совпадает с ожидаемым - NEW");
        assertEquals(TaskStatus.NEW, getEpicSecond.getStatus(), "Статус не совпадает с ожидаемым - NEW");
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