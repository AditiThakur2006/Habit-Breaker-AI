import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

/**
 * Task.java
 * 
 * Represents a single task with automatic time tracking.
 * Demonstrates ENCAPSULATION — all fields are private with getters/setters.
 * 
 * The system records start/end times automatically using LocalDateTime.
 * The user NEVER enters actual time — it is always calculated.
 * 
 * Fields:
 *   - taskName           : name of the task
 *   - expectedTimeMinutes: how long the user expects to take (in minutes)
 *   - startTime          : auto-recorded when task starts
 *   - endTime            : auto-recorded when task stops
 *   - actualTimeMinutes  : auto-calculated (endTime - startTime)
 *   - status             : NOT_STARTED / IN_PROGRESS / COMPLETED / INCOMPLETE
 *   - startHour          : hour of day when task was started (for insight analysis)
 *   - priority           : HIGH / MEDIUM / LOW (auto-set based on energy)
 */
public class Task {

    // ── Private fields (Encapsulation) ──────────────────────────────────
    private String taskName;
    private double expectedTimeMinutes;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double actualTimeMinutes;
    private String status;
    private int startHour;       // Hour of day when started (0–23)
    private String priority;     // HIGH, MEDIUM, LOW

    // Status constants
    public static final String NOT_STARTED  = "NOT_STARTED";
    public static final String IN_PROGRESS  = "IN_PROGRESS";
    public static final String COMPLETED    = "COMPLETED";
    public static final String INCOMPLETE   = "INCOMPLETE";

    // Priority constants
    public static final String PRIORITY_HIGH   = "HIGH";
    public static final String PRIORITY_MEDIUM = "MEDIUM";
    public static final String PRIORITY_LOW    = "LOW";

    // Formatter for displaying times
    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("hh:mm:ss a");

    // ── Constructor ─────────────────────────────────────────────────────
    public Task(String taskName, double expectedTimeMinutes) {
        this.taskName = taskName;
        this.expectedTimeMinutes = expectedTimeMinutes;
        this.startTime = null;
        this.endTime = null;
        this.actualTimeMinutes = 0;
        this.status = NOT_STARTED;
        this.startHour = -1;
        this.priority = PRIORITY_MEDIUM;
    }

    // ════════════════════════════════════════════════════════════════════
    //  Core Methods — Automatic Time Tracking
    // ════════════════════════════════════════════════════════════════════

    /**
     * Start the task — records the current time automatically.
     * Uses LocalDateTime.now() — NO manual input.
     */
    public void startTask() {
        this.startTime = LocalDateTime.now();
        this.startHour = startTime.getHour();
        this.status = IN_PROGRESS;
        System.out.println("\n   ▶ Task \"" + taskName + "\" started at "
                + startTime.format(TIME_FMT));
        System.out.println("   ⏱ Expected duration: " + formatMinutes(expectedTimeMinutes));
        System.out.println("   📌 Status: IN PROGRESS");
        System.out.println("   ⚡ Priority: " + priority);
    }

    /**
     * Stop/complete the task — records end time and calculates duration.
     * actualTime = endTime - startTime (automatic calculation).
     */
    public void endTask() {
        if (startTime == null) {
            System.out.println("   ⚠ Task has not been started yet!");
            return;
        }
        this.endTime = LocalDateTime.now();
        this.status = COMPLETED;
        calculateActualTime();

        System.out.println("\n   ⏹ Task \"" + taskName + "\" stopped at "
                + endTime.format(TIME_FMT));
        System.out.println("   ⏱ Actual time taken: " + formatMinutes(actualTimeMinutes));

        if (isDelayed()) {
            double overBy = actualTimeMinutes - expectedTimeMinutes;
            System.out.println("   ⚠ DELAYED — exceeded expected time by "
                    + formatMinutes(overBy));
        } else {
            System.out.println("   ✅ Completed within expected time!");
        }
    }

    /**
     * Mark task as INCOMPLETE (started but never finished / abandoned).
     */
    public void markIncomplete() {
        if (startTime != null) {
            this.endTime = LocalDateTime.now();
            calculateActualTime();
        }
        this.status = INCOMPLETE;
    }

    /**
     * Calculate actual time taken in minutes.
     * Uses Duration.between() — fully automatic, no user input.
     */
    public void calculateActualTime() {
        if (startTime != null && endTime != null) {
            Duration duration = Duration.between(startTime, endTime);
            this.actualTimeMinutes = duration.toMillis() / 60000.0;
        }
    }

    /**
     * Check if the task took longer than expected.
     */
    public boolean isDelayed() {
        return actualTimeMinutes > expectedTimeMinutes;
    }

    /**
     * Get the elapsed time in minutes (for in-progress tasks).
     */
    public double getElapsedMinutes() {
        if (startTime == null) return 0;
        LocalDateTime end = (endTime != null) ? endTime : LocalDateTime.now();
        Duration duration = Duration.between(startTime, end);
        return duration.toMillis() / 60000.0;
    }

    /**
     * Get the time period when this task was started.
     * Used for behavior insights.
     */
    public String getStartPeriod() {
        if (startHour < 0) return "UNKNOWN";
        if (startHour >= 6 && startHour < 12) return "MORNING";
        if (startHour >= 12 && startHour < 17) return "AFTERNOON";
        if (startHour >= 17 && startHour < 21) return "EVENING";
        return "NIGHT";
    }

    // ── Getters (Encapsulation) ─────────────────────────────────────────

    public String getTaskName()            { return taskName; }
    public double getExpectedTimeMinutes() { return expectedTimeMinutes; }
    public LocalDateTime getStartTime()    { return startTime; }
    public LocalDateTime getEndTime()      { return endTime; }
    public double getActualTimeMinutes()   { return actualTimeMinutes; }
    public String getStatus()              { return status; }
    public int getStartHour()              { return startHour; }
    public String getPriority()            { return priority; }

    // ── Setters with validation (Encapsulation) ─────────────────────────

    public void setTaskName(String taskName) {
        if (taskName != null && !taskName.trim().isEmpty()) {
            this.taskName = taskName.trim();
        }
    }

    public void setExpectedTimeMinutes(double minutes) {
        if (minutes > 0) this.expectedTimeMinutes = minutes;
    }

    public void setStatus(String status) { this.status = status; }

    public void setPriority(String priority) { this.priority = priority; }

    // ── Display helpers ─────────────────────────────────────────────────

    public static String formatMinutes(double minutes) {
        if (minutes < 0) minutes = 0;
        int totalSeconds = (int) Math.round(minutes * 60);
        int hrs  = totalSeconds / 3600;
        int mins = (totalSeconds % 3600) / 60;
        int secs = totalSeconds % 60;

        if (hrs > 0)       return String.format("%dh %dm %ds", hrs, mins, secs);
        else if (mins > 0) return String.format("%dm %ds", mins, secs);
        else               return String.format("%ds", secs);
    }

    public String getStatusEmoji() {
        switch (status) {
            case NOT_STARTED: return "⬜";
            case IN_PROGRESS: return "🔄";
            case COMPLETED:   return isDelayed() ? "⚠" : "✅";
            case INCOMPLETE:  return "❌";
            default:          return "❓";
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getStatusEmoji()).append(" ").append(taskName);
        sb.append("  |  Expected: ").append(formatMinutes(expectedTimeMinutes));

        if (status.equals(IN_PROGRESS)) {
            sb.append("  |  ⏱ Running: ").append(formatMinutes(getElapsedMinutes()));
        } else if (status.equals(COMPLETED) || status.equals(INCOMPLETE)) {
            sb.append("  |  Actual: ").append(formatMinutes(actualTimeMinutes));
            if (isDelayed()) sb.append("  |  🔴 DELAYED");
            else if (status.equals(COMPLETED)) sb.append("  |  🟢 ON TIME");
        }

        sb.append("  |  [").append(status).append("]");
        if (!priority.equals(PRIORITY_MEDIUM)) {
            sb.append("  |  ⚡").append(priority);
        }
        return sb.toString();
    }
}
