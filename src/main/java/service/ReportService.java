package service;

import ds.BST;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedList;
import model.Goal;
import model.Meal;
import model.User;
import model.Workout;


public class ReportService {

    private final UserService userService = new UserService();
    private final WorkoutService workoutService = new WorkoutService();
    private final MealService mealService = new MealService();
    private final GoalService goalService = new GoalService();

    /** from / to may be blank (blank = no limit). Dates are yyyy-MM-dd. */
    public String generate(int userId, String from, String to) throws SQLException {
        User user = userService.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User ID " + userId + " does not exist.");
        }
        String fromDate = isBlank(from) ? "0000-01-01" : DateUtil.parse(from, "From date");
        String toDate = isBlank(to) ? "9999-12-31" : DateUtil.parse(to, "To date");
        if (fromDate.compareTo(toDate) > 0) {
            throw new IllegalArgumentException("'From' date cannot be after 'To' date.");
        }

        Workout[] workouts = workoutService.getWorkouts(userId);
        Meal[] meals = mealService.getMeals(userId);

  
        BST days = new BST();
        int totalBurned = 0;
        int totalMinutes = 0;
        int totalEaten = 0;
        int workoutCount = 0;
        int mealCount = 0;
        Workout bestWorkout = null;

        for (int i = 0; i < workouts.length; i++) {
            Workout w = workouts[i];
            if (w.getDate().compareTo(fromDate) < 0 || w.getDate().compareTo(toDate) > 0) {
                continue;
            }
            int[] d = dayEntry(days, w.getDate());
            d[0] += w.getCaloriesBurned();
            d[1] += w.getDuration();
            d[3]++;
            totalBurned += w.getCaloriesBurned();
            totalMinutes += w.getDuration();
            workoutCount++;
            if (bestWorkout == null || w.getCaloriesBurned() > bestWorkout.getCaloriesBurned()) {
                bestWorkout = w;
            }
        }
        for (int i = 0; i < meals.length; i++) {
            Meal m = meals[i];
            if (m.getDate().compareTo(fromDate) < 0 || m.getDate().compareTo(toDate) > 0) {
                continue;
            }
            int[] d = dayEntry(days, m.getDate());
            d[2] += m.getCalories();
            d[4]++;
            totalEaten += m.getCalories();
            mealCount++;
        }

        StringBuilder sb = new StringBuilder();
        String line = "==============================================================\n";
        sb.append(line);
        sb.append("  FITNESS PROGRESS REPORT\n");
        sb.append(line);
        sb.append(String.format("Member    : %s (ID %d)%n", user.getName(), user.getId()));
        sb.append(String.format("Period    : %s to %s%n", isBlank(from) ? "beginning" : fromDate,
                isBlank(to) ? "today" : toDate));
        sb.append(String.format("Generated : %s%n%n", LocalDate.now()));

        sb.append("SUMMARY\n");
        sb.append(String.format("  Workouts logged       : %d%n", workoutCount));
        sb.append(String.format("  Total workout time    : %d min%n", totalMinutes));
        sb.append(String.format("  Calories burned       : %d kcal%n", totalBurned));
        sb.append(String.format("  Meals logged          : %d%n", mealCount));
        sb.append(String.format("  Calories eaten        : %d kcal%n", totalEaten));
        sb.append(String.format("  Net calories          : %d kcal (eaten - burned)%n", totalEaten - totalBurned));
        if (workoutCount > 0) {
            sb.append(String.format("  Average burn/workout  : %d kcal%n", totalBurned / workoutCount));
            sb.append(String.format("  Best workout          : %s (%d kcal on %s)%n", bestWorkout.getName(),
                    bestWorkout.getCaloriesBurned(), bestWorkout.getDate()));
        }

        sb.append("\nDAILY BREAKDOWN (in-order traversal of the BST = date order)\n");
        sb.append(String.format("  %-12s %9s %8s %8s %8s%n", "Date", "Workouts", "Minutes", "Burned", "Eaten"));
        LinkedList<String> dates = days.inOrder();
        if (dates.isEmpty()) {
            sb.append("  (no workouts or meals in this period)\n");
        }
        for (int i = 0; i < dates.size(); i++) {
            int[] d = days.search(dates.get(i));
            sb.append(String.format("  %-12s %9d %8d %8d %8d%n", dates.get(i), d[3], d[1], d[0], d[2]));
        }

        sb.append("\nGOALS\n");
        Goal[] goals = goalService.getGoals(userId);
        if (goals.length == 0) {
            sb.append("  (no goals set)\n");
        }
        for (int i = 0; i < goals.length; i++) {
            Goal g = goals[i];
            sb.append(String.format("  - %s [%s] %d/%d (%d%%) deadline %s%n", g.getTitle(),
                    goalService.statusOf(g), g.getCurrentValue(), g.getTargetValue(),
                    goalService.percentOf(g), g.getDeadline()));
        }
        sb.append(line);
        return sb.toString();
    }

    /** Returns the day's totals, creating an empty entry in the tree the first time a date is seen. */
    private int[] dayEntry(BST days, String date) {
        int[] d = days.search(date);
        if (d == null) {
            d = new int[5];
            days.insert(date, d);
        }
        return d;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
