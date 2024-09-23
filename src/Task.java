import java.util.Objects;

public class Task {

    protected int id;
    protected String title;
    protected String description;
    protected TaskStatus status;
    private static int idCount = 0;

    public Task() {
        this.setStatus(TaskStatus.NEW);
        this.setId();
    }

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.setStatus(TaskStatus.NEW);
        this.setId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public int getId() {
        return id;
    }

    public void setId() {
        idCount++;
        this.id = idCount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
