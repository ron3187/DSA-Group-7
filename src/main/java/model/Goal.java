package model;


public class Goal {

    public static final String BURN_CALORIES = "BURN_CALORIES";
    public static final String WORKOUT_MINUTES = "WORKOUT_MINUTES";
    public static final String EAT_LIMIT = "EAT_LIMIT";

    private int goalId;
    private int userId;
    private String title;
    private String type;
    private int targetValue;
    private String startDate;    
    private String deadline;      
    private int currentValue;    

    public Goal() {
    }

    public Goal(int goalId, int userId, String title, String type, int targetValue,
                String startDate, String deadline) {
        this.goalId = goalId;
        this.userId = userId;
        this.title = title;
        this.type = type;
        this.targetValue = targetValue;
        this.startDate = startDate;
        this.deadline = deadline;
    }

    public int getGoalId() { return goalId; }
    public void setGoalId(int goalId) { this.goalId = goalId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getTargetValue() { return targetValue; }
    public void setTargetValue(int targetValue) { this.targetValue = targetValue; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }

    public int getCurrentValue() { return currentValue; }
    public void setCurrentValue(int currentValue) { this.currentValue = currentValue; }

    @Override
    public String toString() {
        return "Goal[" + goalId + ", " + title + ", " + type + ", " + currentValue + "/" + targetValue + "]";
    }
}
