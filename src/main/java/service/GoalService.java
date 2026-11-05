package service;

import database.DBConnection;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.PriorityQueue;
import model.Goal;


public class GoalService {

    public Goal[] getGoals(int userId) throws SQLException {
        LinkedList<Goal> list = new LinkedList<Goal>();
        String sql = "SELECT goal_id, user_id, title, goal_type, target_value, start_date, deadline FROM goals"
                + (userId > 0 ? " WHERE user_id = ?" : "")
                + " ORDER BY deadline, goal_id";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (userId > 0) {
                ps.setInt(1, userId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Goal(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4),
                            rs.getInt(5), rs.getString(6), rs.getString(7)));
                }
            }
            for (int i = 0; i < list.size(); i++) {
                Goal g = list.get(i);
                g.setCurrentValue(computeProgress(con, g));
            }
        }
        return list.toArray(new Goal[0]);
    }

    /** One line per goal, nearest deadline first (PriorityQueue). */
    public LinkedList<String> getNotifications(int userId) throws SQLException {
        Goal[] goals = getGoals(userId);
        PriorityQueue<Goal> queue = new PriorityQueue<Goal>(11, Comparators.GOAL_BY_DEADLINE);
        for (int i = 0; i < goals.length; i++) {
            queue.add(goals[i]);
        }
        LinkedList<String> messages = new LinkedList<String>();
        while (!queue.isEmpty()) {
            messages.add(buildMessage(queue.poll(), userId < 0));
        }
        return messages;
    }

    public String statusOf(Goal g) {
        LocalDate today = LocalDate.now();
        LocalDate deadline = LocalDate.parse(g.getDeadline());
        if (Goal.EAT_LIMIT.equals(g.getType())) {
            if (g.getCurrentValue() > g.getTargetValue()) {
                return "EXCEEDED LIMIT";
            }
            return today.isAfter(deadline) ? "ACHIEVED" : "ON TRACK";
        }
        if (g.getCurrentValue() >= g.getTargetValue()) {
            return "ACHIEVED";
        }
        return today.isAfter(deadline) ? "MISSED" : "IN PROGRESS";
    }

    public int percentOf(Goal g) {
        if (g.getTargetValue() <= 0) {
            return 0;
        }
        return (int) ((long) g.getCurrentValue() * 100 / g.getTargetValue());
    }

    public void addGoal(Goal g) throws SQLException {
        validate(g);
        String sql = "INSERT INTO goals (user_id, title, goal_type, target_value, start_date, deadline) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, g.getUserId());
            ps.setString(2, g.getTitle());
            ps.setString(3, g.getType());
            ps.setInt(4, g.getTargetValue());
            ps.setDate(5, Date.valueOf(g.getStartDate()));
            ps.setDate(6, Date.valueOf(g.getDeadline()));
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new IllegalArgumentException("User ID " + g.getUserId() + " does not exist.");
        }
    }

    public void updateGoal(Goal g) throws SQLException {
        validate(g);
        String sql = "UPDATE goals SET user_id = ?, title = ?, goal_type = ?, target_value = ?, start_date = ?, deadline = ? WHERE goal_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, g.getUserId());
            ps.setString(2, g.getTitle());
            ps.setString(3, g.getType());
            ps.setInt(4, g.getTargetValue());
            ps.setDate(5, Date.valueOf(g.getStartDate()));
            ps.setDate(6, Date.valueOf(g.getDeadline()));
            ps.setInt(7, g.getGoalId());
            if (ps.executeUpdate() == 0) {
                throw new IllegalArgumentException("Goal not found.");
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new IllegalArgumentException("User ID " + g.getUserId() + " does not exist.");
        }
    }

    public void deleteGoal(int goalId) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM goals WHERE goal_id = ?")) {
            ps.setInt(1, goalId);
            ps.executeUpdate();
        }
    }

   
    private int computeProgress(Connection con, Goal g) throws SQLException {
        String sql;
        if (Goal.BURN_CALORIES.equals(g.getType())) {
            sql = "SELECT COALESCE(SUM(calories_burned), 0) FROM workouts WHERE user_id = ? AND workout_date BETWEEN ? AND ?";
        } else if (Goal.WORKOUT_MINUTES.equals(g.getType())) {
            sql = "SELECT COALESCE(SUM(duration), 0) FROM workouts WHERE user_id = ? AND workout_date BETWEEN ? AND ?";
        } else {
            sql = "SELECT COALESCE(SUM(calories), 0) FROM meals WHERE user_id = ? AND meal_date BETWEEN ? AND ?";
        }
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, g.getUserId());
            ps.setDate(2, Date.valueOf(g.getStartDate()));
            ps.setDate(3, Date.valueOf(g.getDeadline()));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    private String buildMessage(Goal g, boolean showUser) {
        String status = statusOf(g);
        String prefix = showUser ? "[User " + g.getUserId() + "] " : "";
        String progress = g.getCurrentValue() + "/" + g.getTargetValue() + " (" + percentOf(g) + "%)";
        long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(g.getDeadline()));

        if ("ACHIEVED".equals(status)) {
            return prefix + "ACHIEVED: " + g.getTitle() + " - " + progress;
        } else if ("MISSED".equals(status)) {
            return prefix + "MISSED: " + g.getTitle() + " - ended " + g.getDeadline() + ", reached " + progress;
        } else if ("EXCEEDED LIMIT".equals(status)) {
            return prefix + "WARNING: " + g.getTitle() + " - over the limit " + progress;
        } else if (daysLeft == 0) {
            return prefix + "DUE TODAY: " + g.getTitle() + " - " + progress;
        } else {
            return prefix + g.getTitle() + " - " + daysLeft + " day(s) left, " + progress;
        }
    }

    private void validate(Goal g) {
        if (g.getUserId() <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number.");
        }
        if (g.getTitle() == null || g.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Goal title is required.");
        }
        g.setTitle(g.getTitle().trim());
        if (!Goal.BURN_CALORIES.equals(g.getType()) && !Goal.WORKOUT_MINUTES.equals(g.getType())
                && !Goal.EAT_LIMIT.equals(g.getType())) {
            throw new IllegalArgumentException("Unknown goal type.");
        }
        if (g.getTargetValue() <= 0) {
            throw new IllegalArgumentException("Target must be greater than 0.");
        }
        g.setStartDate(DateUtil.parse(g.getStartDate(), "Start date"));
        g.setDeadline(DateUtil.parse(g.getDeadline(), "Deadline"));
        if (g.getDeadline().compareTo(g.getStartDate()) < 0) {
            throw new IllegalArgumentException("Deadline cannot be before the start date.");
        }
    }
}
