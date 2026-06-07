/**
 * FocusManager.java
 * 
 * SMART FOCUS SYSTEM — Pomodoro-based focus session recommendations.
 * 
 * Adapts focus duration based on strict mode level:
 *   - Normal:      25 min work → 5 min break  (Standard Pomodoro)
 *   - Strict Mode: 50 min work → 10 min break (Extended focus)
 *   - Hard Strict: Continuous work → No breaks until task done
 * 
 * Also provides focus tips and session planning.
 */
public class FocusManager {

    // ════════════════════════════════════════════════════════════════════
    //  suggestFocusSession  –  Adaptive Pomodoro
    // ════════════════════════════════════════════════════════════════════
    /**
     * Generate a focus session recommendation based on strict mode level.
     * 
     * @param strictLevel 0=Normal, 1=FocusSuggested, 2=Strict, 3=HardStrict
     * @return Formatted focus session recommendation.
     */
    public String suggestFocusSession(int strictLevel) {
        StringBuilder sb = new StringBuilder();

        sb.append("\n   ── 🍅 Smart Focus Session ────────────────────────────────\n\n");

        switch (strictLevel) {

            case 0: // Normal — gentle suggestion
                sb.append("   🟢 Mode: NORMAL POMODORO\n\n");
                sb.append("   You're doing well! Here's the standard Pomodoro to stay sharp:\n\n");
                sb.append("   ┌────────────────────────────────────────────────────────┐\n");
                sb.append("   │  🍅 25 min  →  Focused work (no distractions)         │\n");
                sb.append("   │  ☕  5 min  →  Short break (stretch, water)           │\n");
                sb.append("   │  🔁 Repeat 4 times                                    │\n");
                sb.append("   │  🌿 15 min →  Long break after 4 cycles               │\n");
                sb.append("   └────────────────────────────────────────────────────────┘\n\n");
                break;

            case 1: // Focus Suggested — mild procrastination
                sb.append("   🟡 Mode: FOCUS RECOMMENDED\n\n");
                sb.append("   ⚠ Early procrastination signals detected.\n");
                sb.append("   A structured focus session will help get back on track:\n\n");
                sb.append("   ┌────────────────────────────────────────────────────────┐\n");
                sb.append("   │  🍅 25 min  →  Deep focus (phone on silent)           │\n");
                sb.append("   │  ☕  5 min  →  Quick break (NO screen)                │\n");
                sb.append("   │  🔁 Complete at least 2 cycles before stopping        │\n");
                sb.append("   └────────────────────────────────────────────────────────┘\n\n");
                sb.append("   💡 Tips:\n");
                sb.append("   • Clear your desk of distractions\n");
                sb.append("   • Use headphones to block noise\n");
                sb.append("   • Start with the EASIEST part of the task\n\n");
                break;

            case 2: // Strict Mode — significant procrastination
                sb.append("   🟠 Mode: STRICT POMODORO\n\n");
                sb.append("   🚨 Procrastination pattern detected! Extended focus required.\n\n");
                sb.append("   ┌────────────────────────────────────────────────────────┐\n");
                sb.append("   │  🔥 50 min  →  Intense focused work                   │\n");
                sb.append("   │  ☕ 10 min  →  Controlled break (walk, stretch)        │\n");
                sb.append("   │  🔁 Repeat 3 times minimum                            │\n");
                sb.append("   │  ⛔ NO social media during breaks                     │\n");
                sb.append("   └────────────────────────────────────────────────────────┘\n\n");
                sb.append("   ⚠ Mandatory Rules:\n");
                sb.append("   • Phone MUST be in another room\n");
                sb.append("   • Close all non-essential browser tabs\n");
                sb.append("   • No multitasking — ONE task at a time\n");
                sb.append("   • Write down what you'll accomplish BEFORE starting\n\n");
                break;

            case 3: // Hard Strict Mode — severe procrastination
                sb.append("   🔴 Mode: HARD STRICT — NO BREAKS\n\n");
                sb.append("   🚨🚨 SEVERE PROCRASTINATION DETECTED 🚨🚨\n\n");
                sb.append("   ┌────────────────────────────────────────────────────────┐\n");
                sb.append("   │  🔴 CONTINUOUS WORK until task is COMPLETED            │\n");
                sb.append("   │  ⛔ NO breaks until at least one task finishes on time │\n");
                sb.append("   │  🎯 ONLY high-priority tasks allowed                  │\n");
                sb.append("   │  📵 ALL devices OFF except for work                   │\n");
                sb.append("   └────────────────────────────────────────────────────────┘\n\n");
                sb.append("   🔴 Emergency Protocol:\n");
                sb.append("   1. Pick the MOST IMPORTANT task\n");
                sb.append("   2. Remove ALL distractions (phone, social media, TV)\n");
                sb.append("   3. Set a firm deadline (even if self-imposed)\n");
                sb.append("   4. Work non-stop until it's DONE\n");
                sb.append("   5. Only then can you take a break\n\n");
                sb.append("   💡 \"Discipline is choosing between what you want NOW\n");
                sb.append("      and what you want MOST.\"\n\n");
                break;
        }

        return sb.toString();
    }

    // ════════════════════════════════════════════════════════════════════
    //  getQuickFocusTip  –  One-liner based on level
    // ════════════════════════════════════════════════════════════════════
    /**
     * Returns a quick one-line focus tip.
     */
    public String getQuickFocusTip(int strictLevel) {
        switch (strictLevel) {
            case 0:  return "🍅 Try a 25-min Pomodoro to stay productive!";
            case 1:  return "🍅 Start a focus session NOW — 25 min, no distractions.";
            case 2:  return "🔥 Strict Pomodoro: 50 min work, 10 min break. Start NOW!";
            case 3:  return "🔴 HARD MODE: No breaks. Complete one task before stopping!";
            default: return "🍅 Stay focused!";
        }
    }
}
