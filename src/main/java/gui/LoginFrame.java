package gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import model.User;

public class LoginFrame extends JFrame implements ActionListener {

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

        JLabel lblUsername = new JLabel("Username");
        lblUsername.setBounds(100, 135, 360, 22);
        add(lblUsername);

        txtUsername = new JTextField();
        txtUsername.setBounds(100, 160, 360, 36);
        add(txtUsername);

        JLabel lblPassword = new JLabel("Password");
        lblPassword.setBounds(100, 210, 360, 22);
        add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(100, 235, 360, 36);
        txtPassword.addActionListener(this);
        add(txtPassword);

        btnLogin = new JButton("Login");
        btnLogin.setBounds(100, 295, 170, 44);
        btnLogin.addActionListener(this);
        add(btnLogin);

        btnExit = new JButton("Exit");
        btnExit.setBounds(290, 295, 170, 44);
        btnExit.addActionListener(this);
        add(btnExit);

        JLabel lblHint = new JLabel("Click Login to continue", SwingConstants.CENTER);
        lblHint.setBounds(40, 365, 480, 24);
        lblHint.setFont(new Font("SansSerif", Font.PLAIN, 12));
        add(lblHint);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnLogin || e.getSource() == txtPassword) {
            User user = new User(1, "guest", "", "Guest", "MEMBER");
            MainFrame main = new MainFrame(user);
            main.setVisible(true);
            dispose();
        } else if (e.getSource() == btnExit) {
            System.exit(0);
        }
    }
}
