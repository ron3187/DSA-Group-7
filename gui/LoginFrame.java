/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.fitness_tracker_app.gui;

/**
 *
 * @author Gabrielle
 */

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField txtUserId;
    private JTextField txtUserName;
    private JButton btnLogin;

    public LoginFrame() {
        setTitle("Fitness Tracker - User Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        mainPanel.add(new JLabel("User ID:"));
        txtUserId = new JTextField(15);
        mainPanel.add(txtUserId);

        mainPanel.add(new JLabel("User Name:"));
        txtUserName = new JTextField(15);
        mainPanel.add(txtUserName);

        mainPanel.add(new JLabel());
        btnLogin = new JButton("Login");
        mainPanel.add(btnLogin);

        setContentPane(mainPanel);

        btnLogin.addActionListener(e -> handleLogin());

        pack();
        setLocationRelativeTo(null);
    }

    private void handleLogin() {
        String userId = txtUserId.getText();
        String userName = txtUserName.getText();
        
        if (userId.isEmpty() || userName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both User ID and Name.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Welcome, " + userName + " (ID: " + userId + ")!");
        this.dispose();

        MainFrame mainFrame = new MainFrame(userId, userName);
        mainFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}