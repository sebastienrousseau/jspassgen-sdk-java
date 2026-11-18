// SPDX-License-Identifier: Apache-2.0 OR MIT
package com.jspassgen;

import java.nio.ByteBuffer;
import java.security.SecureRandom;

/** A SecureRandom that returns scripted bytes, to drive exact draws. */
final class ScriptedRandom extends SecureRandom {

  private static final long serialVersionUID = 1L;

  private final transient ByteBuffer script;

  private ScriptedRandom(ByteBuffer script) {
    this.script = script;
  }

  /** Each value becomes the next four bytes, big-endian. */
  static ScriptedRandom ofUnsigned32(long... values) {
    ByteBuffer buffer = ByteBuffer.allocate(values.length * 4);
    for (long v : values) {
      buffer.putInt((int) v);
    }
    buffer.flip();
    return new ScriptedRandom(buffer);
  }

  int remaining() {
    return script.remaining();
  }

  @Override
  public void nextBytes(byte[] bytes) {
    script.get(bytes);
  }
}
