/**
 * Basic n^2 sorting algorithms and insertionSubsort.
 *
 * @author Nick Simoncelli and Nathan Sprague
 * @version pa2
 * 
 *          This code complies with the JMU Honor Code. Used provided code for insertion sort and
 *          selection sort then implemented code for insertionSubsort. Collaborated with Jaden
 *          Cioffi on big picture concepts regarding the overall idea of pa2.
 * 
 */
public class BasicSorts {
  /**
   * Sort the given array using insertion sort.
   *
   * @param items The array to sort.
   */
  public static <T extends Comparable<T>> void insertionSort(T[] items) {
    for (int i = 1; i < items.length; i++) {
      T itemToShift = items[i];

      // Displace any smaller items one cell to the right.
      int j = i;
      while (j > 0 && itemToShift.compareTo(items[j - 1]) < 0) {
        items[j] = items[j - 1];
        j--;
      }

      items[j] = itemToShift;
    }
  }

  /**
   * Sort only the given window of the array.
   *
   * @param items The array to partially sort.
   * @param start The inclusive starting index of the window.
   * @param end The inclusive ending index of the window.
   */
  public static <T extends Comparable<T>> void insertionSubsort(T[] items, int start, int end) {
    int size = end - start + 1;
    @SuppressWarnings("unchecked")
    T[] tempArray = (T[]) new Comparable[size];
    for (int i = 0; i < size; i++) {
      tempArray[i] = items[i + start];
    }
    insertionSort(tempArray);
    for (int j = 0; j < size; j++) {
      items[j + start] = tempArray[j];
    }
  }

  /**
   * Selection sort the given array.
   *
   * @param items The array to sort.
   */
  public static <T extends Comparable<T>> void selectionSort(T[] items) {
    for (int i = 0; i < items.length - 1; i++) {
      int indexSmallest = i;

      for (int j = i + 1; j < items.length; j++) {
        if (items[j].compareTo(items[indexSmallest]) < 0) {
          indexSmallest = j;
        }
      }

      T temp = items[i];
      items[i] = items[indexSmallest];
      items[indexSmallest] = temp;
    }
  }

}
