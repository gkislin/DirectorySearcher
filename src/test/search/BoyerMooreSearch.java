package test.search;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

/**
 * GKislin
 * 01.03.2015.
 * <p>
 * Based on
 *
 * @link https://github.com/samskivert/ikvm-openjdk/blob/master/build/linux-amd64/impsrc/com/sun/xml/internal/org/jvnet/mimepull/MIMEParser.java
 * @link theory: http://www.cs.princeton.edu/courses/archive/spr04/cos226/lectures/string.4up.pdf
 */
public class BoyerMooreSearch {
    private final byte[] patternBytes;
    private final int patternBytesLength;

    private final int[] bcs = new int[128]; // BnM algo: Bad Character Shift table
    private final int[] gss;                // BnM algo : Good Suffix Shift table

    public BoyerMooreSearch(byte[] patternBytes) {
        this.patternBytes = patternBytes;
        this.patternBytesLength = patternBytes.length;
        gss = new int[patternBytesLength];
        compilePattern();
    }

    /**
     * Boyer-Moore search method. Copied from java.util.regex.Pattern.java
     * <p>
     * Pre calculates arrays needed to generate the bad character
     * shift and the good suffix shift. Only the last seven bits
     * are used to see if chars match; This keeps the tables small
     * and covers the heavily used ASCII range, but occasionally
     * results in an aliased match for the bad character shift.
     */
    private void compilePattern() {
        int i, j;

        // Precalculate part of the bad character shift
        // It is a table for where in the pattern each
        // lower 7-bit value occurs
        for (i = 0; i < patternBytesLength; i++) {
            bcs[patternBytes[i] & 0x7F] = i + 1;
        }

        // Precalculate the good suffix shift
        // i is the shift amount being considered
        NEXT:
        for (i = patternBytesLength; i > 0; i--) {
            // j is the beginning index of suffix being considered
            for (j = patternBytesLength - 1; j >= i; j--) {
                // Testing for good suffix
                if (patternBytes[j] == patternBytes[j - i]) {
                    // src[j..len] is a good suffix
                    gss[j - 1] = i;
                } else {
                    // No match. The array has already been
                    // filled up with correct values before.
                    continue NEXT;
                }
            }
            // This fills up the remaining of optoSft
            // any suffix can not have larger shift amount
            // then its sub-suffix. Why???
            while (j > 0) {
                gss[--j] = i;
            }
        }
        // Set the guard value because of unicode compression
        gss[patternBytesLength - 1] = 1;
    }

    /**
     * Finds the pattern in the given buffer using Boyer-Moore algo.
     * Copied from java.util.regex.Pattern.java
     *
     * @param buffer pattern to be searched in this buffer
     * @param off    start index in buffer
     * @param len    number of bytes in buffer
     * @return -1 if there is no match or index where the match starts
     */
    public int match(ByteBuffer buffer, int off, int len) {
        int last = len - patternBytesLength;

        // Loop over all possible match positions in text
        NEXT:
        while (off <= last) {
            // Loop over pattern from right to left
            for (int j = patternBytesLength - 1; j >= 0; j--) {
                byte ch = buffer.get(off + j);
                if (ch != patternBytes[j]) {
                    // Shift search to the right by the maximum of the
                    // bad character shift and the good suffix shift
                    off += Math.max(j + 1 - bcs[ch & 0x7F], gss[j]);
                    continue NEXT;
                }
            }
            // Entire pattern matched starting at off
            return off;
        }
        return -1;
    }

    /**
     * @param bb       - buffer
     * @param consumer - strategy for result treatment
     * @return boolean : is pattern have been found at least once
     */
    public boolean matches(ByteBuffer bb, Consumer<Integer> consumer) {
        int matchIdx;
        int offset = 0;
        final int length = bb.limit();
        boolean isFound = false;
        while (true) {
            matchIdx = match(bb, offset, length);
            if (matchIdx == -1) {
                return isFound;
            }
            isFound = true;
            consumer.accept(matchIdx);
            offset = matchIdx + patternBytesLength;
        }
    }
}
