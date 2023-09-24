/**
 * QuickSort class.
 */
public class QuickSort {
  /**
   * Quicksort the provided array.
   */
  public static <T extends Comparable<T>> void quickSort(T[] items) {
    if (items.length > 0) {
      quickSort(items, 0, items.length - 1);
    }
  }

  /**
   * Recursive helper method for quicksort.
   *
   * @param items The items to sort
   * @param left The starting index of the region to sort
   * @param right The ending index of the region to sort.
   */
  private static <T extends Comparable<T>> void quickSort(T[] items, int left, int right) {
    if (left < right) {
      int lastSmallIndex = partition(items, left, right);
      quickSort(items, left, lastSmallIndex);
      quickSort(items, lastSmallIndex + 1, right);
    }
  }

  /**
   * Partition the indicated region of the array. The pivot item will be placed in its final sorted
   * position, with all smaller elements moved to the left and all larger elements moved to the
   * right.
   *
   * @return The final index of the pivot item.
   */
  protected static <T extends Comparable<T>> int partition(T[] items, int left, int right) {
    int pivotIndex = left + (right - left) / 2;
    T pivotItem = items[pivotIndex];

    // Advance from both ends until window collapses.
    boolean isDone = false;
    while (!isDone) {
      // Skip rightward past items < pivot.
      while (items[left].compareTo(pivotItem) < 0) {
        left++;
      }

      // Skip leftward past items > pivot.
      while (items[right].compareTo(pivotItem) > 0) {
        right--;
      }

      if (left >= right) {
        isDone = true;

      } else {
        T temp = items[left];
        items[left] = items[right];
        items[right] = temp;

        left++;
        right--;
      }
    }

    return right;
  }
}
