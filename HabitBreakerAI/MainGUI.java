import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * MainGUI.java
 *
 * Swing-based GUI entry point for Habit Breaker AI.
 * Replaces the console Main.java so the app runs as a windowed desktop
 * application — no command prompt required.
 *
 * ── OOP Concepts preserved (all backend classes unchanged) ──────────
 *   ✦ Encapsulation  → Task, TaskManager, BehaviorHistory
 *   ✦ Inheritance     → ProcrastinationAnalyzer extends HabitAnalyzer
 *   ✦ Polymorphism    → analyzeTasks() override, interface reference
 *   ✦ Abstraction     → HabitAnalyzer (abstract), DecisionEngine (interface)
 *
 * ── GUI Features ────────────────────────────────────────────────────
 *   ✦ Dark-themed modern interface
 *   ✦ Sidebar navigation with 9 options
 *   ✦ CardLayout content panels
 *   ✦ Live status bar (active task, energy, mode, clock)
 *   ✦ System.out redirect to capture backend output
 */
public class MainGUI extends JFrame {

    // ── Backend instances (same as original Main.java) ──────────────
    private TaskManager manager               = new TaskManager();
    private BehaviorHistory history            = new BehaviorHistory();
    private ProcrastinationAnalyzer analyzer   = new ProcrastinationAnalyzer();
    private SmartDecisionEngine engine         = new SmartDecisionEngine();
    private FocusManager focusManager          = new FocusManager();

    // ── Color palette (dark theme) ─────────────────────────────────
    private static final Color BG_DARK       = new Color(0x1a, 0x1a, 0x2e);
    private static final Color PANEL_BG      = new Color(0x16, 0x21, 0x3e);
    private static final Color SIDEBAR_BG    = new Color(0x0f, 0x34, 0x60);
    private static final Color ACCENT        = new Color(0xe9, 0x45, 0x60);
    private static final Color TEXT_LIGHT     = new Color(0xea, 0xea, 0xea);
    private static final Color TEXT_DIM       = new Color(0x99, 0x99, 0xbb);
    private static final Color SUCCESS_GREEN  = new Color(0x00, 0xb8, 0x94);
    private static final Color WARNING_AMBER  = new Color(0xfd, 0xcb, 0x6e);
    private static final Color BTN_HOVER      = new Color(0x1a, 0x47, 0x7a);
    private static final Color INPUT_BG       = new Color(0x22, 0x2b, 0x45);
    private static final Color STATUS_BG      = new Color(0x0d, 0x0d, 0x1a);
    private static final Color CARD_HEADER    = new Color(0x0f, 0x34, 0x60);

    // ── Layout components ──────────────────────────────────────────
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JLabel statusLabel;
    private JButton activeNavButton = null;

    // ── Content text areas (reused for report views) ───────────────
    private JTextArea viewTasksArea;
    private JTextArea analyzeArea;
    private JTextArea productivityArea;
    private JTextArea suggestionsArea;
    private JTextArea insightsArea;
    private JTextArea addTaskOutputArea;
    private JTextArea startTaskOutputArea;
    private JTextArea stopTaskOutputArea;

    // ── Fonts ──────────────────────────────────────────────────────
    private static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_MONO    = new Font("Consolas", Font.PLAIN, 13);
    private static final Font FONT_NAV     = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_STATUS  = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONT_BTN     = new Font("Segoe UI", Font.BOLD, 14);

    // ── Original System.out (for restoring after capture) ──────────
    private final PrintStream originalOut = System.out;

    // ════════════════════════════════════════════════════════════════
    //  Constructor
    // ════════════════════════════════════════════════════════════════
    public MainGUI() {
        setTitle("Habit Breaker AI — Advanced Task Tracking & Procrastination Detection");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout(0, 0));

        // ── Build UI ───────────────────────────────────────────────
        add(createSidebar(), BorderLayout.WEST);
        add(createContentPanel(), BorderLayout.CENTER);
        add(createStatusBar(), BorderLayout.SOUTH);

        // ── Live clock timer (updates every second) ────────────────
        Timer timer = new Timer(1000, e -> updateStatusBar());
        timer.start();

        // Show welcome panel
        showCard("welcome");
    }

    // ════════════════════════════════════════════════════════════════
    //  Sidebar
    // ════════════════════════════════════════════════════════════════
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // ── Logo / Title area ──────────────────────────────────────
        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setBackground(new Color(0x0a, 0x24, 0x47));
        logoPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        logoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JLabel titleLabel = new JLabel("\uD83E\uDDE0 HABIT BREAKER AI");
        titleLabel.setForeground(ACCENT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Smart Task Tracking");
        subtitleLabel.setForeground(TEXT_DIM);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        logoPanel.add(titleLabel);
        logoPanel.add(Box.createVerticalStrut(4));
        logoPanel.add(subtitleLabel);
        sidebar.add(logoPanel);

        // ── Separator ──────────────────────────────────────────────
        sidebar.add(createSidebarSeparator());
        sidebar.add(Box.createVerticalStrut(8));

        // ── Navigation buttons ─────────────────────────────────────
        String[][] navItems = {
            {"\u2795  Add Task",          "addTask"},
            {"\u25B6  Start Task",        "startTask"},
            {"\u23F9  Stop Task",         "stopTask"},
            {"\uD83D\uDCCB  View All Tasks",  "viewTasks"},
            {"\uD83D\uDD0D  Analyze Behavior", "analyze"},
            {"\uD83C\uDFC6  Productivity Score","productivity"},
            {"\uD83D\uDCA1  Smart Suggestions", "suggestions"},
            {"\uD83D\uDCCA  View Insights",     "insights"},
        };

        for (String[] item : navItems) {
            JButton btn = createNavButton(item[0], item[1]);
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(2));
        }

        sidebar.add(Box.createVerticalGlue());
        sidebar.add(createSidebarSeparator());
        sidebar.add(Box.createVerticalStrut(4));

        // ── Exit button ────────────────────────────────────────────
        JButton exitBtn = createNavButton("\uD83D\uDEAA  Exit", "exit");
        exitBtn.setForeground(ACCENT);
        sidebar.add(exitBtn);
        sidebar.add(Box.createVerticalStrut(15));

        return sidebar;
    }

    private JButton createNavButton(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_NAV);
        btn.setForeground(TEXT_LIGHT);
        btn.setBackground(SIDEBAR_BG);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setPreferredSize(new Dimension(230, 42));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 10));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn != activeNavButton) {
                    btn.setBackground(BTN_HOVER);
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (btn != activeNavButton) {
                    btn.setBackground(SIDEBAR_BG);
                }
            }
        });

        btn.addActionListener(e -> {
            if (cardName.equals("exit")) {
                handleExit();
                return;
            }
            // Update active button styling
            if (activeNavButton != null) {
                activeNavButton.setBackground(SIDEBAR_BG);
                activeNavButton.setForeground(TEXT_LIGHT);
            }
            activeNavButton = btn;
            btn.setBackground(ACCENT);
            btn.setForeground(Color.WHITE);

            // Refresh data for view panels
            switch (cardName) {
                case "viewTasks":    refreshViewTasks();       break;
                case "analyze":      refreshAnalyze();         break;
                case "productivity": refreshProductivity();    break;
                case "suggestions":  refreshSuggestions();     break;
                case "insights":     refreshInsights();        break;
                case "startTask":    refreshStartTaskPanel();  break;
                case "stopTask":     refreshStopTaskPanel();   break;
                case "addTask":      refreshAddTaskPanel();    break;
            }
            showCard(cardName);
        });

        return btn;
    }

    private JSeparator createSidebarSeparator() {
        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setForeground(new Color(0x1a, 0x47, 0x7a));
        sep.setBackground(SIDEBAR_BG);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }

    // ════════════════════════════════════════════════════════════════
    //  Content Panel (CardLayout)
    // ════════════════════════════════════════════════════════════════
    private JPanel createContentPanel() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BG_DARK);

        contentPanel.add(createWelcomePanel(),       "welcome");
        contentPanel.add(createAddTaskPanel(),        "addTask");
        contentPanel.add(createStartTaskPanel(),      "startTask");
        contentPanel.add(createStopTaskPanel(),       "stopTask");
        contentPanel.add(createViewTasksPanel(),      "viewTasks");
        contentPanel.add(createAnalyzePanel(),        "analyze");
        contentPanel.add(createProductivityPanel(),   "productivity");
        contentPanel.add(createSuggestionsPanel(),    "suggestions");
        contentPanel.add(createInsightsPanel(),       "insights");

        return contentPanel;
    }

    private void showCard(String name) {
        cardLayout.show(contentPanel, name);
    }

    // ════════════════════════════════════════════════════════════════
    //  Welcome Panel
    // ════════════════════════════════════════════════════════════════
    private JPanel createWelcomePanel() {
        JPanel panel = createBasePanel();
        panel.setLayout(new GridBagLayout());

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);

        JLabel brain = new JLabel("\uD83E\uDDE0");
        brain.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 72));
        brain.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(brain);

        center.add(Box.createVerticalStrut(15));

        JLabel title = new JLabel("HABIT BREAKER AI");
        title.setFont(new Font("Segoe UI", Font.BOLD, 36));
        title.setForeground(ACCENT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(title);

        center.add(Box.createVerticalStrut(8));

        JLabel subtitle = new JLabel("Advanced Task Tracking & Procrastination Detection");
        subtitle.setFont(FONT_HEADING);
        subtitle.setForeground(TEXT_DIM);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(subtitle);

        center.add(Box.createVerticalStrut(30));

        // Feature list
        String[] features = {
            "\u2726 Automatic time tracking (no manual input)",
            "\u2726 Auto energy simulation (time-based)",
            "\u2726 Behavior memory & trend detection",
            "\u2726 Dynamic Strict Mode (Normal \u2192 Strict \u2192 Hard Strict)",
            "\u2726 Smart Pomodoro focus sessions",
            "\u2726 Intelligent behavior insights"
        };
        for (String feat : features) {
            JLabel fl = new JLabel(feat);
            fl.setFont(FONT_BODY);
            fl.setForeground(TEXT_LIGHT);
            fl.setAlignmentX(Component.CENTER_ALIGNMENT);
            center.add(fl);
            center.add(Box.createVerticalStrut(6));
        }

        center.add(Box.createVerticalStrut(25));

        JLabel cta = new JLabel("\u2190  Select an option from the sidebar to begin");
        cta.setFont(FONT_BODY);
        cta.setForeground(WARNING_AMBER);
        cta.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(cta);

        panel.add(center);
        return panel;
    }

    // ════════════════════════════════════════════════════════════════
    //  Add Task Panel
    // ════════════════════════════════════════════════════════════════
    private JPanel createAddTaskPanel() {
        JPanel panel = createBasePanel();
        panel.setLayout(new BorderLayout(0, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        // Header
        panel.add(createCardHeader("\u2795  ADD NEW TASK", "Enter task name and expected time"), BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        // Energy recommendation
        JPanel energyPanel = createInfoBanner(
            "\u26A1 Energy: " + EnergySimulator.getCurrentEnergy() +
            "  |  " + EnergySimulator.getTimePeriod() +
            "  |  \uD83D\uDCA1 " + EnergySimulator.getEnergyBasedRecommendation()
        );
        energyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(energyPanel);
        formPanel.add(Box.createVerticalStrut(20));

        // Task name field
        JLabel nameLabel = createFieldLabel("\uD83D\uDCDD Task Name:");
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(nameLabel);
        formPanel.add(Box.createVerticalStrut(6));

        JTextField nameField = createStyledTextField("Enter task name...");
        nameField.setMaximumSize(new Dimension(450, 40));
        nameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(nameField);
        formPanel.add(Box.createVerticalStrut(15));

        // Expected time field
        JLabel timeLabel = createFieldLabel("\u23F1 Expected Time (minutes):");
        timeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(timeLabel);
        formPanel.add(Box.createVerticalStrut(6));

        JTextField timeField = createStyledTextField("e.g. 30");
        timeField.setMaximumSize(new Dimension(200, 40));
        timeField.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(timeField);
        formPanel.add(Box.createVerticalStrut(20));

        // Add button
        JButton addBtn = createAccentButton("\u2795  Add Task");
        addBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(addBtn);
        formPanel.add(Box.createVerticalStrut(15));

        // Output area
        addTaskOutputArea = createOutputArea();
        JScrollPane scrollOut = wrapInScroll(addTaskOutputArea);
        scrollOut.setPreferredSize(new Dimension(0, 150));
        scrollOut.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(scrollOut);

        panel.add(formPanel, BorderLayout.CENTER);

        // Action
        addBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String timeStr = timeField.getText().trim();

            if (name.isEmpty() || name.equals("Enter task name...")) {
                showMessage(addTaskOutputArea, "\u26A0 Task name cannot be empty.");
                return;
            }
            double expected;
            try {
                expected = Double.parseDouble(timeStr);
                if (expected <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                showMessage(addTaskOutputArea, "\u26A0 Enter a valid positive number for expected time.");
                return;
            }

            // Hard Strict Mode restriction check
            if (history.getStrictModeLevel() == 3) {
                showMessage(addTaskOutputArea, "\uD83D\uDD34 HARD STRICT MODE: Only high-priority tasks allowed!");
            }

            String output = captureOutput(() -> manager.addTask(name, expected));
            showMessage(addTaskOutputArea, output);
            nameField.setText("");
            timeField.setText("");
            updateStatusBar();
        });

        return panel;
    }

    // ════════════════════════════════════════════════════════════════
    //  Start Task Panel
    // ════════════════════════════════════════════════════════════════
    private JPanel startTaskPanel; // reference for refresh
    private JComboBox<String> startTaskCombo;

    private JPanel createStartTaskPanel() {
        startTaskPanel = createBasePanel();
        startTaskPanel.setLayout(new BorderLayout(0, 15));
        startTaskPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        startTaskPanel.add(createCardHeader("\u25B6  START TASK", "Select a task to start tracking time"), BorderLayout.NORTH);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        JLabel selectLabel = createFieldLabel("\uD83D\uDCCB Select Task:");
        selectLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(selectLabel);
        formPanel.add(Box.createVerticalStrut(6));

        startTaskCombo = createStyledComboBox();
        startTaskCombo.setMaximumSize(new Dimension(450, 40));
        startTaskCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(startTaskCombo);
        formPanel.add(Box.createVerticalStrut(20));

        JButton startBtn = createAccentButton("\u25B6  Start Task");
        startBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(startBtn);
        formPanel.add(Box.createVerticalStrut(15));

        startTaskOutputArea = createOutputArea();
        JScrollPane scrollOut = wrapInScroll(startTaskOutputArea);
        scrollOut.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(scrollOut);

        startTaskPanel.add(formPanel, BorderLayout.CENTER);

        startBtn.addActionListener(e -> {
            int idx = startTaskCombo.getSelectedIndex();
            if (idx < 0) {
                showMessage(startTaskOutputArea, "\u26A0 No task selected. Add tasks first.");
                return;
            }
            String output = captureOutput(() -> {
                if (manager.startTask(idx + 1)) {
                    System.out.println("\n   " + focusManager.getQuickFocusTip(history.getStrictModeLevel()));
                }
            });
            showMessage(startTaskOutputArea, output);
            updateStatusBar();
        });

        return startTaskPanel;
    }

    private void refreshStartTaskPanel() {
        startTaskCombo.removeAllItems();
        List<Task> tasks = manager.getTasks();
        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.get(i);
            startTaskCombo.addItem((i + 1) + ". " + t.getStatusEmoji() + " "
                + t.getTaskName() + " (" + Task.formatMinutes(t.getExpectedTimeMinutes())
                + ") [" + t.getStatus() + "]");
        }
        if (startTaskOutputArea != null) startTaskOutputArea.setText("");
    }

    // ════════════════════════════════════════════════════════════════
    //  Stop Task Panel
    // ════════════════════════════════════════════════════════════════
    private JLabel stopActiveLabel;
    private JLabel stopElapsedLabel;
    private JButton stopBtn;
    private JButton markIncompleteBtn;
    private JComboBox<String> incompleteCombo;

    private JPanel createStopTaskPanel() {
        JPanel panel = createBasePanel();
        panel.setLayout(new BorderLayout(0, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        panel.add(createCardHeader("\u23F9  STOP TASK", "Stop the active task or mark as incomplete"), BorderLayout.NORTH);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        // Active task info
        stopActiveLabel = new JLabel("\u26A0 No task is currently running.");
        stopActiveLabel.setFont(FONT_BODY);
        stopActiveLabel.setForeground(WARNING_AMBER);
        stopActiveLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(stopActiveLabel);
        formPanel.add(Box.createVerticalStrut(6));

        stopElapsedLabel = new JLabel("");
        stopElapsedLabel.setFont(FONT_BODY);
        stopElapsedLabel.setForeground(TEXT_DIM);
        stopElapsedLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(stopElapsedLabel);
        formPanel.add(Box.createVerticalStrut(20));

        // Buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        stopBtn = createAccentButton("\u23F9  Stop Task");
        btnRow.add(stopBtn);

        markIncompleteBtn = createStyledButton("\u274C  Mark Incomplete", WARNING_AMBER);
        btnRow.add(markIncompleteBtn);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        formPanel.add(btnRow);
        formPanel.add(Box.createVerticalStrut(10));

        // Incomplete task combo (shown when no active task)
        JLabel incLabel = createFieldLabel("Select task to mark incomplete:");
        incLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(incLabel);
        formPanel.add(Box.createVerticalStrut(6));

        incompleteCombo = createStyledComboBox();
        incompleteCombo.setMaximumSize(new Dimension(450, 40));
        incompleteCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(incompleteCombo);
        formPanel.add(Box.createVerticalStrut(15));

        stopTaskOutputArea = createOutputArea();
        JScrollPane scrollOut = wrapInScroll(stopTaskOutputArea);
        scrollOut.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(scrollOut);

        panel.add(formPanel, BorderLayout.CENTER);

        // Actions
        stopBtn.addActionListener(e -> {
            if (!manager.hasActiveTask()) {
                showMessage(stopTaskOutputArea, "\u26A0 No task is currently running.");
                return;
            }
            String output = captureOutput(() -> {
                manager.stopTask();
                history.updateFromTasks(manager.getTasks());
                System.out.println("\n   \uD83D\uDEA6 Mode: " + history.getStrictModeLabel());
            });
            showMessage(stopTaskOutputArea, output);
            refreshStopTaskPanel();
            updateStatusBar();
        });

        markIncompleteBtn.addActionListener(e -> {
            int idx = incompleteCombo.getSelectedIndex();
            if (idx < 0) {
                showMessage(stopTaskOutputArea, "\u26A0 No task selected.");
                return;
            }
            String output = captureOutput(() -> manager.markIncomplete(idx + 1));
            showMessage(stopTaskOutputArea, output);
            refreshStopTaskPanel();
            updateStatusBar();
        });

        return panel;
    }

    private void refreshStopTaskPanel() {
        if (manager.hasActiveTask()) {
            Task active = manager.getActiveTask();
            stopActiveLabel.setText("\uD83D\uDD04 Running: \"" + active.getTaskName() + "\"");
            stopActiveLabel.setForeground(SUCCESS_GREEN);
            stopElapsedLabel.setText("\u23F1 Elapsed: " + Task.formatMinutes(active.getElapsedMinutes())
                + "  |  Expected: " + Task.formatMinutes(active.getExpectedTimeMinutes()));
            stopBtn.setEnabled(true);
        } else {
            stopActiveLabel.setText("\u26A0 No task is currently running.");
            stopActiveLabel.setForeground(WARNING_AMBER);
            stopElapsedLabel.setText("");
            stopBtn.setEnabled(false);
        }

        incompleteCombo.removeAllItems();
        List<Task> tasks = manager.getTasks();
        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.get(i);
            incompleteCombo.addItem((i + 1) + ". " + t.getTaskName() + " [" + t.getStatus() + "]");
        }
        if (stopTaskOutputArea != null) stopTaskOutputArea.setText("");
    }

    // ════════════════════════════════════════════════════════════════
    //  View Tasks Panel
    // ════════════════════════════════════════════════════════════════
    private JPanel createViewTasksPanel() {
        JPanel panel = createBasePanel();
        panel.setLayout(new BorderLayout(0, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        panel.add(createCardHeader("\uD83D\uDCCB  ALL TASKS", "View all tasks with auto-tracked times"), BorderLayout.NORTH);

        viewTasksArea = createOutputArea();
        panel.add(wrapInScroll(viewTasksArea), BorderLayout.CENTER);

        return panel;
    }

    private void refreshViewTasks() {
        String output = captureOutput(() -> manager.displayAllTasks());
        viewTasksArea.setText(output.isEmpty() ? "\u26A0 No tasks added yet." : output);
        viewTasksArea.setCaretPosition(0);
    }

    // ════════════════════════════════════════════════════════════════
    //  Analyze Behavior Panel
    // ════════════════════════════════════════════════════════════════
    private JPanel createAnalyzePanel() {
        JPanel panel = createBasePanel();
        panel.setLayout(new BorderLayout(0, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        panel.add(createCardHeader("\uD83D\uDD0D  BEHAVIOR ANALYSIS", "Polymorphism: HabitAnalyzer \u2192 ProcrastinationAnalyzer"), BorderLayout.NORTH);

        analyzeArea = createOutputArea();
        panel.add(wrapInScroll(analyzeArea), BorderLayout.CENTER);

        return panel;
    }

    private void refreshAnalyze() {
        // Demonstrate polymorphism — abstract class reference
        HabitAnalyzer habitAnalyzer = analyzer;
        String report = habitAnalyzer.analyzeTasks(manager.getTasks(), history);
        analyzeArea.setText(report);
        analyzeArea.setCaretPosition(0);
    }

    // ════════════════════════════════════════════════════════════════
    //  Productivity Score Panel
    // ════════════════════════════════════════════════════════════════
    private JPanel createProductivityPanel() {
        JPanel panel = createBasePanel();
        panel.setLayout(new BorderLayout(0, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        panel.add(createCardHeader("\uD83C\uDFC6  PRODUCTIVITY SCORE", "Detailed score breakdown with visual progress bar"), BorderLayout.NORTH);

        productivityArea = createOutputArea();
        panel.add(wrapInScroll(productivityArea), BorderLayout.CENTER);

        return panel;
    }

    private void refreshProductivity() {
        if (manager.getTaskCount() == 0) {
            productivityArea.setText("\u26A0 No tasks to score. Add and complete tasks first.");
            return;
        }

        history.updateFromTasks(manager.getTasks());

        StringBuilder sb = new StringBuilder();
        int score = engine.calculateProductivityScore(manager.getTasks());

        sb.append("\n").append(engine.getScoreBar(score)).append("\n");
        sb.append(engine.getScoreLabel(score)).append("\n");
        sb.append(engine.getGrade(score)).append("\n\n");

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

        sb.append("   \u2500\u2500 Score Breakdown \u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\n");
        sb.append(String.format("   \uD83D\uDCCB Total Tasks:      %d\n", total));
        sb.append(String.format("   \u2705 Completed:         %d  (%.1f%%)\n", completed, total > 0 ? completed * 100.0 / total : 0));
        sb.append(String.format("   \uD83D\uDFE2 On Time:           %d\n", onTime));
        sb.append(String.format("   \uD83D\uDD34 Delayed:           %d\n", delayed));
        sb.append(String.format("   \u274C Incomplete:         %d\n", incomplete));
        sb.append("   \u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\n");
        sb.append("   \uD83D\uDCCA SCORE: ").append(score).append(" / 100\n");
        sb.append("   \u26A1 Energy: ").append(EnergySimulator.getCurrentEnergy())
          .append("  |  \uD83D\uDCC8 Trend: ").append(history.getBehaviorTrend()).append("\n");
        sb.append("   \uD83D\uDEA6 Mode: ").append(history.getStrictModeLabel()).append("\n\n");

        sb.append(engine.getMotivationalMessage(score, history.isImproving(), history.isDeclining())).append("\n\n");
        sb.append(history.getFullReport());

        productivityArea.setText(sb.toString());
        productivityArea.setCaretPosition(0);
    }

    // ════════════════════════════════════════════════════════════════
    //  Smart Suggestions Panel
    // ════════════════════════════════════════════════════════════════
    private JPanel createSuggestionsPanel() {
        JPanel panel = createBasePanel();
        panel.setLayout(new BorderLayout(0, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        panel.add(createCardHeader("\uD83D\uDCA1  SMART SUGGESTIONS", "Interface: DecisionEngine \u2192 SmartDecisionEngine"), BorderLayout.NORTH);

        suggestionsArea = createOutputArea();
        panel.add(wrapInScroll(suggestionsArea), BorderLayout.CENTER);

        return panel;
    }

    private void refreshSuggestions() {
        // Demonstrate interface polymorphism
        DecisionEngine de = engine;
        String suggestions = de.suggestAction(manager.getTasks(), history);
        suggestionsArea.setText(suggestions);
        suggestionsArea.setCaretPosition(0);
    }

    // ════════════════════════════════════════════════════════════════
    //  View Insights Panel
    // ════════════════════════════════════════════════════════════════
    private JPanel createInsightsPanel() {
        JPanel panel = createBasePanel();
        panel.setLayout(new BorderLayout(0, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        panel.add(createCardHeader("\uD83D\uDCCA  BEHAVIOR INSIGHTS", "Time-of-day analysis, patterns, and energy-based insights"), BorderLayout.NORTH);

        insightsArea = createOutputArea();
        panel.add(wrapInScroll(insightsArea), BorderLayout.CENTER);

        return panel;
    }

    private void refreshInsights() {
        String insights = engine.generateInsights(manager.getTasks(), history);
        insightsArea.setText(insights);
        insightsArea.setCaretPosition(0);
    }

    // ════════════════════════════════════════════════════════════════
    //  Refresh helpers for Add Task panel
    // ════════════════════════════════════════════════════════════════
    private void refreshAddTaskPanel() {
        if (addTaskOutputArea != null) addTaskOutputArea.setText("");
    }

    // ════════════════════════════════════════════════════════════════
    //  Status Bar
    // ════════════════════════════════════════════════════════════════
    private JPanel createStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(STATUS_BG);
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0x2a, 0x2a, 0x4a)),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        bar.setPreferredSize(new Dimension(0, 38));

        statusLabel = new JLabel();
        statusLabel.setFont(FONT_STATUS);
        statusLabel.setForeground(TEXT_DIM);
        bar.add(statusLabel, BorderLayout.CENTER);

        updateStatusBar();
        return bar;
    }

    private void updateStatusBar() {
        StringBuilder sb = new StringBuilder();
        LocalTime now = LocalTime.now();
        String time = now.format(DateTimeFormatter.ofPattern("hh:mm:ss a"));

        if (manager.hasActiveTask()) {
            Task active = manager.getActiveTask();
            sb.append("\uD83D\uDD04 Active: \"").append(active.getTaskName()).append("\"")
              .append("  |  \u23F1 ").append(Task.formatMinutes(active.getElapsedMinutes()));
        } else {
            sb.append("\u26AA No active task");
        }

        sb.append("  |  \u26A1 Energy: ").append(EnergySimulator.getCurrentEnergy());
        sb.append("  |  ").append(EnergySimulator.getTimePeriod());
        sb.append("  |  \uD83D\uDEA6 ").append(history.getStrictModeLabel());
        sb.append("  |  \uD83D\uDD50 ").append(time);

        if (statusLabel != null) {
            statusLabel.setText(sb.toString());
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  Exit Handler
    // ════════════════════════════════════════════════════════════════
    private void handleExit() {
        StringBuilder msg = new StringBuilder();

        if (manager.hasActiveTask()) {
            manager.getActiveTask().markIncomplete();
            msg.append("\u26A0 Active task \"").append(manager.getActiveTask().getTaskName())
               .append("\" marked as INCOMPLETE.\n\n");
        }

        if (manager.getTaskCount() > 0) {
            int score = engine.calculateProductivityScore(manager.getTasks());
            msg.append("\uD83D\uDCCA Final Productivity Score: ").append(score).append("/100\n");
            msg.append(engine.getGrade(score)).append("\n\n");
        }

        msg.append("\uD83C\uDF1F Thank you for using Habit Breaker AI!\n");
        msg.append("\"The secret of getting ahead is getting started.\"");

        JOptionPane.showMessageDialog(this, msg.toString(),
            "Goodbye!", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }

    // ════════════════════════════════════════════════════════════════
    //  System.out Capture Utility
    // ════════════════════════════════════════════════════════════════
    /**
     * Captures everything written to System.out while the given
     * Runnable executes, then restores the original System.out.
     */
    private String captureOutput(Runnable action) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream capture = new PrintStream(baos);
        System.setOut(capture);
        try {
            action.run();
        } finally {
            System.out.flush();
            System.setOut(originalOut);
        }
        return baos.toString();
    }

    // ════════════════════════════════════════════════════════════════
    //  UI Factory Helpers
    // ════════════════════════════════════════════════════════════════

    private JPanel createBasePanel() {
        JPanel p = new JPanel();
        p.setBackground(BG_DARK);
        return p;
    }

    private JPanel createCardHeader(String title, String subtitle) {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(CARD_HEADER);
        header.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(0x1a, 0x47, 0x7a), 1, true),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(TEXT_LIGHT);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.add(titleLabel);

        if (subtitle != null && !subtitle.isEmpty()) {
            header.add(Box.createVerticalStrut(4));
            JLabel sub = new JLabel(subtitle);
            sub.setFont(FONT_STATUS);
            sub.setForeground(TEXT_DIM);
            sub.setAlignmentX(Component.LEFT_ALIGNMENT);
            header.add(sub);
        }

        return header;
    }

    private JPanel createInfoBanner(String text) {
        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(new Color(0x1a, 0x3a, 0x5c));
        banner.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(0x2a, 0x5a, 0x8c), 1, true),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        banner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JLabel label = new JLabel(text);
        label.setFont(FONT_STATUS);
        label.setForeground(WARNING_AMBER);
        banner.add(label, BorderLayout.CENTER);

        return banner;
    }

    private JLabel createFieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_BODY);
        lbl.setForeground(TEXT_LIGHT);
        return lbl;
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(FONT_BODY);
        field.setForeground(TEXT_LIGHT);
        field.setBackground(INPUT_BG);
        field.setCaretColor(TEXT_LIGHT);
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(0x3a, 0x4a, 0x6a), 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setPreferredSize(new Dimension(300, 40));

        // Placeholder behavior
        field.setText(placeholder);
        field.setForeground(TEXT_DIM);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXT_LIGHT);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(TEXT_DIM);
                }
            }
        });

        return field;
    }

    @SuppressWarnings("unchecked")
    private JComboBox<String> createStyledComboBox() {
        JComboBox<String> combo = new JComboBox<>();
        combo.setFont(FONT_BODY);
        combo.setForeground(TEXT_LIGHT);
        combo.setBackground(INPUT_BG);
        combo.setBorder(BorderFactory.createLineBorder(new Color(0x3a, 0x4a, 0x6a), 1));
        combo.setPreferredSize(new Dimension(400, 40));

        // Custom renderer for dark theme
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? ACCENT : INPUT_BG);
                setForeground(TEXT_LIGHT);
                setFont(FONT_BODY);
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return this;
            }
        });

        return combo;
    }

    private JButton createAccentButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BTN);
        btn.setForeground(Color.WHITE);
        btn.setBackground(ACCENT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(200, 42));
        btn.setMaximumSize(new Dimension(200, 42));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        btn.addMouseListener(new MouseAdapter() {
            Color original = ACCENT;
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(ACCENT.brighter());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(original);
            }
            @Override
            public void mousePressed(MouseEvent e) {
                btn.setBackground(ACCENT.darker());
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                btn.setBackground(ACCENT);
            }
        });

        return btn;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BTN);
        btn.setForeground(Color.WHITE);
        btn.setBackground(color.darker());
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(220, 42));
        btn.setMaximumSize(new Dimension(220, 42));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btn.setBackground(color); }
            @Override
            public void mouseExited(MouseEvent e)  { btn.setBackground(color.darker()); }
        });

        return btn;
    }

    private JTextArea createOutputArea() {
        JTextArea area = new JTextArea();
        area.setFont(FONT_MONO);
        area.setForeground(TEXT_LIGHT);
        area.setBackground(new Color(0x12, 0x16, 0x28));
        area.setCaretColor(TEXT_LIGHT);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        return area;
    }

    private JScrollPane wrapInScroll(JTextArea area) {
        JScrollPane sp = new JScrollPane(area);
        sp.setBorder(new LineBorder(new Color(0x2a, 0x2a, 0x4a), 1, true));
        sp.getVerticalScrollBar().setUnitIncrement(16);

        // Dark scrollbar
        sp.getVerticalScrollBar().setBackground(new Color(0x12, 0x16, 0x28));
        sp.getHorizontalScrollBar().setBackground(new Color(0x12, 0x16, 0x28));

        return sp;
    }

    private void showMessage(JTextArea area, String msg) {
        area.setText(msg);
        area.setCaretPosition(0);
    }

    // ════════════════════════════════════════════════════════════════
    //  Main Entry Point
    // ════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        // Use system look-and-feel for better scrollbar integration
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Set dark tooltip defaults
        UIManager.put("ToolTip.background", new Color(0x16, 0x21, 0x3e));
        UIManager.put("ToolTip.foreground", new Color(0xea, 0xea, 0xea));
        UIManager.put("OptionPane.background", new Color(0x1a, 0x1a, 0x2e));
        UIManager.put("Panel.background", new Color(0x1a, 0x1a, 0x2e));
        UIManager.put("OptionPane.messageForeground", new Color(0xea, 0xea, 0xea));

        SwingUtilities.invokeLater(() -> {
            MainGUI gui = new MainGUI();
            gui.setVisible(true);
        });
    }
}
