// SPDX-License-Identifier: Apache-2.0 OR MIT
package com.jspassgen;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * A SecureRandom whose bytes come from the fuzzer and are recorded, so a
 * fuzz test can check a result against the exact draws behind it. Once
 * the input is used up it returns zero bytes, which keeps every
 * rejection loop finite.
 */
final class FuzzedRandom extends SecureRandom {

  private static final long serialVersionUID = 1L;

  private final transient FuzzedDataProvider data;

  private final transient ByteArrayOutputStream drawn = new ByteArrayOutputStream();

  FuzzedRandom(FuzzedDataProvider data) {
    this.data = data;
  }

  @Override
  public void nextBytes(byte[] bytes) {
    byte[] fuzzed = data.consumeBytes(bytes.length);
    System.arraycopy(fuzzed, 0, bytes, 0, fuzzed.length);
    for (int i = fuzzed.length; i < bytes.length; i++) {
      bytes[i] = 0;
    }
    drawn.writeBytes(bytes);
  }

  /** Every byte returned so far, in order. */
  byte[] drawnBytes() {
    return drawn.toByteArray();
  }

  /** The bytes returned so far read as big-endian unsigned 32-bit draws. */
  List<Long> draws() {
    byte[] all = drawnBytes();
    List<Long> out = new ArrayList<>();
    for (int i = 0; i + 4 <= all.length; i += 4) {
      long v = 0;
      for (int j = i; j < i + 4; j++) {
        v = (v << 8) | (all[j] & 0xffL);
      }
      out.add(v);
    }
    return out;
  }
}
