package mtymes.javafixes.test;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Random {

    public static int randomInt(int from, int to) {
        return (int) (Math.random() * (((long) to) - from + 1)) + from;
    }

    public static int randomInt() {
        return randomInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public static long randomLong(long from, long to) {
        // todo: implement properly
        return from;
    }

    public static long randomLong() {
        return randomLong(Long.MIN_VALUE, Long.MAX_VALUE);
    }

    public static BigInteger randomBigInteger(String fromValue, String toValue) {
        // todo: implement properly
        return new BigInteger(fromValue);
    }

    public static BigInteger randomBigInteger() {
        // todo: implement properly
        return randomBigInteger("-999999999999999999", "999999999999999999");
    }

    public static BigDecimal randomBigDecimal(String fromValue, String toValue) {
        // todo: implement properly
        return new BigDecimal(fromValue);
    }

    public static BigDecimal randomBigDecimal() {
        return randomBigDecimal("-999999999999999999.99999", "999999999999999999.99999");
    }

    @SafeVarargs
    public static  <T> T pickRandomValue(T... values) {
        // todo: implement properly
        return values[0];
    }
}
