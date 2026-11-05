package gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import model.Goal;
import model.User;
import service.GoalService;


public class GoalPanel extends JPanel implements ActionListener, ListSelectionListener {

    private final User currentUser;
    private final boolean isAdmin;
    private final GoalService service = new GoalService();

    private JTextField txtUserId, txtTitle, txtTarget, txtStart, txtDeadline;
    private JComboBox<String> cmbType;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnNotify;
    private JLabel lblStatus;
    private JTextArea txtNotifications;
    private JTable table;
    private DefaultTableModel model;
    private int selectedId = -1;

    public GoalPanel(User currentUser) {
        this.currentUser = currentUser;
        this.isAdmin = currentUser.isAdmin();
        setLayout(null);

        add(GuiUtil.makeLabel("User ID", 15, 15, 80, 22));
        txtUserId = GuiUtil.makeField(15, 40, 80, 30);
        add(txtUserId);

        add(GuiUtil.makeLabel("Goal Title", 110, 15, 200, 22));
        txtTitle = GuiUtil.makeField(110, 40, 250, 30);
        add(txtTitle);

        add(GuiUtil.makeLabel("Goal Type", 375, 15, 150, 22));
        cmbType = new JComboBox<String>(new String[] { Goal.BURN_CALORIES, Goal.WORKOUT_MINUTES, Goal.EAT_LIMIT });
        cmbType.setBounds(375, 40, 200, 30);
        cmbType.setFont(GuiUtil.FIELD_FONT);
        add(cmbType);

        add(GuiUtil.makeLabel("Target", 590, 15, 100, 22));
        txtTarget = GuiUtil.makeField(590, 40, 120, 30);
        add(txtTarget);

        add(GuiUtil.makeLabel("Start Date (yyyy-mm-dd)", 15, 80, 180, 22));
        txtStart = GuiUtil.makeField(15, 105, 160, 30);
        add(txtStart);

        add(GuiUtil.makeLabel("Deadline (yyyy-mm-dd)", 190, 80, 180, 22));
        txtDeadline = GuiUtil.makeField(190, 105, 160, 30);
        add(txtDeadline);

        btnAdd = GuiUtil.makeButton("Add Goal", 15, 150, 130, 36, this);
        btnUpdate = GuiUtil.makeButton("Update Selected", 155, 150, 170, 36, this);
        btnDelete = GuiUtil.makeButton("Delete Selected", 335, 150, 170, 36, this);
        btnClear = GuiUtil.makeButton("Clear Form", 515, 150, 130, 36, this);
        btnNotify = GuiUtil.makeButton("Refresh Notifications", 655, 150, 200, 36, this);
        add(btnAdd);
        add(btnUpdate);
        add(btnDelete);
        add(btnClear);
        add(btnNotify);

        JLabel hint = GuiUtil.makeLabel("BURN_CALORIES / WORKOUT_MINUTES: reach the target.   "
                + "EAT_LIMIT: stay at or below the target.   Progress counts logs between start date and deadline.",
                15, 192, 860, 20);
        hint.setFont(new Font("SansSerif", Font.PLAIN, 11));
        add(hint);

        lblStatus = GuiUtil.makeLabel(" ", 15, 214, 860, 22);
        add(lblStatus);

        model = GuiUtil.makeModel(new String[] { "ID", "User", "Title", "Type", "Start", "Deadline",
                "Target", "Progress", "Status" });
        table = GuiUtil.makeTable(model);
        table.getSelectionModel().addListSelectionListener(this);
        add(GuiUtil.makeScroll(table, 15, 240, 860, 235));

        add(GuiUtil.makeLabel("Notifications (nearest deadline first)", 15, 482, 400, 22));
        txtNotifications = new JTextArea();
        txtNotifications.setEditable(false);
        txtNotifications.setFont(new Font("Monospaced", Font.PLAIN, 13));
        add(GuiUtil.makeScroll(txtNotifications, 15, 508, 860, 130));

        clearForm();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        try {
            if (src == btnAdd) {
                service.addGoal(readForm());
                refreshTable();
                clearForm();
                lblStatus.setText("Goal added.");
            } else if (src == btnUpdate) {
                if (selectedId < 0) {
                    GuiUtil.showError(this, "Select a row in the table first, then press Update Selected.");
                    return;
                }
                Goal g = readForm();
                g.setGoalId(selectedId);
                service.updateGoal(g);
                refreshTable();
                clearForm();
                lblStatus.setText("Goal updated.");
            } else if (src == btnDelete) {
                if (selectedId < 0) {
                    GuiUtil.showError(this, "Select a row in the table first, then press Delete Selected.");
                    return;
                }
                if (GuiUtil.confirm(this, "Delete the selected goal?")) {
                    service.deleteGoal(selectedId);
                    refreshTable();
                    clearForm();
                    lblStatus.setText("Goal deleted.");
                }
            } else if (src == btnClear) {
                clearForm();
                lblStatus.setText(" ");
            } else if (src == btnNotify) {
                refreshNotifications();
                lblStatus.setText("Notifications refreshed.");
            }
        } catch (NumberFormatException ex) {
            GuiUtil.showError(this, "Please enter whole numbers for User ID and Target.");
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
        txtTitle.setText(model.getValueAt(row, 2).toString());
        cmbType.setSelectedItem(model.getValueAt(row, 3).toString());
        txtStart.setText(model.getValueAt(row, 4).toString());
        txtDeadline.setText(model.getValueAt(row, 5).toString());
        txtTarget.setText(model.getValueAt(row, 6).toString());
        lblStatus.setText("Row selected. Edit the fields and press Update Selected.");
    }

    public void refreshTable() {
        try {
            Goal[] goals = service.getGoals(viewUserId());
            model.setRowCount(0);
            for (int i = 0; i < goals.length; i++) {
                Goal g = goals[i];
                String progress = g.getCurrentValue() + " (" + service.percentOf(g) + "%)";
                model.addRow(new Object[] { g.getGoalId(), g.getUserId(), g.getTitle(), g.getType(),
                        g.getStartDate(), g.getDeadline(), g.getTargetValue(), progress, service.statusOf(g) });
            }
            refreshNotifications();
        } catch (SQLException ex) {
            GuiUtil.showError(this, "Cannot load goals: " + ex.getMessage());
        }
    }

    private void refreshNotifications() {
        try {
            List<String> messages = service.getNotifications(viewUserId());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < messages.size(); i++) {
                sb.append(messages.get(i)).append("\n");
            }
            if (messages.isEmpty()) {
                sb.append("No goals yet. Add one above to start getting notifications.");
            }
            txtNotifications.setText(sb.toString());
            txtNotifications.setCaretPosition(0);
        } catch (SQLException ex) {
            GuiUtil.showError(this, "Cannot load notifications: " + ex.getMessage());
        }
    }

    private int viewUserId() {
        return isAdmin ? -1 : currentUser.getId();
    }

    private Goal readForm() {
        int userId = isAdmin ? Integer.parseInt(txtUserId.getText().trim()) : currentUser.getId();
        return new Goal(0, userId, txtTitle.getText().trim(), (String) cmbType.getSelectedItem(),
                Integer.parseInt(txtTarget.getText().trim()),
                txtStart.getText().trim(), txtDeadline.getText().trim());
    }

    private void clearForm() {
        selectedId = -1;
        table.clearSelection();
        txtUserId.setText(String.valueOf(currentUser.getId()));
        txtUserId.setEditable(isAdmin);
        txtTitle.setText("");
        cmbType.setSelectedIndex(0);
        txtTarget.setText("");
        txtStart.setText(LocalDate.now().toString());
        txtDeadline.setText(LocalDate.now().plusDays(30).toString());
    }
}
