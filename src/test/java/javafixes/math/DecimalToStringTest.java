package javafixes.math;

import org.junit.Test;

import java.math.BigInteger;

import static java.lang.Long.MAX_VALUE;
import static java.lang.Long.MIN_VALUE;
import static javafixes.math.Decimal.d;
import static javafixes.math.Decimal.decimal;
import static javafixes.test.Condition.*;
import static javafixes.test.Random.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class DecimalToStringTest {

    @Test
    public void shouldPrintDecimalAsPlainString() {
        assertThat(d(123, 0).toPlainString(), equalTo("123"));
        assertThat(d(-123, 0).toPlainString(), equalTo("-123"));

        assertThat(d(456, 1).toPlainString(), equalTo("45.6"));
        assertThat(d(-456, 1).toPlainString(), equalTo("-45.6"));
        assertThat(d(456, 2).toPlainString(), equalTo("4.56"));
        assertThat(d(-456, 2).toPlainString(), equalTo("-4.56"));
        assertThat(d(456, 3).toPlainString(), equalTo("0.456"));
        assertThat(d(-456, 3).toPlainString(), equalTo("-0.456"));
        assertThat(d(456, 4).toPlainString(), equalTo("0.0456"));
        assertThat(d(-456, 4).toPlainString(), equalTo("-0.0456"));

        assertThat(d(456, -1).toPlainString(), equalTo("4560"));
        assertThat(d(456, -4).toPlainString(), equalTo("4560000"));

        assertThat(d(new BigInteger("12345678901234567890123456789"), 0).toPlainString(), equalTo("12345678901234567890123456789"));
        assertThat(d(new BigInteger("-12345678901234567890123456789"), 0).toPlainString(), equalTo("-12345678901234567890123456789"));
        assertThat(d(new BigInteger("12345678901234567890123456789"), 1).toPlainString(), equalTo("1234567890123456789012345678.9"));
        assertThat(d(new BigInteger("-12345678901234567890123456789"), 1).toPlainString(), equalTo("-1234567890123456789012345678.9"));
        assertThat(d(new BigInteger("12345678901234567890123456789"), 28).toPlainString(), equalTo("1.2345678901234567890123456789"));
        assertThat(d(new BigInteger("-12345678901234567890123456789"), 28).toPlainString(), equalTo("-1.2345678901234567890123456789"));
        assertThat(d(new BigInteger("12345678901234567890123456789"), 29).toPlainString(), equalTo("0.12345678901234567890123456789"));
        assertThat(d(new BigInteger("-12345678901234567890123456789"), 29).toPlainString(), equalTo("-0.12345678901234567890123456789"));
        assertThat(d(new BigInteger("12345678901234567890123456789"), 30).toPlainString(), equalTo("0.012345678901234567890123456789"));
        assertThat(d(new BigInteger("-12345678901234567890123456789"), 30).toPlainString(), equalTo("-0.012345678901234567890123456789"));

        assertThat(d(789, 0).toPlainString(0), equalTo("789"));
        assertThat(d(-789, 0).toPlainString(0), equalTo("-789"));
        assertThat(d(789, 0).toPlainString(1), equalTo("789.0"));
        assertThat(d(-789, 0).toPlainString(1), equalTo("-789.0"));
        assertThat(d(789, 0).toPlainString(2), equalTo("789.00"));
        assertThat(d(-789, 0).toPlainString(2), equalTo("-789.00"));
        assertThat(d(789, 0).toPlainString(-1), equalTo("789"));
        assertThat(d(-789, 0).toPlainString(-1), equalTo("-789"));
        assertThat(d(789, 0).toPlainString(-2), equalTo("789"));
        assertThat(d(-789, 0).toPlainString(-2), equalTo("-789"));

        assertThat(d(123, 2).toPlainString(2), equalTo("1.23"));
        assertThat(d(-123, 2).toPlainString(2), equalTo("-1.23"));
        assertThat(d(123, 2).toPlainString(3), equalTo("1.230"));
        assertThat(d(-123, 2).toPlainString(3), equalTo("-1.230"));
        assertThat(d(123, 2).toPlainString(1), equalTo("1.23"));
        assertThat(d(-123, 2).toPlainString(1), equalTo("-1.23"));

        assertThat(d(456, -1).toPlainString(-1), equalTo("4560"));
        assertThat(d(-456, -1).toPlainString(-1), equalTo("-4560"));
        assertThat(d(456, -1).toPlainString(-2), equalTo("4560"));
        assertThat(d(-456, -1).toPlainString(-2), equalTo("-4560"));
        assertThat(d(456, -1).toPlainString(0), equalTo("4560"));
        assertThat(d(-456, -1).toPlainString(0), equalTo("-4560"));
        assertThat(d(456, -1).toPlainString(1), equalTo("4560.0"));
        assertThat(d(-456, -1).toPlainString(1), equalTo("-4560.0"));

        assertThat(d(new BigInteger("12345678901234567890123456789"), 0).toPlainString(-1), equalTo("12345678901234567890123456789"));
        assertThat(d(new BigInteger("-12345678901234567890123456789"), 0).toPlainString(-1), equalTo("-12345678901234567890123456789"));
        assertThat(d(new BigInteger("12345678901234567890123456789"), 0).toPlainString(0), equalTo("12345678901234567890123456789"));
        assertThat(d(new BigInteger("-12345678901234567890123456789"), 0).toPlainString(0), equalTo("-12345678901234567890123456789"));
        assertThat(d(new BigInteger("12345678901234567890123456789"), 0).toPlainString(1), equalTo("12345678901234567890123456789.0"));
        assertThat(d(new BigInteger("-12345678901234567890123456789"), 0).toPlainString(1), equalTo("-12345678901234567890123456789.0"));

        assertThat(d(new BigInteger("12345678901234567890123456789"), 2).toPlainString(1), equalTo("123456789012345678901234567.89"));
        assertThat(d(new BigInteger("-12345678901234567890123456789"), 2).toPlainString(1), equalTo("-123456789012345678901234567.89"));
        assertThat(d(new BigInteger("12345678901234567890123456789"), 2).toPlainString(2), equalTo("123456789012345678901234567.89"));
        assertThat(d(new BigInteger("-12345678901234567890123456789"), 2).toPlainString(2), equalTo("-123456789012345678901234567.89"));
        assertThat(d(new BigInteger("12345678901234567890123456789"), 2).toPlainString(3), equalTo("123456789012345678901234567.890"));
        assertThat(d(new BigInteger("-12345678901234567890123456789"), 2).toPlainString(3), equalTo("-123456789012345678901234567.890"));

        assertThat(d(new BigInteger("12345678901234567890123456789"), -1).toPlainString(-2), equalTo("123456789012345678901234567890"));
        assertThat(d(new BigInteger("-12345678901234567890123456789"), -1).toPlainString(-2), equalTo("-123456789012345678901234567890"));
        assertThat(d(new BigInteger("12345678901234567890123456789"), -1).toPlainString(-1), equalTo("123456789012345678901234567890"));
        assertThat(d(new BigInteger("-12345678901234567890123456789"), -1).toPlainString(-1), equalTo("-123456789012345678901234567890"));
        assertThat(d(new BigInteger("12345678901234567890123456789"), -1).toPlainString(0), equalTo("123456789012345678901234567890"));
        assertThat(d(new BigInteger("-12345678901234567890123456789"), -1).toPlainString(0), equalTo("-123456789012345678901234567890"));
        assertThat(d(new BigInteger("12345678901234567890123456789"), -1).toPlainString(1), equalTo("123456789012345678901234567890.0"));
        assertThat(d(new BigInteger("-12345678901234567890123456789"), -1).toPlainString(1), equalTo("-123456789012345678901234567890.0"));
    }

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

    @Test
    public void shouldPrintDecimalAsString() {
        long unscaledValueL = randomLong(notDivisibleBy10());
        BigInteger unscaledValueB = randomBigInteger(notDivisibleBy10(), notFitIntoLong());

        for (int scale = -18; scale < 19; scale++) {
            Decimal decimalL = decimal(unscaledValueL, scale);
            assertThat(decimalL.toString(), equalTo(decimalL.toPlainString()));

            Decimal decimalB = decimal(unscaledValueB, scale);
            assertThat(decimalB.toString(), equalTo(decimalB.toPlainString()));
        }

        for (int scale = -100; scale <= -19; scale++) {
            Decimal decimalL = decimal(unscaledValueL, scale);
            assertThat(decimalL.toString(), equalTo(decimalL.toScientificNotation()));

            Decimal decimalB = decimal(unscaledValueB, scale);
            assertThat(decimalB.toString(), equalTo(decimalB.toScientificNotation()));
        }

        for (int scale = 19; scale <= 100; scale++) {
            Decimal decimalL = decimal(unscaledValueL, scale);
            assertThat(decimalL.toString(), equalTo(decimalL.toScientificNotation()));

            Decimal decimalB = decimal(unscaledValueB, scale);
            assertThat(decimalB.toString(), equalTo(decimalB.toScientificNotation()));
        }
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