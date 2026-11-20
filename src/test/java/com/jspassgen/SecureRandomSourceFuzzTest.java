// SPDX-License-Identifier: Apache-2.0 OR MIT
package com.jspassgen;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.code_intelligence.jazzer.junit.FuzzTest;
import java.util.List;

/**
 * Jazzer fuzz tests of the {@link SecureRandomSource} contract. The
 * fuzzer chooses both the argument and every byte the wrapped generator
 * returns, so each property is checked against draws the fuzzer
 * controls. A plain {@code mvn test} replays the committed inputs under
 * {@code src/test/resources/com/jspassgen/SecureRandomSourceFuzzTestInputs/};
 * {@code JAZZER_FUZZ=1} fuzzes (DEVELOPMENT.md).
 */
class SecureRandomSourceFuzzTest {

  private static final long RANGE = 1L << 32;

  @FuzzTest
  void randomIntIsRejectionSampling(FuzzedDataProvider data) {
    int max = data.consumeInt();
    FuzzedRandom random = new FuzzedRandom(data);
    RandomSource source = new SecureRandomSource(random);
    if (max < 1) {
      IllegalArgumentException e =
          assertThrows(IllegalArgumentException.class, () -> source.randomInt(max));
      assertEquals("max must be a positive integer", e.getMessage());
      assertTrue(random.draws().isEmpty(), "a rejected bound draws nothing");
      return;
    }
    int r = source.randomInt(max);
    assertTrue(r >= 0 && r < max, () -> r + " not in [0, " + max + ")");
    // The largest multiple of max that fits in 32 bits: values at or
    // above it are the biased tail and must be redrawn.
    long limit = RANGE / max * max;
    List<Long> draws = random.draws();
    long accepted = draws.get(draws.size() - 1);
    assertTrue(accepted < limit, "the accepted draw is below the limit");
    for (long rejected : draws.subList(0, draws.size() - 1)) {
      assertTrue(rejected >= limit, () -> rejected + " was redrawn below the limit");
    }
    assertEquals(accepted % max, r);
  }

  @FuzzTest
  void bytesReturnsExactlyTheDrawnBytes(FuzzedDataProvider data) {
    int n = data.consumeShort();
    FuzzedRandom random = new FuzzedRandom(data);
    RandomSource source = new SecureRandomSource(random);
    if (n < 1) {
      IllegalArgumentException e =
          assertThrows(IllegalArgumentException.class, () -> source.bytes(n));
      assertEquals("n must be a positive integer", e.getMessage());
      assertEquals(0, random.drawnBytes().length, "a rejected length draws nothing");
      return;
    }
    byte[] got = source.bytes(n);
    assertEquals(n, got.length);
    assertArrayEquals(random.drawnBytes(), got);
  }
}
