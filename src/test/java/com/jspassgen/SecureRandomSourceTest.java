// SPDX-License-Identifier: Apache-2.0 OR MIT
package com.jspassgen;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SecureRandomSourceTest {

  private static final long RANGE = 1L << 32;

  private final RandomSource source = new SecureRandomSource();

  @Test
  void versionIsTheSuiteVersion() {
    assertEquals("0.0.23", Jspassgen.VERSION);
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 2, 16, 1024})
  void bytesReturnsANewArrayOfTheRequestedLength(int n) {
    byte[] first = source.bytes(n);
    byte[] second = source.bytes(n);
    assertEquals(n, first.length);
    assertNotSame(first, second);
  }

  @Test
  void bytesComeFromTheWrappedGenerator() {
    ScriptedRandom scripted = ScriptedRandom.ofUnsigned32(0x01020304L);
    byte[] got = new SecureRandomSource(scripted).bytes(4);
    assertArrayEquals(new byte[] {1, 2, 3, 4}, got);
  }

  @ParameterizedTest
  @ValueSource(ints = {0, -1, Integer.MIN_VALUE})
  void bytesRejectsANonPositiveLength(int n) {
    IllegalArgumentException e =
        assertThrows(IllegalArgumentException.class, () -> source.bytes(n));
    assertEquals("n must be a positive integer", e.getMessage());
  }

  @ParameterizedTest
  @ValueSource(ints = {0, -1, Integer.MIN_VALUE})
  void randomIntRejectsANonPositiveBound(int max) {
    IllegalArgumentException e =
        assertThrows(IllegalArgumentException.class, () -> source.randomInt(max));
    assertEquals("max must be a positive integer", e.getMessage());
  }

  @Test
  void constructorRejectsNull() {
    assertThrows(NullPointerException.class, () -> new SecureRandomSource(null));
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 2, 3, 10, 62, 7776, Integer.MAX_VALUE})
  void randomIntStaysInRange(int max) {
    for (int i = 0; i < 1_000; i++) {
      int r = source.randomInt(max);
      assertTrue(r >= 0 && r < max, () -> r + " not in [0, " + max + ")");
    }
  }

  @Test
  void randomIntRedrawsValuesInTheBiasedTail() {
    int max = 3;
    long limit = RANGE - RANGE % max; // 4294967295: only 2^32 - 1 is rejected
    ScriptedRandom scripted = ScriptedRandom.ofUnsigned32(limit, 7);
    assertEquals(7 % max, new SecureRandomSource(scripted).randomInt(max));
    assertEquals(0, scripted.remaining());
  }

  @Test
  void randomIntAcceptsTheLastValueBelowTheLimit() {
    int max = 3;
    long limit = RANGE - RANGE % max;
    ScriptedRandom scripted = ScriptedRandom.ofUnsigned32(limit - 1);
    assertEquals((int) ((limit - 1) % max), new SecureRandomSource(scripted).randomInt(max));
  }

  @Test
  void randomIntIsUniform() {
    // Chi-squared over ten buckets, 9 degrees of freedom. 44.81 is the
    // critical value at p = 1e-6, so a correct generator fails this
    // test about once in a million runs.
    int max = 10;
    int draws = 100_000;
    long[] counts = new long[max];
    for (int i = 0; i < draws; i++) {
      counts[source.randomInt(max)]++;
    }
    double expected = (double) draws / max;
    double sum = 0;
    for (long c : counts) {
      sum += (c - expected) * (c - expected) / expected;
    }
    double chiSquared = sum;
    assertTrue(chiSquared < 44.81, () -> "chi-squared " + chiSquared);
  }

  @Test
  void randomIntHasNoModuloBias() {
    // max = 3 * 2^29. A bare `u32 % max` maps three 32-bit values onto
    // each result below 2^30 and two onto the rest, so 3/4 of results
    // would fall below 2^30; unbiased, the share is 2^30 / max = 2/3.
    // Over 100 000 draws the standard deviation is about 0.0015, so
    // 0.01 is more than six of them.
    int max = 3 << 29;
    int draws = 100_000;
    int below = 0;
    for (int i = 0; i < draws; i++) {
      if (source.randomInt(max) < (1 << 30)) {
        below++;
      }
    }
    double share = (double) below / draws;
    assertEquals(2.0 / 3.0, share, 0.01);
  }
}
