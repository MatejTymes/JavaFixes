package javafixes.math;

import org.junit.Test;

import java.math.RoundingMode;

import static javafixes.common.CollectionUtil.newList;
import static javafixes.math.Decimal.decimal;
import static javafixes.test.Random.randomInt;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class DecimalDescaleTest {

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

        assertThat(decimal("5.501").descaleTo(0, RoundingMode.UP), equalTo(decimal("6")));
        assertThat(decimal("2.501").descaleTo(0, RoundingMode.UP), equalTo(decimal("3")));
        assertThat(decimal("1.001").descaleTo(0, RoundingMode.UP), equalTo(decimal("2")));
        assertThat(decimal("0.001").descaleTo(0, RoundingMode.UP), equalTo(decimal("1")));
        assertThat(decimal("-0.001").descaleTo(0, RoundingMode.UP), equalTo(decimal("-1")));
        assertThat(decimal("-1.001").descaleTo(0, RoundingMode.UP), equalTo(decimal("-2")));
        assertThat(decimal("-2.501").descaleTo(0, RoundingMode.UP), equalTo(decimal("-3")));
        assertThat(decimal("-5.501").descaleTo(0, RoundingMode.UP), equalTo(decimal("-6")));

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

        assertThat(decimal("5.501").descaleTo(0, RoundingMode.DOWN), equalTo(decimal("5")));
        assertThat(decimal("2.501").descaleTo(0, RoundingMode.DOWN), equalTo(decimal("2")));
        assertThat(decimal("1.001").descaleTo(0, RoundingMode.DOWN), equalTo(decimal("1")));
        assertThat(decimal("0.001").descaleTo(0, RoundingMode.DOWN), equalTo(decimal("0")));
        assertThat(decimal("-0.001").descaleTo(0, RoundingMode.DOWN), equalTo(decimal("0")));
        assertThat(decimal("-1.001").descaleTo(0, RoundingMode.DOWN), equalTo(decimal("-1")));
        assertThat(decimal("-2.501").descaleTo(0, RoundingMode.DOWN), equalTo(decimal("-2")));
        assertThat(decimal("-5.501").descaleTo(0, RoundingMode.DOWN), equalTo(decimal("-5")));

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

        assertThat(decimal("5.501").descaleTo(0, RoundingMode.CEILING), equalTo(decimal("6")));
        assertThat(decimal("2.501").descaleTo(0, RoundingMode.CEILING), equalTo(decimal("3")));
        assertThat(decimal("1.001").descaleTo(0, RoundingMode.CEILING), equalTo(decimal("2")));
        assertThat(decimal("0.001").descaleTo(0, RoundingMode.CEILING), equalTo(decimal("1")));
        assertThat(decimal("-0.001").descaleTo(0, RoundingMode.CEILING), equalTo(decimal("0")));
        assertThat(decimal("-1.001").descaleTo(0, RoundingMode.CEILING), equalTo(decimal("-1")));
        assertThat(decimal("-2.501").descaleTo(0, RoundingMode.CEILING), equalTo(decimal("-2")));
        assertThat(decimal("-5.501").descaleTo(0, RoundingMode.CEILING), equalTo(decimal("-5")));

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

        assertThat(decimal("5.501").descaleTo(0, RoundingMode.FLOOR), equalTo(decimal("5")));
        assertThat(decimal("2.501").descaleTo(0, RoundingMode.FLOOR), equalTo(decimal("2")));
        assertThat(decimal("1.001").descaleTo(0, RoundingMode.FLOOR), equalTo(decimal("1")));
        assertThat(decimal("0.001").descaleTo(0, RoundingMode.FLOOR), equalTo(decimal("0")));
        assertThat(decimal("-0.001").descaleTo(0, RoundingMode.FLOOR), equalTo(decimal("-1")));
        assertThat(decimal("-1.001").descaleTo(0, RoundingMode.FLOOR), equalTo(decimal("-2")));
        assertThat(decimal("-2.501").descaleTo(0, RoundingMode.FLOOR), equalTo(decimal("-3")));
        assertThat(decimal("-5.501").descaleTo(0, RoundingMode.FLOOR), equalTo(decimal("-6")));

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

        assertThat(decimal("5.501").descaleTo(0, RoundingMode.HALF_UP), equalTo(decimal("6")));
        assertThat(decimal("2.501").descaleTo(0, RoundingMode.HALF_UP), equalTo(decimal("3")));
        assertThat(decimal("1.001").descaleTo(0, RoundingMode.HALF_UP), equalTo(decimal("1")));
        assertThat(decimal("0.001").descaleTo(0, RoundingMode.HALF_UP), equalTo(decimal("0")));
        assertThat(decimal("-0.001").descaleTo(0, RoundingMode.HALF_UP), equalTo(decimal("-0")));
        assertThat(decimal("-1.001").descaleTo(0, RoundingMode.HALF_UP), equalTo(decimal("-1")));
        assertThat(decimal("-2.501").descaleTo(0, RoundingMode.HALF_UP), equalTo(decimal("-3")));
        assertThat(decimal("-5.501").descaleTo(0, RoundingMode.HALF_UP), equalTo(decimal("-6")));

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

        assertThat(decimal("5.501").descaleTo(0, RoundingMode.HALF_DOWN), equalTo(decimal("6")));
        assertThat(decimal("2.501").descaleTo(0, RoundingMode.HALF_DOWN), equalTo(decimal("3")));
        assertThat(decimal("1.001").descaleTo(0, RoundingMode.HALF_DOWN), equalTo(decimal("1")));
        assertThat(decimal("0.001").descaleTo(0, RoundingMode.HALF_DOWN), equalTo(decimal("0")));
        assertThat(decimal("-0.001").descaleTo(0, RoundingMode.HALF_DOWN), equalTo(decimal("-0")));
        assertThat(decimal("-1.001").descaleTo(0, RoundingMode.HALF_DOWN), equalTo(decimal("-1")));
        assertThat(decimal("-2.501").descaleTo(0, RoundingMode.HALF_DOWN), equalTo(decimal("-3")));
        assertThat(decimal("-5.501").descaleTo(0, RoundingMode.HALF_DOWN), equalTo(decimal("-6")));

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

        assertThat(decimal("5.501").descaleTo(0, RoundingMode.HALF_EVEN), equalTo(decimal("6")));
        assertThat(decimal("2.501").descaleTo(0, RoundingMode.HALF_EVEN), equalTo(decimal("3")));
        assertThat(decimal("1.001").descaleTo(0, RoundingMode.HALF_EVEN), equalTo(decimal("1")));
        assertThat(decimal("0.001").descaleTo(0, RoundingMode.HALF_EVEN), equalTo(decimal("0")));
        assertThat(decimal("-0.001").descaleTo(0, RoundingMode.HALF_EVEN), equalTo(decimal("0")));
        assertThat(decimal("-1.001").descaleTo(0, RoundingMode.HALF_EVEN), equalTo(decimal("-1")));
        assertThat(decimal("-2.501").descaleTo(0, RoundingMode.HALF_EVEN), equalTo(decimal("-3")));
        assertThat(decimal("-5.501").descaleTo(0, RoundingMode.HALF_EVEN), equalTo(decimal("-6")));

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

        try { decimal("5.501").descaleTo(0, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("2.501").descaleTo(0, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("1.001").descaleTo(0, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("0.001").descaleTo(0, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("-0.001").descaleTo(0, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("-1.001").descaleTo(0, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("-2.501").descaleTo(0, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("-5.501").descaleTo(0, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
    }

    @Test
    public void shouldDoNothingWhenDescaleOfLongDecimalIsNotNeeded() {
        newList(
                "5.5",
                "2.5",
                "1.6",
                "1.1",
                "1.0",
                "-1.0",
                "-1.1",
                "-1.6",
                "-2.5",
                "-5.5"
        ).forEach(value -> {
            for (RoundingMode roundingMode : RoundingMode.values()) {
                assertThat(decimal(value).descaleTo(1, roundingMode), equalTo(decimal(value)));
                assertThat(decimal(value).descaleTo(Integer.MAX_VALUE, roundingMode), equalTo(decimal(value)));
                assertThat(decimal(value).descaleTo(randomInt(2, Integer.MAX_VALUE - 1), roundingMode), equalTo(decimal(value)));
            }
        });

        newList(
                "5.501",
                "2.501",
                "1.001",
                "0.001",
                "-0.001",
                "-1.001",
                "-2.501",
                "-5.501"
        ).forEach(value -> {
            for (RoundingMode roundingMode : RoundingMode.values()) {
                assertThat(decimal(value).descaleTo(3, roundingMode), equalTo(decimal(value)));
                assertThat(decimal(value).descaleTo(Integer.MAX_VALUE, roundingMode), equalTo(decimal(value)));
                assertThat(decimal(value).descaleTo(randomInt(4, Integer.MAX_VALUE - 1), roundingMode), equalTo(decimal(value)));
            }
        });
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

        assertThat(decimal("100000000000000000000000005.501").descaleTo(0, RoundingMode.UP), equalTo(decimal("100000000000000000000000006")));
        assertThat(decimal("100000000000000000000000002.501").descaleTo(0, RoundingMode.UP), equalTo(decimal("100000000000000000000000003")));
        assertThat(decimal("100000000000000000000000001.001").descaleTo(0, RoundingMode.UP), equalTo(decimal("100000000000000000000000002")));
        assertThat(decimal("100000000000000000000000000.001").descaleTo(0, RoundingMode.UP), equalTo(decimal("100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000000.001").descaleTo(0, RoundingMode.UP), equalTo(decimal("-100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000001.001").descaleTo(0, RoundingMode.UP), equalTo(decimal("-100000000000000000000000002")));
        assertThat(decimal("-100000000000000000000000002.501").descaleTo(0, RoundingMode.UP), equalTo(decimal("-100000000000000000000000003")));
        assertThat(decimal("-100000000000000000000000005.501").descaleTo(0, RoundingMode.UP), equalTo(decimal("-100000000000000000000000006")));

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

        assertThat(decimal("100000000000000000000000005.501").descaleTo(0, RoundingMode.DOWN), equalTo(decimal("100000000000000000000000005")));
        assertThat(decimal("100000000000000000000000002.501").descaleTo(0, RoundingMode.DOWN), equalTo(decimal("100000000000000000000000002")));
        assertThat(decimal("100000000000000000000000001.001").descaleTo(0, RoundingMode.DOWN), equalTo(decimal("100000000000000000000000001")));
        assertThat(decimal("100000000000000000000000000.001").descaleTo(0, RoundingMode.DOWN), equalTo(decimal("100000000000000000000000000")));
        assertThat(decimal("-100000000000000000000000000.001").descaleTo(0, RoundingMode.DOWN), equalTo(decimal("-100000000000000000000000000")));
        assertThat(decimal("-100000000000000000000000001.001").descaleTo(0, RoundingMode.DOWN), equalTo(decimal("-100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000002.501").descaleTo(0, RoundingMode.DOWN), equalTo(decimal("-100000000000000000000000002")));
        assertThat(decimal("-100000000000000000000000005.501").descaleTo(0, RoundingMode.DOWN), equalTo(decimal("-100000000000000000000000005")));

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

        assertThat(decimal("100000000000000000000000005.501").descaleTo(0, RoundingMode.CEILING), equalTo(decimal("100000000000000000000000006")));
        assertThat(decimal("100000000000000000000000002.501").descaleTo(0, RoundingMode.CEILING), equalTo(decimal("100000000000000000000000003")));
        assertThat(decimal("100000000000000000000000001.001").descaleTo(0, RoundingMode.CEILING), equalTo(decimal("100000000000000000000000002")));
        assertThat(decimal("100000000000000000000000000.001").descaleTo(0, RoundingMode.CEILING), equalTo(decimal("100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000000.001").descaleTo(0, RoundingMode.CEILING), equalTo(decimal("-100000000000000000000000000")));
        assertThat(decimal("-100000000000000000000000001.001").descaleTo(0, RoundingMode.CEILING), equalTo(decimal("-100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000002.501").descaleTo(0, RoundingMode.CEILING), equalTo(decimal("-100000000000000000000000002")));
        assertThat(decimal("-100000000000000000000000005.501").descaleTo(0, RoundingMode.CEILING), equalTo(decimal("-100000000000000000000000005")));

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

        assertThat(decimal("100000000000000000000000005.501").descaleTo(0, RoundingMode.FLOOR), equalTo(decimal("100000000000000000000000005")));
        assertThat(decimal("100000000000000000000000002.501").descaleTo(0, RoundingMode.FLOOR), equalTo(decimal("100000000000000000000000002")));
        assertThat(decimal("100000000000000000000000001.001").descaleTo(0, RoundingMode.FLOOR), equalTo(decimal("100000000000000000000000001")));
        assertThat(decimal("100000000000000000000000000.001").descaleTo(0, RoundingMode.FLOOR), equalTo(decimal("100000000000000000000000000")));
        assertThat(decimal("-100000000000000000000000000.001").descaleTo(0, RoundingMode.FLOOR), equalTo(decimal("-100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000001.001").descaleTo(0, RoundingMode.FLOOR), equalTo(decimal("-100000000000000000000000002")));
        assertThat(decimal("-100000000000000000000000002.501").descaleTo(0, RoundingMode.FLOOR), equalTo(decimal("-100000000000000000000000003")));
        assertThat(decimal("-100000000000000000000000005.501").descaleTo(0, RoundingMode.FLOOR), equalTo(decimal("-100000000000000000000000006")));

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

        assertThat(decimal("100000000000000000000000005.501").descaleTo(0, RoundingMode.HALF_UP), equalTo(decimal("100000000000000000000000006")));
        assertThat(decimal("100000000000000000000000002.501").descaleTo(0, RoundingMode.HALF_UP), equalTo(decimal("100000000000000000000000003")));
        assertThat(decimal("100000000000000000000000001.001").descaleTo(0, RoundingMode.HALF_UP), equalTo(decimal("100000000000000000000000001")));
        assertThat(decimal("100000000000000000000000000.001").descaleTo(0, RoundingMode.HALF_UP), equalTo(decimal("100000000000000000000000000")));
        assertThat(decimal("-100000000000000000000000000.001").descaleTo(0, RoundingMode.HALF_UP), equalTo(decimal("-100000000000000000000000000")));
        assertThat(decimal("-100000000000000000000000001.001").descaleTo(0, RoundingMode.HALF_UP), equalTo(decimal("-100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000002.501").descaleTo(0, RoundingMode.HALF_UP), equalTo(decimal("-100000000000000000000000003")));
        assertThat(decimal("-100000000000000000000000005.501").descaleTo(0, RoundingMode.HALF_UP), equalTo(decimal("-100000000000000000000000006")));

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

        assertThat(decimal("100000000000000000000000005.501").descaleTo(0, RoundingMode.HALF_DOWN), equalTo(decimal("100000000000000000000000006")));
        assertThat(decimal("100000000000000000000000002.501").descaleTo(0, RoundingMode.HALF_DOWN), equalTo(decimal("100000000000000000000000003")));
        assertThat(decimal("100000000000000000000000001.001").descaleTo(0, RoundingMode.HALF_DOWN), equalTo(decimal("100000000000000000000000001")));
        assertThat(decimal("100000000000000000000000000.001").descaleTo(0, RoundingMode.HALF_DOWN), equalTo(decimal("100000000000000000000000000")));
        assertThat(decimal("-100000000000000000000000000.001").descaleTo(0, RoundingMode.HALF_DOWN), equalTo(decimal("-100000000000000000000000000")));
        assertThat(decimal("-100000000000000000000000001.001").descaleTo(0, RoundingMode.HALF_DOWN), equalTo(decimal("-100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000002.501").descaleTo(0, RoundingMode.HALF_DOWN), equalTo(decimal("-100000000000000000000000003")));
        assertThat(decimal("-100000000000000000000000005.501").descaleTo(0, RoundingMode.HALF_DOWN), equalTo(decimal("-100000000000000000000000006")));

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

        assertThat(decimal("100000000000000000000000005.501").descaleTo(0, RoundingMode.HALF_EVEN), equalTo(decimal("100000000000000000000000006")));
        assertThat(decimal("100000000000000000000000002.501").descaleTo(0, RoundingMode.HALF_EVEN), equalTo(decimal("100000000000000000000000003")));
        assertThat(decimal("100000000000000000000000001.001").descaleTo(0, RoundingMode.HALF_EVEN), equalTo(decimal("100000000000000000000000001")));
        assertThat(decimal("100000000000000000000000000.001").descaleTo(0, RoundingMode.HALF_EVEN), equalTo(decimal("100000000000000000000000000")));
        assertThat(decimal("-100000000000000000000000000.001").descaleTo(0, RoundingMode.HALF_EVEN), equalTo(decimal("-100000000000000000000000000")));
        assertThat(decimal("-100000000000000000000000001.001").descaleTo(0, RoundingMode.HALF_EVEN), equalTo(decimal("-100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000002.501").descaleTo(0, RoundingMode.HALF_EVEN), equalTo(decimal("-100000000000000000000000003")));
        assertThat(decimal("-100000000000000000000000005.501").descaleTo(0, RoundingMode.HALF_EVEN), equalTo(decimal("-100000000000000000000000006")));

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

        try { decimal("100000000000000000000000005.501").descaleTo(0, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("100000000000000000000000002.501").descaleTo(0, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("100000000000000000000000001.001").descaleTo(0, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("100000000000000000000000000.001").descaleTo(0, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("-100000000000000000000000000.001").descaleTo(0, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("-100000000000000000000000001.001").descaleTo(0, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("-100000000000000000000000002.501").descaleTo(0, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("-100000000000000000000000005.501").descaleTo(0, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
    }

    @Test
    public void shouldDoNothingWhenDescaleOfHugeDecimalIsNotNeeded() {
        newList(
                "100000000000000000000000005.5",
                "100000000000000000000000002.5",
                "100000000000000000000000001.6",
                "100000000000000000000000001.1",
                "100000000000000000000000001.0",
                "-100000000000000000000000001.0",
                "-100000000000000000000000001.1",
                "-100000000000000000000000001.6",
                "-100000000000000000000000002.5",
                "-100000000000000000000000005.5"
        ).forEach(value -> {
            for (RoundingMode roundingMode : RoundingMode.values()) {
                assertThat(decimal(value).descaleTo(1, roundingMode), equalTo(decimal(value)));
                assertThat(decimal(value).descaleTo(Integer.MAX_VALUE, roundingMode), equalTo(decimal(value)));
                assertThat(decimal(value).descaleTo(randomInt(2, Integer.MAX_VALUE - 1), roundingMode), equalTo(decimal(value)));
            }
        });

        newList(
                "100000000000000000000000005.501",
                "100000000000000000000000002.501",
                "100000000000000000000000001.001",
                "100000000000000000000000000.001",
                "-100000000000000000000000000.001",
                "-100000000000000000000000001.001",
                "-100000000000000000000000002.501",
                "-100000000000000000000000005.501"
        ).forEach(value -> {
            for (RoundingMode roundingMode : RoundingMode.values()) {
                assertThat(decimal(value).descaleTo(3, roundingMode), equalTo(decimal(value)));
                assertThat(decimal(value).descaleTo(Integer.MAX_VALUE, roundingMode), equalTo(decimal(value)));
                assertThat(decimal(value).descaleTo(randomInt(4, Integer.MAX_VALUE - 1), roundingMode), equalTo(decimal(value)));
            }
        });
    }
}
