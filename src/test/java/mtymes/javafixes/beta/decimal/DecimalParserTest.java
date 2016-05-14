package mtymes.javafixes.beta.decimal;

import mtymes.javafixes.beta.decimal.Decimal.LongDecimal;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import static mtymes.javafixes.test.Conditions.*;
import static mtymes.javafixes.test.Random.randomInt;
import static mtymes.javafixes.test.Random.randomLong;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class DecimalParserTest {

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
        DecimalParser.parseString("0");
        DecimalParser.parseString("+_1_000");
        DecimalParser.parseString("-_1_000.00_");
        DecimalParser.parseString("___+1_000");
        DecimalParser.parseString("___-1_000");
        DecimalParser.parseString("0.0");
        DecimalParser.parseString(".0");
        DecimalParser.parseString("0.");
    }

    @Test
    public void shouldParseDecimalStringIntoLong() {
        shouldFindUnscaledValueOnStringPermutations("1", 1L);
        shouldFindUnscaledValueOnStringPermutations("9223372036854775807", Long.MAX_VALUE);
        shouldFindUnscaledValueOnStringPermutations("-9223372036854775808", Long.MIN_VALUE);

        long value = randomLong(positive(), notDivisibleBy10());
        shouldFindUnscaledValueOnStringPermutations(Long.toString(value), value);

        value = randomLong(negative(), notDivisibleBy10());
        shouldFindUnscaledValueOnStringPermutations(Long.toString(value), value);

        shouldFindUnscaledValueOnStringPermutations("0", 0L);
    }

    private void shouldFindUnscaledValueOnStringPermutations(String text, long expectedUnscaledValue) {

        Decimal decimal = DecimalParser.parseString(text);
        assertThat(decimal, instanceOf(LongDecimal.class));
        assertThat(decimal.unscaledValue(), equalTo(expectedUnscaledValue));
        assertThat(decimal.scale(), equalTo(0));

        // todo: improve with random decimal point, zeros and underscores

        int zerosCount = randomInt(1, 5);

        decimal = DecimalParser.parseString(text + nZeros(zerosCount));
        assertThat(decimal, instanceOf(LongDecimal.class));
        assertThat(decimal.unscaledValue(), equalTo(expectedUnscaledValue));
        int expectedScale = expectedUnscaledValue == 0 ? 0 : -zerosCount;
        assertThat(decimal.scale(), equalTo(expectedScale));

        decimal = DecimalParser.parseString(text + "." + nZeros(zerosCount));
        assertThat(decimal, instanceOf(LongDecimal.class));
        assertThat(decimal.unscaledValue(), equalTo(expectedUnscaledValue));
        assertThat(decimal.scale(), equalTo(0));

        decimal = DecimalParser.parseString(addAfterSign(text, "0." + nZeros(zerosCount)));
        assertThat(decimal, instanceOf(LongDecimal.class));
        assertThat(decimal.unscaledValue(), equalTo(expectedUnscaledValue));
        expectedScale = expectedUnscaledValue == 0 ? 0 : zerosCount + numberOfDigits(text);
        assertThat(decimal.scale(), equalTo(expectedScale));
    }

    private String addAfterSign(String text, String textToAdd) {
        return hasSignCharacter(text)
                ? text.substring(0, 1) + textToAdd + text.substring(1)
                : textToAdd + text;
    }

    private int numberOfDigits(String text) {
        return hasSignCharacter(text) ? text.length() - 1 : text.length();
    }

    private boolean hasSignCharacter(String text) {
        return text.charAt(0) == '-' || text.charAt(0) == '+';
    }

    private String nZeros(int n) {
        return StringUtils.repeat("0", n);
    }

    private void assertStringFailsParsing(String value) {
        try {
            DecimalParser.parseString(value);

            fail("expected NumberFormatException for String '" + value + "'");

        } catch (NumberFormatException e) {
            // expected
        }
    }

}