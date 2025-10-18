import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

public class TaskCLI {
    private static final String FILE_NAME = "tasks.json";
    private static List<Task> tasks = new ArrayList<>();
    
    // Regex pattern to extract a single task JSON object from the file content.
    // This is highly simplified and assumes the JSON is cleanly formatted by saveTasks.
    private static final Pattern TASK_PATTERN = Pattern.compile(
        "\\{\"id\":(\\d+),\"description\":\"(.*?)\",\"status\":\"(.*?)\",\"createdAt\":\"(.*?)\",\"updatedAt\":\"(.*?)\"\\}",
        Pattern.DOTALL
    );

    // --- FILE I/O ---

    private static void loadTasks() {
        Path filePath = Paths.get(FILE_NAME);
        if (!Files.exists(filePath)) {
            return; // File does not exist, start with an empty list.
        }

        try {
            String jsonContent = new String(Files.readAllBytes(filePath));
            if (jsonContent.trim().isEmpty() || "[]".equals(jsonContent.trim())) {
                return; // Empty or default JSON content
            }
            
            // Remove the outer [ and ] to process individual task objects
            String taskListContent = jsonContent.trim().substring(1, jsonContent.length() - 1);
            
            // Use regex matcher to find and parse each task object
            Matcher matcher = TASK_PATTERN.matcher(taskListContent);
            while (matcher.find()) {
                try {
                    int id = Integer.parseInt(matcher.group(1));
                    String description = matcher.group(2).replace("\\\"", "\""); // Unescape quotes
                    String status = matcher.group(3);
                    String createdAt = matcher.group(4);
                    String updatedAt = matcher.group(5);

                    tasks.add(new Task(id, description, status, createdAt, updatedAt));
                } catch (NumberFormatException e) {
                    System.err.println("Warning: Corrupt ID found in JSON. Skipping task.");
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading tasks file: " + e.getMessage());
        }
    }

    private static void saveTasks() {
        try {
            // Collect all Task JSON strings, join them with a comma, and wrap in a JSON array
            String jsonContent = "[\n" +
                tasks.stream()
                     .map(Task::toJsonString)
                     .collect(Collectors.joining(",\n")) +
                "\n]";
                
            Files.write(Paths.get(FILE_NAME), jsonContent.getBytes());
        } catch (IOException e) {
            System.err.println("Error saving tasks to file: " + e.getMessage());
        }
    }

    // --- ID Management ---
    private static int getNextId() {
        // Find the maximum existing ID and increment it, or start at 1 if no tasks exist
        return tasks.stream()
                    .mapToInt(t -> t.id)
                    .max()
                    .orElse(0) + 1;
    }

    // --- MAIN METHOD & COMMAND PARSING ---

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java TaskCLI <command> [arguments...]");
            return;
        }

        // 1. Load data from JSON file
        loadTasks();

        String command = args[0].toLowerCase();
        
        try {
            switch (command) {
                case "add":
                    if (args.length < 2) throw new IllegalArgumentException("Usage: task-cli add \"<description>\"");
                    addTask(args[1]);
                    break;
                case "update":
                    if (args.length < 3) throw new IllegalArgumentException("Usage: task-cli update <id> \"<description>\"");
                    updateTask(Integer.parseInt(args[1]), args[2]);
                    break;
                case "delete":
                    if (args.length < 2) throw new IllegalArgumentException("Usage: task-cli delete <id>");
                    deleteTask(Integer.parseInt(args[1]));
                    break;
                case "mark-in-progress":
                    if (args.length < 2) throw new IllegalArgumentException("Usage: task-cli mark-in-progress <id>");
                    markTaskStatus(Integer.parseInt(args[1]), "in-progress");
                    break;
                case "mark-done":
                    if (args.length < 2) throw new IllegalArgumentException("Usage: task-cli mark-done <id>");
                    markTaskStatus(Integer.parseInt(args[1]), "done");
                    break;
                case "list":
                    // If no filter is provided, default to "all"
                    listTasks(args.length > 1 ? args[1].toLowerCase() : "all");
                    break;
                default:
                    System.out.println("Error: Unknown command '" + command + "'");
            }
        } catch (NumberFormatException e) {
            System.err.println("Error: Task ID must be a number.");
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        } finally {
            // 3. Save data back to JSON file after any operation
            saveTasks();
        }
    }

    // --- FEATURE IMPLEMENTATION METHODS ---
    
    private static void addTask(String description) {
        int newId = getNextId();
        Task newTask = new Task(newId, description);
        tasks.add(newTask);
        System.out.printf("Task added successfully (ID: %d)\n", newId);
    }

    private static void updateTask(int id, String newDescription) {
        Task task = tasks.stream().filter(t -> t.id == id).findFirst().orElse(null);

        if (task != null) {
            task.description = newDescription;
            task.updatedAt = LocalDateTime.now().format(Task.FORMATTER);
            System.out.printf("Task %d updated successfully.\n", id);
        } else {
            System.out.printf("Error: Task with ID %d not found.\n", id);
        }
    }

    private static void deleteTask(int id) {
        boolean removed = tasks.removeIf(t -> t.id == id);
        if (removed) {
            System.out.printf("Task %d deleted successfully.\n", id);
        } else {
            System.out.printf("Error: Task with ID %d not found.\n", id);
        }
    }

    private static void markTaskStatus(int id, String status) {
        Task task = tasks.stream().filter(t -> t.id == id).findFirst().orElse(null);

        if (task != null) {
            task.status = status;
            task.updatedAt = LocalDateTime.now().format(Task.FORMATTER);
            System.out.printf("Task %d marked as %s.\n", id, status.replace('-', ' '));
        } else {
            System.out.printf("Error: Task with ID %d not found.\n", id);
        }
    }

    private static void listTasks(String statusFilter) {
        List<Task> filteredTasks = new ArrayList<>();
        
        switch (statusFilter) {
            case "done":
            case "todo":
            case "in-progress":
                filteredTasks = tasks.stream().filter(t -> statusFilter.equals(t.status)).collect(Collectors.toList());
                break;
            case "all":
            default:
                filteredTasks = tasks;
                break;
        }

        System.out.printf("--- Tasks (%s) ---\n", statusFilter.toUpperCase());
        if (filteredTasks.isEmpty()) {
            System.out.println("No tasks found.");
            return;
        }

        for (Task task : filteredTasks) {
            // Display: [ID] [STATUS] Description (UpdatedAt)
            System.out.printf("[%d] [%s] %s (Updated: %s)\n", 
                task.id, 
                task.status.toUpperCase(), 
                task.description, 
                task.updatedAt.split("T")[0] 
            );
        }
    }
}