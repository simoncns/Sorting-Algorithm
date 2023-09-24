/**
 * IntrospectiveSort class.
 *
 * @author Nick Simoncelli
 * @version pa2
 * 
 *          This code complies with the JMU Honor Code. Collaborated with Jaden Cioffi on big
 *          picture concepts regarding the overall idea of pa2.
 */
public class IntrospectiveSort {
  /**
   * Sort the provided items using introspective sort.
   *
   * @param items array of items to sort
   */
  public static <T extends Comparable<T>> void introspectiveSort(T[] items) {
    MergeSortImproved.mergeSubsortAdaptive(items, 0, items.length - 1);
  }
}
