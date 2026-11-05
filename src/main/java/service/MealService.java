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
import model.Meal;


public class MealService {

    public static final int SORT_CALORIES_ASC = 0;
    public static final int SORT_CALORIES_DESC = 1;
    public static final int SORT_PROTEIN_DESC = 2;
    public static final int SORT_CARBS_ASC = 3;
    public static final int SORT_FAT_ASC = 4;
    public static final int SORT_NAME = 5;

    /** The array that holds the meals of the last load (newest first). */
    private Meal[] meals = new Meal[0];

    public Meal[] getMeals(int userId) throws SQLException {
        loadFromDatabase(userId);
        return meals;
    }

  
    public Meal[] sortedMeals(int userId, int mode) throws SQLException {
        Meal[] all = getMeals(userId);
        if (mode == SORT_CALORIES_ASC) {
            BubbleSort.sort(all, Comparators.MEAL_BY_CALORIES_ASC);
        } else if (mode == SORT_CALORIES_DESC) {
            BubbleSort.sort(all, Comparators.MEAL_BY_CALORIES_DESC);
        } else if (mode == SORT_PROTEIN_DESC) {
            BubbleSort.sort(all, Comparators.MEAL_BY_PROTEIN_DESC);
        } else if (mode == SORT_CARBS_ASC) {
            BubbleSort.sort(all, Comparators.MEAL_BY_CARBS_ASC);
        } else if (mode == SORT_FAT_ASC) {
            BubbleSort.sort(all, Comparators.MEAL_BY_FAT_ASC);
        } else {
            BubbleSort.sort(all, Comparators.MEAL_BY_NAME);
        }
        return all;
    }

   
    public Meal[] searchByName(int userId, String name) throws SQLException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Type a meal name to search for.");
        }
        Meal[] all = getMeals(userId);
        BubbleSort.sort(all, Comparators.MEAL_BY_NAME);

        Meal probe = new Meal();
        probe.setName(name.trim());
        int first = BinarySearch.searchFirst(all, all.length, probe, Comparators.MEAL_BY_NAME);
        if (first < 0) {
            return new Meal[0];
        }
        int last = first;
        while (last + 1 < all.length && Comparators.MEAL_BY_NAME.compare(all[last + 1], probe) == 0) {
            last++;
        }
        Meal[] result = new Meal[last - first + 1];
        for (int i = 0; i < result.length; i++) {
            result[i] = all[first + i];
        }
        return result;
    }

    public void addMeal(Meal m) throws SQLException {
        validate(m);
        String sql = "INSERT INTO meals (user_id, name, calories, protein, carbs, fat, nutritional_info, meal_date) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, m.getUserId());
            ps.setString(2, m.getName());
            ps.setInt(3, m.getCalories());
            ps.setInt(4, m.getProtein());
            ps.setInt(5, m.getCarbs());
            ps.setInt(6, m.getFat());
            ps.setString(7, m.getNutritionalInfo());
            ps.setDate(8, Date.valueOf(m.getDate()));
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new IllegalArgumentException("User ID " + m.getUserId() + " does not exist.");
        }
    }

    public void updateMeal(Meal m) throws SQLException {
        validate(m);
        String sql = "UPDATE meals SET user_id = ?, name = ?, calories = ?, protein = ?, carbs = ?, fat = ?, "
                + "nutritional_info = ?, meal_date = ? WHERE meal_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, m.getUserId());
            ps.setString(2, m.getName());
            ps.setInt(3, m.getCalories());
            ps.setInt(4, m.getProtein());
            ps.setInt(5, m.getCarbs());
            ps.setInt(6, m.getFat());
            ps.setString(7, m.getNutritionalInfo());
            ps.setDate(8, Date.valueOf(m.getDate()));
            ps.setInt(9, m.getMealId());
            if (ps.executeUpdate() == 0) {
                throw new IllegalArgumentException("Meal not found.");
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new IllegalArgumentException("User ID " + m.getUserId() + " does not exist.");
        }
    }

    public void deleteMeal(int mealId) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM meals WHERE meal_id = ?")) {
            ps.setInt(1, mealId);
            ps.executeUpdate();
        }
    }

    /** Counts the rows first so the array can be created with the exact size, then fills it. */
    private void loadFromDatabase(int userId) throws SQLException {
        String where = (userId > 0) ? " WHERE user_id = ?" : "";
        try (Connection con = DBConnection.getConnection()) {
            int count = 0;
            try (PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM meals" + where)) {
                if (userId > 0) {
                    ps.setInt(1, userId);
                }
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        count = rs.getInt(1);
                    }
                }
            }

            Meal[] loaded = new Meal[count];
            String sql = "SELECT meal_id, user_id, name, calories, protein, carbs, fat, nutritional_info, meal_date FROM meals"
                    + where + " ORDER BY meal_date DESC, meal_id DESC";
            int filled = 0;
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                if (userId > 0) {
                    ps.setInt(1, userId);
                }
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next() && filled < count) {
                        loaded[filled] = new Meal(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getInt(4),
                                rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getString(8), rs.getString(9));
                        filled++;
                    }
                }
            }
            if (filled < count) {
                Meal[] smaller = new Meal[filled];
                for (int i = 0; i < filled; i++) {
                    smaller[i] = loaded[i];
                }
                loaded = smaller;
            }
            meals = loaded;
        }
    }

    private void validate(Meal m) {
        if (m.getUserId() <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number.");
        }
        if (m.getName() == null || m.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Meal name is required.");
        }
        m.setName(m.getName().trim());
        if (m.getCalories() < 0 || m.getProtein() < 0 || m.getCarbs() < 0 || m.getFat() < 0) {
            throw new IllegalArgumentException("Calories, protein, carbs and fat cannot be negative.");
        }
        if (m.getNutritionalInfo() == null) {
            m.setNutritionalInfo("");
        }
        m.setNutritionalInfo(m.getNutritionalInfo().trim());
        m.setDate(DateUtil.parse(m.getDate(), "Date"));
    }
}
