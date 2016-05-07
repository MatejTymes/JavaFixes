package mtymes.javafixes.beta.decimal;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static mtymes.javafixes.beta.decimal.Decimal.decimal;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class DecimalTest {

    // todo: add negate test

    @Test
    public void shouldConvertToInt() {
        assertThat(decimal("16.456").intValue(), equalTo(16));
        assertThat(decimal("-150.0030500").intValue(), equalTo(-150));
        assertThat(decimal("+73000").intValue(), equalTo(73000));
        assertThat(decimal("0.0035").intValue(), equalTo(0));
        assertThat(decimal("13800000.00").intValue(), equalTo(13800000));
        // todo: should throw numer overflow exception
//        assertThat(decimal("123456789012345679012345678901234567890").intValue(), equalTo(1.2345679E38f));
    }

    @Test
    public void shouldConvertToLong() {
        assertThat(decimal("16.456").longValue(), equalTo(16L));
        assertThat(decimal("+73000").longValue(), equalTo(73000L));
        assertThat(decimal("-150.0030500").longValue(), equalTo(-150L));
        assertThat(decimal("0.0035").longValue(), equalTo(0L));
        assertThat(decimal("13800000.00").longValue(), equalTo(13800000L));
        assertThat(decimal(Long.MAX_VALUE).longValue(), equalTo(Long.MAX_VALUE));
        assertThat(decimal(Long.MIN_VALUE).longValue(), equalTo(Long.MIN_VALUE));
        // todo: should throw numer overflow exception
//        assertThat(decimal("123456789012345679012345678901234567890").longValue(), equalTo(1.2345679E38f));
    }

    @Test
    public void shouldConvertToFloat() {
        assertThat(decimal("16.456").floatValue(), equalTo(16.456f));
        assertThat(decimal("-150.0030500").floatValue(), equalTo(-150.00305f));
        assertThat(decimal("+73000").floatValue(), equalTo(73000f));
        assertThat(decimal("0.0035").floatValue(), equalTo(0.0035f));
        assertThat(decimal("13800000.00").floatValue(), equalTo(13800000f));
        assertThat(decimal("123456789012345679012345678901234567890").floatValue(), equalTo(1.2345679E38f));
    }

    @Test
    public void shouldConvertToDouble() {
        assertThat(decimal("16.456").doubleValue(), equalTo(16.456d));
        assertThat(decimal("-150.0030500").doubleValue(), equalTo(-150.00305d));
        assertThat(decimal("+73000").doubleValue(), equalTo(73000d));
        assertThat(decimal("0.0035").doubleValue(), equalTo(0.0035d));
        assertThat(decimal("13800000.00").doubleValue(), equalTo(13800000d));
        assertThat(decimal("123456789012345679012345678901234567890").doubleValue(), equalTo(1.2345678901234568E38d));
    }

    @Test
    public void shouldConvertToBigDecimal() {
        assertThat(decimal("16.456").bigDecimalValue(), equalTo(new BigDecimal("16.456")));
        assertThat(decimal("-150.0030500").bigDecimalValue(), equalTo(new BigDecimal("-150.00305")));
        assertThat(decimal("+73000").bigDecimalValue(), equalTo(new BigDecimal("73000")));
        assertThat(decimal(Long.MAX_VALUE).bigDecimalValue(), equalTo(new BigDecimal(Long.MAX_VALUE)));
        assertThat(decimal(Long.MIN_VALUE).bigDecimalValue(), equalTo(new BigDecimal(Long.MIN_VALUE)));
        assertThat(decimal("0.0035").bigDecimalValue(), equalTo(new BigDecimal("0.0035")));
        assertThat(decimal("13800000.00").bigDecimalValue(), equalTo(new BigDecimal("13800000")));
    }

    @Test
    public void shouldConvertToPlainString() {
        assertThat(decimal("0").toPlainString(), equalTo("0"));
        assertThat(decimal("1").toPlainString(), equalTo("1"));
        assertThat(decimal("-5").toPlainString(), equalTo("-5"));
        assertThat(decimal("16.456").toPlainString(), equalTo("16.456"));
        assertThat(decimal("-150.0030500").toPlainString(), equalTo("-150.00305"));
        assertThat(decimal("+73000").toPlainString(), equalTo("73000"));
        assertThat(decimal(Long.MAX_VALUE, 8).toPlainString(), equalTo("92233720368.54775807"));
        assertThat(decimal(Long.MIN_VALUE, 8).toPlainString(), equalTo("-92233720368.54775808"));
        assertThat(decimal("0.0035").toPlainString(), equalTo("0.0035"));
        assertThat(decimal("0.120").toPlainString(), equalTo("0.12"));
        assertThat(decimal("-0.120").toPlainString(), equalTo("-0.12"));
        assertThat(decimal("-0.00005903200").toPlainString(), equalTo("-0.000059032"));
        assertThat(decimal("13800000.00").toPlainString(), equalTo("13800000"));
        assertThat(decimal("12783418431290438190831940123400.00").toPlainString(), equalTo("12783418431290438190831940123400"));
        assertThat(decimal("-0.00210327400284000920491734320939000").toPlainString(), equalTo("-0.00210327400284000920491734320939"));
    }

    @Test
    public void shouldConvertToPlainStringUsingMinimalScale() {
        assertThat(decimal("0").toPlainString(4), equalTo("0.0000"));
        assertThat(decimal("1").toPlainString(4), equalTo("1.0000"));
        assertThat(decimal("-5").toPlainString(4), equalTo("-5.0000"));
        assertThat(decimal("16.456").toPlainString(4), equalTo("16.4560"));
        assertThat(decimal("-150.0030500").toPlainString(4), equalTo("-150.00305"));
        assertThat(decimal("+73000").toPlainString(4), equalTo("73000.0000"));
        assertThat(decimal(Long.MAX_VALUE, 8).toPlainString(4), equalTo("92233720368.54775807"));
        assertThat(decimal(Long.MIN_VALUE, 8).toPlainString(4), equalTo("-92233720368.54775808"));
        assertThat(decimal("0.0035").toPlainString(4), equalTo("0.0035"));
        assertThat(decimal("0.120").toPlainString(4), equalTo("0.1200"));
        assertThat(decimal("-0.120").toPlainString(4), equalTo("-0.1200"));
        assertThat(decimal("-0.00005903200").toPlainString(), equalTo("-0.000059032"));
        assertThat(decimal("13800000.00").toPlainString(4), equalTo("13800000.0000"));
    }

    @Test
    public void shouldConvertToScientificNotation() {
        assertThat(decimal("0").toScientificNotation(), equalTo("0e0"));
        assertThat(decimal("1").toScientificNotation(), equalTo("1e0"));
        assertThat(decimal("-5").toScientificNotation(), equalTo("-5e0"));
        assertThat(decimal("0.1").toScientificNotation(), equalTo("1e-1"));
        assertThat(decimal("-0.05").toScientificNotation(), equalTo("-5e-2"));
        assertThat(decimal("16.456").toScientificNotation(), equalTo("1.6456e1"));
        assertThat(decimal("-150.0030500").toScientificNotation(), equalTo("-1.5000305e2"));
        assertThat(decimal("+73000").toScientificNotation(), equalTo("7.3e4"));
        assertThat(decimal(Long.MAX_VALUE, 8).toScientificNotation(), equalTo("9.223372036854775807e10"));
        assertThat(decimal(Long.MIN_VALUE, 8).toScientificNotation(), equalTo("-9.223372036854775808e10"));
        assertThat(decimal("0.0035").toScientificNotation(), equalTo("3.5e-3"));
        assertThat(decimal("0.120").toScientificNotation(), equalTo("1.2e-1"));
        assertThat(decimal("-0.120").toScientificNotation(), equalTo("-1.2e-1"));
        assertThat(decimal("-0.00005903200").toScientificNotation(), equalTo("-5.9032e-5"));
        assertThat(decimal("13800000.00").toScientificNotation(), equalTo("1.38e7"));
        assertThat(decimal("12783418431290438190831940123400.00").toScientificNotation(), equalTo("1.27834184312904381908319401234e31"));
        assertThat(decimal("-0.00210327400284000920491734320939000").toScientificNotation(), equalTo("-2.10327400284000920491734320939e-3"));
    }

    @Test
    public void shouldCompareTwoValues() {
        assertThat(decimal("-4320.156").compareTo(decimal("-4320.156")), equalTo(0));
        assertThat(decimal("-4320.156").compareTo(decimal("-0004320.1560000000")), equalTo(0));

        assertThat(decimal("0").compareTo(decimal("0.156")), equalTo(-1));
        assertThat(decimal("123").compareTo(decimal("456")), equalTo(-1));

        assertThat(decimal("4.123").compareTo(decimal("4.122")), equalTo(1));
        assertThat(decimal("123").compareTo(decimal("-123")), equalTo(1));

        assertThat(decimal("123450").compareTo(decimal("123459")), equalTo(-1));
        assertThat(decimal("123459").compareTo(decimal("123450")), equalTo(1));

        assertThat(decimal("12300000000000000000").compareTo(decimal("0.999999999")), equalTo(1));
        assertThat(decimal("0.999999999").compareTo(decimal("12300000000000000000")), equalTo(-1));

        assertThat(decimal(Long.MAX_VALUE, 100).compareTo(decimal(9999L, -100)), equalTo(-1));
        assertThat(decimal(9999L, -100).compareTo(decimal(Long.MAX_VALUE, 100)), equalTo(1));
        assertThat(decimal(Long.MAX_VALUE, -100).compareTo(decimal(9999L, 100)), equalTo(1));
        assertThat(decimal(9999L, 100).compareTo(decimal(Long.MAX_VALUE, -100)), equalTo(-1));

        assertThat(decimal(Long.MIN_VALUE, 500).compareTo(decimal(Long.MIN_VALUE, -200)), equalTo(1));
        assertThat(decimal(Long.MIN_VALUE, -200).compareTo(decimal(Long.MIN_VALUE, 500)), equalTo(-1));
    }

    @Test
    public void shouldFindIfValuesAreEqual() {
        assertThat(decimal("-1.2").equals(decimal("-1.2")), is(true));
        assertThat(decimal("-1.2").equals(decimal("-001.2")), is(true));

        assertThat(decimal("-1.2").equals(decimal("-1.200")), is(true));

        assertThat(decimal("-1.2").equals(decimal("-1.23")), is(false));
        assertThat(decimal("-1.2").equals(decimal("1.2")), is(false));
        assertThat(decimal("-1.2").equals(decimal("2.3")), is(false));
    }

    @Test
    public void shouldProvideTheSameHashCodeForEqualValues() {
        assertThat(decimal("-1.2").hashCode(), equalTo(decimal("-1.2").hashCode()));
        assertThat(decimal("-1.2").hashCode(), equalTo(decimal("-001.2").hashCode()));
        assertThat(decimal("-1.2").hashCode(), equalTo(decimal("-1.200").hashCode()));
        assertThat(decimal("1234567890123456.7890123456789012345678900000000").hashCode(), equalTo(decimal("1234567890123456.78901234567890123456789000").hashCode()));
        assertThat(decimal("-1.23456789012345678901234567890123456789").hashCode(), equalTo(decimal("-1.23456789012345678901234567890123456789000000").hashCode()));

        assertThat(decimal("-1.2").hashCode(), not(equalTo(decimal("1.2").hashCode())));
        assertThat(decimal("-1.2").hashCode(), not(equalTo(decimal("2.3").hashCode())));
        assertThat(decimal("1234567890123456.7890123456789012345678900000001").hashCode(), not(equalTo(decimal("1234567890123456.78901234567890123456789000").hashCode())));
        assertThat(decimal("-1.23456789012345678901234567890123456789").hashCode(), not(equalTo(decimal("-1.23456789012345678901234567890123456789001000").hashCode())));
    }

    @Test
    public void shouldBeAbleToGenerateHashCodeForLimitValues() {
        decimal(Long.MAX_VALUE).hashCode();
        decimal(Long.MIN_VALUE).hashCode();
    }

    @Test
    public void shouldDescaleAndRoundValueProperly() {

        assertThat(decimal("5.5").descaleTo(0, RoundingMode.UP), equalTo(decimal("6")));
        assertThat(decimal("2.5").descaleTo(0, RoundingMode.UP), equalTo(decimal("3")));
        assertThat(decimal("1.6").descaleTo(0, RoundingMode.UP), equalTo(decimal("2")));
        assertThat(decimal("1.1").descaleTo(0, RoundingMode.UP), equalTo(decimal("2")));
        assertThat(decimal("1.0").descaleTo(0, RoundingMode.UP), equalTo(decimal("1")));
        assertThat(decimal("-1.0").descaleTo(0, RoundingMode.UP), equalTo(decimal("-1")));
        assertThat(decimal("-1.1").descaleTo(0, RoundingMode.UP), equalTo(decimal("-2")));
        assertThat(decimal("-1.6").descaleTo(0, RoundingMode.UP), equalTo(decimal("-2")));
        assertThat(decimal("-2.5").descaleTo(0, RoundingMode.UP), equalTo(decimal("-3")));
        assertThat(decimal("-5.5").descaleTo(0, RoundingMode.UP), equalTo(decimal("-6")));

        assertThat(decimal("5.5").descaleTo(0, RoundingMode.DOWN), equalTo(decimal("5")));
        assertThat(decimal("2.5").descaleTo(0, RoundingMode.DOWN), equalTo(decimal("2")));
        assertThat(decimal("1.6").descaleTo(0, RoundingMode.DOWN), equalTo(decimal("1")));
        assertThat(decimal("1.1").descaleTo(0, RoundingMode.DOWN), equalTo(decimal("1")));
        assertThat(decimal("1.0").descaleTo(0, RoundingMode.DOWN), equalTo(decimal("1")));
        assertThat(decimal("-1.0").descaleTo(0, RoundingMode.DOWN), equalTo(decimal("-1")));
        assertThat(decimal("-1.1").descaleTo(0, RoundingMode.DOWN), equalTo(decimal("-1")));
        assertThat(decimal("-1.6").descaleTo(0, RoundingMode.DOWN), equalTo(decimal("-1")));
        assertThat(decimal("-2.5").descaleTo(0, RoundingMode.DOWN), equalTo(decimal("-2")));
        assertThat(decimal("-5.5").descaleTo(0, RoundingMode.DOWN), equalTo(decimal("-5")));

        assertThat(decimal("5.5").descaleTo(0, RoundingMode.CEILING), equalTo(decimal("6")));
        assertThat(decimal("2.5").descaleTo(0, RoundingMode.CEILING), equalTo(decimal("3")));
        assertThat(decimal("1.6").descaleTo(0, RoundingMode.CEILING), equalTo(decimal("2")));
        assertThat(decimal("1.1").descaleTo(0, RoundingMode.CEILING), equalTo(decimal("2")));
        assertThat(decimal("1.0").descaleTo(0, RoundingMode.CEILING), equalTo(decimal("1")));
        assertThat(decimal("-1.0").descaleTo(0, RoundingMode.CEILING), equalTo(decimal("-1")));
        assertThat(decimal("-1.1").descaleTo(0, RoundingMode.CEILING), equalTo(decimal("-1")));
        assertThat(decimal("-1.6").descaleTo(0, RoundingMode.CEILING), equalTo(decimal("-1")));
        assertThat(decimal("-2.5").descaleTo(0, RoundingMode.CEILING), equalTo(decimal("-2")));
        assertThat(decimal("-5.5").descaleTo(0, RoundingMode.CEILING), equalTo(decimal("-5")));

        assertThat(decimal("5.5").descaleTo(0, RoundingMode.FLOOR), equalTo(decimal("5")));
        assertThat(decimal("2.5").descaleTo(0, RoundingMode.FLOOR), equalTo(decimal("2")));
        assertThat(decimal("1.6").descaleTo(0, RoundingMode.FLOOR), equalTo(decimal("1")));
        assertThat(decimal("1.1").descaleTo(0, RoundingMode.FLOOR), equalTo(decimal("1")));
        assertThat(decimal("1.0").descaleTo(0, RoundingMode.FLOOR), equalTo(decimal("1")));
        assertThat(decimal("-1.0").descaleTo(0, RoundingMode.FLOOR), equalTo(decimal("-1")));
        assertThat(decimal("-1.1").descaleTo(0, RoundingMode.FLOOR), equalTo(decimal("-2")));
        assertThat(decimal("-1.6").descaleTo(0, RoundingMode.FLOOR), equalTo(decimal("-2")));
        assertThat(decimal("-2.5").descaleTo(0, RoundingMode.FLOOR), equalTo(decimal("-3")));
        assertThat(decimal("-5.5").descaleTo(0, RoundingMode.FLOOR), equalTo(decimal("-6")));

        assertThat(decimal("5.5").descaleTo(0, RoundingMode.HALF_UP), equalTo(decimal("6")));
        assertThat(decimal("2.5").descaleTo(0, RoundingMode.HALF_UP), equalTo(decimal("3")));
        assertThat(decimal("1.6").descaleTo(0, RoundingMode.HALF_UP), equalTo(decimal("2")));
        assertThat(decimal("1.1").descaleTo(0, RoundingMode.HALF_UP), equalTo(decimal("1")));
        assertThat(decimal("1.0").descaleTo(0, RoundingMode.HALF_UP), equalTo(decimal("1")));
        assertThat(decimal("-1.0").descaleTo(0, RoundingMode.HALF_UP), equalTo(decimal("-1")));
        assertThat(decimal("-1.1").descaleTo(0, RoundingMode.HALF_UP), equalTo(decimal("-1")));
        assertThat(decimal("-1.6").descaleTo(0, RoundingMode.HALF_UP), equalTo(decimal("-2")));
        assertThat(decimal("-2.5").descaleTo(0, RoundingMode.HALF_UP), equalTo(decimal("-3")));
        assertThat(decimal("-5.5").descaleTo(0, RoundingMode.HALF_UP), equalTo(decimal("-6")));

        assertThat(decimal("5.5").descaleTo(0, RoundingMode.HALF_DOWN), equalTo(decimal("5")));
        assertThat(decimal("2.5").descaleTo(0, RoundingMode.HALF_DOWN), equalTo(decimal("2")));
        assertThat(decimal("1.6").descaleTo(0, RoundingMode.HALF_DOWN), equalTo(decimal("2")));
        assertThat(decimal("1.1").descaleTo(0, RoundingMode.HALF_DOWN), equalTo(decimal("1")));
        assertThat(decimal("1.0").descaleTo(0, RoundingMode.HALF_DOWN), equalTo(decimal("1")));
        assertThat(decimal("-1.0").descaleTo(0, RoundingMode.HALF_DOWN), equalTo(decimal("-1")));
        assertThat(decimal("-1.1").descaleTo(0, RoundingMode.HALF_DOWN), equalTo(decimal("-1")));
        assertThat(decimal("-1.6").descaleTo(0, RoundingMode.HALF_DOWN), equalTo(decimal("-2")));
        assertThat(decimal("-2.5").descaleTo(0, RoundingMode.HALF_DOWN), equalTo(decimal("-2")));
        assertThat(decimal("-5.5").descaleTo(0, RoundingMode.HALF_DOWN), equalTo(decimal("-5")));

        assertThat(decimal("5.5").descaleTo(0, RoundingMode.HALF_EVEN), equalTo(decimal("6")));
        assertThat(decimal("2.5").descaleTo(0, RoundingMode.HALF_EVEN), equalTo(decimal("2")));
        assertThat(decimal("1.6").descaleTo(0, RoundingMode.HALF_EVEN), equalTo(decimal("2")));
        assertThat(decimal("1.1").descaleTo(0, RoundingMode.HALF_EVEN), equalTo(decimal("1")));
        assertThat(decimal("1.0").descaleTo(0, RoundingMode.HALF_EVEN), equalTo(decimal("1")));
        assertThat(decimal("-1.0").descaleTo(0, RoundingMode.HALF_EVEN), equalTo(decimal("-1")));
        assertThat(decimal("-1.1").descaleTo(0, RoundingMode.HALF_EVEN), equalTo(decimal("-1")));
        assertThat(decimal("-1.6").descaleTo(0, RoundingMode.HALF_EVEN), equalTo(decimal("-2")));
        assertThat(decimal("-2.5").descaleTo(0, RoundingMode.HALF_EVEN), equalTo(decimal("-2")));
        assertThat(decimal("-5.5").descaleTo(0, RoundingMode.HALF_EVEN), equalTo(decimal("-6")));

        try { decimal("5.5").descaleTo(0, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("2.5").descaleTo(0, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("1.6").descaleTo(0, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("1.1").descaleTo(0, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        assertThat(decimal("1.0").descaleTo(0, RoundingMode.UNNECESSARY), equalTo(decimal("1")));
        assertThat(decimal("-1.0").descaleTo(0, RoundingMode.UNNECESSARY), equalTo(decimal("-1")));
        try { decimal("-1.1").descaleTo(0, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("-1.6").descaleTo(0, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("-2.5").descaleTo(0, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("-5.5").descaleTo(0, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
    }

    @Test
    public void shouldDescaleAndRoundHugeValueProperly() {

        assertThat(decimal("100000000000000000000000005.5").descaleTo(0, RoundingMode.UP), equalTo(decimal("100000000000000000000000006")));
        assertThat(decimal("100000000000000000000000002.5").descaleTo(0, RoundingMode.UP), equalTo(decimal("100000000000000000000000003")));
        assertThat(decimal("100000000000000000000000001.6").descaleTo(0, RoundingMode.UP), equalTo(decimal("100000000000000000000000002")));
        assertThat(decimal("100000000000000000000000001.1").descaleTo(0, RoundingMode.UP), equalTo(decimal("100000000000000000000000002")));
        assertThat(decimal("100000000000000000000000001.0").descaleTo(0, RoundingMode.UP), equalTo(decimal("100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000001.0").descaleTo(0, RoundingMode.UP), equalTo(decimal("-100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000001.1").descaleTo(0, RoundingMode.UP), equalTo(decimal("-100000000000000000000000002")));
        assertThat(decimal("-100000000000000000000000001.6").descaleTo(0, RoundingMode.UP), equalTo(decimal("-100000000000000000000000002")));
        assertThat(decimal("-100000000000000000000000002.5").descaleTo(0, RoundingMode.UP), equalTo(decimal("-100000000000000000000000003")));
        assertThat(decimal("-100000000000000000000000005.5").descaleTo(0, RoundingMode.UP), equalTo(decimal("-100000000000000000000000006")));

        assertThat(decimal("100000000000000000000000005.5").descaleTo(0, RoundingMode.DOWN), equalTo(decimal("100000000000000000000000005")));
        assertThat(decimal("100000000000000000000000002.5").descaleTo(0, RoundingMode.DOWN), equalTo(decimal("100000000000000000000000002")));
        assertThat(decimal("100000000000000000000000001.6").descaleTo(0, RoundingMode.DOWN), equalTo(decimal("100000000000000000000000001")));
        assertThat(decimal("100000000000000000000000001.1").descaleTo(0, RoundingMode.DOWN), equalTo(decimal("100000000000000000000000001")));
        assertThat(decimal("100000000000000000000000001.0").descaleTo(0, RoundingMode.DOWN), equalTo(decimal("100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000001.0").descaleTo(0, RoundingMode.DOWN), equalTo(decimal("-100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000001.1").descaleTo(0, RoundingMode.DOWN), equalTo(decimal("-100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000001.6").descaleTo(0, RoundingMode.DOWN), equalTo(decimal("-100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000002.5").descaleTo(0, RoundingMode.DOWN), equalTo(decimal("-100000000000000000000000002")));
        assertThat(decimal("-100000000000000000000000005.5").descaleTo(0, RoundingMode.DOWN), equalTo(decimal("-100000000000000000000000005")));

        assertThat(decimal("100000000000000000000000005.5").descaleTo(0, RoundingMode.CEILING), equalTo(decimal("100000000000000000000000006")));
        assertThat(decimal("100000000000000000000000002.5").descaleTo(0, RoundingMode.CEILING), equalTo(decimal("100000000000000000000000003")));
        assertThat(decimal("100000000000000000000000001.6").descaleTo(0, RoundingMode.CEILING), equalTo(decimal("100000000000000000000000002")));
        assertThat(decimal("100000000000000000000000001.1").descaleTo(0, RoundingMode.CEILING), equalTo(decimal("100000000000000000000000002")));
        assertThat(decimal("100000000000000000000000001.0").descaleTo(0, RoundingMode.CEILING), equalTo(decimal("100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000001.0").descaleTo(0, RoundingMode.CEILING), equalTo(decimal("-100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000001.1").descaleTo(0, RoundingMode.CEILING), equalTo(decimal("-100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000001.6").descaleTo(0, RoundingMode.CEILING), equalTo(decimal("-100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000002.5").descaleTo(0, RoundingMode.CEILING), equalTo(decimal("-100000000000000000000000002")));
        assertThat(decimal("-100000000000000000000000005.5").descaleTo(0, RoundingMode.CEILING), equalTo(decimal("-100000000000000000000000005")));

        assertThat(decimal("100000000000000000000000005.5").descaleTo(0, RoundingMode.FLOOR), equalTo(decimal("100000000000000000000000005")));
        assertThat(decimal("100000000000000000000000002.5").descaleTo(0, RoundingMode.FLOOR), equalTo(decimal("100000000000000000000000002")));
        assertThat(decimal("100000000000000000000000001.6").descaleTo(0, RoundingMode.FLOOR), equalTo(decimal("100000000000000000000000001")));
        assertThat(decimal("100000000000000000000000001.1").descaleTo(0, RoundingMode.FLOOR), equalTo(decimal("100000000000000000000000001")));
        assertThat(decimal("100000000000000000000000001.0").descaleTo(0, RoundingMode.FLOOR), equalTo(decimal("100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000001.0").descaleTo(0, RoundingMode.FLOOR), equalTo(decimal("-100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000001.1").descaleTo(0, RoundingMode.FLOOR), equalTo(decimal("-100000000000000000000000002")));
        assertThat(decimal("-100000000000000000000000001.6").descaleTo(0, RoundingMode.FLOOR), equalTo(decimal("-100000000000000000000000002")));
        assertThat(decimal("-100000000000000000000000002.5").descaleTo(0, RoundingMode.FLOOR), equalTo(decimal("-100000000000000000000000003")));
        assertThat(decimal("-100000000000000000000000005.5").descaleTo(0, RoundingMode.FLOOR), equalTo(decimal("-100000000000000000000000006")));

        assertThat(decimal("100000000000000000000000005.5").descaleTo(0, RoundingMode.HALF_UP), equalTo(decimal("100000000000000000000000006")));
        assertThat(decimal("100000000000000000000000002.5").descaleTo(0, RoundingMode.HALF_UP), equalTo(decimal("100000000000000000000000003")));
        assertThat(decimal("100000000000000000000000001.6").descaleTo(0, RoundingMode.HALF_UP), equalTo(decimal("100000000000000000000000002")));
        assertThat(decimal("100000000000000000000000001.1").descaleTo(0, RoundingMode.HALF_UP), equalTo(decimal("100000000000000000000000001")));
        assertThat(decimal("100000000000000000000000001.0").descaleTo(0, RoundingMode.HALF_UP), equalTo(decimal("100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000001.0").descaleTo(0, RoundingMode.HALF_UP), equalTo(decimal("-100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000001.1").descaleTo(0, RoundingMode.HALF_UP), equalTo(decimal("-100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000001.6").descaleTo(0, RoundingMode.HALF_UP), equalTo(decimal("-100000000000000000000000002")));
        assertThat(decimal("-100000000000000000000000002.5").descaleTo(0, RoundingMode.HALF_UP), equalTo(decimal("-100000000000000000000000003")));
        assertThat(decimal("-100000000000000000000000005.5").descaleTo(0, RoundingMode.HALF_UP), equalTo(decimal("-100000000000000000000000006")));

        assertThat(decimal("100000000000000000000000005.5").descaleTo(0, RoundingMode.HALF_DOWN), equalTo(decimal("100000000000000000000000005")));
        assertThat(decimal("100000000000000000000000002.5").descaleTo(0, RoundingMode.HALF_DOWN), equalTo(decimal("100000000000000000000000002")));
        assertThat(decimal("100000000000000000000000001.6").descaleTo(0, RoundingMode.HALF_DOWN), equalTo(decimal("100000000000000000000000002")));
        assertThat(decimal("100000000000000000000000001.1").descaleTo(0, RoundingMode.HALF_DOWN), equalTo(decimal("100000000000000000000000001")));
        assertThat(decimal("100000000000000000000000001.0").descaleTo(0, RoundingMode.HALF_DOWN), equalTo(decimal("100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000001.0").descaleTo(0, RoundingMode.HALF_DOWN), equalTo(decimal("-100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000001.1").descaleTo(0, RoundingMode.HALF_DOWN), equalTo(decimal("-100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000001.6").descaleTo(0, RoundingMode.HALF_DOWN), equalTo(decimal("-100000000000000000000000002")));
        assertThat(decimal("-100000000000000000000000002.5").descaleTo(0, RoundingMode.HALF_DOWN), equalTo(decimal("-100000000000000000000000002")));
        assertThat(decimal("-100000000000000000000000005.5").descaleTo(0, RoundingMode.HALF_DOWN), equalTo(decimal("-100000000000000000000000005")));

        assertThat(decimal("100000000000000000000000005.5").descaleTo(0, RoundingMode.HALF_EVEN), equalTo(decimal("100000000000000000000000006")));
        assertThat(decimal("100000000000000000000000002.5").descaleTo(0, RoundingMode.HALF_EVEN), equalTo(decimal("100000000000000000000000002")));
        assertThat(decimal("100000000000000000000000001.6").descaleTo(0, RoundingMode.HALF_EVEN), equalTo(decimal("100000000000000000000000002")));
        assertThat(decimal("100000000000000000000000001.1").descaleTo(0, RoundingMode.HALF_EVEN), equalTo(decimal("100000000000000000000000001")));
        assertThat(decimal("100000000000000000000000001.0").descaleTo(0, RoundingMode.HALF_EVEN), equalTo(decimal("100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000001.0").descaleTo(0, RoundingMode.HALF_EVEN), equalTo(decimal("-100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000001.1").descaleTo(0, RoundingMode.HALF_EVEN), equalTo(decimal("-100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000001.6").descaleTo(0, RoundingMode.HALF_EVEN), equalTo(decimal("-100000000000000000000000002")));
        assertThat(decimal("-100000000000000000000000002.5").descaleTo(0, RoundingMode.HALF_EVEN), equalTo(decimal("-100000000000000000000000002")));
        assertThat(decimal("-100000000000000000000000005.5").descaleTo(0, RoundingMode.HALF_EVEN), equalTo(decimal("-100000000000000000000000006")));

        try { decimal("100000000000000000000000005.5").descaleTo(0, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("100000000000000000000000002.5").descaleTo(0, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("100000000000000000000000001.6").descaleTo(0, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("100000000000000000000000001.1").descaleTo(0, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        assertThat(decimal("100000000000000000000000001.0").descaleTo(0, RoundingMode.UNNECESSARY), equalTo(decimal("100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000001.0").descaleTo(0, RoundingMode.UNNECESSARY), equalTo(decimal("-100000000000000000000000001")));
        try { decimal("-100000000000000000000000001.1").descaleTo(0, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("-100000000000000000000000001.6").descaleTo(0, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("-100000000000000000000000002.5").descaleTo(0, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("-100000000000000000000000005.5").descaleTo(0, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
    }

    // todo: properly test arithmetic operations

    @Test
    public void shouldCalculateValues() {
        assertThat(
                decimal("12.34").plus(decimal("45.67")), equalTo(decimal("58.01")));
        assertThat(
                decimal("1000").plus(decimal("-0.972")), equalTo(decimal("999.028")));

        assertThat(
                decimal("12.34").minus(decimal("45.67")), equalTo(decimal("-33.33")));
        assertThat(
                decimal("1000").minus(decimal("-0.972")), equalTo(decimal("1000.972")));

        assertThat(
                decimal("12.34").times(decimal("45.67")), equalTo(decimal("563.5678")));
        assertThat(
                decimal("1000").times(decimal("-0.972")), equalTo(decimal("-972")));

        assertThat(
                decimal("12.34").div(decimal("45.67")), equalTo(decimal("0.2701992555287935187212612218")));
        assertThat(
                decimal("1000").div(decimal("-0.972")), equalTo(decimal("-1028.8065843621399176954732510288")));
        assertThat(
                decimal("1").div(decimal("3")), equalTo(decimal("0.3333333333333333333333333333")));
    }

}