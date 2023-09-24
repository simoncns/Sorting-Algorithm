/**
 * Functional interface for methods that generate arrays of Integers.
 *
 * @author CS 240 Instructors
 * @version 2/2019
 *
 */

@FunctionalInterface
public interface Generator {

  /**
   * Generate an array of integers.
   *
   * @param size The length of the resulting array
   * @return The populated array
   */
  Integer[] generate(int size);
}
