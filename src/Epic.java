public class Epic extends Task {

    public Epic() {
        super();
    }

    public Epic(String title, String description) {
        super(title, description);
    }

    @Override
    public String toString() {
        String result =  "Epic{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status;
        result += "}";

        return result;
    }
}
