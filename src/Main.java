import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;
import taskmanager.TaskManager;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = new TaskManager();
        Task task1 = new Task("task1", "description2");
        Task task2 = new Task("task2", "description2");
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        Subtask subtask1 = new Subtask("subtask1", "descr1");
        Subtask subtask2 = new Subtask("subtask2", "descriprion2");
        Epic epic1 = new Epic("epicTitle1", "epicDescription1");
        taskManager.addEpic(epic1);
        subtask1.setEpicId(epic1.getId());
        subtask2.setEpicId(epic1.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        Epic epic2 = new Epic();
        Subtask subtask3 = new Subtask("subtask3", "descr3");
        taskManager.addEpic(epic2);
        subtask3.setEpicId(epic2.getId());
        taskManager.addSubtask(subtask3);

        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllEpicSubtasks(epic1));
        System.out.println(taskManager.getAllEpicSubtasks(epic2));

        Task updateTask1 = taskManager.getTaskById(1);
        updateTask1.setStatus(TaskStatus.DONE);
        taskManager.updateTask(updateTask1);
        Task updateTask2 = taskManager.getTaskById(2);
        updateTask2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(updateTask2);
        System.out.println(taskManager.getAllTasks());

        Subtask updateSubtask1 = taskManager.getSubtaskById(4);
        Subtask updateSubtask2 = taskManager.getSubtaskById(5);
        updateSubtask1.setStatus(TaskStatus.IN_PROGRESS);
        updateSubtask2.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(updateSubtask1);
        taskManager.updateSubtask(updateSubtask2);
        System.out.println(taskManager.getAllSubtasks());
        System.out.println(taskManager.getAllEpics());

        taskManager.removeTaskById(1);
        taskManager.removeSubtaskById(4);
        taskManager.removeEpicById(6);
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println(taskManager.getAllEpics());

    }
}
