# Habit-Breaker-AI
Habit Breaker AI – A Java-based productivity and procrastination detection system.

## Overview

Habit Breaker AI is an intelligent Java-based productivity assistant designed to automatically track tasks, detect procrastination patterns, and provide adaptive productivity recommendations.

Unlike traditional task trackers, Habit Breaker AI automatically records task start and end times using Java's date-time APIs, eliminating manual time entry. The system analyzes user behavior, tracks productivity trends, adjusts focus strategies, and provides AI-like recommendations based on energy levels and historical performance.

This project demonstrates the practical implementation of Object-Oriented Programming (OOP) concepts including:

* Encapsulation
* Inheritance
* Polymorphism
* Abstraction

---

## Key Features

### ⏱ Automatic Time Tracking

* Records task start and end times automatically using `LocalDateTime`.
* Calculates actual task duration without user input.
* Detects delayed tasks by comparing expected and actual completion time.

### 🔋 Energy Simulation

* Automatically determines user energy levels based on current time:

  * Morning → High Energy
  * Afternoon → Medium Energy
  * Evening/Night → Low Energy

### 🧠 Behavior Memory System

* Tracks lifetime productivity statistics.
* Stores:

  * Completed tasks
  * Delayed tasks
  * Incomplete tasks
* Detects productivity trends:

  * Improving
  * Stable
  * Declining

### 🚨 Dynamic Strict Mode

Escalates productivity interventions based on procrastination signals:

| Signals | Mode             |
| ------- | ---------------- |
| 0–1     | Normal           |
| 2       | Focus Suggested  |
| 3–4     | Strict Mode      |
| 5+      | Hard Strict Mode |

### 🍅 Smart Pomodoro System

Adaptive focus sessions based on behavior:

* Normal → 25 min work / 5 min break
* Focus Mode → Mandatory focus cycles
* Strict Mode → 50 min work / 10 min break
* Hard Strict Mode → Continuous work until completion

### 🤖 Smart Decision Engine

Provides personalized recommendations by combining:

* Energy level
* Delay count
* Productivity trends
* Behavior history

### 📊 Productivity Analytics

Generates:

* Productivity score (0–100)
* Star rating
* Letter grade
* Performance reports
* Time-of-day behavior insights

---

## Project Structure

```text
HabitBreakerAI/
│
├── Main.java
├── Task.java
├── TaskManager.java
├── EnergySimulator.java
├── BehaviorHistory.java
├── FocusManager.java
├── HabitAnalyzer.java
├── ProcrastinationAnalyzer.java
├── DecisionEngine.java
└── SmartDecisionEngine.java


## Technologies Used

* Java
* Java Collections Framework (ArrayList)
* LocalDateTime API
* LocalTime API
* Duration API
* Object-Oriented Programming Principles

## Application Menu

```text
1. Add Task
2. Start Task
3. Stop Task
4. View Tasks
5. Analyze Behavior
6. Productivity Score
7. Smart Suggestions
8. View Insights
9. Exit
```

---

## Sample Workflow

1. Add a task.
2. Enter expected completion time.
3. Start the task.
4. Work normally.
5. Stop the task.
6. View automatic analysis.
7. Receive smart productivity suggestions.

---

## Future Enhancements

* Database integration (MySQL)
* GUI using JavaFX/Swing
* Machine Learning based productivity prediction
* Mobile companion application
* Cloud synchronization
* Real-time notifications and reminders

---

## Learning Outcomes

This project demonstrates:

* Real-world use of OOP principles
* Java Date & Time API usage
* Behavioral pattern analysis
* Adaptive decision-making systems
* Productivity analytics implementation

---

## Authors

**Aditi Thakur**
Developer

**Gursirat Kaur**
Developer

---

## Conclusion

Habit Breaker AI is an intelligent productivity management system that goes beyond traditional task tracking. By combining automatic time tracking, procrastination detection, behavior analysis, adaptive focus strategies, and AI-inspired recommendations, the system helps users develop better work habits and improve productivity through data-driven insights.
