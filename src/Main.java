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

        // Дополнительное задание
        Task task1 = new Task("task1", "description2", TaskStatus.NEW);
        Task task2 = new Task("task2", "description2", TaskStatus.NEW);
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        Epic epic1 = new Epic("epicTitle1", "epicDescription1");
        taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("subtask1", "descr1", TaskStatus.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("subtask2", "descriprion2", TaskStatus.NEW, epic1.getId());
        Subtask subtask3 = new Subtask("subtask3", "descriprion3", TaskStatus.NEW, epic1.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        Epic epic2 = new Epic("epicTitle2", "epicDescription2");
        taskManager.addEpic(epic2);

        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getSubtaskById(subtask2.getId());
        taskManager.getEpicById(epic2.getId());
        taskManager.getSubtaskById(subtask3.getId());
        taskManager.getTaskById(task2.getId());
        printAllTasks(taskManager);

        taskManager.getEpicById(epic1.getId());
        printAllTasks(taskManager);

        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getSubtaskById(subtask3.getId());
        taskManager.getTaskById(task1.getId());

        printAllTasks(taskManager);

        taskManager.removeSubtaskById(subtask1.getId());
        printAllTasks(taskManager);

        taskManager.removeEpicById(epic1.getId());
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
