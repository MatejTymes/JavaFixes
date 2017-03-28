package javafixes.math;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.math.BigInteger;

import static javafixes.test.Condition.*;
import static javafixes.test.Random.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class DecimalCreationFromStringTest {

    @Test
    public void shouldFailOnInvalidString() {
        assertStringFailsParsing("");
        assertStringFailsParsing("+");
        assertStringFailsParsing("-");
        assertStringFailsParsing("_");
        assertStringFailsParsing(".");
        assertStringFailsParsing("+.");
        assertStringFailsParsing("++1");
        assertStringFailsParsing("-+1");
        assertStringFailsParsing("+-1");
        assertStringFailsParsing("--1");
        assertStringFailsParsing("1-1");
        assertStringFailsParsing("1+1");
        assertStringFailsParsing("1.1.1");
        assertStringFailsParsing("-.");
        assertStringFailsParsing("_._");
        assertStringFailsParsing("+_._");
        assertStringFailsParsing("-_._");
        assertStringFailsParsing("abc");
    }

    @Test
    public void shouldPassOnValidString() {
        Decimal.decimal("0");
        Decimal.decimal("+_1_000");
        Decimal.decimal("-_1_000.00_");
        Decimal.decimal("___+1_000");
        Decimal.decimal("___-1_000");
        Decimal.decimal("0.0");
        Decimal.decimal(".0");
        Decimal.decimal("0.");

        Decimal.d("0");
        Decimal.d("+_1_000");
        Decimal.d("-_1_000.00_");
        Decimal.d("___+1_000");
        Decimal.d("___-1_000");
        Decimal.d("0.0");
        Decimal.d(".0");
        Decimal.d("0.");
    }

    @Test
    public void shouldParseDecimalStringIntoLong() {
        shouldFindUnscaledValueOnStringPermutations("9223372036854775807", Long.MAX_VALUE);
        shouldFindUnscaledValueOnStringPermutations("-9223372036854775808", Long.MIN_VALUE);

        long value = randomLong(positive(), notDivisibleBy10());
        shouldFindUnscaledValueOnStringPermutations(Long.toString(value), value);

        value = randomLong(negative(), notDivisibleBy10());
        shouldFindUnscaledValueOnStringPermutations(Long.toString(value), value);

        shouldFindUnscaledValueOnStringPermutations("0", 0L);
    }

    @Test
    public void shouldParseDecimalStringIntoBigInteger() {
        shouldFindUnscaledValueOnStringPermutations("9223372036854775808", BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE));
        shouldFindUnscaledValueOnStringPermutations("-9223372036854775809", BigInteger.valueOf(Long.MIN_VALUE).subtract(BigInteger.ONE));

        BigInteger value = randomBigInteger(positive(), notDivisibleBy10(), notFitIntoLong());
        shouldFindUnscaledValueOnStringPermutations(value.toString(), value);

        value = randomBigInteger(negative(), notDivisibleBy10(), notFitIntoLong());
        shouldFindUnscaledValueOnStringPermutations(value.toString(), value);
    }

    /* ========================== */
    /* ---   helper methods   --- */
    /* ========================== */

    private void shouldFindUnscaledValueOnStringPermutations(String text, Number expectedUnscaledValue) {

        boolean expectsZero = signum(expectedUnscaledValue) == 0;

        Decimal decimal = Decimal.decimal(text);
        Decimal d = Decimal.d(text);
        assertThat(decimal.unscaledValue(), equalTo(expectedUnscaledValue));
        assertThat(decimal.scale(), equalTo(0));
        assertThat(d.unscaledValue(), equalTo(expectedUnscaledValue));
        assertThat(d.scale(), equalTo(0));


        int zerosCount = randomInt(1, 5);

        // append n zeros after value
        String newText = text + nZeros(zerosCount);
        decimal = Decimal.decimal(newText);
        d = Decimal.d(newText);
        assertThat(decimal.unscaledValue(), equalTo(expectedUnscaledValue));
        assertThat(decimal.scale(), equalTo(expectsZero ? 0 : -zerosCount));
        assertThat(d.unscaledValue(), equalTo(expectedUnscaledValue));
        assertThat(d.scale(), equalTo(expectsZero ? 0 : -zerosCount));

        newText = addUnderscores(newText);
        decimal = Decimal.decimal(newText);
        d = Decimal.d(newText);
        assertThat(decimal.unscaledValue(), equalTo(expectedUnscaledValue));
        assertThat(decimal.scale(), equalTo(expectsZero ? 0 : -zerosCount));
        assertThat(d.unscaledValue(), equalTo(expectedUnscaledValue));
        assertThat(d.scale(), equalTo(expectsZero ? 0 : -zerosCount));

        // add empty decimal point after value
        newText = text + ".";
        decimal = Decimal.decimal(newText);
        d = Decimal.d(newText);
        assertThat(decimal.unscaledValue(), equalTo(expectedUnscaledValue));
        assertThat(decimal.scale(), equalTo(0));
        assertThat(d.unscaledValue(), equalTo(expectedUnscaledValue));
        assertThat(d.scale(), equalTo(0));

        newText = addUnderscores(newText);
        decimal = Decimal.decimal(newText);
        d = Decimal.d(newText);
        assertThat(decimal.unscaledValue(), equalTo(expectedUnscaledValue));
        assertThat(decimal.scale(), equalTo(0));
        assertThat(d.unscaledValue(), equalTo(expectedUnscaledValue));
        assertThat(d.scale(), equalTo(0));

        // add decimal point with n zeros after value
        newText = text + "." + nZeros(zerosCount);
        decimal = Decimal.decimal(newText);
        d = Decimal.d(newText);
        assertThat(decimal.unscaledValue(), equalTo(expectedUnscaledValue));
        assertThat(decimal.scale(), equalTo(0));
        assertThat(d.unscaledValue(), equalTo(expectedUnscaledValue));
        assertThat(d.scale(), equalTo(0));

        newText = addUnderscores(newText);
        decimal = Decimal.decimal(newText);
        d = Decimal.d(newText);
        assertThat(decimal.unscaledValue(), equalTo(expectedUnscaledValue));
        assertThat(decimal.scale(), equalTo(0));
        assertThat(d.unscaledValue(), equalTo(expectedUnscaledValue));
        assertThat(d.scale(), equalTo(0));

        // prepend with "0." and n zeros before value
        newText = sign(text) + "0." + nZeros(zerosCount) + valueAfterSign(text);
        decimal = Decimal.decimal(newText);
        d = Decimal.d(newText);
        assertThat(decimal.unscaledValue(), equalTo(expectedUnscaledValue));
        assertThat(decimal.scale(), equalTo(expectsZero ? 0 : zerosCount + numberOfDigits(text)));
        assertThat(d.unscaledValue(), equalTo(expectedUnscaledValue));
        assertThat(d.scale(), equalTo(expectsZero ? 0 : zerosCount + numberOfDigits(text)));

        newText = addUnderscores(newText);
        decimal = Decimal.decimal(newText);
        d = Decimal.d(newText);
        assertThat(decimal.unscaledValue(), equalTo(expectedUnscaledValue));
        assertThat(decimal.scale(), equalTo(expectsZero ? 0 : zerosCount + numberOfDigits(text)));
        assertThat(d.unscaledValue(), equalTo(expectedUnscaledValue));
        assertThat(d.scale(), equalTo(expectsZero ? 0 : zerosCount + numberOfDigits(text)));

        // prepend with "." and n zeros before value
        newText = sign(text) + "." + nZeros(zerosCount) + valueAfterSign(text);
        decimal = Decimal.decimal(newText);
        d = Decimal.d(newText);
        assertThat(decimal.unscaledValue(), equalTo(expectedUnscaledValue));
        assertThat(decimal.scale(), equalTo(expectsZero ? 0 : zerosCount + numberOfDigits(text)));
        assertThat(d.unscaledValue(), equalTo(expectedUnscaledValue));
        assertThat(d.scale(), equalTo(expectsZero ? 0 : zerosCount + numberOfDigits(text)));

        newText = addUnderscores(newText);
        decimal = Decimal.decimal(newText);
        d = Decimal.d(newText);
        assertThat(decimal.unscaledValue(), equalTo(expectedUnscaledValue));
        assertThat(decimal.scale(), equalTo(expectsZero ? 0 : zerosCount + numberOfDigits(text)));
        assertThat(d.unscaledValue(), equalTo(expectedUnscaledValue));
        assertThat(d.scale(), equalTo(expectsZero ? 0 : zerosCount + numberOfDigits(text)));

        // insert decimal point inside of value
        if (numberOfDigits(text) > 1) {
            int scaleTo = randomInt(1, numberOfDigits(text));
            newText = text.substring(0, text.length() - scaleTo) + "." + text.substring(text.length() - scaleTo);
            decimal = Decimal.decimal(newText);
            d = Decimal.d(newText);
            assertThat(decimal.unscaledValue(), equalTo(expectedUnscaledValue));
            assertThat(decimal.scale(), equalTo(scaleTo));
            assertThat(d.unscaledValue(), equalTo(expectedUnscaledValue));
            assertThat(d.scale(), equalTo(scaleTo));

            newText = addUnderscores(newText);
            decimal = Decimal.decimal(newText);
            d = Decimal.d(newText);
            assertThat(decimal.unscaledValue(), equalTo(expectedUnscaledValue));
            assertThat(decimal.scale(), equalTo(scaleTo));
            assertThat(d.unscaledValue(), equalTo(expectedUnscaledValue));
            assertThat(d.scale(), equalTo(scaleTo));
        }
    }

    private void assertStringFailsParsing(String value) {
        try {
            Decimal.decimal(value);

            fail("expected NumberFormatException for String '" + value + "'");

        } catch (NumberFormatException e) {
            // expected
        }

        try {
            Decimal.d(value);

            fail("expected NumberFormatException for String '" + value + "'");

        } catch (NumberFormatException e) {
            // expected
        }
    }

    private void shouldFindUnscaledValueOnStringPermutations(String text, long expectedUnscaledValue) {
        shouldFindUnscaledValueOnStringPermutations(text, expectedUnscaledValue);
    }

    private void shouldFindUnscaledValueOnStringPermutations(String text, BigInteger expectedUnscaledValue) {
        shouldFindUnscaledValueOnStringPermutations(text, expectedUnscaledValue);
    }

    private String nZeros(int n) {
        return StringUtils.repeat("0", n);
    }

    private boolean hasSignCharacter(String text) {
        return text.charAt(0) == '-' || text.charAt(0) == '+';
    }

    private int numberOfDigits(String text) {
        return hasSignCharacter(text) ? text.length() - 1 : text.length();
    }

    private String sign(String text) {
        return hasSignCharacter(text) ? text.substring(0, 1) : "";
    }

    private String valueAfterSign(String text) {
        return hasSignCharacter(text) ? text.substring(1) : text;
    }

    private String addUnderscores(String text) {
        StringBuilder sb = new StringBuilder(text);
        int count = randomInt(1, 10);
        for (int i = 0; i < count; i++) {
            sb.insert(randomInt(0, sb.length()), "_");
        }
        return sb.toString();
    }
}
