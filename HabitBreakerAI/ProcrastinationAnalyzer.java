import java.util.List;

/**
 * ProcrastinationAnalyzer.java
 * 
 * Inherits from HabitAnalyzer (Inheritance).
 * Overrides analyzeTasks() with procrastination-specific logic (Polymorphism).
 * 
 * Detects:
 *   1. DELAY       → actualTime > expectedTime
 *   2. AVOIDANCE   → task started but not completed (INCOMPLETE)
 *   3. PATTERN     → repeated delays
 *   4. CONSISTENCY → tasks completed on time
 * 
 * Integrates with BehaviorHistory for trend detection
 * and dynamic strict mode levels.
 */
public class ProcrastinationAnalyzer extends HabitAnalyzer {

    // ════════════════════════════════════════════════════════════════════
    //  analyzeTasks  –  Full behavior analysis (Polymorphism)
    // ════════════════════════════════════════════════════════════════════
    @Override
    public String analyzeTasks(List<Task> tasks, BehaviorHistory history) {
        if (tasks.isEmpty()) {
            return "   ⚠ No tasks to analyze. Add and complete tasks first.\n";
        }

        // Update behavior history from current tasks
        history.updateFromTasks(tasks);

        StringBuilder report = new StringBuilder();

        // ── Count statistics ────────────────────────────────────────────
        int total = tasks.size(), completed = 0, delayed = 0;
        int incomplete = 0, onTime = 0, notStarted = 0, inProgress = 0;

        for (Task t : tasks) {
            switch (t.getStatus()) {
                case Task.COMPLETED:
                    completed++;
                    if (t.isDelayed()) delayed++;
                    else onTime++;
                    break;
                case Task.INCOMPLETE:  incomplete++; break;
                case Task.NOT_STARTED: notStarted++; break;
                case Task.IN_PROGRESS: inProgress++; break;
            }
        }

        // ── Report header ───────────────────────────────────────────────
        report.append("\n╔══════════════════════════════════════════════════════════════════╗\n");
        report.append("║              🔍  BEHAVIOR ANALYSIS REPORT  🔍                   ║\n");
        report.append("╚══════════════════════════════════════════════════════════════════╝\n\n");

        // ── Current energy level ────────────────────────────────────────
        report.append("   ").append(EnergySimulator.getEnergyBanner()).append("\n\n");

        // ── Task-by-task breakdown ──────────────────────────────────────
        report.append("── 📋 Task Breakdown ──────────────────────────────────────────\n\n");

        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.get(i);
            report.append("   ").append(i + 1).append(". ").append(t.toString()).append("\n");

            if (t.getStatus().equals(Task.COMPLETED)) {
                if (t.isDelayed()) {
                    double overBy = t.getActualTimeMinutes() - t.getExpectedTimeMinutes();
                    report.append("      → ⚠ DELAY: Exceeded by ")
                          .append(Task.formatMinutes(overBy)).append("\n");
                } else {
                    report.append("      → ✅ On time\n");
                }
            } else if (t.getStatus().equals(Task.INCOMPLETE)) {
                report.append("      → ❌ AVOIDANCE: Task abandoned\n");
            } else if (t.getStatus().equals(Task.IN_PROGRESS)) {
                report.append("      → 🔄 Running...\n");
            }
        }

        // ── Statistics ──────────────────────────────────────────────────
        report.append("\n── 📊 Statistics ──────────────────────────────────────────────\n\n");
        report.append("   📋 Total Tasks:          ").append(total).append("\n");
        report.append("   ✅ Completed On Time:     ").append(onTime).append("\n");
        report.append("   ⚠ Completed Delayed:     ").append(delayed).append("\n");
        report.append("   ❌ Incomplete (Avoided):   ").append(incomplete).append("\n");
        report.append("   ⬜ Not Started:           ").append(notStarted).append("\n");
        report.append("   🔄 In Progress:           ").append(inProgress).append("\n");

        // ── Behavior Detection ──────────────────────────────────────────
        report.append("\n── 🧠 Behavior Detection ──────────────────────────────────────\n\n");

        if (delayed > 0) {
            report.append("   ⚠ DELAY DETECTED: ").append(delayed)
                  .append(" task(s) took longer than expected.\n");
        }
        if (incomplete > 0) {
            report.append("   ❌ AVOIDANCE DETECTED: ").append(incomplete)
                  .append(" task(s) abandoned.\n");
        }

        int procSignals = delayed + incomplete;

        // ── Dynamic Strict Mode ─────────────────────────────────────────
        int strictLevel = history.getStrictModeLevel();
        report.append("\n   🚦 Mode: ").append(history.getStrictModeLabel()).append("\n");

        if (strictLevel == 1) {
            report.append("   ⚠ ").append(procSignals)
                  .append(" signals — focus sessions recommended.\n");
        } else if (strictLevel == 2) {
            report.append("\n   🚨 ═══ STRICT MODE ACTIVATED ═══════════════════════════\n");
            report.append("      ").append(procSignals)
                  .append(" procrastination signals — structured schedule enforced!\n");
            report.append("   🚨 ══════════════════════════════════════════════════════\n");
        } else if (strictLevel == 3) {
            report.append("\n   🔴 ═══ HARD STRICT MODE — MAXIMUM RESTRICTION ═════════\n");
            report.append("      ").append(procSignals)
                  .append(" signals — SEVERE procrastination detected!\n");
            report.append("      ⛔ No breaks allowed. Only high-priority tasks.\n");
            report.append("      Complete at least ONE task on time to exit this mode.\n");
            report.append("   🔴 ══════════════════════════════════════════════════════\n");
        }

        // ── Behavior trend ──────────────────────────────────────────────
        report.append("\n── 📈 Behavior Trend ──────────────────────────────────────────\n\n");
        if (history.isImproving()) {
            report.append("   📈 IMPROVING — your productivity is trending upward!\n");
        } else if (history.isDeclining()) {
            report.append("   📉 DECLINING — your delays are increasing. Take action!\n");
        } else {
            report.append("   ➡ STABLE — consistent performance. Push for improvement!\n");
        }

        // ── Consistency (positive) ──────────────────────────────────────
        if (completed > 0 && delayed == 0 && incomplete == 0) {
            report.append("\n   🌟 EXCELLENT CONSISTENCY! All tasks completed on time!\n");
        }

        if (notStarted > 0) {
            report.append("\n   ⬜ ").append(notStarted)
                  .append(" task(s) still pending. Don't keep postponing!\n");
        }

        report.append("\n");
        return report.toString();
    }

    // ── Helper methods for SmartDecisionEngine ──────────────────────────

    public int countProcrastinationSignals(List<Task> tasks) {
        int count = 0;
        for (Task t : tasks) {
            if (t.getStatus().equals(Task.COMPLETED) && t.isDelayed()) count++;
            if (t.getStatus().equals(Task.INCOMPLETE)) count++;
        }
        return count;
    }

    public int countOnTime(List<Task> tasks) {
        int count = 0;
        for (Task t : tasks) {
            if (t.getStatus().equals(Task.COMPLETED) && !t.isDelayed()) count++;
        }
        return count;
    }
}
