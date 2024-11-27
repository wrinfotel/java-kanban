package taskmanager;

import exceptions.ManagerSaveException;
import formatters.TaskFormatter;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.LocalDateTime;
import java.util.Optional;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        super();
        this.file = file;
    }

    @Override
    public void removeTasks() {
        super.removeTasks();
        save();
    }

    @Override
    public int addTask(Task task) {
        int id;
        if (task.getId() == 0) {
            id = super.addTask(task);
        } else {
            tasks.put(task.getId(), task);
            setIdCount(task.getId());
            id = task.getId();
        }
        save();
        return id;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeSubtasks() {
        super.removeSubtasks();
        save();
    }

    @Override
    public int addSubtask(Subtask subtask) {
        int id;
        if (subtask.getId() == 0) {
            id = super.addSubtask(subtask);
        } else {
            setIdCount(subtask.getId());
            subtasks.put(subtask.getId(), subtask);
            if (subtask.getEpicId() != 0) {
                Epic epic = getEpic(subtask.getEpicId());
                if (epic != null) {
                    epic.addSubtask(subtask);
                    updateEpicStatus(epic);
                }
            }
            id = subtask.getId();
        }
        save();
        return id;
    }

    @Override
    public void updateSubtask(Subtask updatedSubtask) {
        super.updateSubtask(updatedSubtask);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }


    @Override
    public void removeEpics() {
        super.removeEpics();
        save();
    }

    @Override
    public int addEpic(Epic epic) {
        int id;
        if (epic.getId() == 0) {
            id = super.addEpic(epic);
        } else {
            setIdCount(epic.getId());
            epics.put(epic.getId(), epic);
            id = epic.getId();
        }
        save();

        return id;
    }

    private void setIdCount(int id) {
        if (idCount < id) {
            idCount = id;
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    private void save() {
        try (Writer fileWriter = new FileWriter(this.file)) {
            String head = "id,type,name,status,description,epic";
            fileWriter.write(head + "\n");
            for (Task task : this.getAllTasks()) {
                fileWriter.write(taskToString(task) + "\n");
            }

            for (Subtask subtask : this.getAllSubtasks()) {
                fileWriter.write(taskToString(subtask) + "\n");
            }

            for (Epic epic : this.getAllEpics()) {
                fileWriter.write(taskToString(epic) + "\n");
            }

        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    private String taskToString(Task task) {
        StringBuilder builder = new StringBuilder();
        builder.append(task.getId()).append(",");
        if (task instanceof Epic) {
            builder.append(TaskType.EPIC).append(",");
        } else if (task instanceof Subtask) {
            builder.append(TaskType.SUBTASK).append(",");
        } else {
            builder.append(TaskType.TASK).append(",");
        }
        builder.append(task.getTitle()).append(",");
        builder.append(task.getStatus()).append(",");
        builder.append(task.getDescription()).append(",");
        if (task instanceof Subtask) {
            builder.append(((Subtask) task).getEpicId()).append(",");
        }
        if (!(task instanceof  Epic)) {
            builder.append(task.getDuration().toMinutes()).append(",");
            Optional<LocalDateTime> startTime = Optional.ofNullable(task.getStartTime());
            builder.append(startTime.orElse(null)).append(",");
        }
        if (task instanceof Epic) {
            Optional<LocalDateTime> endTime = Optional.ofNullable(task.getEndTime());
            builder.append(endTime.orElse(null));
        }
        return builder.toString();
    }


    //Дополнительное задание
    public static void main(String[] args) {
        File managerFile = new File("testfile.csv");
        TaskManager taskManager = new FileBackedTaskManager(managerFile);
        Task task1 = new Task("task1", "description2", TaskStatus.NEW, 10, LocalDateTime.now());
        Task task2 = new Task("task2", "description2", TaskStatus.NEW);
        Task task3 = new Task("task3", "description2", TaskStatus.NEW, 10, LocalDateTime.now().minusMinutes(3));
        Task task4 = new Task("task4", "description2", TaskStatus.NEW, 10, LocalDateTime.now().plusHours(2));
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        taskManager.addTask(task4);

        Epic epic1 = new Epic("epicTitle1", "epicDescription1");
        taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("subtask1", "descr1", TaskStatus.NEW, epic1.getId(), 10, LocalDateTime.now());
        Subtask subtask2 = new Subtask("subtask2", "descriprion2", TaskStatus.NEW, epic1.getId(), 25, LocalDateTime.now().plusDays(1));
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task4.getId());
        taskManager.getSubtaskById(subtask2.getId());
        taskManager.removeTasks();
        taskManager.removeSubtasks();
        TaskManager restoredTaskManager = TaskFormatter.loadFromFile(managerFile);
        compareFileTaskManagers(taskManager, restoredTaskManager);
    }

    private static void compareFileTaskManagers(TaskManager taskManager, TaskManager restoredTaskManager) {
        if (taskManager.getAllTasks().size() == restoredTaskManager.getAllTasks().size()) {
            System.out.println("Количество задач совпадает");
        }
        if (taskManager.getAllSubtasks().size() == restoredTaskManager.getAllSubtasks().size()) {
            System.out.println("Количество подзадач совпадает");
        }
        if (taskManager.getAllEpics().size() == restoredTaskManager.getAllEpics().size()) {
            System.out.println("Количество эпиков совпадает");
        }
        for (Task task : taskManager.getAllTasks()) {
            Task restoredTask = restoredTaskManager.getTaskById(task.getId());
            if (restoredTask == null) {
                System.out.println("Задача id" + task.getId() + " не найдена");
            }
        }
        for (Subtask subtask : taskManager.getAllSubtasks()) {
            Subtask restoredSubtask = restoredTaskManager.getSubtaskById(subtask.getId());
            if (restoredSubtask == null) {
                System.out.println("Подзадача id" + subtask.getId() + " не найдена");
            }
        }
        for (Epic epic : taskManager.getAllEpics()) {
            Epic restoredEpic = restoredTaskManager.getEpicById(epic.getId());
            if (restoredEpic == null) {
                System.out.println("Эпик id" + epic.getId() + " не найден");
            }
        }
    }
}
