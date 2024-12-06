package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {

    private final ArrayList<Integer> subtasks;
    private LocalDateTime endTime;

    public Epic(String title, String description) {
        super(title, description, TaskStatus.NEW);
        this.subtasks = new ArrayList<>();
        this.duration = Duration.ZERO;
    }

    public Epic(int id, String title, String description, ArrayList<Integer> subtasks) {
        super(id, title, description, TaskStatus.NEW);
        this.subtasks = subtasks;
        this.duration = Duration.ZERO;
    }

    public Epic(int id, String title, String description, ArrayList<Integer> subtasks, LocalDateTime endTime) {
        super(id, title, description, TaskStatus.NEW);
        this.subtasks = subtasks;
        this.duration = Duration.ZERO;
        this.endTime = endTime;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask.getId());
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

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getEndTime() {
        return this.endTime;
    }


    public void removeSubtask(Integer subtaskId) {
        subtasks.remove(subtaskId);
    }

    public void changeStatus(TaskStatus newStatus) {
        status = newStatus;
    }
}
