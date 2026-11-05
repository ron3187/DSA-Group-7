  package Dsa;

import Dsa.WorkoutPanel.WorkoutItem;
import java.awt.*;
import java.util.Comparator;
import java.util.List;
import javax.swing.*;

public class WorkoutSearch extends JFrame {

    private final WorkoutPanel workoutPanel;

    public WorkoutSearch() {
        setTitle("Fitness Tracker - Workout Manager");
        setSize(650, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        workoutPanel = new WorkoutPanel();
        add(workoutPanel, BorderLayout.CENTER);
    }

    public static WorkoutItem binarySearchById(List<WorkoutItem> list, String targetid) {
        if (list == null || list.isEmpty() || targetid == null) {
            return null;
        }

        sortWorkoutsById(list);

        int left = 0;
        int right = list.size() - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            WorkoutItem midItem = list.get(mid);
            
            int comparison = midItem.id.compareToIgnoreCase(targetid.trim());

            if (comparison == 0) {
                return midItem; 
            } else if (comparison < 0) {
                left = mid + 1; 
            } else {
                right = mid - 1; 
            }
        }

        return null; 
    }

    public static void sortWorkoutsById(List<WorkoutItem> list) {
        list.sort(Comparator.comparing(w -> w.id, String.CASE_INSENSITIVE_ORDER));
    }
}