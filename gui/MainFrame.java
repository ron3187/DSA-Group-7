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

public class MainFrame extends JFrame {
    public MainFrame(String userId, String userName) {
        setTitle("Fitness Tracker Dashboard - " + userName + " (ID: " + userId + ")");
        setSize(850, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        WorkoutPanel workoutPanel = new WorkoutPanel();
        MealPanel mealPanel = new MealPanel();
        ProgressPanel progressPanel = new ProgressPanel(workoutPanel, mealPanel);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Workouts", workoutPanel);
        tabbedPane.addTab("Meals", mealPanel);
        tabbedPane.addTab("Progress & Goals", progressPanel);

        add(tabbedPane);
    }
}