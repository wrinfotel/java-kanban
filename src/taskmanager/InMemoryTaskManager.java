package taskmanager;

import exceptions.NotFoundException;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;
import taskhistory.HistoryManager;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    protected final HashMap<Integer, Task> tasks;
    protected final HashMap<Integer, Epic> epics;
    protected final HashMap<Integer, Subtask> subtasks;
    protected TreeSet<Task> sortedTasks;
    protected static int idCount = 0;
    private final HistoryManager history;


    public InMemoryTaskManager() {
        this.sortedTasks = new TreeSet<>((Task task1, Task task2) ->
                task1.getStartTime().compareTo(task2.getStartTime()));
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
        if (!tasks.isEmpty()) {
            tasks.keySet().forEach(history::remove);
        }
        removeAllFromSortedByType(TaskType.TASK);
        tasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task == null) {
            throw new NotFoundException("Task not found");
        }
        updateHistory(task);
        return task;
    }

    @Override
    public int addTask(Task task) {
        if (checkIntersection(task)) {
            int id = getIdCount();
            task.setId(id);
            tasks.put(id, task);
            addToSorted(task);
            return id;
        }
        return 0;
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            throw new NotFoundException("Task not found");
        }
    }

    @Override
    public void removeTaskById(int id) {
        Task task = tasks.get(id);
        if (task == null) {
            throw new NotFoundException("Task not found");
        }
        sortedTasks.remove(tasks.get(id));
        tasks.remove(id);
        history.remove(id);
    }

    @Override
    public Collection<Subtask> getAllSubtasks() {
        return subtasks.values();
    }

    @Override
    public void removeSubtasks() {
        if (!epics.isEmpty()) {
            for (Epic epic : epics.values()) {
                ArrayList<Integer> epicSubtasks = epic.getSubtasks();
                if (!epicSubtasks.isEmpty()) {
                    epicSubtasks.forEach(history::remove);
                }
                epic.removeSubtasks();
                epic.changeStatus(TaskStatus.NEW);
            }
        }
        removeAllFromSortedByType(TaskType.SUBTASK);
        subtasks.clear();
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            throw new NotFoundException("Subtask not found");
        }
        updateHistory(subtask);
        return subtask;
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    @Override
    public int addSubtask(Subtask subtask) {
        if (checkIntersection(subtask)) {
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
            addToSorted(subtask);

            return id;
        }
        return 0;
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
        } else {
            throw new NotFoundException("Subtask not found");
        }
    }

    @Override
    public void removeSubtaskById(int id) {
        Subtask subtask = getSubtask(id);
        history.remove(id);
        if (subtask == null) {
            throw new NotFoundException("Subtask not found");
        }

        sortedTasks.remove(subtask);
        subtasks.remove(subtask.getId());
        if (subtask.getEpicId() != 0) {
            Epic epic = getEpic(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(subtask.getId());
                updateEpicStatus(epic);
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
        if (!epics.isEmpty()) {
            epics.keySet().forEach(history::remove);
        }
        epics.clear();
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            throw new NotFoundException("epic not found");
        }
        updateHistory(epic);
        return epic;
    }

    protected Epic getEpic(int id) {
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
        } else {
            throw new NotFoundException("Epic not found");
        }
    }

    @Override
    public void removeEpicById(int id) {
        Epic epic = getEpic(id);
        if (epic == null) {
            throw new NotFoundException("Epic not found");
        }
        for (Integer subtaskId : epic.getSubtasks()) {
            subtasks.remove(subtaskId);
            history.remove(subtaskId);
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
        return subtasks.values().stream()
                .filter(subtask -> epic.getSubtasks().contains(subtask.getId())).collect(Collectors
                        .toCollection(ArrayList::new));
    }

    protected void updateEpicStatus(Epic epic) {
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
        updateEpicEndTime(epic);
    }

    protected void updateEpicEndTime(Epic epic) {
        epic.setDuration(Duration.ZERO);
        epic.getSubtasks().forEach(subtaskId -> {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask != null) {
                Optional<Duration> subtaskDuration = Optional.ofNullable(subtask.getDuration());
                epic.setDuration(epic.getDuration().plus(subtaskDuration.orElse(Duration.ZERO)));
                if (subtask.getStartTime() != null && epic.getStartTime() == null) {
                    epic.setStartTime(subtask.getStartTime());
                } else if (subtask.getStartTime() != null && subtask.getStartTime().isBefore(epic.getStartTime())) {
                    epic.setStartTime(subtask.getStartTime());
                }
                if (subtask.getStartTime() != null) {
                    if (subtask.getEndTime() != null && epic.getEndTime() == null) {
                        epic.setEndTime(subtask.getEndTime());
                    } else if (subtask.getEndTime() != null && subtask.getEndTime().isAfter(epic.getEndTime())) {
                        epic.setEndTime(subtask.getEndTime());
                    }
                }
            } else {
                throw new NotFoundException("Subtask not found");
            }
        });
    }

    void addToSorted(Task task) {
        if (task.getStartTime() != null) {
            sortedTasks.add(task);
        }
    }

    private void removeAllFromSortedByType(TaskType taskType) {
        if (taskType == TaskType.TASK) {
            sortedTasks = sortedTasks.stream().filter(task -> task instanceof Subtask)
                    .collect(Collectors.toCollection(
                            () -> new TreeSet<>((Task task1, Task task2) ->
                                    task1.getStartTime().compareTo(task2.getStartTime()))));
        } else {
            sortedTasks = sortedTasks.stream().filter(task -> !(task instanceof Subtask))
                    .collect(Collectors.toCollection(
                            () -> new TreeSet<>((Task task1, Task task2) ->
                                    task1.getStartTime().compareTo(task2.getStartTime()))));
        }


    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return this.sortedTasks;
    }

    private boolean checkIntersection(Task task) {
        if (task.getStartTime() != null) {
            List<Task> findedTasks = sortedTasks.stream()
                    .filter(task1 -> !(task1.getEndTime().isBefore(task.getStartTime()) || task1.getStartTime().isAfter(task.getEndTime())))
                    .toList();
            return findedTasks.isEmpty();
        }
        return true;
    }
}
