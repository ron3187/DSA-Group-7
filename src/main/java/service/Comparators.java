package service;

import java.util.Comparator;
import model.Goal;
import model.Meal;
import model.Workout;

/** Ready-made comparators used by the sorting / searching code. */
public class Comparators {

    private Comparators() {
    }

    public static final Comparator<Workout> WORKOUT_BY_NAME = new Comparator<Workout>() {
        @Override
        public int compare(Workout a, Workout b) {
            return a.getName().compareToIgnoreCase(b.getName());
        }
    };

    public static final Comparator<Workout> WORKOUT_BY_CALORIES_DESC = new Comparator<Workout>() {
        @Override
        public int compare(Workout a, Workout b) {
            return Integer.compare(b.getCaloriesBurned(), a.getCaloriesBurned());
        }
    };

    public static final Comparator<Meal> MEAL_BY_NAME = new Comparator<Meal>() {
        @Override
        public int compare(Meal a, Meal b) {
            return a.getName().compareToIgnoreCase(b.getName());
        }
    };

    public static final Comparator<Meal> MEAL_BY_CALORIES_ASC = new Comparator<Meal>() {
        @Override
        public int compare(Meal a, Meal b) {
            return Integer.compare(a.getCalories(), b.getCalories());
        }
    };

    public static final Comparator<Meal> MEAL_BY_CALORIES_DESC = new Comparator<Meal>() {
        @Override
        public int compare(Meal a, Meal b) {
            return Integer.compare(b.getCalories(), a.getCalories());
        }
    };

    public static final Comparator<Meal> MEAL_BY_PROTEIN_DESC = new Comparator<Meal>() {
        @Override
        public int compare(Meal a, Meal b) {
            return Integer.compare(b.getProtein(), a.getProtein());
        }
    };

    public static final Comparator<Meal> MEAL_BY_CARBS_ASC = new Comparator<Meal>() {
        @Override
        public int compare(Meal a, Meal b) {
            return Integer.compare(a.getCarbs(), b.getCarbs());
        }
    };

    public static final Comparator<Meal> MEAL_BY_FAT_ASC = new Comparator<Meal>() {
        @Override
        public int compare(Meal a, Meal b) {
            return Integer.compare(a.getFat(), b.getFat());
        }
    };

    /** Earliest deadline first (yyyy-MM-dd strings sort correctly as text). */
    public static final Comparator<Goal> GOAL_BY_DEADLINE = new Comparator<Goal>() {
        @Override
        public int compare(Goal a, Goal b) {
            return a.getDeadline().compareTo(b.getDeadline());
        }
    };
}
