package ds;

import java.util.Comparator;

/**
 * Binary search (Week 1: look at the middle, then keep only the half that can contain the target).
 * The array MUST already be sorted with the SAME comparator.  Time: O(log n).
 */
public class BinarySearch {

    private BinarySearch() {
    }

    /** Index of any item equal to key, or -1 when it is not there. */
    public static <T> int search(T[] sorted, int size, T key, Comparator<? super T> cmp) {
        int left = 0;
        int right = size - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int result = cmp.compare(sorted[mid], key);
            if (result == 0) {
                return mid;
            } else if (result < 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return -1;
    }

    /** Index of the FIRST (leftmost) item equal to key, or -1.  Useful when names repeat. */
    public static <T> int searchFirst(T[] sorted, int size, T key, Comparator<? super T> cmp) {
        int left = 0;
        int right = size - 1;
        int found = -1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int result = cmp.compare(sorted[mid], key);
            if (result == 0) {
                found = mid;        // remember it, then keep looking on the left side
                right = mid - 1;
            } else if (result < 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return found;
    }
}
