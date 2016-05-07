package mtymes.javafixes.test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.UUID.randomUUID;

public class Random {

    public static boolean randomBoolean() {
        return pickRandomValue(true, false);
    }

    public static int randomInt(int from, int to) {
        // typecast it to long as otherwise we could get int overflow
        return (int) ((long) (Math.random() * ((long) to - (long) from + 1L)) + (long) from);
    }

    public static int randomInt() {
        return randomInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public static long randomLong(long from, long to) {
        return ThreadLocalRandom.current().nextLong(from, to) + (long) randomInt(0, 1);
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

    public static String randomUUIDString() {
        return randomUUID().toString();
    }

    public static char randomDigit() {
        return pickRandomValue('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');
    }

    public static char randomNonZeroDigit() {
        return pickRandomValue('1', '2', '3', '4', '5', '6', '7', '8', '9');
    }

    public static String randomSign() {
        return pickRandomValue("+", "-", "");
    }

    public static String randomDecimalString() {
        StringBuilder number = new StringBuilder(randomSign());
        int mantisaDigitsCount = randomInt(1, 30);
        int fractionalDigitsCount = randomInt(0, 30);

        for (int i = 0; i < mantisaDigitsCount; i++) {
            number.append(randomDigit());
        }

        if (fractionalDigitsCount > 0) {
            number.append(".");
            for (int i = 0; i < fractionalDigitsCount; i++) {
                number.append(randomDigit());
            }
        }

        return number.toString();
    }

    public static String randomScientificNotationDecimalString() {
        StringBuilder number = new StringBuilder(randomSign());

        number.append(randomNonZeroDigit());

        int fractionalDigitsCount = randomInt(0, 30);
        if (fractionalDigitsCount > 0) {
            number.append(".");
            for (int i = 0; i < fractionalDigitsCount; i++) {
                number.append(randomDigit());
            }
        }

        number.append(pickRandomValue('e', 'E')).append(randomInt());

        return number.toString();
    }

    public static RoundingMode randomRoundingMode() {
        return pickRandomValue(RoundingMode.values());
    }

    @SafeVarargs
    public static <T> T pickRandomValue(T... values) {
        return values[randomInt(0, values.length - 1)];
    }
}
