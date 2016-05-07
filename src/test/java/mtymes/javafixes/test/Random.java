package mtymes.javafixes.test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.UUID.randomUUID;

public class Random {

    private static final BigInteger BIG_PLUS_INTEGER = BigInteger.valueOf(Long.MAX_VALUE);
    private static final BigInteger BIG_MINUS_INTEGER = BigInteger.valueOf(Long.MIN_VALUE + 1);

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

    public static BigInteger randomBigInteger() {
        boolean positive = randomBoolean();
        return (positive ? BIG_PLUS_INTEGER : BIG_MINUS_INTEGER)
                .multiply(BigInteger.valueOf(randomLong(0, 100)))
                .add(BigInteger.valueOf(positive ? randomLong(0, Long.MAX_VALUE) : randomLong(Long.MIN_VALUE + 1, 0)));
    }

    public static BigInteger randomNegativeBigInteger() {
        return BIG_MINUS_INTEGER
                .multiply(BigInteger.valueOf(randomLong(0, 100)))
                .add(BigInteger.valueOf(randomLong(Long.MIN_VALUE + 1, -1)));
    }

    public static BigInteger randomPositiveBigInteger() {
        return BIG_PLUS_INTEGER
                .multiply(BigInteger.valueOf(randomLong(0, 100)))
                .add(BigInteger.valueOf(randomLong(1, Long.MAX_VALUE)));
    }

    public static BigDecimal randomBigDecimal() {
        return new BigDecimal(randomBigInteger(), randomInt(-100, 100));
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

    public static <T> T pickRandomValue(List<T> values) {
        return values.get(randomInt(0, values.size() - 1));
    }
}
