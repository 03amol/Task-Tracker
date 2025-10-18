import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public int id;
    public String description;
    public String status; // "todo", "in-progress", "done"
    public String createdAt;
    public String updatedAt;

    public Task(int id, String description, String status) {
        this.id = id;
        this.description = description;
        this.status = status;
        String now = LocalDateTime.now().format(FORMATTER);
        this.createdAt = now;
        this.updatedAt = now;
    }

    // Method to convert the Task object to a JSON string fragment
    public String toJsonString() {
        return String.format(
            "{\"id\":%d,\"description\":\"%s\",\"status\":\"%s\",\"createdAt\":\"%s\",\"updatedAt\":\"%s\"}",
            id,
            description.replace("\"", "\\\""), // Escape quotes in description
            status,
            createdAt,
            updatedAt
        );
    }

}