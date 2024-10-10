package taskmanager;

import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;
import taskhistory.HistoryManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;
    private static int idCount = 0;
    private final HistoryManager history;


    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.history = Managers.getDefaultHistory();
    }

    public static int getIdCount() {
        idCount++;
        return idCount;
    }

    @Override
    public Collection<Task> getAllTasks() {
        return tasks.values();
    }

    @Override
    public void removeTasks() {
        tasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);
            updateHistory(task);
            return task;
        }
        return null;
    }

    @Override
    public int addTask(Task task) {
        int id = getIdCount();
        task.setId(id);
        tasks.put(id, task);
        return id;
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public Collection<Subtask> getAllSubtasks() {
        return subtasks.values();
    }

    @Override
    public void removeSubtasks() {
        for (Epic epic : epics.values()) {
            epic.removeSubtasks();
        }
        subtasks.clear();
        checkEpicsStatus();
    }

    @Override
    public Subtask getSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            updateHistory(subtask);
            return subtask;
        }
        return null;
    }

    public Subtask getSubtask(int id) {
        if (subtasks.containsKey(id)) {
            return subtasks.get(id);
        }
        return null;
    }

    @Override
    public int addSubtask(Subtask subtask) {
        int id = getIdCount();
        subtask.setId(id);
        subtasks.put(id, subtask);
        if (subtask.getEpicId() != 0) {
            Epic epic = getEpic(subtask.getEpicId());
            if(epic != null) {
                epic.addSubtask(subtask);
            }
        }
        checkEpicsStatus();
        return id;
    }

    @Override
    public void updateSubtask(Subtask updatedSubtask) {
        for (Epic epic : epics.values()) {
            if (epic.getSubtasks().contains(updatedSubtask.getId())) {
                epic.removeSubtask(updatedSubtask.getId());
            }
            if (epic.getId() == updatedSubtask.getEpicId()) {
                epic.addSubtask(updatedSubtask);
            }
        }
        subtasks.put(updatedSubtask.getId(), updatedSubtask);
        checkEpicsStatus();
    }

    @Override
    public void removeSubtaskById(int id) {
        Subtask subtask = getSubtask(id);
        if (subtask != null) {
            subtasks.remove(subtask.getId());
            if (subtask.getEpicId() != 0) {
                Epic epic = getEpic(subtask.getEpicId());
                if(epic != null) {
                    epic.removeSubtask(subtask.getId());
                }
            }
        }
        checkEpicsStatus();
    }

    @Override
    public Collection<Epic> getAllEpics() {
        return epics.values();
    }

    @Override
    public void removeEpics() {
        for (Epic epic : epics.values()) {
            if (!epic.getSubtasks().isEmpty()) {
                for (Integer subtaskId : epic.getSubtasks()) {
                    subtasks.remove(subtaskId);
                }
            }
        }
        epics.clear();
    }

    @Override
    public Epic getEpicById(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            updateHistory(epic);
            return epic;
        }
        return null;
    }

    private Epic getEpic(int id) {
        if (epics.containsKey(id)) {
            return epics.get(id);
        }
        return null;
    }

    @Override
    public int addEpic(Epic epic) {
        int id = getIdCount();
        epic.setId(id);
        epic.changeStatus(TaskStatus.NEW);
        epics.put(id, epic);
        return id;
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        checkEpicsStatus();
    }

    @Override
    public void removeEpicById(int id) {
        Epic epic = getEpic(id);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtasks()) {
                subtasks.remove(subtaskId);
            }
        }
        epics.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }

    public void updateHistory(Task addTask) {
        history.add(addTask);
    }

    @Override
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
                    epic.changeStatus(TaskStatus.DONE);
                } else if (newCount == epic.getSubtasks().size()) {
                    epic.changeStatus(TaskStatus.NEW);
                } else {
                    epic.changeStatus(TaskStatus.IN_PROGRESS);
                }
            } else {
                epic.changeStatus(TaskStatus.NEW);
            }
        }
    }
}
