// SPDX-License-Identifier: Apache-2.0 OR MIT
package com.jspassgen;

import java.security.SecureRandom;
import java.util.Objects;

/**
 * A {@link RandomSource} backed by {@link SecureRandom}, the platform
 * CSPRNG.
 *
 * <p>{@link #randomInt(int)} is unbiased: it draws 32 random bits and
 * rejects any value at or above the largest multiple of {@code max}
 * below 2<sup>32</sup>, so every result in {@code [0, max)} has the
 * same probability. It never reduces raw bytes by a bare modulo.
 *
 * <p>Instances are thread-safe when the wrapped {@code SecureRandom} is,
 * as the JDK's are.
 */
public final class SecureRandomSource implements RandomSource {

  private static final long RANGE = 1L << 32;

  private final SecureRandom random;

  /** Uses a new {@link SecureRandom} with the platform's default algorithm. */
  public SecureRandomSource() {
    this(new SecureRandom());
  }

  /**
   * Uses the given {@link SecureRandom}, for example one obtained with
   * {@code SecureRandom.getInstance("DRBG")}.
   *
   * @param random the generator to draw from
   * @throws NullPointerException if {@code random} is null
   */
  public SecureRandomSource(SecureRandom random) {
    this.random = Objects.requireNonNull(random, "random");
  }

  @Override
  public byte[] bytes(int n) {
    if (n < 1) {
      throw new IllegalArgumentException("n must be a positive integer");
    }
    byte[] out = new byte[n];
    random.nextBytes(out);
    return out;
  }

  @Override
  public int randomInt(int max) {
    if (max < 1) {
      throw new IllegalArgumentException("max must be a positive integer");
    }
    long limit = RANGE - RANGE % max;
    long value = nextUnsigned32();
    while (value >= limit) {
      value = nextUnsigned32();
    }
    return (int) (value % max);
  }

  private long nextUnsigned32() {
    byte[] b = new byte[4];
    random.nextBytes(b);
    long value = 0;
    for (byte x : b) {
      value = (value << 8) | (x & 0xffL);
    }
    return value;
  }
}
