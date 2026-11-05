package gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import model.User;
import service.ReportService;

/** Progress report generated from the user's input (user id + optional date range). */
public class ReportPanel extends JPanel implements ActionListener {

    private final User currentUser;
    private final boolean isAdmin;
    private final ReportService service = new ReportService();

    private JTextField txtUserId, txtFrom, txtTo;
    private JButton btnGenerate, btnExport;
    private JLabel lblStatus;
    private JTextArea txtReport;

    public ReportPanel(User currentUser) {
        this.currentUser = currentUser;
        this.isAdmin = currentUser.isAdmin();
        setLayout(null);

        add(GuiUtil.makeLabel("User ID", 15, 15, 80, 22));
        txtUserId = GuiUtil.makeField(15, 40, 100, 30);
        txtUserId.setText(String.valueOf(currentUser.getId()));
        txtUserId.setEditable(isAdmin);
        add(txtUserId);

        add(GuiUtil.makeLabel("From (yyyy-mm-dd, optional)", 130, 15, 200, 22));
        txtFrom = GuiUtil.makeField(130, 40, 170, 30);
        add(txtFrom);

        add(GuiUtil.makeLabel("To (yyyy-mm-dd, optional)", 315, 15, 200, 22));
        txtTo = GuiUtil.makeField(315, 40, 170, 30);
        add(txtTo);

        btnGenerate = GuiUtil.makeButton("Generate Report", 500, 38, 170, 34, this);
        btnExport = GuiUtil.makeButton("Export to .txt", 680, 38, 170, 34, this);
        add(btnGenerate);
        add(btnExport);

        lblStatus = GuiUtil.makeLabel("Leave the dates empty to include everything.", 15, 82, 860, 24);
        add(lblStatus);

        txtReport = new JTextArea();
        txtReport.setEditable(false);
        txtReport.setFont(new Font("Monospaced", Font.PLAIN, 13));
        add(GuiUtil.makeScroll(txtReport, 15, 112, 860, 518));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        try {
            if (src == btnGenerate) {
                int userId = isAdmin ? Integer.parseInt(txtUserId.getText().trim()) : currentUser.getId();
                txtReport.setText(service.generate(userId, txtFrom.getText(), txtTo.getText()));
                txtReport.setCaretPosition(0);
                lblStatus.setText("Report generated.");
            } else if (src == btnExport) {
                if (txtReport.getText().trim().isEmpty()) {
                    GuiUtil.showError(this, "Generate a report first.");
                    return;
                }
                JFileChooser chooser = new JFileChooser();
                chooser.setSelectedFile(new File("progress_report.txt"));
                if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    FileWriter writer = new FileWriter(chooser.getSelectedFile());
                    try {
                        writer.write(txtReport.getText());
                    } finally {
                        writer.close();
                    }
                    lblStatus.setText("Saved to " + chooser.getSelectedFile().getAbsolutePath());
                }
            }
        } catch (NumberFormatException ex) {
            GuiUtil.showError(this, "User ID must be a whole number.");
        } catch (IllegalArgumentException ex) {
            GuiUtil.showError(this, ex.getMessage());
        } catch (IOException ex) {
            GuiUtil.showError(this, "Could not save the file: " + ex.getMessage());
        } catch (SQLException ex) {
            GuiUtil.showError(this, "Database error: " + ex.getMessage());
        }
    }

    public void refreshTable() {
        // nothing to reload: the report is only built when the button is pressed
    }
}
