package gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import model.User;
import service.UserService;

public class LoginFrame extends JFrame implements ActionListener {

    private final UserService userService = new UserService();

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnExit;

    public LoginFrame() {
        setTitle("Fitness Tracker - Login");
        setSize(560, 460);
        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel lblTitle = new JLabel("Fitness Tracker", SwingConstants.CENTER);
        lblTitle.setBounds(40, 30, 480, 44);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 30));
        add(lblTitle);

        JLabel lblSub = new JLabel("Log in to track workouts, meals and goals", SwingConstants.CENTER);
        lblSub.setBounds(40, 78, 480, 26);
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 14));
        add(lblSub);

        add(GuiUtil.makeLabel("Username", 100, 135, 360, 22));
        txtUsername = GuiUtil.makeField(100, 160, 360, 36);
        txtUsername.addActionListener(this);
        add(txtUsername);

        add(GuiUtil.makeLabel("Password", 100, 210, 360, 22));
        txtPassword = new JPasswordField();
        txtPassword.setBounds(100, 235, 360, 36);
        txtPassword.setFont(GuiUtil.FIELD_FONT);
        txtPassword.addActionListener(this);      // pressing Enter logs in
        add(txtPassword);

        btnLogin = GuiUtil.makeButton("Login", 100, 295, 170, 44, this);
        btnExit = GuiUtil.makeButton("Exit", 290, 295, 170, 44, this);
        add(btnLogin);
        add(btnExit);

        JLabel lblHint = new JLabel("Admin: admin / admin123",
                SwingConstants.CENTER);
        lblHint.setBounds(40, 365, 480, 24);
        lblHint.setFont(new Font("SansSerif", Font.PLAIN, 12));
        add(lblHint);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == btnLogin || src == txtPassword || src == txtUsername) {
            doLogin();
        } else if (src == btnExit) {
            System.exit(0);
        }
    }

    private void doLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            GuiUtil.showError(this, "Please enter your username and password.");
            return;
        }
        try {
            User user = userService.login(username, password);
            if (user == null) {
                GuiUtil.showError(this, "Wrong username or password.");
                txtPassword.setText("");
                return;
            }
            MainFrame main = new MainFrame(user);
            main.setVisible(true);
            dispose();
            main.showStartupNotifications();
        } catch (SQLException ex) {
            GuiUtil.showError(this, "Cannot connect to the database. Check MySQL and DBConnection settings.\n" + ex.getMessage());
        }
    }
}
