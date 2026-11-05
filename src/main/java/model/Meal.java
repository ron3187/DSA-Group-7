package model;


public class Meal {

    private int mealId;
    private int userId;
    private String name;
    private int calories;
    private int protein;
    private int carbs;
    private int fat;
    private String nutritionalInfo;  
    private String date;             
    public Meal() {
    }

    public Meal(int mealId, int userId, String name, int calories, int protein, int carbs, int fat,
                String nutritionalInfo, String date) {
        this.mealId = mealId;
        this.userId = userId;
        this.name = name;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
        this.nutritionalInfo = nutritionalInfo;
        this.date = date;
    }

    public int getMealId() { return mealId; }
    public void setMealId(int mealId) { this.mealId = mealId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCalories() { return calories; }
    public void setCalories(int calories) { this.calories = calories; }

    public int getProtein() { return protein; }
    public void setProtein(int protein) { this.protein = protein; }

    public int getCarbs() { return carbs; }
    public void setCarbs(int carbs) { this.carbs = carbs; }

    public int getFat() { return fat; }
    public void setFat(int fat) { this.fat = fat; }

    public String getNutritionalInfo() { return nutritionalInfo; }
    public void setNutritionalInfo(String nutritionalInfo) { this.nutritionalInfo = nutritionalInfo; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    @Override
    public String toString() {
        return "Meal[" + mealId + ", " + name + ", " + calories + " kcal, P" + protein
                + " C" + carbs + " F" + fat + ", " + date + "]";
    }
}
