package co.uk.matejtymes.test;

import java.math.BigInteger;

public class Random {

    public static int randomInt(int from, int to) {
        return (int) (Math.random() * (((long) to) - from + 1)) + from;
    }

    // todo: implement properly
    public static long randomLong(long from, long to) {
        return from;
    }

    // todo: implement properly
    public static BigInteger randomBigInteger(String fromValue, String toValue) {
        return new BigInteger(fromValue);
    }
}
