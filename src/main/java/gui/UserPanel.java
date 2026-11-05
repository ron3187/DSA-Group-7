package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import model.User;
import service.UserService;

/** Admin only: manage user accounts. */
public class UserPanel extends JPanel implements ActionListener, ListSelectionListener {

    private final User currentUser;
    private final UserService service = new UserService();

    private JTextField txtUsername, txtName;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRole;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;
    private JLabel lblStatus;
    private JTable table;
    private DefaultTableModel model;
    private int selectedId = -1;

    public UserPanel(User currentUser) {
        this.currentUser = currentUser;
        setLayout(null);

        add(GuiUtil.makeLabel("Username", 15, 15, 180, 22));
        txtUsername = GuiUtil.makeField(15, 40, 180, 30);
        add(txtUsername);

        add(GuiUtil.makeLabel("Password (blank = keep old)", 210, 15, 200, 22));
        txtPassword = new JPasswordField();
        txtPassword.setBounds(210, 40, 190, 30);
        txtPassword.setFont(GuiUtil.FIELD_FONT);
        add(txtPassword);

        add(GuiUtil.makeLabel("Full Name", 415, 15, 200, 22));
        txtName = GuiUtil.makeField(415, 40, 250, 30);
        add(txtName);

        add(GuiUtil.makeLabel("Role", 680, 15, 100, 22));
        cmbRole = new JComboBox<String>(new String[] { "MEMBER", "ADMIN" });
        cmbRole.setBounds(680, 40, 150, 30);
        cmbRole.setFont(GuiUtil.FIELD_FONT);
        add(cmbRole);

        btnAdd = GuiUtil.makeButton("Add User", 15, 90, 130, 36, this);
        btnUpdate = GuiUtil.makeButton("Update Selected", 155, 90, 170, 36, this);
        btnDelete = GuiUtil.makeButton("Delete Selected", 335, 90, 170, 36, this);
        btnClear = GuiUtil.makeButton("Clear Form", 515, 90, 130, 36, this);
        add(btnAdd);
        add(btnUpdate);
        add(btnDelete);
        add(btnClear);

        lblStatus = GuiUtil.makeLabel(" ", 15, 138, 860, 24);
        add(lblStatus);

        model = GuiUtil.makeModel(new String[] { "ID", "Username", "Full Name", "Role" });
        table = GuiUtil.makeTable(model);
        table.getSelectionModel().addListSelectionListener(this);
        add(GuiUtil.makeScroll(table, 15, 170, 860, 460));

        clearForm();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        try {
            if (src == btnAdd) {
                service.addUser(readForm(), new String(txtPassword.getPassword()));
                refreshTable();
                clearForm();
                lblStatus.setText("User added.");
            } else if (src == btnUpdate) {
                if (selectedId < 0) {
                    GuiUtil.showError(this, "Click a row in the table first, then press Update Selected.");
                    return;
                }
                User u = readForm();
                u.setId(selectedId);
                service.updateUser(u, new String(txtPassword.getPassword()));
                refreshTable();
                clearForm();
                lblStatus.setText("User updated.");
            } else if (src == btnDelete) {
                if (selectedId < 0) {
                    GuiUtil.showError(this, "Click a row in the table first, then press Delete Selected.");
                    return;
                }
                if (selectedId == currentUser.getId()) {
                    GuiUtil.showError(this, "You cannot delete the account you are logged in with.");
                    return;
                }
                if (GuiUtil.confirm(this, "Delete this user AND all of their workouts, meals and goals?")) {
                    service.deleteUser(selectedId);
                    refreshTable();
                    clearForm();
                    lblStatus.setText("User deleted.");
                }
            } else if (src == btnClear) {
                clearForm();
                lblStatus.setText(" ");
            }
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
        txtUsername.setText(model.getValueAt(row, 1).toString());
        txtName.setText(model.getValueAt(row, 2).toString());
        cmbRole.setSelectedItem(model.getValueAt(row, 3).toString());
        txtPassword.setText("");
        lblStatus.setText("Row selected. Edit the fields and press Update Selected.");
    }

    public void refreshTable() {
        User[] users = service.getAll();
        model.setRowCount(0);
        for (int i = 0; i < users.length; i++) {
            User u = users[i];
            model.addRow(new Object[] { u.getId(), u.getUsername(), u.getName(), u.getRole() });
        }
    }

    private User readForm() {
        User u = new User();
        u.setUsername(txtUsername.getText().trim());
        u.setName(txtName.getText().trim());
        u.setRole((String) cmbRole.getSelectedItem());
        return u;
    }

    private void clearForm() {
        selectedId = -1;
        table.clearSelection();
        txtUsername.setText("");
        txtPassword.setText("");
        txtName.setText("");
        cmbRole.setSelectedIndex(0);
    }
}
