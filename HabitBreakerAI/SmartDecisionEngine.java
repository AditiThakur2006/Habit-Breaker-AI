import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * SmartDecisionEngine.java
 * 
 * Implements DecisionEngine interface — the BRAIN of the application.
 * 
 * SMART RECOMMENDATION ENGINE — combines:
 *   - Delay detection
 *   - Pattern analysis
 *   - Auto Energy level (from EnergySimulator)
 *   - Behavior history trends
 * 
 * Features:
 *   - Intelligent action suggestions
 *   - Dynamic strict mode (adaptive, not just ON/OFF)
 *   - Pomodoro focus sessions (via FocusManager)
 *   - Productivity scoring (0–100)
 *   - Behavior insights (time-of-day analysis)
 *   - Motivational messages
 */
public class SmartDecisionEngine implements DecisionEngine {

    private ProcrastinationAnalyzer analyzer = new ProcrastinationAnalyzer();
    private FocusManager focusManager = new FocusManager();

    // ════════════════════════════════════════════════════════════════════
    //  suggestAction  –  Core AI Decision Logic
    // ════════════════════════════════════════════════════════════════════
    @Override
    public String suggestAction(List<Task> tasks, BehaviorHistory history) {
        if (tasks.isEmpty()) {
            return "   ⚠ No tasks recorded. Add tasks to get suggestions.\n";
        }

        // Update history
        history.updateFromTasks(tasks);

        StringBuilder sb = new StringBuilder();
        String energy   = EnergySimulator.getCurrentEnergy();
        int strictLevel = history.getStrictModeLevel();
        boolean isImproving = history.isImproving();
        boolean isDeclining = history.isDeclining();

        int delayed = 0, incomplete = 0, onTime = 0, notStarted = 0;
        for (Task t : tasks) {
            if (t.getStatus().equals(Task.COMPLETED) && t.isDelayed()) delayed++;
            if (t.getStatus().equals(Task.COMPLETED) && !t.isDelayed()) onTime++;
            if (t.getStatus().equals(Task.INCOMPLETE)) incomplete++;
            if (t.getStatus().equals(Task.NOT_STARTED)) notStarted++;
        }
        int procSignals = delayed + incomplete;

        sb.append("\n╔══════════════════════════════════════════════════════════════════╗\n");
        sb.append("║              💡  SMART SUGGESTIONS  💡                          ║\n");
        sb.append("╚══════════════════════════════════════════════════════════════════╝\n\n");

        // ── Current energy ──────────────────────────────────────────────
        sb.append("   ").append(EnergySimulator.getEnergyBanner()).append("\n\n");

        // ════════════════════════════════════════════════════════════════
        //  SMART RECOMMENDATION ENGINE
        //  Combines: Energy + Delays + Patterns + History
        // ════════════════════════════════════════════════════════════════

        sb.append("── 🧠 Smart Recommendations ───────────────────────────────────\n\n");

        // Rule: HIGH energy + procrastination → push hard
        if (energy.equals("HIGH") && procSignals > 0) {
            sb.append("   ⚡ HIGH Energy + Procrastination detected:\n");
            sb.append("   📌 NOW is the best time to tackle your hardest task!\n");
            sb.append("   → Study a difficult subject or solve challenging problems.\n");
            sb.append("   → Your energy is at its peak — don't waste it!\n\n");
        }
        // Rule: HIGH energy + no issues → challenge yourself
        else if (energy.equals("HIGH") && procSignals == 0) {
            sb.append("   ⚡ HIGH Energy + Good Performance:\n");
            sb.append("   📌 You're doing great! Challenge yourself with advanced tasks.\n");
            sb.append("   → Try reducing expected times to push your limits.\n\n");
        }

        // Rule: MEDIUM energy + delays → moderate recovery
        if (energy.equals("MEDIUM") && delayed > 0) {
            sb.append("   🌤 MEDIUM Energy + Delays found:\n");
            sb.append("   📌 Do revision or moderate tasks to build momentum.\n");
            sb.append("   → Complete one easy task first to gain confidence.\n\n");
        }

        // Rule: LOW energy + delays → short focus
        if (energy.equals("LOW") && delayed > 0) {
            sb.append("   🌙 LOW Energy + Delays found:\n");
            sb.append("   📌 Do a short focus session (15–20 min max).\n");
            sb.append("   → Light work: review notes, plan tomorrow's tasks.\n");
            sb.append("   → Don't force heavy work — rest is productive too.\n\n");
        }
        // Rule: LOW energy + no issues → light maintenance
        else if (energy.equals("LOW") && procSignals == 0) {
            sb.append("   🌙 LOW Energy + On Track:\n");
            sb.append("   📌 Great job today! Wind down with light reading or planning.\n\n");
        }

        // Rule: Performance improving → motivate
        if (isImproving) {
            sb.append("   📈 POSITIVE TREND — Your productivity is improving!\n");
            sb.append("   🌟 Keep this momentum going. You're building real habits!\n\n");
        }

        // Rule: Performance declining → urgent action
        if (isDeclining) {
            sb.append("   📉 WARNING — Declining productivity detected!\n");
            sb.append("   ⚠ Your delays are increasing over time.\n");
            sb.append("   ➜ Break the cycle NOW — start with a 5-minute commit.\n\n");
        }

        // Rule: Incomplete tasks → address avoidance
        if (incomplete > 0) {
            sb.append("── ❌ Avoidance Recovery ───────────────────────────────────────\n\n");
            sb.append("   ❌ ").append(incomplete).append(" task(s) abandoned.\n\n");
            sb.append("   💡 Overcome avoidance:\n");
            sb.append("   • Don't aim for perfection — aim for progress\n");
            sb.append("   • Start with just 5 minutes — momentum will follow\n");
            sb.append("   • Reward yourself after completing a tough task\n\n");
        }

        // Rule: Not-started tasks → prioritize
        if (notStarted > 0) {
            sb.append("── ⬜ Pending Tasks ────────────────────────────────────────────\n\n");
            sb.append("   ⬜ ").append(notStarted).append(" task(s) not started.\n\n");
            if (energy.equals("HIGH")) {
                sb.append("   💡 Your energy is HIGH — start the hardest one NOW!\n\n");
            } else if (energy.equals("LOW")) {
                sb.append("   💡 Energy is low — start the EASIEST one to build momentum.\n\n");
            } else {
                sb.append("   💡 Pick one and commit. Action beats planning!\n\n");
            }
        }

        // Rule: All on time → celebrate
        if (onTime > 0 && delayed == 0 && incomplete == 0 && notStarted == 0) {
            sb.append("── 🌟 Outstanding Performance ─────────────────────────────────\n\n");
            sb.append("   🏆 ALL tasks completed on time! You're crushing it!\n");
            sb.append("   → Challenge yourself with harder tasks tomorrow.\n");
            sb.append("   → Try tighter deadlines to sharpen your focus.\n\n");
        }

        // ── Dynamic Focus Session ───────────────────────────────────────
        sb.append(focusManager.suggestFocusSession(strictLevel));

        // ── Productivity Score ──────────────────────────────────────────
        int score = calculateProductivityScore(tasks);
        sb.append("── 🏆 Productivity Score ──────────────────────────────────────\n\n");
        sb.append(getScoreBar(score)).append("\n");
        sb.append(getScoreLabel(score)).append("\n");
        sb.append(getGrade(score)).append("\n\n");
        sb.append(getMotivationalMessage(score, isImproving, isDeclining)).append("\n\n");

        return sb.toString();
    }

    // ════════════════════════════════════════════════════════════════════
    //  generateInsights  –  Behavior Insights (time-of-day analysis)
    // ════════════════════════════════════════════════════════════════════
    /**
     * Analyze WHEN delays happen most and generate insights.
     * Checks what time of day tasks are delayed/completed.
     */
    public String generateInsights(List<Task> tasks, BehaviorHistory history) {
        if (tasks.isEmpty()) {
            return "   ⚠ No tasks to analyze for insights.\n";
        }

        history.updateFromTasks(tasks);

        StringBuilder sb = new StringBuilder();

        sb.append("\n╔══════════════════════════════════════════════════════════════════╗\n");
        sb.append("║              📊  BEHAVIOR INSIGHTS  📊                          ║\n");
        sb.append("╚══════════════════════════════════════════════════════════════════╝\n\n");

        // ── Current energy context ──────────────────────────────────────
        sb.append("   ").append(EnergySimulator.getEnergyBanner()).append("\n\n");

        // ── Time-of-day analysis ────────────────────────────────────────
        Map<String, int[]> periodStats = new HashMap<>();
        // int[0] = total tasks, int[1] = delayed, int[2] = on time

        for (Task t : tasks) {
            if (!t.getStatus().equals(Task.COMPLETED) 
                    && !t.getStatus().equals(Task.INCOMPLETE)) continue;

            String period = t.getStartPeriod();
            if (period.equals("UNKNOWN")) continue;

            periodStats.putIfAbsent(period, new int[3]);
            int[] stats = periodStats.get(period);
            stats[0]++;
            if (t.getStatus().equals(Task.COMPLETED) && t.isDelayed()) stats[1]++;
            if (t.getStatus().equals(Task.COMPLETED) && !t.isDelayed()) stats[2]++;
        }

        sb.append("── 🕐 Time-of-Day Analysis ────────────────────────────────────\n\n");

        if (periodStats.isEmpty()) {
            sb.append("   Not enough completed task data yet for time analysis.\n\n");
        } else {
            // Find worst and best periods
            String worstPeriod = null, bestPeriod = null;
            double worstRate = -1, bestRate = 2;

            for (Map.Entry<String, int[]> entry : periodStats.entrySet()) {
                String period = entry.getKey();
                int[] stats = entry.getValue();
                int totalPeriod = stats[0];
                int delayedPeriod = stats[1];
                int onTimePeriod = stats[2];

                String emoji;
                switch (period) {
                    case "MORNING":   emoji = "🌅"; break;
                    case "AFTERNOON": emoji = "🌤"; break;
                    case "EVENING":   emoji = "🌙"; break;
                    default:          emoji = "🌑"; break;
                }

                double delayRate = (totalPeriod > 0) ? (delayedPeriod * 1.0 / totalPeriod) : 0;

                sb.append(String.format("   %s %-10s │ Tasks: %d │ On Time: %d │ Delayed: %d",
                        emoji, period, totalPeriod, onTimePeriod, delayedPeriod));

                if (totalPeriod > 0 && delayedPeriod > 0) {
                    sb.append(String.format(" │ Delay Rate: %.0f%%", delayRate * 100));
                }
                sb.append("\n");

                if (delayRate > worstRate) { worstRate = delayRate; worstPeriod = period; }
                if (delayRate < bestRate)  { bestRate = delayRate; bestPeriod = period; }
            }

            // ── Key Insights ────────────────────────────────────────────
            sb.append("\n── 💡 Key Insights ────────────────────────────────────────────\n\n");

            if (worstPeriod != null && worstRate > 0) {
                sb.append("   📊 Insight: You tend to delay tasks in the ")
                  .append(worstPeriod.toLowerCase()).append(".\n");
                sb.append("   💡 Suggestion: ");

                switch (worstPeriod) {
                    case "MORNING":
                        sb.append("You might need a better morning routine.\n");
                        sb.append("      Try starting with a small, easy task to build momentum.\n");
                        break;
                    case "AFTERNOON":
                        sb.append("Post-lunch dip is common.\n");
                        sb.append("      Try a short walk before afternoon tasks.\n");
                        break;
                    case "EVENING":
                        sb.append("Do important work in the MORNING instead.\n");
                        sb.append("      Save evenings for light tasks and planning.\n");
                        break;
                    case "NIGHT":
                        sb.append("Working at night reduces performance.\n");
                        sb.append("      Shift important tasks to morning hours.\n");
                        break;
                }
                sb.append("\n");
            }

            if (bestPeriod != null && bestRate == 0 && periodStats.get(bestPeriod)[0] > 0) {
                sb.append("   🌟 Insight: You perform best in the ")
                  .append(bestPeriod.toLowerCase()).append("!\n");
                sb.append("   💡 Suggestion: Schedule your hardest tasks during this time.\n\n");
            }
        }

        // ── Task completion pattern ─────────────────────────────────────
        sb.append("── 📈 Performance Pattern ─────────────────────────────────────\n\n");

        int totalCompleted = history.getCompletedTasks();
        int totalDelayed   = history.getDelayedTasks();
        int totalIncomplete = history.getIncompleteTasks();

        if (totalCompleted > 0) {
            double onTimeRate = ((totalCompleted - totalDelayed) * 100.0) / totalCompleted;
            sb.append(String.format("   📊 On-Time Rate: %.1f%% of completed tasks\n", onTimeRate));

            if (onTimeRate >= 80) {
                sb.append("   🌟 Excellent! You complete most tasks on time.\n");
            } else if (onTimeRate >= 50) {
                sb.append("   ⚠ Room for improvement. Aim for 80%+ on-time.\n");
            } else {
                sb.append("   🔴 Critical: Most tasks are delayed. Review your estimates.\n");
            }
        }

        if (totalIncomplete > 0) {
            sb.append("   ❌ Avoidance Pattern: ").append(totalIncomplete)
              .append(" task(s) abandoned. Face difficult tasks head-on!\n");
        }

        // ── Trend insight ───────────────────────────────────────────────
        sb.append("\n── 📈 Trend Analysis ──────────────────────────────────────────\n\n");
        if (history.isImproving()) {
            sb.append("   📈 Your productivity has been IMPROVING!\n");
            sb.append("   🌟 You're building great habits. Consistency is the key!\n");
            sb.append("   💡 \"Success is the sum of small efforts, repeated.\"\n");
        } else if (history.isDeclining()) {
            sb.append("   📉 Your productivity has been DECLINING.\n");
            sb.append("   ⚠ Delays are increasing — you're slipping into a pattern.\n");
            sb.append("   💡 \"The secret of getting ahead is getting started.\"\n");
            sb.append("   ➜ Start with ONE small task right now. Build from there.\n");
        } else {
            sb.append("   ➡ Your performance is STABLE.\n");
            sb.append("   💡 Push for improvement — try to beat yesterday's performance!\n");
        }

        // ── Energy-based suggestion ─────────────────────────────────────
        sb.append("\n── ⚡ Energy-Based Action ──────────────────────────────────────\n\n");
        sb.append("   📌 Right now: ").append(EnergySimulator.getEnergyBasedRecommendation());
        sb.append("\n\n");

        return sb.toString();
    }

    // ════════════════════════════════════════════════════════════════════
    //  Productivity Score  –  (completedTasks / totalTasks) * 100
    // ════════════════════════════════════════════════════════════════════
    public int calculateProductivityScore(List<Task> tasks) {
        if (tasks.isEmpty()) return 0;

        int total = tasks.size();
        int completed = 0, onTime = 0, incomplete = 0;

        for (Task t : tasks) {
            if (t.getStatus().equals(Task.COMPLETED)) {
                completed++;
                if (!t.isDelayed()) onTime++;
            }
            if (t.getStatus().equals(Task.INCOMPLETE)) incomplete++;
        }

        // Score = (completedTasks / totalTasks) * 100, adjusted for quality
        double baseScore = (completed * 100.0) / total;

        // Bonus for on-time completion
        double qualityBonus = (completed > 0) ? (onTime * 20.0 / completed) : 0;

        // Penalty for incomplete tasks
        double incompletePenalty = incomplete * 10.0;

        int score = (int) Math.round(baseScore + qualityBonus - incompletePenalty);
        return Math.max(0, Math.min(100, score));
    }

    // ── Visual aids ─────────────────────────────────────────────────────

    public String getScoreBar(int score) {
        int filled = score / 5;
        int empty  = 20 - filled;
        StringBuilder bar = new StringBuilder("   [");
        for (int i = 0; i < filled; i++) bar.append("█");
        for (int i = 0; i < empty; i++)  bar.append("░");
        bar.append("] ").append(score).append("/100");
        return bar.toString();
    }

    public String getScoreLabel(int score) {
        if (score >= 80) return "   📊 Rating: ★★★★★ EXCELLENT";
        if (score >= 50) return "   📊 Rating: ★★★☆☆ AVERAGE";
        return "   📊 Rating: ★☆☆☆☆ POOR";
    }

    public String getGrade(int score) {
        String grade;
        if (score >= 90) grade = "A+ (Outstanding!)";
        else if (score >= 80) grade = "A  (Excellent!)";
        else if (score >= 70) grade = "B+ (Very Good)";
        else if (score >= 60) grade = "B  (Good)";
        else if (score >= 50) grade = "C  (Average)";
        else if (score >= 30) grade = "D  (Needs Improvement)";
        else grade = "F  (Critical — Take Action!)";
        return "   🎯 Grade: " + grade;
    }

    public String getMotivationalMessage(int score, boolean improving, boolean declining) {
        // Context-aware motivational messages
        if (improving && score >= 70) {
            return "   🌟 \"You're on fire! Your improvement shows real dedication!\"";
        }
        if (declining && score < 50) {
            return "   ⚡ \"Tough day, but legends are made in moments like this. Fight back!\"";
        }
        if (score >= 90) {
            return "   🌟 \"Unstoppable! Keep this momentum — greatness is a habit!\"";
        } else if (score >= 70) {
            return "   💪 \"Solid effort! Push a little harder for the top!\"";
        } else if (score >= 50) {
            return "   🔥 \"Not bad, but you can do better. Small steps, big results!\"";
        } else if (score >= 30) {
            return "   ⚡ \"Every expert was once a beginner. Start small, stay consistent!\"";
        } else {
            return "   🌱 \"Today was tough, but tomorrow is a fresh start. Don't give up!\"";
        }
    }
}
