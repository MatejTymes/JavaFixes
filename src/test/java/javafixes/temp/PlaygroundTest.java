package javafixes.temp;

import javafixes.math.Decimal;
import javafixes.math.Scale;
import org.junit.Ignore;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import static javafixes.math.Decimal.d;
import static javafixes.math.Precision._34_SIGNIFICANT_DIGITS;
import static javafixes.test.Random.randomInt;
import static javafixes.test.Random.randomLong;

// todo: remove from git
@Ignore
public class PlaygroundTest {


    @Test
    public void thinkingAboutToString() {

        System.out.println(Math.pow(4, 0.5));

        System.out.println(d("1.1").pow(500).deprecisionTo(_34_SIGNIFICANT_DIGITS).toScientificNotation());

//        System.out.println(d("     100_000_000.0"));
        System.out.println(d("_____100_000_000.0"));
        System.out.println(d("__12_300_000_000.0"));
        System.out.println(d("___1_000_000_000.0"));
        System.out.println(d("_123_000_000_000.0"));
        System.out.println(d("_______________0.000_000_001"));
        System.out.println(d("_______________0.000_000_001_23"));
        System.out.println(d("_______________0.000_000_000_1"));
        System.out.println(d("_______________0.000_000_000_123"));

        int scale = 5000;
        System.out.println(
                d("1.23").div(d("7.12"), Scale.of(scale)).doubleValue()
        );
        System.out.println(
                d("1.23").div(d("7.12"), Scale.of(scale))
        );
        System.out.println(
                new BigDecimal("1.23").divide(new BigDecimal("7.12"), scale, RoundingMode.HALF_UP)
//                new BigDecimal("1.23").divide(new BigDecimal("7.12"))
        );
    }

    @Test
    public void powerOf10() {
        long power = 1;
        BigInteger value = BigInteger.TEN;
        System.out.println(power);
//            System.out.println(power + " - " + value);

        long startTime;
        long duration;

        while (power < Integer.MAX_VALUE) {
            power *= 2;

            System.out.println(power);

            startTime = System.currentTimeMillis();
            value = value.multiply(value);
            duration = System.currentTimeMillis() - startTime;
            System.out.println("- smart duration: " + duration + "ms");

            startTime = System.currentTimeMillis();
            BigInteger standard = BigInteger.TEN.pow((int) power);
            duration = System.currentTimeMillis() - startTime;
            System.out.println("- standard duration: " + duration + "ms");

            if (!value.equals(standard)) {
                System.out.println("wow, difference");
            }
//            System.out.println(power + " - " + value);
        }
    }

    @Test
    public void divisionTest() {
        BigDecimal valueA = new BigDecimal("3.14");
        BigDecimal valueB = new BigDecimal("3182");

        System.out.println(valueA.divide(valueB, 30, RoundingMode.HALF_UP));
    }

    @Test
    public void shouldQuicklyCalculatePowerOf10() {
        int scale = 1234567890;

        long start = System.currentTimeMillis();
        BigInteger value = BigInteger.TEN.pow(scale);
        long duration = start - System.currentTimeMillis();

        System.out.println(duration + "ms");
    }

    @Test
    public void shouldCompareBigDecimalsQuickly() {
        for (int i = 0; i < 100; i++) {
            long unscaledValueA = randomLong();
            long unscaledValueB = randomLong();
            int scaleA = randomInt();
            int scaleB = randomInt();

            BigDecimal bdA = BigDecimal.valueOf(unscaledValueA, scaleA);
            BigDecimal bdB = BigDecimal.valueOf(unscaledValueB, scaleB);

            System.out.println("BD:" + bdA + ".compareTo(" + bdB + ") = ");
            System.out.println(" " + bdA.compareTo(bdB));

            Decimal dA = Decimal.d(unscaledValueA, scaleA);
            Decimal dB = Decimal.d(unscaledValueB, scaleB);

            System.out.println("D:" + dA + ".compareTo(" + dB + ") = ");
            System.out.println(" " + dA.compareTo(dB));
        }
    }

    @Test
    public void shouldCompareBigDecimalsQuicklyV1() {
        long unscaledValueA = 7389723322205810361L;
        BigInteger unscaledValueB = new BigInteger("844416801447803537172");
        int scaleA = 604814438;
        int scaleB = 536395671;

        BigDecimal bdA = BigDecimal.valueOf(unscaledValueA, scaleA);
        BigDecimal bdB = new BigDecimal(unscaledValueB, scaleB);

        System.out.println("BD:" + bdA + ".compareTo(" + bdB + ") = ");
        System.out.println(" " + bdA.compareTo(bdB));

        Decimal dA = Decimal.d(unscaledValueA, scaleA);
        Decimal dB = Decimal.d(unscaledValueB, scaleB);

        System.out.println("D:" + dA + ".compareTo(" + dB + ") = ");
        System.out.println(" " + dA.compareTo(dB));
    }

    @Test
    public void shouldPrintDecimal() {
        System.out.println(BigDecimal.valueOf(123, Integer.MAX_VALUE).toPlainString());
        System.out.println(Decimal.decimal(123, Integer.MAX_VALUE).toPlainString());
    }

}
