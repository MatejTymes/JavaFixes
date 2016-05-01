package co.uk.matejtymes.tdp;

import java.math.BigInteger;

// todo: test it
public class DecimalMath {

    private static final long[] powersOf10L = {
            1L,                  // 10 ^ 0
            10L,                 // 10 ^ 1
            100L,                // 10 ^ 2
            1000L,               // 10 ^ 3
            10000L,              // 10 ^ 4
            100000L,             // 10 ^ 5
            1000000L,            // 10 ^ 6
            10000000L,           // 10 ^ 7
            100000000L,          // 10 ^ 8
            1000000000L,         // 10 ^ 9
            10000000000L,        // 10 ^ 10
            100000000000L,       // 10 ^ 11
            1000000000000L,      // 10 ^ 12
            10000000000000L,     // 10 ^ 13
            100000000000000L,    // 10 ^ 14
            1000000000000000L,   // 10 ^ 15
            10000000000000000L,  // 10 ^ 16
            100000000000000000L, // 10 ^ 17
            1000000000000000000L // 10 ^ 18
    };
    private static final BigInteger[] powersOf10B = {
            BigInteger.ONE,                          // 10 ^ 0
            BigInteger.TEN,                          // 10 ^ 1
            BigInteger.valueOf(100L),                // 10 ^ 2
            BigInteger.valueOf(1000L),               // 10 ^ 3
            BigInteger.valueOf(10000L),              // 10 ^ 4
            BigInteger.valueOf(100000L),             // 10 ^ 5
            BigInteger.valueOf(1000000L),            // 10 ^ 6
            BigInteger.valueOf(10000000L),           // 10 ^ 7
            BigInteger.valueOf(100000000L),          // 10 ^ 8
            BigInteger.valueOf(1000000000L),         // 10 ^ 9
            BigInteger.valueOf(10000000000L),        // 10 ^ 10
            BigInteger.valueOf(100000000000L),       // 10 ^ 11
            BigInteger.valueOf(1000000000000L),      // 10 ^ 12
            BigInteger.valueOf(10000000000000L),     // 10 ^ 13
            BigInteger.valueOf(100000000000000L),    // 10 ^ 14
            BigInteger.valueOf(1000000000000000L),   // 10 ^ 15
            BigInteger.valueOf(10000000000000000L),  // 10 ^ 16
            BigInteger.valueOf(100000000000000000L), // 10 ^ 17
            BigInteger.valueOf(1000000000000000000L) // 10 ^ 18
    };

    public static int maxLongPowerOf10() {
        return powersOf10L.length - 1;
    }

    public static int maxBigPowerOf10() {
        return powersOf10B.length - 1;
    }

    public static long powerOf10Long(int n) {
        return powersOf10L[n];
    }

    public static BigInteger powerOf10Big(int n) {
        return powersOf10B[n];
    }
}
