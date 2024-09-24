import java.util.ArrayList;

public class Epic extends Task {

    private final ArrayList<Subtask> subtasks;

    public Epic() {
        super();
        this.subtasks = new ArrayList<>();
    }

    public Epic(String title, String description) {
        super(title, description);
        this.subtasks = new ArrayList<>();
    }


    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
        checkStatus();
    }

    private void checkStatus() {
        int doneCount = 0;
        int newCount = 0;
        if (!subtasks.isEmpty()) {
            for (Subtask subtask : subtasks) {
                if (subtask.status.equals(TaskStatus.NEW)) {
                    newCount++;
                }
                if (subtask.status.equals(TaskStatus.DONE)) {
                    doneCount++;
                }
            }
            if (doneCount == subtasks.size()) {
                status = TaskStatus.DONE;
            } else if (newCount == subtasks.size()) {
                status = TaskStatus.NEW;
            } else {
                status = TaskStatus.IN_PROGRESS;
            }
        } else {
            status = TaskStatus.NEW;
        }
    }

    @Override
    public String toString() {
        String result = "Epic{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status;
        if (!subtasks.isEmpty()) {
            result += "}, Subtasks[";
            for (Subtask subtask : subtasks) {
                result += subtask.toString();
            }
            result += "]";
        }
        result += "}";

        return result;
    }

    public void removeSubtasks() {
        subtasks.clear();
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
        checkStatus();
    }

    public void updateSubtask(Subtask subtask) {
        if (subtask.getEpicId() == 0) {
            subtask.setEpicId(this.getId());
        }
        checkStatus();
    }
}
