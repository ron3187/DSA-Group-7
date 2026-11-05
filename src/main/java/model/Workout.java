package model;


public class Workout {

    private int workoutId;
    private int userId;
    private String name;
    private int duration;      
    private int caloriesBurned;
    private String date;         

    public Workout() {
    }

    public Workout(int workoutId, int userId, String name, int duration, int caloriesBurned, String date) {
        this.workoutId = workoutId;
        this.userId = userId;
        this.name = name;
        this.duration = duration;
        this.caloriesBurned = caloriesBurned;
        this.date = date;
    }

    public int getWorkoutId() { return workoutId; }
    public void setWorkoutId(int workoutId) { this.workoutId = workoutId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public int getCaloriesBurned() { return caloriesBurned; }
    public void setCaloriesBurned(int caloriesBurned) { this.caloriesBurned = caloriesBurned; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    @Override
    public String toString() {
        return "Workout[" + workoutId + ", " + name + ", " + duration + " min, "
                + caloriesBurned + " kcal, " + date + "]";
    }
}
