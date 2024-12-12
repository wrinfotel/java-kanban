package taskmanager;

import exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;
import taskhistory.HistoryManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;
    protected HistoryManager historyManager;

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
        assertThrows(NotFoundException.class,
                () -> taskManager.getTaskById(taskId),
                "Задача найдена.");
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
        assertThrows(NotFoundException.class,
                () -> taskManager.getSubtaskById(subtaskId),
                "Подзадача найдена.");
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
        assertThrows(NotFoundException.class,
                () -> taskManager.getEpicById(epicId),
                "Задача найдена.");

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

        assertEquals(2, getEpic.getSubtasks().size(), "Количество подзадач не совпадает");
        assertEquals(TaskStatus.NEW, getEpic.getStatus(), "Статус не совпадает с ожидаемым - NEW");

        Subtask updateSubtask2 = new Subtask(subtask2Id, "title2", "description2", TaskStatus.IN_PROGRESS, epicId);
        taskManager.updateSubtask(updateSubtask2);

        assertEquals(2, getEpic.getSubtasks().size(), "Количество подзадач после изменения статуса - не совпадает");
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

        assertEquals(0, getEpic.getSubtasks().size(), "Количество подзадач удаления подзадач не совпадает");
        assertEquals(2, getEpicSecond.getSubtasks().size(), "Количество подзадач после добавления поздадач не совпадает");
        assertEquals(TaskStatus.NEW, getEpic.getStatus(), "Статус не совпадает с ожидаемым - New");
        assertEquals(TaskStatus.DONE, getEpicSecond.getStatus(), "Статус не совпадает с ожидаемым - Done");

        taskManager.removeSubtasks();
        assertEquals(TaskStatus.NEW, getEpic.getStatus(), "Статус не совпадает с ожидаемым - NEW");
        assertEquals(TaskStatus.NEW, getEpicSecond.getStatus(), "Статус не совпадает с ожидаемым - NEW");
    }

    @Test
    void shouldNotAddTasksWithIntersection() {
        Task task1 = new Task("task1", "description2", TaskStatus.NEW, 10, LocalDateTime.now());
        Task task2 = new Task("task2", "description2", TaskStatus.NEW);
        Task task3 = new Task("task3", "description2", TaskStatus.NEW, 10, LocalDateTime.now().minusMinutes(3));
        Task task4 = new Task("task4", "description2", TaskStatus.NEW, 10, LocalDateTime.now().plusHours(2));
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        taskManager.addTask(task4);
        assertEquals(2, taskManager.getPrioritizedTasks().size(), "Количество задач не корректно");
        assertEquals(3, taskManager.getAllTasks().size(), "Количество задач не корректно");

        Epic epic1 = new Epic("epicTitle1", "epicDescription1");
        taskManager.addEpic(epic1);
        Subtask subtask1 = new Subtask("subtask1", "descr1", TaskStatus.NEW, epic1.getId(), 10, LocalDateTime.now());
        Subtask subtask2 = new Subtask("subtask2", "descriprion2", TaskStatus.NEW, epic1.getId(), 25, LocalDateTime.now().plusDays(1));
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        assertEquals(3, taskManager.getPrioritizedTasks().size(), "Количество задач не корректно");
        assertEquals(1, taskManager.getEpicById(epic1.getId()).getSubtasks().size(), "Количество подзадач в эпике не корректно");

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task4.getId());
        taskManager.getSubtaskById(subtask2.getId());
        assertEquals(4, taskManager.getHistory().size(), "Количество задач в истории не корректно");

        taskManager.removeTasks();
        taskManager.removeSubtasks();
        TreeSet<Task> preorites = taskManager.getPrioritizedTasks();
        assertEquals(0, preorites.size(), "Количество задач после удаления не корректно");
        assertEquals(1, taskManager.getHistory().size(), "Количество задач в истории после кдаления не корректно");
    }

    @Test
    void shouldAddAndRemoveSortedTask() {
        Task task1 = new Task("task1", "description2", TaskStatus.NEW, 10, LocalDateTime.now());
        Task task4 = new Task("task4", "description2", TaskStatus.NEW, 10,
                LocalDateTime.now().plusHours(2));
        taskManager.addTask(task1);
        taskManager.addTask(task4);
        TreeSet<Task> preoritizedTasks = taskManager.getPrioritizedTasks();
        assertEquals(2, preoritizedTasks.size(), "Количество добавленных зачач не совпадает");

        Epic epic1 = new Epic("epicTitle1", "epicDescription1");
        taskManager.addEpic(epic1);

        Subtask subtask2 = new Subtask("subtask2", "descriprion2", TaskStatus.NEW, epic1.getId(),
                25, LocalDateTime.now().plusDays(1));
        taskManager.addSubtask(subtask2);

        assertEquals(3, preoritizedTasks.size(),
                "Количество добавленных зачач после добавления новых - не совпадает");
        taskManager.removeTasks();
        preoritizedTasks = taskManager.getPrioritizedTasks();
        assertEquals(1, preoritizedTasks.size(),
                "Количество зачач после удаления - не совпадает");

        taskManager.removeSubtasks();
        preoritizedTasks = taskManager.getPrioritizedTasks();
        assertEquals(0, preoritizedTasks.size(),
                "Количество после удаления всех задач - не совпадает");
    }

    @Test
    void shouldNotAddTasksWithoutStartDateToSortedTasks() {
        Task task1 = new Task("task1", "description2", TaskStatus.NEW, 10, LocalDateTime.now());
        Task task2 = new Task("task2", "description2", TaskStatus.NEW);
        Task task4 = new Task("task4", "description2", TaskStatus.NEW, 10,
                LocalDateTime.now().plusHours(2));
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task4);
        TreeSet<Task> preoritizedTasks = taskManager.getPrioritizedTasks();
        assertEquals(2, preoritizedTasks.size(), "Количество добавленных зачач не совпадает");

        Epic epic1 = new Epic("epicTitle1", "epicDescription1");
        taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("subtask1", "descr1", TaskStatus.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("subtask2", "descriprion2", TaskStatus.NEW, epic1.getId(),
                25, LocalDateTime.now().plusDays(1));
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        assertEquals(3, preoritizedTasks.size(),
                "Количество добавленных зачач после добавления новых - не совпадает");
    }
}