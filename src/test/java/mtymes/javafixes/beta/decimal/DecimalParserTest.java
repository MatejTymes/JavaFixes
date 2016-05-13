package mtymes.javafixes.beta.decimal;

import mtymes.javafixes.beta.decimal.Decimal.LongDecimal;
import org.junit.Test;

import static mtymes.javafixes.test.Conditions.*;
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
        DecimalParser.parseString("0.0");
        DecimalParser.parseString(".0");
        DecimalParser.parseString("0.");
    }

    @Test
    public void shouldParseDecimalStringIntoLong() {
        shouldFindUnscaledValue("9223372036854775807", Long.MAX_VALUE);
        shouldFindUnscaledValue("-9223372036854775808", Long.MIN_VALUE);

        long value = 0;
        shouldFindUnscaledValue(Long.toString(value), value);

        value = randomLong(positive(), notDivisibleBy10());
        shouldFindUnscaledValue(Long.toString(value), value);

        value = randomLong(negative(), notDivisibleBy10());
        shouldFindUnscaledValue(Long.toString(value), value);
    }

    private void shouldFindUnscaledValue(String stringValue, long expectedUnscaledValue) {
        Decimal decimal = DecimalParser.parseString(stringValue);

        assertThat(decimal, instanceOf(LongDecimal.class));
        assertThat(decimal.unscaledValue(), equalTo(expectedUnscaledValue));
        assertThat(decimal.scale(), equalTo(0));

        // todo: improve with random decimal point, zeros and underscores
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