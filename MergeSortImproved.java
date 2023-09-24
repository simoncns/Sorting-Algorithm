/**
 * Improved MergeSort methods.
 *
 * @author Nick Simoncelli
 * @version pa2
 *
 *          This code complies with the JMU Honor Code. Collaborated with Jaden Cioffi on big
 *          picture concepts regarding the overall idea of pa2.
 */
public class MergeSortImproved {

  private static final int MAX_VALUE_THRESHOLD = 121;

  /**
   * Merge sort the provided array.
   *
   * @param items array to sort
   */
  public static <T extends Comparable<T>> void mergeSortHalfSpace(T[] items) {
    mergeSortHalfSpace(items, 0, items.length - 1);
  }

  /**
   * Recursive helper method for the merge sort algorithm.
   *
   * @param items The array to sort
   * @param start Index of the left end of the region to sort
   * @param end Index of the right end of the region to sort.
   */
  private static <T extends Comparable<T>> void mergeSortHalfSpace(T[] items, int start, int end) {
    if (start < end) {
      int mid = (start + end) / 2;
      mergeSortHalfSpace(items, start, mid);
      mergeSortHalfSpace(items, mid + 1, end);
      merge(items, start, mid, end);
    }
  }

  /**
   * Merge sort the provided array using an improved merge operation.
   *
   * @param items array of items to sort
   * @param start beginning index
   * @param mid middle index
   * @param end last index
   */
  public static <T extends Comparable<T>> void merge(T[] items, int start, int mid, int end) {
    int size = mid - start + 1;
    int tmpIndex = 0;
    int rightIndex = mid + 1;
    int mergeIndex = start;
    @SuppressWarnings("unchecked")
    T[] temp = (T[]) new Comparable[size];
    for (int i = 0; i < size; i++) {
      temp[i] = items[i + start];
    }

    while (tmpIndex < size && rightIndex <= end) {
      if (temp[tmpIndex].compareTo(items[rightIndex]) <= 0) {
        items[mergeIndex] = temp[tmpIndex];
        tmpIndex++;
        mergeIndex++;
      } else {
        items[mergeIndex] = items[rightIndex];
        rightIndex++;
        mergeIndex++;
      }
    }
    while (tmpIndex < size) {
      items[mergeIndex] = temp[tmpIndex];
      mergeIndex++;
      tmpIndex++;
    }

  }

  /**
   * Merge sort the provided array by using an improved merge operation and switching to insertion
   * sort for small sub-arrays.
   *
   * @param items array of objects to be sorted
   */
  public static <T extends Comparable<T>> void mergeSortAdaptive(T[] items) {
    mergeSortAdaptive(items, 0, items.length - 1);
  }

  /**
   * Merge sort the provided array by using an improved merge operation and switching to insertion
   * sort for small sub-arrays.
   *
   * @param items array of items
   * @param start start index
   * @param end end index
   */
  private static <T extends Comparable<T>> void mergeSortAdaptive(T[] items, int start, int end) {
    if (start < end) {
      if ((end - start + 1) < MAX_VALUE_THRESHOLD) {
        BasicSorts.insertionSubsort(items, start, end);
      } else {
        int mid = (start + end) / 2;
        mergeSortHalfSpace(items, start, mid);
        mergeSortHalfSpace(items, mid + 1, end);
        merge(items, start, mid, end);
      }
    }
  }

  /**
   * Merge sort the provided sub-array using our improved merge sort. This is the fallback method
   * used by introspective sort.
   *
   * @param items items to sort
   * @param start beginning index
   * @param end last index
   */
  public static <T extends Comparable<T>> void mergeSubsortAdaptive(T[] items, int start, int end) {
    int size = end - start + 1;
    if (start < end) {
      if (size >= MAX_VALUE_THRESHOLD) {
        if (QuickSort.partition(items, start, end) < 2 * Math.log(size)) {
          @SuppressWarnings("unchecked")
          T[] temp = (T[]) new Comparable[size];
          for (int i = 0; i < size; i++) {
            temp[i] = items[i + start];
          }
          QuickSort.quickSort(temp);
          for (int j = 0; j < size; j++) {
            items[j + start] = temp[j];
          }
        } else {
          mergeSortAdaptive(items, start, end);
        }
      } else {
        BasicSorts.insertionSubsort(items, start, end);
      }
    }
  }
}
