import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class TaskManager {

    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;
    private static int idCount = 0;

    public TaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
    }

    public static int getIdCount() {
        idCount++;
        return idCount;
    }

    public Collection<Task> getAllTasks() {
        return tasks.values();
    }

    public void removeTasks() {
        tasks.clear();
    }

    public Task getTaskById(int id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        }
        return null;
    }

    public void addTask(Task task) {
        int id = getIdCount();
        task.setId(id);
        tasks.put(id, task);
    }

    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    public Collection<Subtask> getAllSubtasks() {
        return subtasks.values();
    }

    public void removeSubtasks() {
        subtasks.clear();
    }

    public void removeEpicsSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() != 0) {
                subtasks.remove(subtask.id);
            }
        }
    }

    public Subtask getSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            return subtasks.get(id);
        }
        return null;
    }

    public void addSubtask(Subtask subtask) {
        int id = getIdCount();
        subtask.setId(id);
        subtasks.put(id, subtask);
        if (subtask.getEpicId() != 0) {
            Epic epic = getEpicById(subtask.getEpicId());
            epic.addSubtask(subtask);
        }
    }

    public void updateSubtask(Subtask updatedSubtask) {
        if (updatedSubtask.getEpicId() != 0) {
            Epic epic = getEpicById(updatedSubtask.getEpicId());
            epic.updateSubtask(updatedSubtask);
        }
    }


    public void removeSubtaskById(int id) {
        Subtask subtask = getSubtaskById(id);
        subtasks.remove(subtask.id);
        if (subtask.getEpicId() != 0) {
            Epic epic = getEpicById(subtask.getEpicId());
            epic.removeSubtask(subtask);
        }
    }

    public Collection<Epic> getAllEpics() {
        return epics.values();
    }

    public void removeEpics() {
        removeEpicsSubtasks();
        epics.clear();
    }

    public Epic getEpicById(int id) {
        if (epics.containsKey(id)) {
            return epics.get(id);
        }
        return null;
    }

    public void addEpic(Epic epic) {
        int id = getIdCount();
        epic.setId(id);
        epics.put(id, epic);
    }

    public void removeEpicById(int id) {
        Epic epic = getEpicById(id);
        if (epic != null) {
            for (Subtask subtask : subtasks.values()) {
                if (subtask.getEpicId() == epic.getId()) {
                    subtasks.remove(subtask.id);
                }
            }
        }
        epics.remove(id);
    }

    public ArrayList<Subtask> getAllEpicSubtasks(Epic epic) {
        return epic.getSubtasks();
    }
}
