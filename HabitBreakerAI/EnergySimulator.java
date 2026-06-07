import java.time.LocalTime;

/**
 * EnergySimulator.java
 * 
 * AUTO Energy Level Simulation — no user input required.
 * Uses LocalTime.now() to determine current energy level:
 * 
 *   Morning   (06:00 – 11:59) → HIGH energy
 *   Afternoon (12:00 – 16:59) → MEDIUM energy
 *   Evening   (17:00 – 20:59) → LOW energy
 *   Night     (21:00 – 05:59) → LOW energy
 * 
 * This replaces manual energy input with realistic simulation.
 */
public class EnergySimulator {

    /**
     * Get the current energy level based on time of day.
     * Uses LocalTime.now() — fully automatic.
     * 
     * @return "HIGH", "MEDIUM", or "LOW"
     */
    public static String getCurrentEnergy() {
        int hour = LocalTime.now().getHour();

        if (hour >= 6 && hour < 12) {
            return "HIGH";      // Morning → peak energy
        } else if (hour >= 12 && hour < 17) {
            return "MEDIUM";    // Afternoon → moderate energy
        } else {
            return "LOW";       // Evening/Night → low energy
        }
    }

    /**
     * Get the current time period as a readable string.
     */
    public static String getTimePeriod() {
        int hour = LocalTime.now().getHour();

        if (hour >= 6 && hour < 12)  return "🌅 Morning";
        if (hour >= 12 && hour < 17) return "🌤 Afternoon";
        if (hour >= 17 && hour < 21) return "🌙 Evening";
        return "🌑 Night";
    }

    /**
     * Get a descriptive energy banner for display.
     */
    public static String getEnergyBanner() {
        String energy = getCurrentEnergy();
        String period = getTimePeriod();
        LocalTime now = LocalTime.now();
        String time = String.format("%02d:%02d", now.getHour(), now.getMinute());

        StringBuilder sb = new StringBuilder();
        sb.append("   ⚡ Energy Level: ").append(energy);
        sb.append("  |  ").append(period);
        sb.append("  |  🕐 ").append(time);

        switch (energy) {
            case "HIGH":
                sb.append("\n   → Best time for difficult, high-focus tasks!");
                break;
            case "MEDIUM":
                sb.append("\n   → Good for revision, moderate tasks, and practice.");
                break;
            case "LOW":
                sb.append("\n   → Best for light work, planning, or short focus sessions.");
                break;
        }
        return sb.toString();
    }

    /**
     * Get task recommendation based on current energy.
     */
    public static String getEnergyBasedRecommendation() {
        String energy = getCurrentEnergy();

        switch (energy) {
            case "HIGH":
                return "Tackle difficult tasks — study new topics, solve hard problems.";
            case "MEDIUM":
                return "Do revision, moderate tasks, or practice questions.";
            case "LOW":
                return "Do light work — short reading, planning, or take a break.";
            default:
                return "Focus on any pending work.";
        }
    }
}
