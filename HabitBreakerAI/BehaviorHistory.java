import java.util.List;

/**
 * BehaviorHistory.java
 * 
 * BEHAVIOR MEMORY SYSTEM — tracks long-term behavior patterns.
 * 
 * Stores:
 *   - Total tasks, delayed tasks, completed tasks
 *   - Historical snapshots for trend detection
 * 
 * Detects:
 *   - "Declining Productivity" → delayed tasks increasing
 *   - "Improving Behavior"     → completed tasks increasing
 *   - "Stable Performance"     → consistent rates
 * 
 * Demonstrates ENCAPSULATION through private fields.
 */
public class BehaviorHistory {

    // ── Private fields (Encapsulation) ──────────────────────────────────
    private int totalTasks;
    private int completedTasks;
    private int delayedTasks;
    private int incompleteTasks;
    private int onTimeTasks;

    // ── Historical snapshots for trend detection ────────────────────────
    // We track the last 3 "check points" to detect trends
    private int[] delayHistory;     // last 3 delay counts
    private int[] completedHistory; // last 3 completed counts
    private int historyIndex;
    private int snapshotsTaken;

    // ── Behavior state ─────────────────────────────────────────────────
    private String behaviorTrend;   // IMPROVING, DECLINING, STABLE
    private int consecutiveDelays;

    public BehaviorHistory() {
        totalTasks = 0;
        completedTasks = 0;
        delayedTasks = 0;
        incompleteTasks = 0;
        onTimeTasks = 0;
        delayHistory = new int[3];
        completedHistory = new int[3];
        historyIndex = 0;
        snapshotsTaken = 0;
        behaviorTrend = "STABLE";
        consecutiveDelays = 0;
    }

    // ════════════════════════════════════════════════════════════════════
    //  updateFromTasks  –  Refresh all statistics from task list
    // ════════════════════════════════════════════════════════════════════
    /**
     * Recalculate all behavior statistics from the current task list.
     * Call this after each task completion/analysis.
     */
    public void updateFromTasks(List<Task> tasks) {
        totalTasks = tasks.size();
        completedTasks = 0;
        delayedTasks = 0;
        incompleteTasks = 0;
        onTimeTasks = 0;
        consecutiveDelays = 0;

        // Count from most recent tasks to detect consecutive delays
        for (int i = tasks.size() - 1; i >= 0; i--) {
            Task t = tasks.get(i);
            switch (t.getStatus()) {
                case Task.COMPLETED:
                    completedTasks++;
                    if (t.isDelayed()) {
                        delayedTasks++;
                        if (i >= tasks.size() - 5) consecutiveDelays++;
                    } else {
                        onTimeTasks++;
                    }
                    break;
                case Task.INCOMPLETE:
                    incompleteTasks++;
                    if (i >= tasks.size() - 5) consecutiveDelays++;
                    break;
            }
        }

        // Take a snapshot for trend detection
        takeSnapshot();

        // Analyze trend
        analyzeTrend();
    }

    // ════════════════════════════════════════════════════════════════════
    //  Trend Detection
    // ════════════════════════════════════════════════════════════════════

    /**
     * Store current delay/completed counts as a historical snapshot.
     */
    private void takeSnapshot() {
        delayHistory[historyIndex] = delayedTasks;
        completedHistory[historyIndex] = onTimeTasks;
        historyIndex = (historyIndex + 1) % 3;
        snapshotsTaken++;
    }

    /**
     * Analyze trend across snapshots.
     */
    private void analyzeTrend() {
        if (snapshotsTaken < 2) {
            behaviorTrend = "STABLE";
            return;
        }

        int prevIdx = (historyIndex - 2 + 3) % 3;
        int currIdx = (historyIndex - 1 + 3) % 3;

        int prevDelays    = delayHistory[prevIdx];
        int currDelays    = delayHistory[currIdx];
        int prevCompleted = completedHistory[prevIdx];
        int currCompleted = completedHistory[currIdx];

        if (currDelays > prevDelays && currCompleted <= prevCompleted) {
            behaviorTrend = "DECLINING";
        } else if (currCompleted > prevCompleted && currDelays <= prevDelays) {
            behaviorTrend = "IMPROVING";
        } else {
            behaviorTrend = "STABLE";
        }
    }

    // ════════════════════════════════════════════════════════════════════
    //  Strict Mode Level  –  Dynamic, adaptive
    // ════════════════════════════════════════════════════════════════════
    /**
     * Determine the current strict mode level:
     *   0 → Normal (no issues)
     *   1 → Focus Suggested (2 delays)
     *   2 → STRICT MODE (3 delays)
     *   3 → HARD STRICT MODE (5+ delays)
     */
    public int getStrictModeLevel() {
        int signals = delayedTasks + incompleteTasks;
        if (signals >= 5) return 3;   // HARD STRICT
        if (signals >= 3) return 2;   // STRICT
        if (signals >= 2) return 1;   // FOCUS SUGGESTED
        return 0;                      // NORMAL
    }

    /**
     * Get strict mode label.
     */
    public String getStrictModeLabel() {
        switch (getStrictModeLevel()) {
            case 3: return "🔴 HARD STRICT MODE";
            case 2: return "🟠 STRICT MODE";
            case 1: return "🟡 FOCUS SUGGESTED";
            default: return "🟢 NORMAL";
        }
    }

    // ════════════════════════════════════════════════════════════════════
    //  Display Report
    // ════════════════════════════════════════════════════════════════════

    public String getFullReport() {
        StringBuilder sb = new StringBuilder();

        sb.append("\n╔══════════════════════════════════════════════════════════════════╗\n");
        sb.append("║              🧠  BEHAVIOR MEMORY SYSTEM  🧠                     ║\n");
        sb.append("╚══════════════════════════════════════════════════════════════════╝\n\n");

        // ── Statistics ──────────────────────────────────────────────────
        sb.append("   ── 📊 Lifetime Statistics ───────────────────────────────────\n\n");
        sb.append("   📋 Total Tasks:          ").append(totalTasks).append("\n");
        sb.append("   ✅ Completed On Time:     ").append(onTimeTasks).append("\n");
        sb.append("   ⚠ Completed Delayed:     ").append(delayedTasks).append("\n");
        sb.append("   ❌ Incomplete (Avoided):   ").append(incompleteTasks).append("\n");

        // ── Completion rate ─────────────────────────────────────────────
        if (totalTasks > 0) {
            double completionRate = (completedTasks * 100.0) / totalTasks;
            sb.append(String.format("\n   📈 Completion Rate: %.1f%%\n", completionRate));
        }

        // ── Behavior trend ──────────────────────────────────────────────
        sb.append("\n   ── 📈 Behavior Trend ────────────────────────────────────────\n\n");
        switch (behaviorTrend) {
            case "IMPROVING":
                sb.append("   📈 IMPROVING BEHAVIOR\n");
                sb.append("   Your completed tasks are increasing and delays are reducing.\n");
                sb.append("   🌟 Great progress — keep building this positive momentum!\n");
                break;
            case "DECLINING":
                sb.append("   📉 DECLINING PRODUCTIVITY\n");
                sb.append("   Your delayed tasks are increasing over time.\n");
                sb.append("   ⚠ Warning: You're slipping into procrastination patterns.\n");
                sb.append("   ➜ Take immediate corrective action!\n");
                break;
            case "STABLE":
                sb.append("   ➡ STABLE PERFORMANCE\n");
                sb.append("   Your behavior is consistent — no major changes detected.\n");
                if (delayedTasks > onTimeTasks) {
                    sb.append("   ⚠ But delays outnumber on-time completions — improve!\n");
                } else {
                    sb.append("   ✅ You're maintaining a good rhythm.\n");
                }
                break;
        }

        // ── Strict mode status ──────────────────────────────────────────
        sb.append("\n   ── 🚦 Mode Status ───────────────────────────────────────────\n\n");
        sb.append("   Current Mode: ").append(getStrictModeLabel()).append("\n");

        int signals = delayedTasks + incompleteTasks;
        if (getStrictModeLevel() == 0) {
            sb.append("   ✅ No procrastination signals. Keep it up!\n");
        } else if (getStrictModeLevel() == 1) {
            sb.append("   ⚠ ").append(signals).append(" signal(s). Focus sessions recommended.\n");
        } else if (getStrictModeLevel() == 2) {
            sb.append("   🚨 ").append(signals).append(" signals! Strict schedule enforced.\n");
        } else {
            sb.append("   🔴 ").append(signals).append(" signals! Maximum restriction active.\n");
            sb.append("   No breaks until a task is completed on time.\n");
        }

        sb.append("\n");
        return sb.toString();
    }

    // ── Getters ─────────────────────────────────────────────────────────

    public int getTotalTasks()       { return totalTasks; }
    public int getCompletedTasks()   { return completedTasks; }
    public int getDelayedTasks()     { return delayedTasks; }
    public int getIncompleteTasks()  { return incompleteTasks; }
    public int getOnTimeTasks()      { return onTimeTasks; }
    public String getBehaviorTrend() { return behaviorTrend; }
    public int getConsecutiveDelays(){ return consecutiveDelays; }

    /**
     * Check if behavior is improving.
     */
    public boolean isImproving() {
        return behaviorTrend.equals("IMPROVING");
    }

    /**
     * Check if behavior is declining.
     */
    public boolean isDeclining() {
        return behaviorTrend.equals("DECLINING");
    }
}
