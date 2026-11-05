package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import model.Meal;
import model.User;
import service.MealService;

/** Meal log: add, update, delete, binary-search by name, sort by nutritional content. */
public class MealPanel extends JPanel implements ActionListener, ListSelectionListener {

    private final User currentUser;
    private final boolean isAdmin;
    private final MealService service = new MealService();

    private JTextField txtUserId, txtName, txtCalories, txtProtein, txtCarbs, txtFat, txtInfo, txtDate, txtSearch;
    private JComboBox<String> cmbSort;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;
    private JButton btnSearch, btnShowAll, btnSort;
    private JLabel lblStatus;
    private JTable table;
    private DefaultTableModel model;
    private int selectedId = -1;

    public MealPanel(User currentUser) {
        this.currentUser = currentUser;
        this.isAdmin = currentUser.isAdmin();
        setLayout(null);

        add(GuiUtil.makeLabel("User ID", 15, 15, 80, 22));
        txtUserId = GuiUtil.makeField(15, 40, 80, 30);
        add(txtUserId);

        add(GuiUtil.makeLabel("Meal Name", 110, 15, 200, 22));
        txtName = GuiUtil.makeField(110, 40, 220, 30);
        add(txtName);

        add(GuiUtil.makeLabel("Calories", 345, 15, 100, 22));
        txtCalories = GuiUtil.makeField(345, 40, 100, 30);
        add(txtCalories);

        add(GuiUtil.makeLabel("Protein (g)", 460, 15, 90, 22));
        txtProtein = GuiUtil.makeField(460, 40, 90, 30);
        add(txtProtein);

        add(GuiUtil.makeLabel("Carbs (g)", 565, 15, 90, 22));
        txtCarbs = GuiUtil.makeField(565, 40, 90, 30);
        add(txtCarbs);

        add(GuiUtil.makeLabel("Fat (g)", 670, 15, 90, 22));
        txtFat = GuiUtil.makeField(670, 40, 90, 30);
        add(txtFat);

        add(GuiUtil.makeLabel("Nutritional Info (notes)", 15, 80, 300, 22));
        txtInfo = GuiUtil.makeField(15, 105, 440, 30);
        add(txtInfo);

        add(GuiUtil.makeLabel("Date (yyyy-mm-dd)", 470, 80, 150, 22));
        txtDate = GuiUtil.makeField(470, 105, 150, 30);
        add(txtDate);

        btnAdd = GuiUtil.makeButton("Add Meal", 15, 150, 130, 36, this);
        btnUpdate = GuiUtil.makeButton("Update Selected", 155, 150, 170, 36, this);
        btnDelete = GuiUtil.makeButton("Delete Selected", 335, 150, 170, 36, this);
        btnClear = GuiUtil.makeButton("Clear Form", 515, 150, 130, 36, this);
        add(btnAdd);
        add(btnUpdate);
        add(btnDelete);
        add(btnClear);

        txtSearch = GuiUtil.makeField(15, 200, 230, 32);
        add(txtSearch);
        btnSearch = GuiUtil.makeButton("Search Name", 255, 199, 150, 34, this);
        btnShowAll = GuiUtil.makeButton("Show All", 415, 199, 100, 34, this);
        add(btnSearch);
        add(btnShowAll);

        cmbSort = new JComboBox<String>(new String[] {
                "Calories (low to high)", "Calories (high to low)", "Protein (high to low)",
                "Carbs (low to high)", "Fat (low to high)", "Name (A-Z)" });
        cmbSort.setBounds(525, 200, 220, 32);
        cmbSort.setFont(GuiUtil.FIELD_FONT);
        add(cmbSort);
        btnSort = GuiUtil.makeButton("Sort", 755, 199, 100, 34, this);
        add(btnSort);

        lblStatus = GuiUtil.makeLabel(" ", 15, 240, 860, 24);
        add(lblStatus);

        model = GuiUtil.makeModel(new String[] { "ID", "User", "Meal", "Calories", "Protein", "Carbs", "Fat", "Info", "Date" });
        table = GuiUtil.makeTable(model);
        table.getSelectionModel().addListSelectionListener(this);
        add(GuiUtil.makeScroll(table, 15, 270, 860, 360));

        clearForm();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        try {
            if (src == btnAdd) {
                service.addMeal(readForm());
                refreshTable();
                clearForm();
                lblStatus.setText("Meal added.");
            } else if (src == btnUpdate) {
                if (selectedId < 0) {
                    GuiUtil.showError(this, "Click a row in the table first, then press Update Selected.");
                    return;
                }
                Meal m = readForm();
                m.setMealId(selectedId);
                service.updateMeal(m);
                refreshTable();
                clearForm();
                lblStatus.setText("Meal updated.");
            } else if (src == btnDelete) {
                if (selectedId < 0) {
                    GuiUtil.showError(this, "Click a row in the table first, then press Delete Selected.");
                    return;
                }
                if (GuiUtil.confirm(this, "Delete the selected meal?")) {
                    service.deleteMeal(selectedId);
                    refreshTable();
                    clearForm();
                    lblStatus.setText("Meal deleted.");
                }
            } else if (src == btnClear) {
                clearForm();
                lblStatus.setText(" ");
            } else if (src == btnSearch) {
                Meal[] found = service.searchByName(viewUserId(), txtSearch.getText());
                fillTable(found);
                if (found.length == 0) {
                    lblStatus.setText("No meal named \"" + txtSearch.getText().trim() + "\" was found.");
                } else {
                    lblStatus.setText("Binary search found " + found.length + " meal(s).");
                }
            } else if (src == btnShowAll) {
                txtSearch.setText("");
                refreshTable();
                lblStatus.setText("Showing all meals (newest first).");
            } else if (src == btnSort) {
                int mode = cmbSort.getSelectedIndex();
                fillTable(service.sortedMeals(viewUserId(), mode));
                lblStatus.setText("Sorted by " + cmbSort.getSelectedItem() + " with bubble sort.");
            }
        } catch (NumberFormatException ex) {
            GuiUtil.showError(this, "Please enter whole numbers for User ID, Calories, Protein, Carbs and Fat.");
        } catch (IllegalArgumentException ex) {
            GuiUtil.showError(this, ex.getMessage());
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
        txtCalories.setText(model.getValueAt(row, 3).toString());
        txtProtein.setText(model.getValueAt(row, 4).toString());
        txtCarbs.setText(model.getValueAt(row, 5).toString());
        txtFat.setText(model.getValueAt(row, 6).toString());
        txtInfo.setText(model.getValueAt(row, 7).toString());
        txtDate.setText(model.getValueAt(row, 8).toString());
        lblStatus.setText("Row selected. Edit the fields and press Update Selected.");
    }

    public void refreshTable() {
        fillTable(service.getMeals(viewUserId()));
    }

    private void fillTable(Meal[] list) {
        model.setRowCount(0);
        for (int i = 0; i < list.length; i++) {
            Meal m = list[i];
            model.addRow(new Object[] { m.getMealId(), m.getUserId(), m.getName(), m.getCalories(),
                    m.getProtein(), m.getCarbs(), m.getFat(), m.getNutritionalInfo(), m.getDate() });
        }
    }

    private int viewUserId() {
        return isAdmin ? -1 : currentUser.getId();
    }

    private Meal readForm() {
        int userId = isAdmin ? Integer.parseInt(txtUserId.getText().trim()) : currentUser.getId();
        return new Meal(0, userId, txtName.getText().trim(),
                Integer.parseInt(txtCalories.getText().trim()),
                Integer.parseInt(txtProtein.getText().trim()),
                Integer.parseInt(txtCarbs.getText().trim()),
                Integer.parseInt(txtFat.getText().trim()),
                txtInfo.getText().trim(), txtDate.getText().trim());
    }

    private void clearForm() {
        selectedId = -1;
        table.clearSelection();
        txtUserId.setText(String.valueOf(currentUser.getId()));
        txtUserId.setEditable(isAdmin);
        txtName.setText("");
        txtCalories.setText("");
        txtProtein.setText("0");
        txtCarbs.setText("0");
        txtFat.setText("0");
        txtInfo.setText("");
        txtDate.setText(LocalDate.now().toString());
    }
}
