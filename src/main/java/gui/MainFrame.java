package gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import model.User;
import service.GoalService;

/** Main window: navigation buttons on the left, one view panel shown at a time on the right. */
public class MainFrame extends JFrame implements ActionListener {

    private final User currentUser;

    private JLabel lblTitle, lblUser;
    private JButton btnWorkouts, btnMeals, btnGoals, btnReports, btnUsers, btnLogout;
    private WorkoutPanel workoutPanel;
    private MealPanel mealPanel;
    private GoalPanel goalPanel;
    private ReportPanel reportPanel;
    private UserPanel userPanel;     // only created for admins

    public MainFrame(User currentUser) {
        this.currentUser = currentUser;

        setTitle("Fitness Tracker");
        setSize(1100, 780);
        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        lblTitle = new JLabel("Fitness Tracker - Workouts");
        lblTitle.setBounds(20, 12, 500, 40);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(lblTitle);

        lblUser = new JLabel("Logged in: " + currentUser.getName() + " (" + currentUser.getRole() + ")");
        lblUser.setBounds(560, 15, 380, 30);
        lblUser.setHorizontalAlignment(SwingConstants.RIGHT);
        lblUser.setFont(new Font("SansSerif", Font.PLAIN, 14));
        add(lblUser);

        btnLogout = GuiUtil.makeButton("Logout", 955, 12, 110, 36, this);
        add(btnLogout);

        btnWorkouts = GuiUtil.makeButton("Workouts", 15, 80, 160, 48, this);
        btnMeals = GuiUtil.makeButton("Meals", 15, 138, 160, 48, this);
        btnGoals = GuiUtil.makeButton("Goals", 15, 196, 160, 48, this);
        btnReports = GuiUtil.makeButton("Reports", 15, 254, 160, 48, this);
        add(btnWorkouts);
        add(btnMeals);
        add(btnGoals);
        add(btnReports);

        workoutPanel = new WorkoutPanel(currentUser);
        mealPanel = new MealPanel(currentUser);
        goalPanel = new GoalPanel(currentUser);
        reportPanel = new ReportPanel(currentUser);

        workoutPanel.setBounds(190, 80, 890, 650);
        mealPanel.setBounds(190, 80, 890, 650);
        goalPanel.setBounds(190, 80, 890, 650);
        reportPanel.setBounds(190, 80, 890, 650);
        add(workoutPanel);
        add(mealPanel);
        add(goalPanel);
        add(reportPanel);

        if (currentUser.isAdmin()) {
            btnUsers = GuiUtil.makeButton("Users", 15, 312, 160, 48, this);
            add(btnUsers);
            userPanel = new UserPanel(currentUser);
            userPanel.setBounds(190, 80, 890, 650);
            add(userPanel);
        }

        showPanel(workoutPanel, "Workouts");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == btnWorkouts) {
            showPanel(workoutPanel, "Workouts");
        } else if (src == btnMeals) {
            showPanel(mealPanel, "Meals");
        } else if (src == btnGoals) {
            showPanel(goalPanel, "Goals");
        } else if (src == btnReports) {
            showPanel(reportPanel, "Reports");
        } else if (src == btnUsers) {
            showPanel(userPanel, "Users");
        } else if (src == btnLogout) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }

    /** Hides every view panel, then shows (and refreshes) the chosen one. */
    private void showPanel(JPanel panel, String title) {
        workoutPanel.setVisible(false);
        mealPanel.setVisible(false);
        goalPanel.setVisible(false);
        reportPanel.setVisible(false);
        if (userPanel != null) {
            userPanel.setVisible(false);
        }

        if (panel == workoutPanel) {
            workoutPanel.refreshTable();
        } else if (panel == mealPanel) {
            mealPanel.refreshTable();
        } else if (panel == goalPanel) {
            goalPanel.refreshTable();
        } else if (panel == reportPanel) {
            reportPanel.refreshTable();
        } else if (panel == userPanel) {
            userPanel.refreshTable();
        }
        panel.setVisible(true);
        lblTitle.setText("Fitness Tracker - " + title);
    }

    /** Pop-up with the goal notifications (called once right after login). */
    public void showStartupNotifications() {
        GoalService goalService = new GoalService();
        int viewUserId = currentUser.isAdmin() ? -1 : currentUser.getId();
        try {
            List<String> messages = goalService.getNotifications(viewUserId);
            if (messages.isEmpty()) {
                return;
            }
            StringBuilder sb = new StringBuilder("Goal notifications (nearest deadline first):\n\n");
            int shown = Math.min(messages.size(), 8);
            for (int i = 0; i < shown; i++) {
                sb.append("- ").append(messages.get(i)).append("\n");
            }
            if (messages.size() > shown) {
                sb.append("\n...and ").append(messages.size() - shown).append(" more on the Goals screen.");
            }
            GuiUtil.showInfo(this, sb.toString());
        } catch (SQLException ex) {
            GuiUtil.showError(this, "Cannot load goal notifications: " + ex.getMessage());
        }
    }
}
