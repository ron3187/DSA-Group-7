package Dsa;

import java.util.Comparator;

/**
 * Bubble sort (same idea as your MealSorter).
 * Compares neighbours and swaps them when they are in the wrong order.
 * Stops early when a full pass makes no swap.
 * Time: O(n^2) worst case, O(n) when already sorted.  It is stable (equal items keep their order).
 *
 * The Comparator only answers "which of these two comes first?", so the same
 * sort works for workouts, meals and goals.
 */
public class BubbleSort {

    private BubbleSort() {
    }

    public static <T> void sort(T[] arr, Comparator<? super T> cmp) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (cmp.compare(arr[j], arr[j + 1]) > 0) {
                    T temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
    }
}
