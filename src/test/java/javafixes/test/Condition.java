package javafixes.test;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Set;
import java.util.function.Function;

import static javafixes.common.CollectionUtil.newSet;
import static javafixes.math.BigIntegerUtil.MAX_LONG_AS_BIG_INTEGER;
import static javafixes.math.BigIntegerUtil.MIN_LONG_AS_BIG_INTEGER;

public interface Condition<T> extends Function<T, Boolean> {

    @SafeVarargs
    static <T> Condition<T> otherThan(T... values) {
        Set<T> exclusions = newSet(values);
        return value -> !exclusions.contains(value);
    }

    static <T extends Number> Condition<T> notDivisibleBy10() {
        return value -> mod(value, 10) != 0;
    }

    static <T extends Number> Condition<T> notZero() {
        return value -> signum(value) != 0;
    }

    static <T extends Number> Condition<T> positive() {
        return value -> signum(value) == 1;
    }

    static <T extends Number> Condition<T> negative() {
        return value -> signum(value) == -1;
    }

    static Condition<BigInteger> fitsIntoLong() {
        return value -> value.compareTo(MIN_LONG_AS_BIG_INTEGER) >= 0 && value.compareTo(MAX_LONG_AS_BIG_INTEGER) <= 0;
    }

    static Condition<BigInteger> notFitIntoLong() {
        return value -> value.compareTo(MIN_LONG_AS_BIG_INTEGER) < 0 || value.compareTo(MIN_LONG_AS_BIG_INTEGER) > 0;
    }

    static int signum(Number value) {
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

    static int mod(Number value, int divisor) {
        if (value instanceof Integer) {
            return ((Integer) value) % divisor;
        } else if (value instanceof Long) {
            return (int) (((Long) value) % divisor);
        } else if (value instanceof BigInteger) {
            return ((BigInteger) value).mod(BigInteger.valueOf(divisor)).intValue();
        } else {
            throw new IllegalArgumentException("Unsupported number type: " + value.getClass());
        }

    }
}
