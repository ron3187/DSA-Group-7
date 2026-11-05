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
import java.util.Comparator;
import java.util.List;

public class MealPanel extends JPanel {
    private JTextField txtMealId, txtName, txtCalories, txtNutritionalInfo;
    private JTextField txtSearchId;
    private JButton btnAddMeal, btnSortMeals, btnSearch;
    private JTextArea displayArea;

    private final List<MealItem> mealList = new ArrayList<>();

    public static class MealItem {
        public String id, name, info;
        public int calories;

        MealItem(String id, String name, int calories, String info) {
            this.id = id;
            this.name = name;
            this.calories = calories;
            this.info = info;
        }
    }

    public MealPanel() {
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Track Meal"));

        formPanel.add(new JLabel("Meal ID:"));
        txtMealId = new JTextField();
        formPanel.add(txtMealId);

        formPanel.add(new JLabel("Meal Name:"));
        txtName = new JTextField();
        formPanel.add(txtName);

        formPanel.add(new JLabel("Calories:"));
        txtCalories = new JTextField();
        formPanel.add(txtCalories);

        formPanel.add(new JLabel("Nutritional Info:"));
        txtNutritionalInfo = new JTextField();
        formPanel.add(txtNutritionalInfo);

        JPanel buttonBar = new JPanel(new FlowLayout());
        btnAddMeal = new JButton("Add Meal");
        btnSortMeals = new JButton("Sort Meals");
        buttonBar.add(btnAddMeal);
        buttonBar.add(btnSortMeals);

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(formPanel, BorderLayout.CENTER);
        topContainer.add(buttonBar, BorderLayout.SOUTH);
        add(topContainer, BorderLayout.NORTH);

        displayArea = new JTextArea();
        displayArea.setEditable(false);
        add(new JScrollPane(displayArea), BorderLayout.CENTER);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Find Meal"));
        searchPanel.add(new JLabel("Meal ID:"));
        txtSearchId = new JTextField(10);
        searchPanel.add(txtSearchId);

        btnSearch = new JButton("Search");
        searchPanel.add(btnSearch);
        add(searchPanel, BorderLayout.SOUTH);

        btnAddMeal.addActionListener(e -> addMeal());
        btnSortMeals.addActionListener(e -> sortMeals());
        btnSearch.addActionListener(e -> searchMeal());
    }

    public List<MealItem> getMealList() {
        return mealList;
    }

    private void addMeal() {
        String id = txtMealId.getText().trim();
        String name = txtName.getText().trim();
        String calStr = txtCalories.getText().trim();
        String info = txtNutritionalInfo.getText().trim();

        if (id.isEmpty() || name.isEmpty() || calStr.isEmpty() || info.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all meal fields.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int calories;
        try {
            calories = Integer.parseInt(calStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Calories must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (MealItem item : mealList) {
            if (item.id.equalsIgnoreCase(id)) {
                JOptionPane.showMessageDialog(this, "Meal ID '" + id + "' already exists!", "Duplicate ID", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        mealList.add(new MealItem(id, name, calories, info));
        displayArea.append("Logged Meal -> ID: " + id + " | Name: " + name + " | Calories: " + calories + " kcal | Info: " + info + "\n");

        txtMealId.setText("");
        txtName.setText("");
        txtCalories.setText("");
        txtNutritionalInfo.setText("");
    }

    private void sortMeals() {
        if (mealList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No meals added yet to sort.", "Sort Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        mealList.sort(Comparator.comparingInt(m -> m.calories));
        displayArea.append("\n--- Meals Sorted by Calories (Ascending) ---\n");
        for (MealItem item : mealList) {
            displayArea.append("ID: " + item.id + " | Name: " + item.name + " | Calories: " + item.calories + " kcal | Info: " + item.info + "\n");
        }
        displayArea.append("---------------------------------------------\n\n");
    }

    private void searchMeal() {
        String searchId = txtSearchId.getText().trim();

        if (searchId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Meal ID to search.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        MealItem foundItem = null;
        for (MealItem item : mealList) {
            if (item.id.equalsIgnoreCase(searchId)) {
                foundItem = item;
                break;
            }
        }

        if (foundItem != null) {
            displayArea.append("[Search Result] Found -> ID: " + foundItem.id 
                + " | Name: " + foundItem.name 
                + " | Calories: " + foundItem.calories + " kcal" 
                + " | Info: " + foundItem.info + "\n");
        } else {
            JOptionPane.showMessageDialog(this, "Meal ID '" + searchId + "' not found!", "Search Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}