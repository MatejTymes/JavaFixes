package mtymes.javafixes.test;

import mtymes.javafixes.beta.decimal.BigIntegerUtil;

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

    public static Function<BigInteger, Boolean> doesFitIntoLong() {
        return value -> value.compareTo(BIG_INTEGER_MIN_LONG) >= 0 && value.compareTo(BigIntegerUtil.BIG_INTEGER_MAX_LONG) <= 0;
    }

    public static Function<BigInteger, Boolean> doesNotFitIntoLong() {
        return value -> value.compareTo(BIG_INTEGER_MIN_LONG) < 0 || value.compareTo(BigIntegerUtil.BIG_INTEGER_MIN_LONG) > 0;
    }

    @SafeVarargs
    public static <T extends Number> Function<T, Boolean> that(Function<T, Boolean>... functions) {
        return value -> {
            boolean result = true;
            for (Function<T, Boolean> function : functions) {
                if (!function.apply(value)) {
                    result = false;
                    break;
                }
            }
            return result;
        };
    }
}
