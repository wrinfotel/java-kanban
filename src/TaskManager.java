import java.util.ArrayList;

public class TaskManager {

    /*
    * Поскольку Id задач генерируются и прявязываются к каждой задаче в родительском классе Task,
    * для хранения обьектов в менеджере был выбран ArrayList
    */
    private final ArrayList<Task> tasks;
    private final ArrayList<Subtask> subtasks;
    private final ArrayList<Epic> epics;

    public TaskManager() {
        this.tasks = new ArrayList<>();
        this.epics = new ArrayList<>();
        this.subtasks = new ArrayList<>();
    }

    public ArrayList<Task> getAllTasks() {
        return tasks;
    }

    public void removeTasks() {
        tasks.clear();
    }

    /* В связи с тем что у каждой задаче назначается id в родительском классе при создания объекта то поиск происходит
    * перебором существующих обьектов
    */
    public Task getTaskById(int id) {
        for (Task task : tasks) {
            if (task.getId() == id) {
                return task;
            }
        }
        return null;
    }

    public void addTask(Task task) {
        this.tasks.add(task);
    }

    public void removeTaskById(int id) {
        for (Task task : tasks) {
            if (task.getId() == id) {
                tasks.remove(task);
                break;
            }
        }
    }

    public ArrayList<Subtask> getAllSubtasks() {
        return subtasks;
    }

    public void removeSubtasks() {
        subtasks.clear();
    }

    public void removeEpicsSubtasks() {
        for (Subtask subtask : subtasks) {
            subtask.setEpicId(0);
        }
    }

    public Subtask getSubtaskById(int id) {
        for (Subtask subtask : subtasks) {
            if (subtask.getId() == id) {
                return subtask;
            }
        }
        return null;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
        if(subtask.getEpicId() != 0) {
            Epic epic = getEpicById(subtask.getEpicId());
            checkEpicStatus(epic);
        }
    }

    public void updateSubtask(Subtask updatedSubtask) {
        if(updatedSubtask.getEpicId() != 0) {
            Epic epic = getEpicById(updatedSubtask.getEpicId());
            checkEpicStatus(epic);
        }
    }


    public void removeSubtaskById(int id) {
        Subtask subtask = getSubtaskById(id);
        subtasks.remove(subtask);
        if(subtask.getEpicId() != 0) {
            Epic epic = getEpicById(subtask.getEpicId());
            checkEpicStatus(epic);
        }
    }

    public ArrayList<Epic> getAllEpics() {
        return epics;
    }

    public void removeEpics() {
        removeEpicsSubtasks();
        epics.clear();
    }

    public Epic getEpicById(int id) {
        for (Epic epic : epics) {
            if (epic.getId() == id) {
                return epic;
            }
        }
        return null;
    }

    public void addEpic(Epic epic) {
        this.epics.add(epic);
    }

    public void removeEpicById(int id) {
        for (Epic epic : epics) {
            if (epic.getId() == id) {
                for (Subtask subtask : subtasks) {
                    if(subtask.getEpicId() == epic.getId()) {
                        subtask.setEpicId(0);
                    }
                }
                epics.remove(epic);
                break;
            }
        }
    }

    public ArrayList<Subtask> getAllEpicSubtasks(Epic epic) {
        ArrayList<Subtask> result = new ArrayList<>();
        for (Subtask subtask : subtasks) {
            if(subtask.getEpicId() == epic.getId()) {
                result.add(subtask);
            }
        }
        return result;
    }

    public void checkEpicStatus(Epic epic) {
        int doneCount = 0;
        int newCount = 0;
        if (!subtasks.isEmpty()) {
            for (Subtask subtask : subtasks) {
                if (subtask.getEpicId() == epic.getId()) {
                    if (subtask.status.equals(TaskStatus.NEW)) {
                        newCount++;
                    }
                    if (subtask.status.equals(TaskStatus.DONE)) {
                        doneCount++;
                    }
                }
            }
            if(doneCount == subtasks.size()) {
                epic.status = TaskStatus.DONE;
            } else if (newCount == subtasks.size()) {
                epic.status = TaskStatus.NEW;
            } else {
                epic.status = TaskStatus.IN_PROGRESS;
            }
        } else {
            epic.status = TaskStatus.NEW;
        }
    }
}
