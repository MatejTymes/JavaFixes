package javafixes.math;

import org.junit.Test;

import java.math.BigInteger;

import static java.lang.Long.MAX_VALUE;
import static java.lang.Long.MIN_VALUE;
import static javafixes.math.Decimal.decimal;
import static javafixes.test.Condition.*;
import static javafixes.test.Random.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class DecimalToStringTest {

    @Test
    public void shouldPrintLongDecimalInScientificNotation() {
        long unscaledPositiveValue = randomLong(10, MAX_VALUE, notDivisibleBy10());
        long unscaledNegativeValue = randomLong(MIN_VALUE, -10, notDivisibleBy10());
        int scale = randomInt();

        int expectedExponent = numberOfDigits(unscaledPositiveValue) - scale - 1;

        String expectedPositiveString = addDecimalPoint(unscaledPositiveValue) + "e" + expectedExponent;
        assertThat(decimal(unscaledPositiveValue, scale).toScientificNotation(), equalTo(expectedPositiveString));
        String expectedNegativeString = addDecimalPoint(unscaledNegativeValue) + "e" + expectedExponent;
        assertThat(decimal(unscaledNegativeValue, scale).toScientificNotation(), equalTo(expectedNegativeString));

        for (long unscaledValue = -9; unscaledValue <= 9; unscaledValue++) {
            if (unscaledValue == 0) {
                assertThat(decimal(unscaledValue, scale).toScientificNotation(), equalTo("0e0"));
            } else {
                assertThat(decimal(unscaledValue, scale).toScientificNotation(), equalTo(unscaledValue + "e" + (-scale)));
            }
        }
    }

    @Test
    public void shouldPrintHugeDecimalInScientificNotation() {
        BigInteger unscaledPositiveValue = randomBigInteger(positive(), notDivisibleBy10(), notFitIntoLong());
        BigInteger unscaledNegativeValue = randomBigInteger(negative(), notDivisibleBy10(), notFitIntoLong());
        int scale = randomInt();

        int expectedExponent = numberOfDigits(unscaledPositiveValue) - scale - 1;

        String expectedPositiveString = addDecimalPoint(unscaledPositiveValue) + "e" + expectedExponent;
        assertThat(decimal(unscaledPositiveValue, scale).toScientificNotation(), equalTo(expectedPositiveString));
        String expectedNegativeString = addDecimalPoint(unscaledNegativeValue) + "e" + expectedExponent;
        assertThat(decimal(unscaledNegativeValue, scale).toScientificNotation(), equalTo(expectedNegativeString));
    }

    @Test
    public void shouldPrintScientificNotationEvenWhenExponentIsLongNumber() {
        long expectedExponent = Integer.MAX_VALUE + 1L;

        long unscaledValueL = randomLong(notDivisibleBy10());
        int scale =(int) (numberOfDigits(unscaledValueL) - expectedExponent - 1L);

        String expectedScientificNotation = addDecimalPoint(unscaledValueL) + "e" + expectedExponent;
        assertThat(decimal(unscaledValueL, scale).toScientificNotation(), equalTo(expectedScientificNotation));


        BigInteger unscaledValueB = randomBigInteger(notDivisibleBy10(), notFitIntoLong());
        scale =(int) (numberOfDigits(unscaledValueB) - expectedExponent - 1L);

        expectedScientificNotation = addDecimalPoint(unscaledValueB) + "e" + expectedExponent;
        assertThat(decimal(unscaledValueB, scale).toScientificNotation(), equalTo(expectedScientificNotation));
    }


    private int numberOfDigits(Number number) {
        return (int) number.toString().chars().filter(Character::isDigit).count();
    }

    private String addDecimalPoint(long number) {
        String value = Long.toString(number);
        int decimalPointPosition = (number >= 0) ? 1 : 2;
        return value.substring(0, decimalPointPosition) + "." + value.substring(decimalPointPosition);
    }

    private String addDecimalPoint(BigInteger number) {
        String value = number.toString();
        int decimalPointPosition = (number.signum() >= 0) ? 1 : 2;
        return value.substring(0, decimalPointPosition) + "." + value.substring(decimalPointPosition);
    }
}