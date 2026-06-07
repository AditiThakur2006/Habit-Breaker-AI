import java.util.List;

/**
 * DecisionEngine.java
 * 
 * INTERFACE demonstrating Abstraction.
 * Defines the contract for any decision engine that suggests
 * actions based on analyzed task data.
 * 
 * Implemented by SmartDecisionEngine.
 */
public interface DecisionEngine {

    /**
     * Suggest intelligent actions based on the user's task history.
     * 
     * @param tasks    The list of tasks to base suggestions on.
     * @param history  The behavior history for trend context.
     * @return         A human-readable suggestion string.
     */
    String suggestAction(List<Task> tasks, BehaviorHistory history);
}
