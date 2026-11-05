package gui;

import database.DBConnection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import model.User;
import model.Workout;

public class WorkoutPanel extends JPanel implements ActionListener, ListSelectionListener {

    private final User currentUser;
    private final boolean isAdmin;

    private JTextField txtUserId, txtName, txtDuration, txtCalories, txtDate, txtSearch;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;
    private JButton btnSearch, btnShowAll, btnSortName, btnSortCalories;
    private JLabel lblStatus;
    private JTable table;
    private DefaultTableModel model;
    private int selectedId = -1;

    public WorkoutPanel(User currentUser) {
        this.currentUser = currentUser;
        this.isAdmin = currentUser.isAdmin();
        setLayout(null);

        add(GuiUtil.makeLabel("User ID", 15, 15, 80, 22));
        txtUserId = GuiUtil.makeField(15, 40, 80, 30);
        add(txtUserId);

        add(GuiUtil.makeLabel("Workout Name", 110, 15, 200, 22));
        txtName = GuiUtil.makeField(110, 40, 220, 30);
        add(txtName);

        add(GuiUtil.makeLabel("Duration (min)", 345, 15, 110, 22));
        txtDuration = GuiUtil.makeField(345, 40, 110, 30);
        add(txtDuration);

        add(GuiUtil.makeLabel("Calories Burned", 470, 15, 130, 22));
        txtCalories = GuiUtil.makeField(470, 40, 130, 30);
        add(txtCalories);

        add(GuiUtil.makeLabel("Date (yyyy-mm-dd)", 615, 15, 150, 22));
        txtDate = GuiUtil.makeField(615, 40, 150, 30);
        add(txtDate);

        btnAdd = GuiUtil.makeButton("Add Workout", 15, 85, 140, 36, this);
        btnUpdate = GuiUtil.makeButton("Update Selected", 165, 85, 170, 36, this);
        btnDelete = GuiUtil.makeButton("Delete Selected", 345, 85, 170, 36, this);
        btnClear = GuiUtil.makeButton("Clear Form", 525, 85, 130, 36, this);
        add(btnAdd);
        add(btnUpdate);
        add(btnDelete);
        add(btnClear);

        add(GuiUtil.makeLabel("Search by exact name", 15, 135, 300, 22));
        txtSearch = GuiUtil.makeField(15, 160, 260, 30);
        add(txtSearch);
        btnSearch = GuiUtil.makeButton("Search", 285, 158, 110, 34, this);
        btnShowAll = GuiUtil.makeButton("Show All", 405, 158, 110, 34, this);
        btnSortName = GuiUtil.makeButton("Sort by Name", 525, 158, 140, 34, this);
        btnSortCalories = GuiUtil.makeButton("Sort by Calories", 675, 158, 170, 34, this);
        add(btnSearch);
        add(btnShowAll);
        add(btnSortName);
        add(btnSortCalories);

        lblStatus = GuiUtil.makeLabel(" ", 15, 205, 860, 24);
        add(lblStatus);

        model = GuiUtil.makeModel(new String[] {
            "ID", "User", "Workout", "Duration (min)", "Calories Burned", "Date"
        });
        table = GuiUtil.makeTable(model);
        table.getSelectionModel().addListSelectionListener(this);
        add(GuiUtil.makeScroll(table, 15, 235, 860, 395));

        clearForm();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        try {
            if (src == btnAdd) {
                Workout workout = readForm();
                String sql = "INSERT INTO workouts (user_id, name, duration, calories_burned, workout_date) VALUES (?, ?, ?, ?, ?)";

                try (Connection con = DBConnection.getConnection();
                     PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setInt(1, workout.getUserId());
                    ps.setString(2, workout.getName());
                    ps.setInt(3, workout.getDuration());
                    ps.setInt(4, workout.getCaloriesBurned());
                    ps.setString(5, workout.getDate());
                    ps.executeUpdate();
                }

                refreshTable();
                clearForm();
                lblStatus.setText("Workout added.");

            } else if (src == btnUpdate) {
                if (selectedId < 0) {
                    GuiUtil.showError(this, "Select a row in the table first.");
                    return;
                }

                Workout workout = readForm();
                String sql = "UPDATE workouts SET user_id=?, name=?, duration=?, calories_burned=?, workout_date=? WHERE workout_id=?";

                try (Connection con = DBConnection.getConnection();
                     PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setInt(1, workout.getUserId());
                    ps.setString(2, workout.getName());
                    ps.setInt(3, workout.getDuration());
                    ps.setInt(4, workout.getCaloriesBurned());
                    ps.setString(5, workout.getDate());
                    ps.setInt(6, selectedId);
                    ps.executeUpdate();
                }

                refreshTable();
                clearForm();
                lblStatus.setText("Workout updated.");

            } else if (src == btnDelete) {
                if (selectedId < 0) {
                    GuiUtil.showError(this, "Select a row in the table first.");
                    return;
                }

                if (GuiUtil.confirm(this, "Delete the selected workout?")) {
                    String sql = "DELETE FROM workouts WHERE workout_id=?";

                    try (Connection con = DBConnection.getConnection();
                         PreparedStatement ps = con.prepareStatement(sql)) {
                        ps.setInt(1, selectedId);
                        ps.executeUpdate();
                    }

                    refreshTable();
                    clearForm();
                    lblStatus.setText("Workout deleted.");
                }

            } else if (src == btnClear) {
                clearForm();
                lblStatus.setText(" ");

            } else if (src == btnSearch) {
                searchWorkouts();

            } else if (src == btnShowAll) {
                txtSearch.setText("");
                refreshTable();
                lblStatus.setText("Showing all workouts.");

            } else if (src == btnSortName) {
                sortWorkouts(true);
                lblStatus.setText("Sorted by name.");

            } else if (src == btnSortCalories) {
                sortWorkouts(false);
                lblStatus.setText("Sorted by calories burned.");
            }

        } catch (NumberFormatException ex) {
            GuiUtil.showError(this, "Please enter whole numbers for User ID, Duration and Calories Burned.");
        } catch (IllegalArgumentException ex) {
            GuiUtil.showError(this, ex.getMessage());
        } catch (SQLException ex) {
            GuiUtil.showError(this, "Database error: " + ex.getMessage());
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }

        int row = table.getSelectedRow();

        if (row < 0) {
            return;
        }

        selectedId = Integer.parseInt(model.getValueAt(row, 0).toString());
        txtUserId.setText(model.getValueAt(row, 1).toString());
        txtName.setText(model.getValueAt(row, 2).toString());
        txtDuration.setText(model.getValueAt(row, 3).toString());
        txtCalories.setText(model.getValueAt(row, 4).toString());
        txtDate.setText(model.getValueAt(row, 5).toString());
        lblStatus.setText("Row selected. Edit the fields and press Update Selected.");
    }

    public void refreshTable() {
        String sql = "SELECT workout_id, user_id, name, duration, calories_burned, workout_date FROM workouts";

        if (!isAdmin) {
            sql += " WHERE user_id=?";
        }

        sql += " ORDER BY workout_id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (!isAdmin) {
                ps.setInt(1, currentUser.getId());
            }

            try (ResultSet rs = ps.executeQuery()) {
                model.setRowCount(0);

                while (rs.next()) {
                    model.addRow(new Object[] {
                        rs.getInt("workout_id"),
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getInt("duration"),
                        rs.getInt("calories_burned"),
                        rs.getString("workout_date")
                    });
                }
            }

        } catch (SQLException ex) {
            GuiUtil.showError(this, "Cannot load workouts: " + ex.getMessage());
        }
    }

    private void searchWorkouts() throws SQLException {
        String search = txtSearch.getText().trim();
        ArrayList<Workout> workouts = getWorkouts();

        int low = 0;
        int high = workouts.size() - 1;
        int found = -1;

        while (low <= high) {
            int mid = (low + high) / 2;
            int comparison = workouts.get(mid).getName().compareToIgnoreCase(search);

            if (comparison == 0) {
                found = mid;
                break;
            } else if (comparison < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        model.setRowCount(0);

        if (found >= 0) {
            Workout w = workouts.get(found);
            addWorkoutRow(w);
            lblStatus.setText("Binary search found the workout.");
        } else {
            lblStatus.setText("Workout not found.");
        }
    }

    private void sortWorkouts(boolean byName) throws SQLException {
        ArrayList<Workout> workouts = getWorkouts();

        for (int i = 0; i < workouts.size() - 1; i++) {
            for (int j = 0; j < workouts.size() - i - 1; j++) {
                boolean swap;

                if (byName) {
                    swap = workouts.get(j).getName().compareToIgnoreCase(
                            workouts.get(j + 1).getName()) > 0;
                } else {
                    swap = workouts.get(j).getCaloriesBurned()
                            < workouts.get(j + 1).getCaloriesBurned();
                }

                if (swap) {
                    Workout temp = workouts.get(j);
                    workouts.set(j, workouts.get(j + 1));
                    workouts.set(j + 1, temp);
                }
            }
        }

        model.setRowCount(0);

        for (Workout w : workouts) {
            addWorkoutRow(w);
        }
    }

    private ArrayList<Workout> getWorkouts() throws SQLException {
        ArrayList<Workout> workouts = new ArrayList<>();
        String sql = "SELECT workout_id, user_id, name, duration, calories_burned, workout_date FROM workouts";

        if (!isAdmin) {
            sql += " WHERE user_id=?";
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (!isAdmin) {
                ps.setInt(1, currentUser.getId());
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    workouts.add(new Workout(
                        rs.getInt("workout_id"),
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getInt("duration"),
                        rs.getInt("calories_burned"),
                        rs.getString("workout_date")
                    ));
                }
            }
        }

        for (int i = 0; i < workouts.size() - 1; i++) {
            for (int j = 0; j < workouts.size() - i - 1; j++) {
                if (workouts.get(j).getName().compareToIgnoreCase(
                        workouts.get(j + 1).getName()) > 0) {
                    Workout temp = workouts.get(j);
                    workouts.set(j, workouts.get(j + 1));
                    workouts.set(j + 1, temp);
                }
            }
        }

        return workouts;
    }

    private void addWorkoutRow(Workout w) {
        model.addRow(new Object[] {
            w.getWorkoutId(),
            w.getUserId(),
            w.getName(),
            w.getDuration(),
            w.getCaloriesBurned(),
            w.getDate()
        });
    }

    private Workout readForm() {
        int userId = isAdmin
                ? Integer.parseInt(txtUserId.getText().trim())
                : currentUser.getId();

        String name = txtName.getText().trim();
        int duration = Integer.parseInt(txtDuration.getText().trim());
        int calories = Integer.parseInt(txtCalories.getText().trim());
        String date = txtDate.getText().trim();

        if (name.isEmpty()) {
            throw new IllegalArgumentException("Please enter a workout name.");
        }

        if (duration <= 0 || calories < 0) {
            throw new IllegalArgumentException("Duration must be above 0 and calories cannot be negative.");
        }

        LocalDate.parse(date);

        return new Workout(0, userId, name, duration, calories, date);
    }

    private void clearForm() {
        selectedId = -1;
        table.clearSelection();
        txtUserId.setText(String.valueOf(currentUser.getId()));
        txtUserId.setEditable(isAdmin);
        txtName.setText("");
        txtDuration.setText("");
        txtCalories.setText("");
        txtDate.setText(LocalDate.now().toString());
    }
}
