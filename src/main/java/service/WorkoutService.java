package service;

import database.DBConnection;
import ds.BinarySearch;
import ds.BubbleSort;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import model.Workout;

/**
 * Workouts: add / update / delete (JDBC) plus array storage, sorting and binary search.
 * The workouts are loaded into a plain Workout[] array (Week 2: array of objects).
 * userId = -1 means "all users" (admin view).
 */
public class WorkoutService {

    /** The array that holds the workouts of the last load (newest first). */
    private Workout[] workouts = new Workout[0];

    public Workout[] getWorkouts(int userId) throws SQLException {
        loadFromDatabase(userId);
        return workouts;
    }

    /** Sorted by calories burned, highest first (bubble sort). */
    public Workout[] sortedByCalories(int userId) throws SQLException {
        Workout[] all = getWorkouts(userId);
        BubbleSort.sort(all, Comparators.WORKOUT_BY_CALORIES_DESC);
        return all;
    }

    /** Sorted by name A-Z (bubble sort). */
    public Workout[] sortedByName(int userId) throws SQLException {
        Workout[] all = getWorkouts(userId);
        BubbleSort.sort(all, Comparators.WORKOUT_BY_NAME);
        return all;
    }

    /**
     * Exact name search (upper/lower case does not matter):
     * 1) sort the array by name   2) binary search for the first match
     * 3) walk to the right to collect every workout with the same name.
     */
    public Workout[] searchByName(int userId, String name) throws SQLException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Type a workout name to search for.");
        }
        Workout[] all = getWorkouts(userId);
        BubbleSort.sort(all, Comparators.WORKOUT_BY_NAME);

        Workout probe = new Workout();
        probe.setName(name.trim());
        int first = BinarySearch.searchFirst(all, all.length, probe, Comparators.WORKOUT_BY_NAME);
        if (first < 0) {
            return new Workout[0];
        }
        int last = first;
        while (last + 1 < all.length && Comparators.WORKOUT_BY_NAME.compare(all[last + 1], probe) == 0) {
            last++;
        }
        Workout[] result = new Workout[last - first + 1];
        for (int i = 0; i < result.length; i++) {
            result[i] = all[first + i];
        }
        return result;
    }

    public void addWorkout(Workout w) throws SQLException {
        validate(w);
        String sql = "INSERT INTO workouts (user_id, name, duration, calories_burned, workout_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, w.getUserId());
            ps.setString(2, w.getName());
            ps.setInt(3, w.getDuration());
            ps.setInt(4, w.getCaloriesBurned());
            ps.setDate(5, Date.valueOf(w.getDate()));
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new IllegalArgumentException("User ID " + w.getUserId() + " does not exist.");
        }
    }

    public void updateWorkout(Workout w) throws SQLException {
        validate(w);
        String sql = "UPDATE workouts SET user_id = ?, name = ?, duration = ?, calories_burned = ?, workout_date = ? WHERE workout_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, w.getUserId());
            ps.setString(2, w.getName());
            ps.setInt(3, w.getDuration());
            ps.setInt(4, w.getCaloriesBurned());
            ps.setDate(5, Date.valueOf(w.getDate()));
            ps.setInt(6, w.getWorkoutId());
            if (ps.executeUpdate() == 0) {
                throw new IllegalArgumentException("Workout not found.");
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new IllegalArgumentException("User ID " + w.getUserId() + " does not exist.");
        }
    }

    public void deleteWorkout(int workoutId) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM workouts WHERE workout_id = ?")) {
            ps.setInt(1, workoutId);
            ps.executeUpdate();
        }
    }

    /** Counts the rows first so the array can be created with the exact size, then fills it. */
    private void loadFromDatabase(int userId) throws SQLException {
        String where = (userId > 0) ? " WHERE user_id = ?" : "";
        try (Connection con = DBConnection.getConnection()) {
            int count = 0;
            try (PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM workouts" + where)) {
                if (userId > 0) {
                    ps.setInt(1, userId);
                }
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        count = rs.getInt(1);
                    }
                }
            }

            Workout[] loaded = new Workout[count];
            String sql = "SELECT workout_id, user_id, name, duration, calories_burned, workout_date FROM workouts"
                    + where + " ORDER BY workout_date DESC, workout_id DESC";
            int filled = 0;
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                if (userId > 0) {
                    ps.setInt(1, userId);
                }
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next() && filled < count) {
                        loaded[filled] = new Workout(rs.getInt(1), rs.getInt(2), rs.getString(3),
                                rs.getInt(4), rs.getInt(5), rs.getString(6));
                        filled++;
                    }
                }
            }
            if (filled < count) {          // rows disappeared between the two queries
                Workout[] smaller = new Workout[filled];
                for (int i = 0; i < filled; i++) {
                    smaller[i] = loaded[i];
                }
                loaded = smaller;
            }
            workouts = loaded;
        }
    }

    private void validate(Workout w) {
        if (w.getUserId() <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number.");
        }
        if (w.getName() == null || w.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Workout name is required.");
        }
        w.setName(w.getName().trim());
        if (w.getDuration() <= 0) {
            throw new IllegalArgumentException("Duration must be greater than 0 minutes.");
        }
        if (w.getCaloriesBurned() < 0) {
            throw new IllegalArgumentException("Calories burned cannot be negative.");
        }
        w.setDate(DateUtil.parse(w.getDate(), "Date"));
    }
}
