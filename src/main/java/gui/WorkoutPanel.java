package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
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
import service.WorkoutService;

/** Workout log: add, update, delete, binary-search by name, sort by calories. */
public class WorkoutPanel extends JPanel implements ActionListener, ListSelectionListener {

    private final User currentUser;
    private final boolean isAdmin;
    private final WorkoutService service = new WorkoutService();

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

        add(GuiUtil.makeLabel("Search by exact name (binary search)", 15, 135, 300, 22));
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

        model = GuiUtil.makeModel(new String[] { "ID", "User", "Workout", "Duration (min)", "Calories Burned", "Date" });
        table = GuiUtil.makeTable(model);
        table.getSelectionModel().addListSelectionListener(this);
        add(GuiUtil.makeScroll(table, 15, 235, 860, 395));

        clearForm();
    }

    // ---------------------------------------------------------------- buttons
    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        try {
            if (src == btnAdd) {
                service.addWorkout(readForm());
                refreshTable();
                clearForm();
                lblStatus.setText("Workout added.");
            } else if (src == btnUpdate) {
                if (selectedId < 0) {
                    GuiUtil.showError(this, "Select a row in the table first, then press Update Selected.");
                    return;
                }
                Workout w = readForm();
                w.setWorkoutId(selectedId);
                service.updateWorkout(w);
                refreshTable();
                clearForm();
                lblStatus.setText("Workout updated.");
            } else if (src == btnDelete) {
                if (selectedId < 0) {
                    GuiUtil.showError(this, "Select a row in the table first, then press Delete Selected.");
                    return;
                }
                if (GuiUtil.confirm(this, "Delete the selected workout?")) {
                    service.deleteWorkout(selectedId);
                    refreshTable();
                    clearForm();
                    lblStatus.setText("Workout deleted.");
                }
            } else if (src == btnClear) {
                clearForm();
                lblStatus.setText(" ");
            } else if (src == btnSearch) {
                Workout[] found = service.searchByName(viewUserId(), txtSearch.getText());
                fillTable(found);
                if (found.length == 0) {
                    lblStatus.setText("No workout named \"" + txtSearch.getText().trim() + "\" was found.");
                } else {
                    lblStatus.setText("Binary search found " + found.length + " workout(s).");
                }
            } else if (src == btnShowAll) {
                txtSearch.setText("");
                refreshTable();
                lblStatus.setText("Showing all workouts (newest first).");
            } else if (src == btnSortName) {
                fillTable(service.sortedByName(viewUserId()));
                lblStatus.setText("Sorted by name (A-Z) with bubble sort.");
            } else if (src == btnSortCalories) {
                fillTable(service.sortedByCalories(viewUserId()));
                lblStatus.setText("Sorted by calories burned (highest first) with bubble sort.");
            }
        } catch (NumberFormatException ex) {
            GuiUtil.showError(this, "Please enter whole numbers for User ID, Duration and Calories Burned.");
        } catch (IllegalArgumentException ex) {
            GuiUtil.showError(this, ex.getMessage());
        } catch (SQLException ex) {
            GuiUtil.showError(this, "Database error: " + ex.getMessage());
        }
    }

    // --------------------------------------------- click a row -> fill the form
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

    // ---------------------------------------------------------------- helpers
    public void refreshTable() {
        try {
            fillTable(service.getWorkouts(viewUserId()));
        } catch (SQLException ex) {
            GuiUtil.showError(this, "Cannot load workouts: " + ex.getMessage());
        }
    }

    private void fillTable(Workout[] list) {
        model.setRowCount(0);
        for (int i = 0; i < list.length; i++) {
            Workout w = list[i];
            model.addRow(new Object[] { w.getWorkoutId(), w.getUserId(), w.getName(),
                    w.getDuration(), w.getCaloriesBurned(), w.getDate() });
        }
    }

    /** -1 = every user (admin), otherwise only the logged-in member's rows. */
    private int viewUserId() {
        return isAdmin ? -1 : currentUser.getId();
    }

    private Workout readForm() {
        int userId = isAdmin ? Integer.parseInt(txtUserId.getText().trim()) : currentUser.getId();
        String name = txtName.getText().trim();
        int duration = Integer.parseInt(txtDuration.getText().trim());
        int calories = Integer.parseInt(txtCalories.getText().trim());
        String date = txtDate.getText().trim();
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
