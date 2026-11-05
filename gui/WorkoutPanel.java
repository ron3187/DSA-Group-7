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
import java.util.ArrayList;
import java.util.List;

public class WorkoutPanel extends JPanel {
    private JTextField txtWorkoutId, txtName, txtDuration, txtCaloriesBurned;
    private JTextField txtSearchId;
    private JButton btnAddWorkout, btnSearch;
    private JTextArea displayArea;

    private final List<WorkoutItem> workoutList = new ArrayList<>();

    public static class WorkoutItem {
        public String id, name, duration, calories;

        WorkoutItem(String id, String name, String duration, String calories) {
            this.id = id;
            this.name = name;
            this.duration = duration;
            this.calories = calories;
        }
    }

    public WorkoutPanel() {
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Log Workout"));

        formPanel.add(new JLabel("Workout ID:"));
        txtWorkoutId = new JTextField();
        formPanel.add(txtWorkoutId);

        formPanel.add(new JLabel("Workout Name:"));
        txtName = new JTextField();
        formPanel.add(txtName);

        formPanel.add(new JLabel("Duration (mins):"));
        txtDuration = new JTextField();
        formPanel.add(txtDuration);

        formPanel.add(new JLabel("Calories Burned:"));
        txtCaloriesBurned = new JTextField();
        formPanel.add(txtCaloriesBurned);

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(formPanel, BorderLayout.CENTER);

        btnAddWorkout = new JButton("Add Workout");
        topContainer.add(btnAddWorkout, BorderLayout.SOUTH);
        add(topContainer, BorderLayout.NORTH);

        displayArea = new JTextArea();
        displayArea.setEditable(false);
        add(new JScrollPane(displayArea), BorderLayout.CENTER);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Find Workout"));
        searchPanel.add(new JLabel("Workout ID:"));
        txtSearchId = new JTextField(10);
        searchPanel.add(txtSearchId);

        btnSearch = new JButton("Search");
        searchPanel.add(btnSearch);
        add(searchPanel, BorderLayout.SOUTH);

        btnAddWorkout.addActionListener(e -> addWorkout());
        btnSearch.addActionListener(e -> searchWorkout());
    }

    public List<WorkoutItem> getWorkoutList() {
        return workoutList;
    }

    private void addWorkout() {
        String id = txtWorkoutId.getText().trim();
        String name = txtName.getText().trim();
        String duration = txtDuration.getText().trim();
        String calories = txtCaloriesBurned.getText().trim();

        if (id.isEmpty() || name.isEmpty() || duration.isEmpty() || calories.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all workout fields.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        for (WorkoutItem item : workoutList) {
            if (item.id.equalsIgnoreCase(id)) {
                JOptionPane.showMessageDialog(this, "Workout ID '" + id + "' already exists!", "Duplicate ID", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        workoutList.add(new WorkoutItem(id, name, duration, calories));
        displayArea.append("Logged Workout -> ID: " + id + " | Name: " + name + " | Duration: " + duration + " mins | Calories: " + calories + " kcal\n");

        txtWorkoutId.setText("");
        txtName.setText("");
        txtDuration.setText("");
        txtCaloriesBurned.setText("");
    }

    private void searchWorkout() {
        String searchId = txtSearchId.getText().trim();

        if (searchId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Workout ID to search.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        WorkoutItem foundItem = null;
        for (WorkoutItem item : workoutList) {
            if (item.id.equalsIgnoreCase(searchId)) {
                foundItem = item;
                break;
            }
        }

        if (foundItem != null) {
            displayArea.append("[Search Result] Found -> ID: " + foundItem.id 
                + " | Name: " + foundItem.name 
                + " | Duration: " + foundItem.duration + " mins" 
                + " | Calories: " + foundItem.calories + " kcal\n");
        } else {
            JOptionPane.showMessageDialog(this, "Workout ID '" + searchId + "' not found!", "Search Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}