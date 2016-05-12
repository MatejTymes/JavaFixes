package mtymes.javafixes.beta.decimal;

import org.junit.Test;

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

    private void assertStringFailsParsing(String value) {
        try {
            DecimalParser.parseString(value);

            fail("expected NumberFormatException for String '" + value + "'");

        } catch (NumberFormatException e) {
            // expected
        }
    }

}