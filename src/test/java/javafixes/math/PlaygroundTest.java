package javafixes.math;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static javafixes.test.Random.randomInt;
import static javafixes.test.Random.randomLong;

// todo: remove from git
public class PlaygroundTest {

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
