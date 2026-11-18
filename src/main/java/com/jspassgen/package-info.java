// SPDX-License-Identifier: Apache-2.0 OR MIT

/**
 * A planned native Java port of the jspassgen core.
 *
 * <p>This version holds only the random port that every generator will
 * draw from: {@link com.jspassgen.RandomSource} and its
 * {@link java.security.SecureRandom} implementation,
 * {@link com.jspassgen.SecureRandomSource}. The generators are not
 * ported yet; each must pass jspassgen's conformance kit.
 */
package com.jspassgen;
