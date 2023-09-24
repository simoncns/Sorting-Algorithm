import java.util.List;

/**
 * Driver class.
 *
 * @author Nick Simoncelli
 * @version pa2
 * 
 *          This code complies with the JMU Honor Code. Collaborated with Jaden Cioffi on big
 *          picture concepts regarding the overall idea of pa2.
 *
 */
public class Driver {
  /**
   * Main method to create and run SortProfiler objects to decide the correct value for
   * MAX_VALUE_THRESHOLD.
   *
   * @param args command-line arguments
   */
  public static void main(String[] args) {
    SortProfiler mergesortHalf = new SortProfiler(List.of(SortProfiler.parseSort("merge_half")),
        List.of("MergesortHalf"), 100, 5, 150, 1000, Generators::generateEvil);
    mergesortHalf.run(System.out);
    System.out.println("");
    SortProfiler insertion = new SortProfiler(List.of(SortProfiler.parseSort("insertion")),
        List.of("Insertion"), 100, 5, 150, 1000, Generators::generateEvil);
    insertion.run(System.out);
  }

}
