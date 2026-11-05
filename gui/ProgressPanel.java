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
import java.util.List;

public class ProgressPanel extends JPanel {
    private JTextField txtFitnessGoal;
    private JButton btnSetGoal, btnTriggerNotification, btnGenerateReport;
    private JTextArea reportDisplay;

    private final WorkoutPanel workoutPanel;
    private final MealPanel mealPanel;

    public ProgressPanel(WorkoutPanel workoutPanel, MealPanel mealPanel) {
        this.workoutPanel = workoutPanel;
        this.mealPanel = mealPanel;

        setLayout(new BorderLayout(10, 10));

        JPanel goalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        goalPanel.setBorder(BorderFactory.createTitledBorder("Fitness Goals"));

        goalPanel.add(new JLabel("Target Goal:"));
        txtFitnessGoal = new JTextField(15);
        goalPanel.add(txtFitnessGoal);

        btnSetGoal = new JButton("Set Goal");
        btnTriggerNotification = new JButton("Send Notification");
        goalPanel.add(btnSetGoal);
        goalPanel.add(btnTriggerNotification);

        add(goalPanel, BorderLayout.NORTH);

        reportDisplay = new JTextArea();
        reportDisplay.setEditable(false);
        add(new JScrollPane(reportDisplay), BorderLayout.CENTER);

        btnGenerateReport = new JButton("Generate Progress Report");
        add(btnGenerateReport, BorderLayout.SOUTH);

        btnSetGoal.addActionListener(e -> {
            reportDisplay.append("Fitness Goal Set: " + txtFitnessGoal.getText().trim() + "\n");
        });

        btnTriggerNotification.addActionListener(e -> {
            String goal = txtFitnessGoal.getText().trim();
            String message = goal.isEmpty() ? "Keep pushing towards your daily fitness goals!" : "Keep up the great work! Target goal: " + goal;
            JOptionPane.showMessageDialog(this, message, "Fitness Notification", JOptionPane.INFORMATION_MESSAGE);
        });

        btnGenerateReport.addActionListener(e -> generateReport());
    }

    private void generateReport() {
        StringBuilder report = new StringBuilder();
        report.append("=========================================\n");
        report.append("         FITNESS PROGRESS REPORT         \n");
        report.append("=========================================\n\n");

        String goal = txtFitnessGoal.getText().trim();
        report.append("TARGET GOAL:\n");
        report.append(goal.isEmpty() ? "   No target goal set yet.\n" : "   • " + goal + "\n");
        report.append("\n-----------------------------------------\n");

        report.append("LOGGED WORKOUTS:\n");
        List<WorkoutPanel.WorkoutItem> workouts = workoutPanel.getWorkoutList();
        if (workouts.isEmpty()) {
            report.append("   No workouts logged yet.\n");
        } else {
            int totalBurned = 0;
            for (WorkoutPanel.WorkoutItem w : workouts) {
                report.append("   • ID: ").append(w.id)
                      .append(" | ").append(w.name)
                      .append(" | ").append(w.duration).append(" mins")
                      .append(" | ").append(w.calories).append(" kcal burned\n");
                try {
                    totalBurned += Integer.parseInt(w.calories);
                } catch (NumberFormatException ignored) {}
            }
            report.append("   [Total Calories Burned: ").append(totalBurned).append(" kcal]\n");
        }

        report.append("\n-----------------------------------------\n");

        report.append("LOGGED MEALS:\n");
        List<MealPanel.MealItem> meals = mealPanel.getMealList();
        if (meals.isEmpty()) {
            report.append("   No meals logged yet.\n");
        } else {
            int totalConsumed = 0;
            for (MealPanel.MealItem m : meals) {
                report.append("   • ID: ").append(m.id)
                      .append(" | ").append(m.name)
                      .append(" | ").append(m.calories).append(" kcal")
                      .append(" | Info: ").append(m.info).append("\n");
                totalConsumed += m.calories;
            }
            report.append("   [Total Calories Consumed: ").append(totalConsumed).append(" kcal]\n");
        }

        report.append("\n=========================================\n");

        reportDisplay.setText(report.toString());
    }
}