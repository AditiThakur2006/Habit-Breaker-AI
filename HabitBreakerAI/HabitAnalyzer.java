import java.util.List;

/**
 * HabitAnalyzer.java
 * 
 * ABSTRACT CLASS demonstrating Abstraction.
 * Serves as the base type for all analyzers (e.g. ProcrastinationAnalyzer).
 * 
 * The abstract method analyzeTasks() forces every subclass to provide
 * its own analysis logic → demonstrates Polymorphism via method overriding.
 */
public abstract class HabitAnalyzer {

    /**
     * Analyze a list of tasks and produce a behavior report.
     * Each subclass provides its own implementation (polymorphism).
     * 
     * @param tasks    The list of tasks to analyze.
     * @param history  The behavior history for trend context.
     * @return         A human-readable analysis report.
     */
    public abstract String analyzeTasks(List<Task> tasks, BehaviorHistory history);
}
