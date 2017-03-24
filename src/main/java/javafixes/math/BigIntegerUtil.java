package javafixes.math;

import java.math.BigInteger;

class BigIntegerUtil {

    static final BigInteger TEN_AS_BIG_INTEGER = BigInteger.TEN;

    private static final BigInteger MIN_LONG_AS_BIG_INTEGER = BigInteger.valueOf(Long.MIN_VALUE);
    private static final BigInteger MAX_LONG_AS_BIG_INTEGER = BigInteger.valueOf(Long.MAX_VALUE);

    static boolean canConvertToLong(BigInteger unscaledValue) {
        return unscaledValue.compareTo(MAX_LONG_AS_BIG_INTEGER) <= 0 && unscaledValue.compareTo(MIN_LONG_AS_BIG_INTEGER) >= 0;
    }
}
