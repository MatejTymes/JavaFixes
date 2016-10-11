package javafixes.math.util;

import java.math.BigInteger;

public class BigIntegerUtil {

    public static final BigInteger TEN_AS_BIG_INTEGER = BigInteger.TEN;

    public static final BigInteger MIN_LONG_AS_BIG_INTEGER = BigInteger.valueOf(Long.MIN_VALUE);
    public static final BigInteger MAX_LONG_AS_BIG_INTEGER = BigInteger.valueOf(Long.MAX_VALUE);

    public static boolean canConvertToLong(BigInteger unscaledValue) {
        return unscaledValue.compareTo(MAX_LONG_AS_BIG_INTEGER) <= 0 && unscaledValue.compareTo(MIN_LONG_AS_BIG_INTEGER) >= 0;
    }
}
