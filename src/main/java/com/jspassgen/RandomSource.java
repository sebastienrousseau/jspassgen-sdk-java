// SPDX-License-Identifier: Apache-2.0 OR MIT
package com.jspassgen;

/**
 * The source of every random value a generator draws: the Java form of
 * jspassgen's {@code RandomGeneratorPort} ({@code generateRandomBytes},
 * {@code generateRandomInt}).
 *
 * <p>Implementations must be cryptographically secure. Injecting a
 * different implementation is how tests replay a fixed sequence of
 * draws.
 */
public interface RandomSource {

  /**
   * Returns {@code n} random bytes in a new array.
   *
   * @param n the number of bytes, at least 1
   * @return a new array of {@code n} random bytes
   * @throws IllegalArgumentException if {@code n} is less than 1
   */
  byte[] bytes(int n);

  /**
   * Returns a uniformly distributed integer in {@code [0, max)}.
   *
   * @param max the exclusive upper bound, at least 1
   * @return an integer {@code r} with {@code 0 <= r < max}
   * @throws IllegalArgumentException if {@code max} is less than 1
   */
  int randomInt(int max);
}
