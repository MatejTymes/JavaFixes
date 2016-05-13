package mtymes.javafixes.test;

import mtymes.javafixes.beta.decimal.BigIntegerUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Function;

import static mtymes.javafixes.beta.decimal.BigIntegerUtil.BIG_INTEGER_MIN_LONG;

public class Conditions {

    public static <T extends Number> Function<T, Boolean> divisibleBy10() {
        return value -> value.longValue() % 10 == 0;
    }

    public static <T extends Number> Function<T, Boolean> notDivisibleBy10() {
        return value -> value.longValue() % 10 != 0;
    }

    public static <T extends Number> Function<T, Boolean> positive() {
        return value -> signum(value) == 1;
    }

    public static <T extends Number> Function<T, Boolean> negative() {
        return value -> signum(value) == -1;
    }

    public static Function<BigInteger, Boolean> doesFitIntoLong() {
        return value -> value.compareTo(BIG_INTEGER_MIN_LONG) >= 0 && value.compareTo(BigIntegerUtil.BIG_INTEGER_MAX_LONG) <= 0;
    }

    public static Function<BigInteger, Boolean> doesNotFitIntoLong() {
        return value -> value.compareTo(BIG_INTEGER_MIN_LONG) < 0 || value.compareTo(BigIntegerUtil.BIG_INTEGER_MIN_LONG) > 0;
    }

    private static int signum(Number value) {
        if (value instanceof Integer) {
            return Integer.signum((Integer) value);
        } else if (value instanceof Long) {
            return Long.signum((Long) value);
        } else if (value instanceof BigInteger) {
            return ((BigInteger) value).signum();
        } else if (value instanceof BigDecimal) {
            return ((BigDecimal) value).signum();
        } else {
            throw new IllegalArgumentException("Unsupported number type: " + value.getClass());
        }
    }
}
