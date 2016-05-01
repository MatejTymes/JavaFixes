package co.uk.matejtymes.tdp;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static co.uk.matejtymes.tdp.Decimal.decimal;
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
    }

    @Test
    public void shouldConvertToFloat() {
        assertThat(decimal("16.456").floatValue(), equalTo(16.456f));
        assertThat(decimal("-150.0030500").floatValue(), equalTo(-150.00305f));
        assertThat(decimal("+73000").floatValue(), equalTo(73000f));
        assertThat(decimal("0.0035").floatValue(), equalTo(0.0035f));
        assertThat(decimal("13800000.00").floatValue(), equalTo(13800000f));
    }

    @Test
    public void shouldConvertToDouble() {
        assertThat(decimal("16.456").doubleValue(), equalTo(16.456d));
        assertThat(decimal("-150.0030500").doubleValue(), equalTo(-150.00305d));
        assertThat(decimal("+73000").doubleValue(), equalTo(73000d));
        assertThat(decimal("0.0035").doubleValue(), equalTo(0.0035d));
        assertThat(decimal("13800000.00").doubleValue(), equalTo(13800000d));
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
    }

    @Test
    public void shouldConvertToPlainStringUsingMinimalScale() {
        assertThat(decimal("0").toPlainString(4), equalTo("0.0000"));
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
    public void shouldCompareTwoValues() {
        assertThat(decimal("-4320.156").compareTo(decimal("-4320.156")), equalTo(0));
        assertThat(decimal("-4320.156").compareTo(decimal("-0004320.1560000000")), equalTo(0));

        assertThat(decimal("0").compareTo(decimal("0.156")), equalTo(-1));
        assertThat(decimal("123").compareTo(decimal("456")), equalTo(-1));

        assertThat(decimal("4.123").compareTo(decimal("4.122")), equalTo(1));
        assertThat(decimal("123").compareTo(decimal("-123")), equalTo(1));
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

        assertThat(decimal("-1.2").hashCode(), not(equalTo(decimal("1.2").hashCode())));
        assertThat(decimal("-1.2").hashCode(), not(equalTo(decimal("2.3").hashCode())));
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
                decimal("12.34").div(decimal("45.67")), equalTo(decimal("0.27019926")));
        assertThat(
                decimal("1000").div(decimal("-0.972")), equalTo(decimal("-1028.80658436")));
        assertThat(
                decimal("1").div(decimal("3")), equalTo(decimal("0.33333333")));
    }

}