import java.util.Scanner;

/**
 * Main.java
 * 
 * Entry point for Habit Breaker AI — Advanced Version.
 * 
 * Menu-driven console interface with 9 options:
 *   1. Add Task           → user enters name + expected time ONLY
 *   2. Start Task         → system auto-records start time
 *   3. Stop Task          → system auto-records end time + calculates duration
 *   4. View Tasks         → displays all tasks with auto-tracked times
 *   5. Analyze Behavior   → ProcrastinationAnalyzer (polymorphism)
 *   6. View Productivity  → Score, grade, visual bar
 *   7. Get Suggestions    → SmartDecisionEngine (interface)
 *   8. View Insights      → Time-of-day analysis, patterns
 *   9. Exit
 * 
 * ⚠ User NEVER enters actual time — everything is auto-tracked.
 * ⚡ Energy level is auto-simulated based on time of day.
 */
public class Main {

    // ── Shared instances ────────────────────────────────────────────────
    private static TaskManager manager                   = new TaskManager();
    private static BehaviorHistory history                = new BehaviorHistory();
    private static ProcrastinationAnalyzer analyzer       = new ProcrastinationAnalyzer();
    private static SmartDecisionEngine engine             = new SmartDecisionEngine();
    private static FocusManager focusManager              = new FocusManager();
    private static Scanner scanner                        = new Scanner(System.in);

    // ════════════════════════════════════════════════════════════════════
    //  main
    // ════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        printBanner();

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readIntInput("👉 Enter your choice: ");

            switch (choice) {
                case 1: addTask();              break;
                case 2: startTask();            break;
                case 3: stopTask();             break;
                case 4: viewTasks();            break;
                case 5: analyzeBehavior();      break;
                case 6: viewProductivityScore();break;
                case 7: getSmartSuggestions();  break;
                case 8: viewInsights();         break;
                case 9:
                    running = false;
                    printExitMessage();
                    break;
                default:
                    System.out.println("\n   ⚠ Invalid choice. Please select 1–9.\n");
            }
        }
        scanner.close();
    }

    // ════════════════════════════════════════════════════════════════════
    //  Banner & Menu
    // ════════════════════════════════════════════════════════════════════

    private static void printBanner() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                                  ║");
        System.out.println("║     🧠  HABIT BREAKER AI  🧠                                     ║");
        System.out.println("║     Advanced Task Tracking & Procrastination Detection            ║");
        System.out.println("║                                                                  ║");
        System.out.println("║     ✦ Automatic time tracking (no manual input)                  ║");
        System.out.println("║     ✦ Auto energy simulation (time-based)                        ║");
        System.out.println("║     ✦ Behavior memory & trend detection                          ║");
        System.out.println("║     ✦ Dynamic Strict Mode (Normal → Strict → Hard Strict)        ║");
        System.out.println("║     ✦ Smart Pomodoro focus sessions                              ║");
        System.out.println("║     ✦ Intelligent behavior insights                              ║");
        System.out.println("║                                                                  ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════╝");
        System.out.println();
    }

    private static void printMenu() {
        // Show active task + energy level at the top
        if (manager.hasActiveTask()) {
            Task active = manager.getActiveTask();
            System.out.println("┌──────────────────────────────────────────────────────────────┐");
            System.out.println("│  🔄 ACTIVE: \"" + padRight(active.getTaskName(), 18)
                    + "\"  ⏱ " + padRight(Task.formatMinutes(active.getElapsedMinutes()), 10)
                    + "  ⚡ " + padRight(EnergySimulator.getCurrentEnergy(), 7) + "│");
            System.out.println("└──────────────────────────────────────────────────────────────┘");
        } else {
            // Show energy level even when no task is active
            System.out.println("   ⚡ Energy: " + EnergySimulator.getCurrentEnergy()
                    + "  |  " + EnergySimulator.getTimePeriod()
                    + "  |  Mode: " + history.getStrictModeLabel());
        }

        System.out.println();
        System.out.println("┌──────────────────────────────────────────┐");
        System.out.println("│           📋  MAIN MENU  📋             │");
        System.out.println("├──────────────────────────────────────────┤");
        System.out.println("│  1. ➕ Add Task                          │");
        System.out.println("│  2. ▶  Start Task                       │");
        System.out.println("│  3. ⏹  Stop Task                        │");
        System.out.println("│  4. 📋 View All Tasks                   │");
        System.out.println("│  5. 🔍 Analyze Behavior                 │");
        System.out.println("│  6. 🏆 View Productivity Score          │");
        System.out.println("│  7. 💡 Get Smart Suggestions            │");
        System.out.println("│  8. 📊 View Insights                    │");
        System.out.println("│  9. 🚪 Exit                             │");
        System.out.println("└──────────────────────────────────────────┘");
    }

    // ════════════════════════════════════════════════════════════════════
    //  Option 1: Add Task
    // ════════════════════════════════════════════════════════════════════
    private static void addTask() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════════╗");
        System.out.println("║                    ➕  ADD NEW TASK  ➕                          ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════╝\n");

        // Show energy-based recommendation
        System.out.println("   " + EnergySimulator.getEnergyBanner());
        System.out.println("   💡 Recommended: " + EnergySimulator.getEnergyBasedRecommendation());
        System.out.println();

        // Hard Strict Mode restriction
        if (history.getStrictModeLevel() == 3) {
            System.out.println("   🔴 HARD STRICT MODE: Only high-priority tasks allowed!");
        }

        System.out.print("   📝 Enter Task Name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("   ⚠ Task name cannot be empty.\n");
            return;
        }

        double expectedTime = readDoubleInput("   ⏱ Enter Expected Time (in minutes): ");
        if (expectedTime <= 0) {
            System.out.println("   ⚠ Expected time must be greater than 0.\n");
            return;
        }

        manager.addTask(name, expectedTime);
        System.out.println();
    }

    // ════════════════════════════════════════════════════════════════════
    //  Option 2: Start Task
    // ════════════════════════════════════════════════════════════════════
    private static void startTask() {
        if (manager.getTaskCount() == 0) {
            System.out.println("\n   ⚠ No tasks added yet. Add a task first.\n");
            return;
        }

        System.out.println("\n╔══════════════════════════════════════════════════════════════════╗");
        System.out.println("║                    ▶  START TASK  ▶                              ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════╝\n");

        showTaskList();
        int index = readIntInput("   👉 Enter task number to start: ");
        if (manager.startTask(index)) {
            System.out.println("\n   " + focusManager.getQuickFocusTip(history.getStrictModeLevel()));
        }
        System.out.println();
    }

    // ════════════════════════════════════════════════════════════════════
    //  Option 3: Stop Task
    // ════════════════════════════════════════════════════════════════════
    private static void stopTask() {
        if (!manager.hasActiveTask()) {
            System.out.println("\n   ⚠ No task is currently running.\n");
            if (manager.getTaskCount() > 0) {
                System.out.print("   ❓ Mark a task as INCOMPLETE instead? (y/n): ");
                String ans = scanner.nextLine().trim().toLowerCase();
                if (ans.equals("y") || ans.equals("yes")) {
                    showTaskList();
                    int idx = readIntInput("   👉 Enter task number to mark incomplete: ");
                    manager.markIncomplete(idx);
                }
            }
            System.out.println();
            return;
        }

        System.out.println("\n╔══════════════════════════════════════════════════════════════════╗");
        System.out.println("║                    ⏹  STOP TASK  ⏹                              ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════╝");

        Task active = manager.getActiveTask();
        System.out.println("\n   🔄 Running: \"" + active.getTaskName() + "\"");
        System.out.println("   ⏱ Elapsed: " + Task.formatMinutes(active.getElapsedMinutes()));
        System.out.println("   ⏱ Expected: " + Task.formatMinutes(active.getExpectedTimeMinutes()));

        // Warn if already exceeded
        if (active.getElapsedMinutes() > active.getExpectedTimeMinutes()) {
            System.out.println("   ⚠ WARNING: You've already exceeded the expected time!");
        }

        System.out.print("\n   ⏹ Stop this task? (y/n): ");
        String ans = scanner.nextLine().trim().toLowerCase();

        if (ans.equals("y") || ans.equals("yes")) {
            manager.stopTask();

            // Update history after stopping
            history.updateFromTasks(manager.getTasks());

            // Show mode status after task completion
            System.out.println("\n   🚦 Mode: " + history.getStrictModeLabel());
        } else {
            System.out.println("   Task continues running.");
        }
        System.out.println();
    }

    // ════════════════════════════════════════════════════════════════════
    //  Option 4: View All Tasks
    // ════════════════════════════════════════════════════════════════════
    private static void viewTasks() {
        manager.displayAllTasks();
    }

    // ════════════════════════════════════════════════════════════════════
    //  Option 5: Analyze Behavior (Polymorphism)
    // ════════════════════════════════════════════════════════════════════
    private static void analyzeBehavior() {
        // Abstract class reference → Polymorphism
        HabitAnalyzer habitAnalyzer = analyzer;
        String report = habitAnalyzer.analyzeTasks(manager.getTasks(), history);
        System.out.println(report);
    }

    // ════════════════════════════════════════════════════════════════════
    //  Option 6: Productivity Score (Detailed)
    // ════════════════════════════════════════════════════════════════════
    private static void viewProductivityScore() {
        if (manager.getTaskCount() == 0) {
            System.out.println("\n   ⚠ No tasks to score. Add and complete tasks first.\n");
            return;
        }

        history.updateFromTasks(manager.getTasks());

        System.out.println("\n╔══════════════════════════════════════════════════════════════════╗");
        System.out.println("║             🏆  PRODUCTIVITY SCORE REPORT  🏆                   ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════╝\n");

        int score = engine.calculateProductivityScore(manager.getTasks());

        // Visual bar
        System.out.println(engine.getScoreBar(score));
        System.out.println(engine.getScoreLabel(score));
        System.out.println(engine.getGrade(score));
        System.out.println();

        // Breakdown
        int total = manager.getTaskCount();
        int completed = 0, onTime = 0, delayed = 0, incomplete = 0;
        for (Task t : manager.getTasks()) {
            if (t.getStatus().equals(Task.COMPLETED)) {
                completed++;
                if (t.isDelayed()) delayed++;
                else onTime++;
            }
            if (t.getStatus().equals(Task.INCOMPLETE)) incomplete++;
        }

        System.out.println("   ── Score Breakdown ─────────────────────────────────────────");
        System.out.printf("   📋 Total Tasks:      %d\n", total);
        System.out.printf("   ✅ Completed:         %d  (%.1f%%)\n",
                completed, total > 0 ? completed * 100.0 / total : 0);
        System.out.printf("   🟢 On Time:           %d\n", onTime);
        System.out.printf("   🔴 Delayed:           %d\n", delayed);
        System.out.printf("   ❌ Incomplete:         %d\n", incomplete);
        System.out.println("   ──────────────────────────────────────────────────────────────");
        System.out.println("   📊 SCORE: " + score + " / 100");
        System.out.println("   ⚡ Energy: " + EnergySimulator.getCurrentEnergy()
                + "  |  📈 Trend: " + history.getBehaviorTrend());
        System.out.println("   🚦 Mode: " + history.getStrictModeLabel());
        System.out.println();

        // Motivational message
        System.out.println(engine.getMotivationalMessage(score,
                history.isImproving(), history.isDeclining()));
        System.out.println();

        // Show behavior history
        System.out.println(history.getFullReport());
    }

    // ════════════════════════════════════════════════════════════════════
    //  Option 7: Get Smart Suggestions (Interface)
    // ════════════════════════════════════════════════════════════════════
    private static void getSmartSuggestions() {
        // Interface reference → Abstraction + Polymorphism
        DecisionEngine de = engine;
        String suggestions = de.suggestAction(manager.getTasks(), history);
        System.out.println(suggestions);
    }

    // ════════════════════════════════════════════════════════════════════
    //  Option 8: View Insights
    // ════════════════════════════════════════════════════════════════════
    private static void viewInsights() {
        String insights = engine.generateInsights(manager.getTasks(), history);
        System.out.println(insights);
    }

    // ════════════════════════════════════════════════════════════════════
    //  Exit
    // ════════════════════════════════════════════════════════════════════
    private static void printExitMessage() {
        if (manager.hasActiveTask()) {
            System.out.println("\n   ⚠ Active task \"" + manager.getActiveTask().getTaskName()
                    + "\" marked as INCOMPLETE on exit.");
            manager.getActiveTask().markIncomplete();
        }

        // Final score
        if (manager.getTaskCount() > 0) {
            int score = engine.calculateProductivityScore(manager.getTasks());
            System.out.println("\n   📊 Final Productivity Score: " + score + "/100");
            System.out.println("   " + engine.getGrade(score));
        }

        System.out.println("\n╔══════════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                                  ║");
        System.out.println("║     🌟 Thank you for using Habit Breaker AI! 🌟                  ║");
        System.out.println("║                                                                  ║");
        System.out.println("║     \"The secret of getting ahead is getting started.\"             ║");
        System.out.println("║     Track your progress, break the procrastination cycle! 💪      ║");
        System.out.println("║                                                                  ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════╝\n");
    }

    // ════════════════════════════════════════════════════════════════════
    //  Helpers
    // ════════════════════════════════════════════════════════════════════

    private static void showTaskList() {
        var tasks = manager.getTasks();
        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.get(i);
            System.out.println("   " + (i + 1) + ". " + t.getStatusEmoji()
                    + " " + t.getTaskName()
                    + " (" + Task.formatMinutes(t.getExpectedTimeMinutes())
                    + " expected) [" + t.getStatus() + "]");
        }
    }

    private static double readDoubleInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double val = Double.parseDouble(scanner.nextLine().trim());
                return val;
            } catch (NumberFormatException e) {
                System.out.println("   ⚠ Invalid input. Enter a valid number.\n");
            }
        }
    }

    private static int readIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("   ⚠ Invalid input. Enter a valid number.\n");
            }
        }
    }

    private static String padRight(String s, int width) {
        if (s.length() >= width) return s.substring(0, width);
        return String.format("%-" + width + "s", s);
    }
}
