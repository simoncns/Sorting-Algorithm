import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

/**
 * Application for testing the run-time behavior of several sorting algorithms. Run with the -h flag
 * for usage information.
 *
 * @author Nathan Sprague
 * @version 2/2023
 */
public class SortProfiler {
  private static final String[] ALL_SORT_NAMES = {"insertion", "selection", "merge", "merge_half",
      "merge_adaptive", "quick", "introspective", "timsort"};

  private Generator gen;
  private List<Sorter<Integer>> sorts;
  private List<String> sortNames;
  private int start;
  private int interval;
  private int max;
  private int trials;

  /**
   * Create a sort profiler object.
   *
   * @param sorts The sorts that will be tested
   * @param sortNames The names of the provided sort methods
   * @param start The starting input size
   * @param interval The interval between input sizes
   * @param max The maximum input size
   * @param trials The number of trials
   * @param gen The sequence generator to use
   */
  public SortProfiler(List<Sorter<Integer>> sorts, List<String> sortNames, int start, int interval,
      int max, int trials, Generator gen) {
    if (start < 0) {
      throw new IllegalArgumentException("Start value cannot be negative.");
    }
    if (interval < 1) {
      throw new IllegalArgumentException("Step value cannot be zero or negative.");
    }
    if (max < 0) {
      throw new IllegalArgumentException("Max value cannot be negative.");
    }
    if (trials < 0) {
      throw new IllegalArgumentException("Number of trials cannot be negative.");
    }

    this.sorts = sorts;
    this.sortNames = sortNames;
    this.gen = gen;
    this.start = start;
    this.interval = interval;
    this.max = max;
    this.trials = trials;
  }

  /**
   * Run the required number of sorting trials and print the results.
   *
   * @param outStream The results will be printed to this stream.
   */
  public void run(OutputStream outStream) {
    PrintStream out = new PrintStream(outStream);

    // Print the column headers.
    out.print("N");
    for (int i = 0; i < sorts.size(); i++) {
      out.print(",\t" + sortNames.get(i));
    }
    out.print("\n");

    // Warm up the JVM by doing some un-timed sorts...
    for (int s = 0; s < sorts.size(); s++) {
      for (int i = 0; i < 10000; i++) {
        Integer[] items = gen.generate(100);
        timeSort(sorts.get(s), items);
      }
    }

    for (int n = start; n <= max; n += interval) {
      // Gather the data for size n
      long[] sortTimeTotals = new long[sorts.size()];
      for (int trial = 0; trial < trials; trial++) {
        Integer[] items = gen.generate(n);
        for (int s = 0; s < sorts.size(); s++) {
          sortTimeTotals[s] += timeSort(sorts.get(s), items);
        }
      }

      // Print one row of data.
      out.print(n);
      for (int s = 0; s < sorts.size(); s++) {
        double averageTime = (sortTimeTotals[s] / trials) / 1000000000.0;
        out.format(",\t%.8f", averageTime);
      }
      out.print("\n");
    }
  }

  /**
   * Process the command line arguments and create a SortProfiler object with the resulting values.
   *
   * @throws IOException (Should never happen)
   */
  public static void main(String[] args) throws IOException {
    // Set up the option parser with the desired arguments.
    OptionParser parser = new OptionParser();

    OptionSpec<Integer> startSpec = parser.accepts("s", "Starting (smallest) input size")
        .withRequiredArg().ofType(Integer.class).describedAs("NUMBER").required();

    OptionSpec<Integer> intervalSpec = parser.accepts("i", "Input size increment").withRequiredArg()
        .ofType(Integer.class).describedAs("NUMBER").required();

    OptionSpec<Integer> maxSpec = parser.accepts("m", "Maximum input size to test")
        .withRequiredArg().ofType(Integer.class).describedAs("NUMBER").required();

    OptionSpec<Integer> trialsSpec = parser.accepts("t", "Number of trials for each input size")
        .withRequiredArg().ofType(Integer.class).describedAs("NUMBER").required();

    OptionSpec<String> sortSpec = parser.accepts("w",
        "Comma separated list of sorts. Options include insertion, selection, merge, merge_half,"
        + " merge_adaptive, quick, introspective and timsort. Default is to execute all sorts.")
        .withOptionalArg().describedAs("SORT1,SORT2,...").ofType(String.class)
        .withValuesSeparatedBy(",");

    OptionSpec<String> genSpec = parser
        .accepts("g",
            "Sequence generator. Options include random, ordered or evil. The default is random")
        .withOptionalArg().ofType(String.class).describedAs("GENERATOR");

    try {
      // Values we need to extract from the command line...
      ArrayList<Sorter<Integer>> sorts;

      // Parse the command line arguments. This will raise an exception if the
      // arguments are not formatted correctly on the command line.
      OptionSet options = parser.parse(args);

      int start = options.valueOf(startSpec);
      int interval = options.valueOf(intervalSpec);
      int max = options.valueOf(maxSpec);
      int trials = options.valueOf(trialsSpec);
      ArrayList<String> sortStrings = new ArrayList<>(options.valuesOf(sortSpec));

      if (sortStrings.isEmpty()) {
        sortStrings = new ArrayList<>(Arrays.asList(ALL_SORT_NAMES));
      }

      sorts = extractSorts(sortStrings);
      String genString = options.has(genSpec) ? options.valueOf(genSpec) : "random";

      Generator gen = switch (genString) {
        case "random" -> Generators::generateRandom;
        case "ordered" -> Generators::generateOrdered;
        case "evil" -> Generators::generateEvil;
        default -> throw new IllegalArgumentException("Unrecognized generator.");
      };

      // Create and run the SortProfiler
      SortProfiler profiler =
          new SortProfiler(sorts, sortStrings, start, interval, max, trials, gen);
      profiler.run(System.out);
    } catch (OptionException | IllegalArgumentException exception) {
      System.out.println(exception.getMessage() + "\n");
      parser.printHelpOn(System.out);
    }
  }

  /**
   * Construct a list of sort objects from a list of sort names.
   *
   * @param sortNames list of sort names
   * @return List of Sorter objects.
   * @throws IllegalArgumentException if the list includes an invalid sort name.
   */
  private static ArrayList<Sorter<Integer>> extractSorts(ArrayList<String> sortNames) {
    sortNames = new ArrayList<>(sortNames);
    ArrayList<Sorter<Integer>> sorts;
    sorts = new ArrayList<>();

    Iterator<String> it = sortNames.iterator();
    while (it.hasNext()) {
      sorts.add(parseSort(it.next()));
      it.remove();
    }

    if (!sortNames.isEmpty()) {
      throw new IllegalArgumentException("Unrecognized sort name.");
    }
    return sorts;
  }

  /**
   * Convert a sort name to the appropriate sorter object.
   */
  public static <T extends Comparable<T>> Sorter<T> parseSort(String sortName) {
    switch (sortName) {
      case "insertion":
        return BasicSorts::insertionSort;
      case "selection":
        return BasicSorts::selectionSort;
      case "merge":
        return MergeSort::mergeSort;
      case "merge_half":
        return MergeSortImproved::mergeSortHalfSpace;
      case "merge_adaptive":
        return MergeSortImproved::mergeSortAdaptive;
      case "quick":
        return QuickSort::quickSort;
      case "introspective":
        return IntrospectiveSort::introspectiveSort;
      case "timsort":
        return Arrays::sort;
      default:
        throw new IllegalArgumentException("Unrecognized sort.");
    }
  }

  /**
   * Return the total number of nanoseconds required for the provided sorting algorithm to sort the
   * provided array. A copy of the array is sorted. The original will be unmodified.
   */
  public static long timeSort(Sorter<Integer> sorter, Integer[] items) {
    Integer[] copy = Arrays.copyOf(items, items.length);
    ThreadMXBean bean = ManagementFactory.getThreadMXBean();
    long start = bean.getCurrentThreadCpuTime();
    sorter.sort(copy);
    long stop = bean.getCurrentThreadCpuTime();
    return stop - start;
  }
}
