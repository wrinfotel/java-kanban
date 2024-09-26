package task;

import java.util.ArrayList;

public class Epic extends Task {

    private final ArrayList<Integer> subtasks;

    public Epic(String title, String description) {
        super(title, description, TaskStatus.NEW);
        this.subtasks = new ArrayList<>();
    }

    public Epic(int id, String title, String description, ArrayList<Integer> subtasks) {
        super(id, title, description, TaskStatus.NEW);
        this.subtasks = subtasks;
    }

    public void addSubtask(Integer subtaskId) {
        subtasks.add(subtaskId);
    }

    @Override
    public String toString() {
        String result = "Epic{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status;
        result += "}";

        return result;
    }

    public void removeSubtasks() {
        subtasks.clear();
    }

    public ArrayList<Integer> getSubtasks() {
        return subtasks;
    }

    public void removeSubtask(Integer subtaskId) {
        subtasks.remove(subtaskId);
    }

    public void changeStatus(TaskStatus newStatus) {
        status = newStatus;
    }
}
