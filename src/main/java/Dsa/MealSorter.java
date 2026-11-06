package Dsa;

import Dsa.MealPanel.MealItem;
import java.awt.*;
import java.util.List;
import javax.swing.*;

public class MealSorter extends JFrame {

    private MealPanel mealPanel;

    public MealSorter() {
        setTitle("Fitness Tracker - Meal Sorter Manager");
        setSize(650, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        mealPanel = new MealPanel();
        add(mealPanel, BorderLayout.CENTER);
    }

    public static void sortByCalories(List<MealItem> mealList) {
        if (mealList == null || mealList.size() <= 1) {
            return;
        }

        int n = mealList.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                MealItem m1 = mealList.get(j);
                MealItem m2 = mealList.get(j + 1);

                if (m1.calories > m2.calories) {
                    mealList.set(j, m2);
                    mealList.set(j + 1, m1);
                }
            }
        }
    }
}