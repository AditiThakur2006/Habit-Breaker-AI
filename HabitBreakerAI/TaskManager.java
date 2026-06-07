import java.util.ArrayList;
import java.util.List;

/**
 * TaskManager.java
 * 
 * Manages all tasks and enforces the rule:
 *   → Only ONE task can be active (IN_PROGRESS) at a time.
 * 
 * Responsibilities:
 *   - Store tasks in ArrayList (daily tracking)
 *   - Add new tasks with auto-assigned priority based on energy
 *   - Start a task (auto-record start time)
 *   - Stop a task (auto-record end time + calculate duration)
 *   - Track the currently active task
 *   - Prevent starting multiple tasks simultaneously
 * 
 * Demonstrates ENCAPSULATION through private fields and controlled access.
 */
public class TaskManager {

    // ── Private fields (Encapsulation) ──────────────────────────────────
    private List<Task> tasks;
    private Task activeTask;

    public TaskManager() {
        tasks = new ArrayList<>();
        activeTask = null;
    }

    // ════════════════════════════════════════════════════════════════════
    //  addTask
    // ════════════════════════════════════════════════════════════════════
    public void addTask(String name, double expectedMinutes) {
        Task task = new Task(name, expectedMinutes);

        // Auto-assign priority based on current energy level (time-based)
        String energy = EnergySimulator.getCurrentEnergy();
        if (energy.equals("HIGH")) {
            task.setPriority(Task.PRIORITY_HIGH);
        } else if (energy.equals("MEDIUM")) {
            task.setPriority(Task.PRIORITY_MEDIUM);
        } else {
            task.setPriority(Task.PRIORITY_LOW);
        }

        tasks.add(task);
        System.out.println("\n   ✅ Task \"" + name + "\" added successfully!");
        System.out.println("   ⏱ Expected time: " + Task.formatMinutes(expectedMinutes));
        System.out.println("   ⚡ Auto-assigned priority: " + task.getPriority()
                + " (based on current energy)");
        System.out.println("   📋 Task #" + tasks.size() + " in your list.");
    }

    // ════════════════════════════════════════════════════════════════════
    //  startTask  –  Auto-records start time
    // ════════════════════════════════════════════════════════════════════
    public boolean startTask(int index) {
        if (index < 1 || index > tasks.size()) {
            System.out.println("\n   ⚠ Invalid task number. Use 1–" + tasks.size());
            return false;
        }

        Task task = tasks.get(index - 1);

        if (task.getStatus().equals(Task.COMPLETED)) {
            System.out.println("\n   ⚠ Task \"" + task.getTaskName()
                    + "\" is already completed.");
            return false;
        }
        if (task.getStatus().equals(Task.IN_PROGRESS)) {
            System.out.println("\n   ⚠ Task \"" + task.getTaskName()
                    + "\" is already running!");
            return false;
        }

        // Enforce: only one active task at a time
        if (activeTask != null) {
            System.out.println("\n   🚫 Cannot start! Another task is already running:");
            System.out.println("      \"" + activeTask.getTaskName() + "\" (in progress)");
            System.out.println("      → Stop it first before starting a new one.");
            return false;
        }

        task.startTask();
        activeTask = task;
        return true;
    }

    // ════════════════════════════════════════════════════════════════════
    //  stopTask  –  Auto-records end time + calculates duration
    // ════════════════════════════════════════════════════════════════════
    public boolean stopTask() {
        if (activeTask == null) {
            System.out.println("\n   ⚠ No task is currently running.");
            return false;
        }
        activeTask.endTask();
        activeTask = null;
        return true;
    }

    public boolean stopTask(int index) {
        if (index < 1 || index > tasks.size()) {
            System.out.println("\n   ⚠ Invalid task number.");
            return false;
        }
        Task task = tasks.get(index - 1);
        if (!task.getStatus().equals(Task.IN_PROGRESS)) {
            System.out.println("\n   ⚠ Task \"" + task.getTaskName()
                    + "\" is not currently running.");
            return false;
        }
        task.endTask();
        if (activeTask == task) activeTask = null;
        return true;
    }

    // ════════════════════════════════════════════════════════════════════
    //  markIncomplete  –  Avoidance behavior
    // ════════════════════════════════════════════════════════════════════
    public void markIncomplete(int index) {
        if (index < 1 || index > tasks.size()) {
            System.out.println("\n   ⚠ Invalid task number.");
            return;
        }
        Task task = tasks.get(index - 1);
        task.markIncomplete();
        if (activeTask == task) activeTask = null;
        System.out.println("\n   ❌ Task \"" + task.getTaskName()
                + "\" marked as INCOMPLETE.");
    }

    // ════════════════════════════════════════════════════════════════════
    //  Display
    // ════════════════════════════════════════════════════════════════════
    public void displayAllTasks() {
        if (tasks.isEmpty()) {
            System.out.println("\n   ⚠ No tasks added yet.");
            return;
        }

        System.out.println("\n╔══════════════════════════════════════════════════════════════════╗");
        System.out.println("║                      📋  ALL TASKS  📋                          ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════╝\n");

        // Show current energy
        System.out.println("   ⚡ Current Energy Level: " + EnergySimulator.getCurrentEnergy()
                + " (" + EnergySimulator.getTimePeriod() + ")\n");

        for (int i = 0; i < tasks.size(); i++) {
            System.out.println("   " + (i + 1) + ". " + tasks.get(i).toString());
        }

        if (activeTask != null) {
            System.out.println("\n   🔄 Active: \"" + activeTask.getTaskName()
                    + "\" (running for " + Task.formatMinutes(activeTask.getElapsedMinutes()) + ")");
        }

        // Summary
        int completed = 0, delayed = 0, incomplete = 0, notStarted = 0, inProgress = 0;
        for (Task t : tasks) {
            switch (t.getStatus()) {
                case Task.COMPLETED:   completed++; if (t.isDelayed()) delayed++; break;
                case Task.INCOMPLETE:  incomplete++; break;
                case Task.NOT_STARTED: notStarted++; break;
                case Task.IN_PROGRESS: inProgress++; break;
            }
        }
        System.out.println("\n   ── Summary ───────────────────────────────────────────────");
        System.out.println("   Total: " + tasks.size()
                + "  |  ✅ Done: " + completed
                + "  |  🔄 Running: " + inProgress
                + "  |  ⬜ Pending: " + notStarted);
        System.out.println("   ⚠ Delayed: " + delayed + "  |  ❌ Incomplete: " + incomplete);
        System.out.println();
    }

    // ── Getters ─────────────────────────────────────────────────────────

    public List<Task> getTasks()       { return tasks; }
    public Task getActiveTask()        { return activeTask; }
    public boolean hasActiveTask()     { return activeTask != null; }
    public int getTaskCount()          { return tasks.size(); }
}
