# Java CLI Task Tracker

A simple Command Line Interface (CLI) application built with Java to manage a to-do list. This project adheres to the constraint of using only **native Java features** and stores all task data in a local **`tasks.json`** file, without relying on any external libraries or frameworks.

## 🚀 How to Run the Project

### Prerequisites

* **Java Development Kit (JDK) 8 or higher** installed on your system.

### Steps to Compile and Run

1.  **Clone the Repository:**
    ```bash
    git clone 
    cd 
    ```

2.  **Compile the Java Files:**
    Open your terminal in the project directory and compile both source files.
    ```bash
    javac Task.java TaskCLI.java
    ```

3.  **Run Commands:**
    Execute the application using the `java TaskCLI` command followed by the action and arguments.

---

## ✨ Available Commands and Usage

All commands use positional arguments. Descriptions containing spaces **must be enclosed in double quotes.**

| Command | Usage Example | Output Example |
| :--- | :--- | :--- |
| **Add** | `java TaskCLI add "Buy groceries"` | `Task added successfully (ID: 1)` |
| **Update** | `java TaskCLI update 1 "Buy groceries and cook dinner"` | `Task 1 updated successfully.` |
| **Delete** | `java TaskCLI delete 1` | `Task 1 deleted successfully.` |
| **Mark In-Progress** | `java TaskCLI mark-in-progress 2` | `Task 2 marked as in progress.` |
| **Mark Done** | `java TaskCLI mark-done 2` | `Task 2 marked as done.` |
| **List All** | `java TaskCLI list` | `[1] [TODO] Buy groceries...` |
| **List Done** | `java TaskCLI list done` | `[2] [DONE] Finish project...` |
| **List Todo** | `java TaskCLI list todo` | `No tasks found.` |

---

## 💾 Task Properties and Storage

Tasks are stored in the local `tasks.json` file. Each task object includes:

* `id`: Unique identifier (auto-incrementing)
* `description`: The task description
* `status`: One of (`todo`, `in-progress`, `done`)
* `createdAt`: Timestamp of creation
* `updatedAt`: Timestamp of last modification

### Project Page URL
**`https://roadmap.sh/projects/task-tracker**
