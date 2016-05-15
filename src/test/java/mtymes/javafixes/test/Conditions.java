package mtymes.javafixes.test;

import mtymes.javafixes.beta.decimal.BigIntegerUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Set;
import java.util.function.Function;

import static mtymes.javafixes.beta.decimal.BigIntegerUtil.BIG_INTEGER_MIN_LONG;
import static mtymes.javafixes.test.CollectionUtil.newSet;

public class Conditions {

    public static <T extends Number> Function<T, Boolean> divisibleBy10() {
        return value -> mod(value, 10) == 0;
    }

    public static <T extends Number> Function<T, Boolean> notDivisibleBy10() {
        return value -> mod(value, 10) != 0;
    }

    public static <T extends Number> Function<T, Boolean> positive() {
        return value -> signum(value) == 1;
    }

    public static <T extends Number> Function<T, Boolean> negative() {
        return value -> signum(value) == -1;
    }

    public static Function<BigInteger, Boolean> fitsIntoLong() {
        return value -> value.compareTo(BIG_INTEGER_MIN_LONG) >= 0 && value.compareTo(BigIntegerUtil.BIG_INTEGER_MAX_LONG) <= 0;
    }

    public static Function<BigInteger, Boolean> notFitIntoLong() {
        return value -> value.compareTo(BIG_INTEGER_MIN_LONG) < 0 || value.compareTo(BigIntegerUtil.BIG_INTEGER_MIN_LONG) > 0;
    }

    public static <T> Function<T, Boolean> excluding(T... values) {
        Set<T> exclusions = newSet(values);
        return value -> !exclusions.contains(value);
    }

    public static int signum(Number value) {
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

    public static int mod(Number value, int divisor) {
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
