package taskmanager;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface TaskManager {
    Collection<Task> getAllTasks();

    void removeTasks();

    Task getTaskById(int id);

    int addTask(Task task);

    void updateTask(Task task);

    void removeTaskById(int id);

    Collection<Subtask> getAllSubtasks();

    void removeSubtasks();

    Subtask getSubtaskById(int id);

    int addSubtask(Subtask subtask);

    void updateSubtask(Subtask updatedSubtask);

    void removeSubtaskById(int id);

    Collection<Epic> getAllEpics();

    void removeEpics();

    Epic getEpicById(int id);

    int addEpic(Epic epic);

    void updateEpic(Epic epic);

    void removeEpicById(int id);

    List<Task> getHistory();

    ArrayList<Subtask> getAllEpicSubtasks(Epic epic);
}
