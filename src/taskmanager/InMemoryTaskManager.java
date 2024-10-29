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
        if(!tasks.isEmpty()) {
            for (Integer taskId : tasks.keySet()) {
                history.remove(taskId);
            }
        }
        tasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            updateHistory(task);
        }
        return task;
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
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void removeTaskById(int id) {
        tasks.remove(id);
        history.remove(id);
    }

    @Override
    public Collection<Subtask> getAllSubtasks() {
        return subtasks.values();
    }

    @Override
    public void removeSubtasks() {
        if(!epics.isEmpty()) {
            for (Epic epic : epics.values()) {
                ArrayList<Integer> epicSubtasks = epic.getSubtasks();
                if (!epicSubtasks.isEmpty()) {
                    for (Integer subtaskId : epicSubtasks) {
                        history.remove(subtaskId);
                    }
                }
                epic.removeSubtasks();
                epic.changeStatus(TaskStatus.NEW);
            }
        }
        subtasks.clear();
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            updateHistory(subtask);
        }
        return subtask;
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    @Override
    public int addSubtask(Subtask subtask) {
        int id = getIdCount();
        subtask.setId(id);
        subtasks.put(id, subtask);
        if (subtask.getEpicId() != 0) {
            Epic epic = getEpic(subtask.getEpicId());
            if (epic != null) {
                epic.addSubtask(subtask);
                updateEpicStatus(epic);
            }
        }

        return id;
    }

    @Override
    public void updateSubtask(Subtask updatedSubtask) {
        if (subtasks.containsKey(updatedSubtask.getId())) {
            subtasks.put(updatedSubtask.getId(), updatedSubtask);
            for (Epic epic : epics.values()) {
                if (epic.getSubtasks().contains(updatedSubtask.getId())) {
                    epic.removeSubtask(updatedSubtask.getId());
                    updateEpicStatus(epic);
                }
                if (epic.getId() == updatedSubtask.getEpicId()) {
                    epic.addSubtask(updatedSubtask);
                    updateEpicStatus(epic);
                }
            }
        }
    }

    @Override
    public void removeSubtaskById(int id) {
        Subtask subtask = getSubtask(id);
        history.remove(id);
        if (subtask != null) {
            subtasks.remove(subtask.getId());
            if (subtask.getEpicId() != 0) {
                Epic epic = getEpic(subtask.getEpicId());
                if (epic != null) {
                    epic.removeSubtask(subtask.getId());
                    updateEpicStatus(epic);
                }
            }
        }

    }

    @Override
    public Collection<Epic> getAllEpics() {
        return epics.values();
    }

    @Override
    public void removeEpics() {
        removeSubtasks();
        if(!epics.isEmpty()) {
            for(Integer epicId : epics.keySet()) {
                history.remove(epicId);
            }
        }
        epics.clear();
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            updateHistory(epic);
        }
        return epic;
    }

    private Epic getEpic(int id) {
        return epics.get(id);
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
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            updateEpicStatus(epic);
        }
    }

    @Override
    public void removeEpicById(int id) {
        Epic epic = getEpic(id);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtasks()) {
                subtasks.remove(subtaskId);
                history.remove(subtaskId);
            }
        }
        epics.remove(id);
        history.remove(id);
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
        for (Integer subtaskId : epic.getSubtasks()) {
                Subtask getSubtask = subtasks.get(subtaskId);
                if(getSubtask != null) {
                    subtasks1.add(getSubtask);
                }
            }
        return subtasks1;
    }

    private void updateEpicStatus(Epic epic) {
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
