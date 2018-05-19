package javafixes.math;

import java.math.BigInteger;

import static java.lang.Math.min;
import static java.lang.String.format;

class PowerUtil {

    static boolean canUpscaleLongByPowerOf10(long value, long n) {
        if (n >= 19) {
            return (value == 0); // only zero can be upscaled in this case
        }
        return minUpscaleLimitForPowOf10[(int) n] <= value && value <= maxUpscaleLimitForPowOf10[(int) n];
    }

    static long upscaleByPowerOf10(long value, long n) throws ArithmeticException {
        long newValue = value;
        if (value != 0) {
            long newN = n;
            while (newN > 0 && newValue != 0) {
                int scale = (int) min(maxLongPowerOf10(), newN);
                if (!canUpscaleLongByPowerOf10(newValue, scale)) {
                    throw new ArithmeticException(format("Can't upscale long '%d' by '%d' power of 10", value, n));
                }
                newValue *= powerOf10Long(scale);
                newN -= scale;
            }
        }
        return newValue;
    }

    static long downscaleByPowerOf10(long value, long n) {
        if (n >= 19) {
            return 0;
        }
        while (n > 0 && value != 0) {
            int scale = (int) min(maxLongPowerOf10(), n);
            value /= powerOf10Long(scale);
            n -= scale;
        }
        return value;
    }

    static BigInteger upscaleByPowerOf10(BigInteger value, long n) {
        // todo: speed up for large n
        if (value.signum() != 0) {
            while (n > 0) {
                int scale = (n > Integer.MAX_VALUE)
                        ? Integer.MAX_VALUE
                        : (int) n;
                value = value.multiply(powerOf10Big(scale));
                n -= scale;
            }
        }
        return value;
    }

    static int numberOfDigits(long value) {
        // this is slower than the if-else algorithm
//        if (value <= 0) {
//            if (value == Long.MIN_VALUE) {
//                return 19;
//            } else if (value == 0) {
//                return 1;
//            }
//            value = -value;
//        }
//        int bitLength = 64 - Long.numberOfLeadingZeros(value);
//        int digitCount = (int) (LOG_2_DIV_LOG_10 * bitLength + 1);
//        if (digitCount < 19 && powersOf10L[digitCount - 1] > value) {
//            digitCount--;
//        }
//        return digitCount;

        if (value < 0) {
            if (value == Long.MIN_VALUE) {
                return 19;
            }
            value = -value;
        }
        if (value <= 99_999_999L) {
            if (value <= 9_999L) {
                if (value <= 99L) {
                    if (value <= 9L) {
                        return 1;
                    } else {
                        return 2;
                    }
                } else if (value <= 999L) {
                    return 3;
                }
                return 4;
            } else if (value <= 999_999L) {
                if (value <= 99_999L) {
                    return 5;
                }
                return 6;
            } else if (value <= 9_999_999L) {
                return 7;
            }
            return 8;
        } else if (value <= 9_999_999_999_999_999L) {
            if (value <= 999_999_999_999L) {
                if (value <= 9_999_999_999L) {
                    if (value <= 999_999_999L) {
                        return 9;
                    } else {
                        return 10;
                    }
                } else if (value <= 99_999_999_999L) {
                    return 11;
                }
                return 12;
            } else if (value <= 99_999_999_999_999L) {
                if (value <= 9_999_999_999_999L) {
                    return 13;
                }
                return 14;
            } else if (value <= 999_999_999_999_999L) {
                return 15;
            }
            return 16;
        } else if (value <= 99_999_999_999_999_999L) {
            return 17;
        } else if (value <= 999_999_999_999_999_999L) {
            return 18;
        } else {
            return 19;
        }
    }

    static int numberOfDigits(BigInteger value) {
        if (value.signum() == 0) {
            return 1;
        } else if (value.signum() < 1) {
            value = value.negate();
        }
        int digitCount = (int) (LOG_2_DIV_LOG_10 * value.bitLength() + 1);
        if (powerOf10Big(digitCount - 1).compareTo(value) > 0) {
            return digitCount - 1;
        }
        return digitCount;
    }

    static BigInteger powerOf10Big(int n) {
        if (n < 0) {
            throw new IllegalArgumentException(format("n '%d' must be greater or equal to 0", n));
        }

        if (n < powersOf10B.length) {
            return powersOf10B[n];
        } else {
            // todo: should we maybe reuse principle from BigDecimal.bigTenToThe(int n) - it could become memory heavy
            return BigInteger.TEN.pow(n);
        }
    }

    static int maxCachedBigPowerOf10() {
        return powersOf10B.length - 1;
    }


    private static int maxLongPowerOf10() {
        return powersOf10L.length - 1;
    }

    private static long powerOf10Long(int n) {
        return powersOf10L[n];
    }

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
    private static final BigInteger[] powersOf10B;
    static {
        BigInteger[] tempPowOf10B = new BigInteger[50];
        tempPowOf10B[0] = BigInteger.ONE;                            // 10 ^ 0
        tempPowOf10B[1] = BigInteger.TEN;                            // 10 ^ 1
        tempPowOf10B[2] = BigInteger.valueOf(100L);                  // 10 ^ 2
        tempPowOf10B[3] = BigInteger.valueOf(1000L);                 // 10 ^ 3
        tempPowOf10B[4] = BigInteger.valueOf(10000L);                // 10 ^ 4
        tempPowOf10B[5] = BigInteger.valueOf(100000L);               // 10 ^ 5
        tempPowOf10B[6] = BigInteger.valueOf(1000000L);              // 10 ^ 6
        tempPowOf10B[7] = BigInteger.valueOf(10000000L);             // 10 ^ 7
        tempPowOf10B[8] = BigInteger.valueOf(100000000L);            // 10 ^ 8
        tempPowOf10B[9] = BigInteger.valueOf(1000000000L);           // 10 ^ 9
        tempPowOf10B[10] = BigInteger.valueOf(10000000000L);         // 10 ^ 10
        tempPowOf10B[11] = BigInteger.valueOf(100000000000L);        // 10 ^ 11
        tempPowOf10B[12] = BigInteger.valueOf(1000000000000L);       // 10 ^ 12
        tempPowOf10B[13] = BigInteger.valueOf(10000000000000L);      // 10 ^ 13
        tempPowOf10B[14] = BigInteger.valueOf(100000000000000L);     // 10 ^ 14
        tempPowOf10B[15] = BigInteger.valueOf(1000000000000000L);    // 10 ^ 15
        tempPowOf10B[16] = BigInteger.valueOf(10000000000000000L);   // 10 ^ 16
        tempPowOf10B[17] = BigInteger.valueOf(100000000000000000L);  // 10 ^ 17
        tempPowOf10B[18] = BigInteger.valueOf(1000000000000000000L); // 10 ^ 18
        for (int i = 19; i < tempPowOf10B.length; i++) {
            tempPowOf10B[i] = tempPowOf10B[i - 1].multiply(BigInteger.TEN);
        }
        powersOf10B = tempPowOf10B;
    }

    private static final long[] minUpscaleLimitForPowOf10 = {
            Long.MIN_VALUE / 1L,
            Long.MIN_VALUE / 10L,
            Long.MIN_VALUE / 100L,
            Long.MIN_VALUE / 1000L,
            Long.MIN_VALUE / 10000L,
            Long.MIN_VALUE / 100000L,
            Long.MIN_VALUE / 1000000L,
            Long.MIN_VALUE / 10000000L,
            Long.MIN_VALUE / 100000000L,
            Long.MIN_VALUE / 1000000000L,
            Long.MIN_VALUE / 10000000000L,
            Long.MIN_VALUE / 100000000000L,
            Long.MIN_VALUE / 1000000000000L,
            Long.MIN_VALUE / 10000000000000L,
            Long.MIN_VALUE / 100000000000000L,
            Long.MIN_VALUE / 1000000000000000L,
            Long.MIN_VALUE / 10000000000000000L,
            Long.MIN_VALUE / 100000000000000000L,
            Long.MIN_VALUE / 1000000000000000000L
    };
    private static final long[] maxUpscaleLimitForPowOf10 = {
            Long.MAX_VALUE / 1L,
            Long.MAX_VALUE / 10L,
            Long.MAX_VALUE / 100L,
            Long.MAX_VALUE / 1000L,
            Long.MAX_VALUE / 10000L,
            Long.MAX_VALUE / 100000L,
            Long.MAX_VALUE / 1000000L,
            Long.MAX_VALUE / 10000000L,
            Long.MAX_VALUE / 100000000L,
            Long.MAX_VALUE / 1000000000L,
            Long.MAX_VALUE / 10000000000L,
            Long.MAX_VALUE / 100000000000L,
            Long.MAX_VALUE / 1000000000000L,
            Long.MAX_VALUE / 10000000000000L,
            Long.MAX_VALUE / 100000000000000L,
            Long.MAX_VALUE / 1000000000000000L,
            Long.MAX_VALUE / 10000000000000000L,
            Long.MAX_VALUE / 100000000000000000L,
            Long.MAX_VALUE / 1000000000000000000L
    };

    private static final double LOG_2_DIV_LOG_10 = Math.log(2) / Math.log(10);
}
