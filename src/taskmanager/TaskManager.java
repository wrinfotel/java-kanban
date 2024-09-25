package taskmanager;

import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

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

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    public Collection<Subtask> getAllSubtasks() {
        return subtasks.values();
    }


    public void removeSubtasks() {
        for (Epic epic : epics.values()) {
            epic.removeSubtasks();
        }
        subtasks.clear();
        checkEpicsStatus();
    }

    public void removeEpicsSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() != 0) {
                subtasks.remove(subtask.getId());
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
            epic.addSubtask(subtask.getId());
        }
        checkEpicsStatus();
    }

    public void updateSubtask(Subtask updatedSubtask) {
            for (Epic epic : epics.values()) {
                if(epic.getSubtasks().contains(updatedSubtask.getId())) {
                    epic.removeSubtask(updatedSubtask.getId());
                }
                if(epic.getId() == updatedSubtask.getEpicId()) {
                    epic.addSubtask(updatedSubtask.getId());
                }
            }
            subtasks.put(updatedSubtask.getId(), updatedSubtask);
            checkEpicsStatus();
    }

    public void removeSubtaskById(int id) {
        Subtask subtask = getSubtaskById(id);
        subtasks.remove(subtask.getId());
        if (subtask.getEpicId() != 0) {
            Epic epic = getEpicById(subtask.getEpicId());
            epic.removeSubtask(subtask.getId());
        }
        checkEpicsStatus();
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

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void removeEpicById(int id) {
        Epic epic = getEpicById(id);
        if (epic != null) {
            for (Subtask subtask : subtasks.values()) {
                if (subtask.getEpicId() == epic.getId()) {
                    subtasks.remove(subtask.getId());
                }
            }
        }
        epics.remove(id);
    }

    public ArrayList<Subtask> getAllEpicSubtasks(Epic epic) {
        ArrayList<Subtask> subtasks1 = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == epic.getId()) {
                subtasks1.add(subtask);
            }
        }
        return subtasks1;
    }

    private void checkEpicsStatus() {
        for (Epic epic : epics.values()) {
            int doneCount = 0;
            int newCount = 0;
            if (!epic.getSubtasks().isEmpty()) {
                for (Subtask subtask : subtasks.values()) {
                    if (epic.getSubtasks().contains(subtask.getId())) {
                        if (subtask.getStatus().equals(TaskStatus.NEW)) {
                            newCount++;
                        }
                        if (subtask.getStatus().equals(TaskStatus.DONE)) {
                            doneCount++;
                        }
                    }
                }
                if (doneCount == epic.getSubtasks().size()) {
                    epic.setStatus(TaskStatus.DONE);
                } else if (newCount == epic.getSubtasks().size()) {
                    epic.setStatus(TaskStatus.NEW);
                } else {
                    epic.setStatus(TaskStatus.IN_PROGRESS);
                }
            } else {
                epic.setStatus(TaskStatus.NEW);
            }
        }
    }
}
