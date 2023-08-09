package javafixes.test;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.UUID.randomUUID;

public class Random {

    private static final BigInteger BIG_PLUS_INTEGER = BigInteger.valueOf(Long.MAX_VALUE);
    private static final BigInteger BIG_MINUS_INTEGER = BigInteger.valueOf(Long.MIN_VALUE + 1);

    public static boolean randomBoolean() {
        return pickRandomValue(true, false);
    }

    public static byte randomByte() {
        return (byte) randomInt(-128, 128);
    }

    public static byte[] randomByteArray(int size) {
        byte[] bytes = new byte[size];
        for (int i = 0; i < size; i++) {
            bytes[i] = randomByte();
        }
        return bytes;
    }

    public static byte[] randomByteArray() {
        return randomByteArray(randomInt(1, 64));
    }

    @SafeVarargs
    public static int randomInt(int from, int to, Function<Integer, Boolean>... validityConditions) {
        return generateValidValue(
                // typecast it to long as otherwise we could get int overflow
                () -> (int) ((long) (Math.random() * ((long) to - (long) from + 1L)) + (long) from),
                validityConditions
        );
    }

    public static int randomInt() {
        return randomInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    @SafeVarargs
    public static int randomInt(Function<Integer, Boolean>... validityConditions) {
        return generateValidValue(Random::randomInt, validityConditions);
    }

    @SafeVarargs
    public static long randomLong(long from, long to, Function<Long, Boolean>... validityConditions) {
        return generateValidValue(
//                () -> new RandomDataGenerator().nextLong(from, to),
                () -> ThreadLocalRandom.current().nextLong(from, to) + (long) randomInt(0, 1),
                validityConditions
        );
    }

    public static long randomLong() {
        return randomLong(Long.MIN_VALUE, Long.MAX_VALUE);
    }

    @SafeVarargs
    public static long randomLong(Function<Long, Boolean>... validityConditions) {
        return generateValidValue(Random::randomLong, validityConditions);
    }

    public static BigInteger randomBigInteger() {
        boolean positive = randomBoolean();
        return (positive ? BIG_PLUS_INTEGER : BIG_MINUS_INTEGER)
                .multiply(BigInteger.valueOf(randomLong(0, 100)))
                .add(BigInteger.valueOf(positive ? randomLong(0, Long.MAX_VALUE) : randomLong(Long.MIN_VALUE + 1, 0)));
    }

    @SafeVarargs
    public static BigInteger randomBigInteger(Function<BigInteger, Boolean>... validityConditions) {
        return generateValidValue(Random::randomBigInteger, validityConditions);
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

    @SafeVarargs
    public static <T extends Enum<T>> T randomEnum(Class<T> enumClass, Function<T, Boolean>... validityConditions) {
        return generateValidValue(
                () -> pickRandomValue(enumClass.getEnumConstants()),
                validityConditions
        );
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
        return randomEnum(RoundingMode.class);
    }

    @SafeVarargs
    public static RoundingMode randomRoundingMode(Function<RoundingMode, Boolean>... validityConditions) {
        return generateValidValue(Random::randomRoundingMode, validityConditions);
    }

    public static Exception randomException() {
        return pickRandomValue(
                randomCheckedException(),
                randomRuntimeException()
        );
    }

    public static RuntimeException randomRuntimeException() {
        return pickRandomValue(
                new RuntimeException("Some unique failure " + randomUUID()),
                new IllegalStateException("Another unique failure " + randomUUID())
        );
    }


    public static Exception randomCheckedException() {
        return pickRandomValue(
                new Exception("Other unique failure " + randomUUID()),
                new IOException("Yet another unique failure " + randomUUID())
        );
    }


    @SafeVarargs
    public static <T> T pickRandomValue(T... values) {
        return values[randomInt(0, values.length - 1)];
    }

    public static <T> T pickRandomValue(List<T> values) {
        return values.get(randomInt(0, values.size() - 1));
    }

    @SafeVarargs
    public static <T> T generateValidValue(Supplier<T> generator, Function<T, Boolean>... validityConditions) {
        T value;

        int infiniteCycleCounter = 0;

        boolean valid;
        do {
            valid = true;
            value = generator.get();
            for (Function<T, Boolean> validityCondition : validityConditions) {
                if (!validityCondition.apply(value)) {
                    valid = false;
                    break;
                }
            }

            if (infiniteCycleCounter++ == 1_000) {
                throw new IllegalStateException("Possibly reached infinite cycle - unable to generate value after 1000 attempts.");
            }
        } while (!valid);

        return value;
    }

//    public static void main(String[] args) {
//        Map<Integer, Integer> digitsCountDistribution = new TreeMap<>();
//
//        for (int i = 0; i < 1_000_000_000; i++) {
//            int value = randomInt();
//            int length = Integer.toString(value).length();
//            if (value < 0) {
//                length = -(length - 1);
//            }
//
//            int count = digitsCountDistribution.getOrDefault(length, 0);
//            digitsCountDistribution.put(length, count + 1);
//        }
//
//        for (int digitCount : digitsCountDistribution.keySet()) {
//            System.out.println(digitCount + " -> " + digitsCountDistribution.get(digitCount));
//        }
//    }
}
