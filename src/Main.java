import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;
import taskmanager.Managers;
import taskmanager.TaskManager;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("task1", "description2", TaskStatus.NEW);
        Task task2 = new Task("task2", "description2", TaskStatus.NEW);
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        Epic epic1 = new Epic("epicTitle1", "epicDescription1");
        taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("subtask1", "descr1", TaskStatus.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("subtask2", "descriprion2", TaskStatus.NEW, epic1.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        Epic epic2 = new Epic("epicTitle2", "epicDescription2");
        taskManager.addEpic(epic2);

        Subtask subtask3 = new Subtask("subtask3", "descr3", TaskStatus.NEW, epic2.getId());
        taskManager.addSubtask(subtask3);

        printAllTasks(taskManager);

        Task updateTask1 = taskManager.getTaskById(task1.getId());
        Task changeTask1 = new Task(updateTask1.getId(), updateTask1.getTitle(), updateTask1.getDescription(),
                TaskStatus.DONE);
        taskManager.updateTask(changeTask1);
        Task updateTask2 = taskManager.getTaskById(task2.getId());
        Task changeTask2 = new Task(updateTask2.getId(), updateTask2.getTitle(), updateTask2.getDescription(),
                TaskStatus.IN_PROGRESS);
        taskManager.updateTask(changeTask2);
        printAllTasks(taskManager);

        Subtask updateSubtask1 = taskManager.getSubtaskById(subtask1.getId());
        Subtask updateSubtask2 = taskManager.getSubtaskById(subtask2.getId());
        Subtask changeSubtask1 = new Subtask(updateSubtask1.getId(), updateSubtask1.getTitle(),
                updateSubtask1.getDescription(), TaskStatus.IN_PROGRESS, epic2.getId());
        Subtask changeSubtask2 = new Subtask(updateSubtask2.getId(), updateSubtask2.getTitle(),
                updateSubtask2.getDescription(), TaskStatus.DONE, updateSubtask2.getEpicId());
        taskManager.updateSubtask(changeSubtask1);
        taskManager.updateSubtask(changeSubtask2);
        printAllTasks(taskManager);

        Epic updateEpic2 = taskManager.getEpicById(epic2.getId());
        Epic changedEpic2 = new Epic(updateEpic2.getId(), "epicTitle2changed", updateEpic2.getDescription(),
                updateEpic2.getSubtasks());
        taskManager.updateEpic(changedEpic2);
        taskManager.removeSubtasks();
        taskManager.removeTaskById(task1.getId());
        taskManager.removeSubtaskById(subtask1.getId());
        taskManager.removeEpicById(epic2.getId());
        taskManager.removeEpics();
        taskManager.removeTasks();
        System.out.println(taskManager.getAllTasks());
        printAllTasks(taskManager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Task task : manager.getAllEpicSubtasks(epic)) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
