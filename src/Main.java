import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;
import taskmanager.TaskManager;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = new TaskManager();
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

        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllEpicSubtasks(epic1));
        System.out.println(taskManager.getAllEpicSubtasks(epic2));

        Task updateTask1 = taskManager.getTaskById(task1.getId());
        Task changeTask1 = new Task(updateTask1.getId(), updateTask1.getTitle(), updateTask1.getDescription(),
                TaskStatus.DONE);
        taskManager.updateTask(changeTask1);
        Task updateTask2 = taskManager.getTaskById(task2.getId());
        Task changeTask2 = new Task(updateTask2.getId(), updateTask2.getTitle(), updateTask2.getDescription(),
                TaskStatus.IN_PROGRESS);
        taskManager.updateTask(changeTask2);
        System.out.println(taskManager.getAllTasks());

        Subtask updateSubtask1 = taskManager.getSubtaskById(subtask1.getId());
        Subtask updateSubtask2 = taskManager.getSubtaskById(subtask2.getId());
        Subtask changeSubtask1 = new Subtask(updateSubtask1.getId(), updateSubtask1.getTitle(),
                updateSubtask1.getDescription(), TaskStatus.IN_PROGRESS, epic2.getId());
        Subtask changeSubtask2 = new Subtask(updateSubtask2.getId(), updateSubtask2.getTitle(),
                updateSubtask2.getDescription(), TaskStatus.DONE, updateSubtask2.getEpicId());
        taskManager.updateSubtask(changeSubtask1);
        taskManager.updateSubtask(changeSubtask2);
        System.out.println(taskManager.getAllSubtasks());
        System.out.println(taskManager.getAllEpics());

        Epic updateEpic2 = taskManager.getEpicById(epic2.getId());
        Epic changedEpic2 = new Epic(updateEpic2.getId(), "epicTitle2changed", updateEpic2.getDescription(),
                updateEpic2.getSubtasks());
        taskManager.updateEpic(changedEpic2);
        System.out.println(taskManager.getAllEpicSubtasks(epic2));
        taskManager.removeSubtasks();
        taskManager.removeTaskById(task1.getId());
        taskManager.removeSubtaskById(subtask1.getId());
        taskManager.removeEpicById(epic2.getId());
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());

        taskManager.removeEpics();
        System.out.println(taskManager.getAllEpics());

        taskManager.removeTasks();
        System.out.println(taskManager.getAllTasks());
    }
}
