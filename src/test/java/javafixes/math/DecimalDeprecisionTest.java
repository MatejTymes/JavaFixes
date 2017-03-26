package javafixes.math;

import org.junit.Test;

import java.math.RoundingMode;

import static javafixes.common.CollectionUtil.newList;
import static javafixes.math.Decimal.d;
import static javafixes.math.Decimal.decimal;
import static javafixes.test.Condition.*;
import static javafixes.test.Random.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class DecimalDeprecisionTest {

    @Test
    public void shouldDeprecisionAndRoundValueProperly() {

        assertThat(decimal("5.5").deprecisionTo(1, RoundingMode.UP), equalTo(decimal("6")));
        assertThat(decimal("2.5").deprecisionTo(1, RoundingMode.UP), equalTo(decimal("3")));
        assertThat(decimal("1.6").deprecisionTo(1, RoundingMode.UP), equalTo(decimal("2")));
        assertThat(decimal("1.1").deprecisionTo(1, RoundingMode.UP), equalTo(decimal("2")));
        assertThat(decimal("1.0").deprecisionTo(1, RoundingMode.UP), equalTo(decimal("1")));
        assertThat(decimal("-1.0").deprecisionTo(1, RoundingMode.UP), equalTo(decimal("-1")));
        assertThat(decimal("-1.1").deprecisionTo(1, RoundingMode.UP), equalTo(decimal("-2")));
        assertThat(decimal("-1.6").deprecisionTo(1, RoundingMode.UP), equalTo(decimal("-2")));
        assertThat(decimal("-2.5").deprecisionTo(1, RoundingMode.UP), equalTo(decimal("-3")));
        assertThat(decimal("-5.5").deprecisionTo(1, RoundingMode.UP), equalTo(decimal("-6")));

        assertThat(decimal("5.501").deprecisionTo(1, RoundingMode.UP), equalTo(decimal("6")));
        assertThat(decimal("2.501").deprecisionTo(1, RoundingMode.UP), equalTo(decimal("3")));
        assertThat(decimal("1.001").deprecisionTo(1, RoundingMode.UP), equalTo(decimal("2")));
        assertThat(decimal("-1.001").deprecisionTo(1, RoundingMode.UP), equalTo(decimal("-2")));
        assertThat(decimal("-2.501").deprecisionTo(1, RoundingMode.UP), equalTo(decimal("-3")));
        assertThat(decimal("-5.501").deprecisionTo(1, RoundingMode.UP), equalTo(decimal("-6")));

        assertThat(decimal("5.5").deprecisionTo(1, RoundingMode.DOWN), equalTo(decimal("5")));
        assertThat(decimal("2.5").deprecisionTo(1, RoundingMode.DOWN), equalTo(decimal("2")));
        assertThat(decimal("1.6").deprecisionTo(1, RoundingMode.DOWN), equalTo(decimal("1")));
        assertThat(decimal("1.1").deprecisionTo(1, RoundingMode.DOWN), equalTo(decimal("1")));
        assertThat(decimal("1.0").deprecisionTo(1, RoundingMode.DOWN), equalTo(decimal("1")));
        assertThat(decimal("-1.0").deprecisionTo(1, RoundingMode.DOWN), equalTo(decimal("-1")));
        assertThat(decimal("-1.1").deprecisionTo(1, RoundingMode.DOWN), equalTo(decimal("-1")));
        assertThat(decimal("-1.6").deprecisionTo(1, RoundingMode.DOWN), equalTo(decimal("-1")));
        assertThat(decimal("-2.5").deprecisionTo(1, RoundingMode.DOWN), equalTo(decimal("-2")));
        assertThat(decimal("-5.5").deprecisionTo(1, RoundingMode.DOWN), equalTo(decimal("-5")));

        assertThat(decimal("5.501").deprecisionTo(1, RoundingMode.DOWN), equalTo(decimal("5")));
        assertThat(decimal("2.501").deprecisionTo(1, RoundingMode.DOWN), equalTo(decimal("2")));
        assertThat(decimal("1.001").deprecisionTo(1, RoundingMode.DOWN), equalTo(decimal("1")));
        assertThat(decimal("-1.001").deprecisionTo(1, RoundingMode.DOWN), equalTo(decimal("-1")));
        assertThat(decimal("-2.501").deprecisionTo(1, RoundingMode.DOWN), equalTo(decimal("-2")));
        assertThat(decimal("-5.501").deprecisionTo(1, RoundingMode.DOWN), equalTo(decimal("-5")));

        assertThat(decimal("5.5").deprecisionTo(1, RoundingMode.CEILING), equalTo(decimal("6")));
        assertThat(decimal("2.5").deprecisionTo(1, RoundingMode.CEILING), equalTo(decimal("3")));
        assertThat(decimal("1.6").deprecisionTo(1, RoundingMode.CEILING), equalTo(decimal("2")));
        assertThat(decimal("1.1").deprecisionTo(1, RoundingMode.CEILING), equalTo(decimal("2")));
        assertThat(decimal("1.0").deprecisionTo(1, RoundingMode.CEILING), equalTo(decimal("1")));
        assertThat(decimal("-1.0").deprecisionTo(1, RoundingMode.CEILING), equalTo(decimal("-1")));
        assertThat(decimal("-1.1").deprecisionTo(1, RoundingMode.CEILING), equalTo(decimal("-1")));
        assertThat(decimal("-1.6").deprecisionTo(1, RoundingMode.CEILING), equalTo(decimal("-1")));
        assertThat(decimal("-2.5").deprecisionTo(1, RoundingMode.CEILING), equalTo(decimal("-2")));
        assertThat(decimal("-5.5").deprecisionTo(1, RoundingMode.CEILING), equalTo(decimal("-5")));

        assertThat(decimal("5.501").deprecisionTo(1, RoundingMode.CEILING), equalTo(decimal("6")));
        assertThat(decimal("2.501").deprecisionTo(1, RoundingMode.CEILING), equalTo(decimal("3")));
        assertThat(decimal("1.001").deprecisionTo(1, RoundingMode.CEILING), equalTo(decimal("2")));
        assertThat(decimal("-1.001").deprecisionTo(1, RoundingMode.CEILING), equalTo(decimal("-1")));
        assertThat(decimal("-2.501").deprecisionTo(1, RoundingMode.CEILING), equalTo(decimal("-2")));
        assertThat(decimal("-5.501").deprecisionTo(1, RoundingMode.CEILING), equalTo(decimal("-5")));

        assertThat(decimal("5.5").deprecisionTo(1, RoundingMode.FLOOR), equalTo(decimal("5")));
        assertThat(decimal("2.5").deprecisionTo(1, RoundingMode.FLOOR), equalTo(decimal("2")));
        assertThat(decimal("1.6").deprecisionTo(1, RoundingMode.FLOOR), equalTo(decimal("1")));
        assertThat(decimal("1.1").deprecisionTo(1, RoundingMode.FLOOR), equalTo(decimal("1")));
        assertThat(decimal("1.0").deprecisionTo(1, RoundingMode.FLOOR), equalTo(decimal("1")));
        assertThat(decimal("-1.0").deprecisionTo(1, RoundingMode.FLOOR), equalTo(decimal("-1")));
        assertThat(decimal("-1.1").deprecisionTo(1, RoundingMode.FLOOR), equalTo(decimal("-2")));
        assertThat(decimal("-1.6").deprecisionTo(1, RoundingMode.FLOOR), equalTo(decimal("-2")));
        assertThat(decimal("-2.5").deprecisionTo(1, RoundingMode.FLOOR), equalTo(decimal("-3")));
        assertThat(decimal("-5.5").deprecisionTo(1, RoundingMode.FLOOR), equalTo(decimal("-6")));

        assertThat(decimal("5.501").deprecisionTo(1, RoundingMode.FLOOR), equalTo(decimal("5")));
        assertThat(decimal("2.501").deprecisionTo(1, RoundingMode.FLOOR), equalTo(decimal("2")));
        assertThat(decimal("1.001").deprecisionTo(1, RoundingMode.FLOOR), equalTo(decimal("1")));
        assertThat(decimal("-1.001").deprecisionTo(1, RoundingMode.FLOOR), equalTo(decimal("-2")));
        assertThat(decimal("-2.501").deprecisionTo(1, RoundingMode.FLOOR), equalTo(decimal("-3")));
        assertThat(decimal("-5.501").deprecisionTo(1, RoundingMode.FLOOR), equalTo(decimal("-6")));

        assertThat(decimal("5.5").deprecisionTo(1, RoundingMode.HALF_UP), equalTo(decimal("6")));
        assertThat(decimal("2.5").deprecisionTo(1, RoundingMode.HALF_UP), equalTo(decimal("3")));
        assertThat(decimal("1.6").deprecisionTo(1, RoundingMode.HALF_UP), equalTo(decimal("2")));
        assertThat(decimal("1.1").deprecisionTo(1, RoundingMode.HALF_UP), equalTo(decimal("1")));
        assertThat(decimal("1.0").deprecisionTo(1, RoundingMode.HALF_UP), equalTo(decimal("1")));
        assertThat(decimal("-1.0").deprecisionTo(1, RoundingMode.HALF_UP), equalTo(decimal("-1")));
        assertThat(decimal("-1.1").deprecisionTo(1, RoundingMode.HALF_UP), equalTo(decimal("-1")));
        assertThat(decimal("-1.6").deprecisionTo(1, RoundingMode.HALF_UP), equalTo(decimal("-2")));
        assertThat(decimal("-2.5").deprecisionTo(1, RoundingMode.HALF_UP), equalTo(decimal("-3")));
        assertThat(decimal("-5.5").deprecisionTo(1, RoundingMode.HALF_UP), equalTo(decimal("-6")));

        assertThat(decimal("5.501").deprecisionTo(1, RoundingMode.HALF_UP), equalTo(decimal("6")));
        assertThat(decimal("2.501").deprecisionTo(1, RoundingMode.HALF_UP), equalTo(decimal("3")));
        assertThat(decimal("1.001").deprecisionTo(1, RoundingMode.HALF_UP), equalTo(decimal("1")));
        assertThat(decimal("-1.001").deprecisionTo(1, RoundingMode.HALF_UP), equalTo(decimal("-1")));
        assertThat(decimal("-2.501").deprecisionTo(1, RoundingMode.HALF_UP), equalTo(decimal("-3")));
        assertThat(decimal("-5.501").deprecisionTo(1, RoundingMode.HALF_UP), equalTo(decimal("-6")));

        assertThat(decimal("5.5").deprecisionTo(1, RoundingMode.HALF_DOWN), equalTo(decimal("5")));
        assertThat(decimal("2.5").deprecisionTo(1, RoundingMode.HALF_DOWN), equalTo(decimal("2")));
        assertThat(decimal("1.6").deprecisionTo(1, RoundingMode.HALF_DOWN), equalTo(decimal("2")));
        assertThat(decimal("1.1").deprecisionTo(1, RoundingMode.HALF_DOWN), equalTo(decimal("1")));
        assertThat(decimal("1.0").deprecisionTo(1, RoundingMode.HALF_DOWN), equalTo(decimal("1")));
        assertThat(decimal("-1.0").deprecisionTo(1, RoundingMode.HALF_DOWN), equalTo(decimal("-1")));
        assertThat(decimal("-1.1").deprecisionTo(1, RoundingMode.HALF_DOWN), equalTo(decimal("-1")));
        assertThat(decimal("-1.6").deprecisionTo(1, RoundingMode.HALF_DOWN), equalTo(decimal("-2")));
        assertThat(decimal("-2.5").deprecisionTo(1, RoundingMode.HALF_DOWN), equalTo(decimal("-2")));
        assertThat(decimal("-5.5").deprecisionTo(1, RoundingMode.HALF_DOWN), equalTo(decimal("-5")));

        assertThat(decimal("5.501").deprecisionTo(1, RoundingMode.HALF_DOWN), equalTo(decimal("6")));
        assertThat(decimal("2.501").deprecisionTo(1, RoundingMode.HALF_DOWN), equalTo(decimal("3")));
        assertThat(decimal("1.001").deprecisionTo(1, RoundingMode.HALF_DOWN), equalTo(decimal("1")));
        assertThat(decimal("-1.001").deprecisionTo(1, RoundingMode.HALF_DOWN), equalTo(decimal("-1")));
        assertThat(decimal("-2.501").deprecisionTo(1, RoundingMode.HALF_DOWN), equalTo(decimal("-3")));
        assertThat(decimal("-5.501").deprecisionTo(1, RoundingMode.HALF_DOWN), equalTo(decimal("-6")));

        assertThat(decimal("5.5").deprecisionTo(1, RoundingMode.HALF_EVEN), equalTo(decimal("6")));
        assertThat(decimal("2.5").deprecisionTo(1, RoundingMode.HALF_EVEN), equalTo(decimal("2")));
        assertThat(decimal("1.6").deprecisionTo(1, RoundingMode.HALF_EVEN), equalTo(decimal("2")));
        assertThat(decimal("1.1").deprecisionTo(1, RoundingMode.HALF_EVEN), equalTo(decimal("1")));
        assertThat(decimal("1.0").deprecisionTo(1, RoundingMode.HALF_EVEN), equalTo(decimal("1")));
        assertThat(decimal("-1.0").deprecisionTo(1, RoundingMode.HALF_EVEN), equalTo(decimal("-1")));
        assertThat(decimal("-1.1").deprecisionTo(1, RoundingMode.HALF_EVEN), equalTo(decimal("-1")));
        assertThat(decimal("-1.6").deprecisionTo(1, RoundingMode.HALF_EVEN), equalTo(decimal("-2")));
        assertThat(decimal("-2.5").deprecisionTo(1, RoundingMode.HALF_EVEN), equalTo(decimal("-2")));
        assertThat(decimal("-5.5").deprecisionTo(1, RoundingMode.HALF_EVEN), equalTo(decimal("-6")));

        assertThat(decimal("5.501").deprecisionTo(1, RoundingMode.HALF_EVEN), equalTo(decimal("6")));
        assertThat(decimal("2.501").deprecisionTo(1, RoundingMode.HALF_EVEN), equalTo(decimal("3")));
        assertThat(decimal("1.001").deprecisionTo(1, RoundingMode.HALF_EVEN), equalTo(decimal("1")));
        assertThat(decimal("-1.001").deprecisionTo(1, RoundingMode.HALF_EVEN), equalTo(decimal("-1")));
        assertThat(decimal("-2.501").deprecisionTo(1, RoundingMode.HALF_EVEN), equalTo(decimal("-3")));
        assertThat(decimal("-5.501").deprecisionTo(1, RoundingMode.HALF_EVEN), equalTo(decimal("-6")));

        try { decimal("5.5").deprecisionTo(1, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("2.5").deprecisionTo(1, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("1.6").deprecisionTo(1, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("1.1").deprecisionTo(1, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        assertThat(decimal("1.0").deprecisionTo(1, RoundingMode.UNNECESSARY), equalTo(decimal("1")));
        assertThat(decimal("-1.0").deprecisionTo(1, RoundingMode.UNNECESSARY), equalTo(decimal("-1")));
        try { decimal("-1.1").deprecisionTo(1, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("-1.6").deprecisionTo(1, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("-2.5").deprecisionTo(1, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("-5.5").deprecisionTo(1, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }

        try { decimal("5.501").deprecisionTo(1, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("2.501").deprecisionTo(1, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("1.001").deprecisionTo(1, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("-1.001").deprecisionTo(1, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("-2.501").deprecisionTo(1, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("-5.501").deprecisionTo(1, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
    }

    @Test
    public void shouldDoNothingWhenDeprecisionOfLongDecimalIsNotNeeded() {
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
                assertThat(decimal(value).deprecisionTo(2, roundingMode), equalTo(decimal(value)));
                assertThat(decimal(value).deprecisionTo(Integer.MAX_VALUE, roundingMode), equalTo(decimal(value)));
                assertThat(decimal(value).deprecisionTo(randomInt(3, Integer.MAX_VALUE - 1), roundingMode), equalTo(decimal(value)));
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
                assertThat(decimal(value).deprecisionTo(4, roundingMode), equalTo(decimal(value)));
                assertThat(decimal(value).deprecisionTo(Integer.MAX_VALUE, roundingMode), equalTo(decimal(value)));
                assertThat(decimal(value).deprecisionTo(randomInt(5, Integer.MAX_VALUE - 1), roundingMode), equalTo(decimal(value)));
            }
        });
    }

    @Test
    public void shouldDeprecisionAndRoundHugeValueProperly() {

        assertThat(decimal("100000000000000000000000005.5").deprecisionTo(27, RoundingMode.UP), equalTo(decimal("100000000000000000000000006")));
        assertThat(decimal("100000000000000000000000002.5").deprecisionTo(27, RoundingMode.UP), equalTo(decimal("100000000000000000000000003")));
        assertThat(decimal("100000000000000000000000001.6").deprecisionTo(27, RoundingMode.UP), equalTo(decimal("100000000000000000000000002")));
        assertThat(decimal("100000000000000000000000001.1").deprecisionTo(27, RoundingMode.UP), equalTo(decimal("100000000000000000000000002")));
        assertThat(decimal("100000000000000000000000001.0").deprecisionTo(27, RoundingMode.UP), equalTo(decimal("100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000001.0").deprecisionTo(27, RoundingMode.UP), equalTo(decimal("-100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000001.1").deprecisionTo(27, RoundingMode.UP), equalTo(decimal("-100000000000000000000000002")));
        assertThat(decimal("-100000000000000000000000001.6").deprecisionTo(27, RoundingMode.UP), equalTo(decimal("-100000000000000000000000002")));
        assertThat(decimal("-100000000000000000000000002.5").deprecisionTo(27, RoundingMode.UP), equalTo(decimal("-100000000000000000000000003")));
        assertThat(decimal("-100000000000000000000000005.5").deprecisionTo(27, RoundingMode.UP), equalTo(decimal("-100000000000000000000000006")));

        assertThat(decimal("100000000000000000000000005.501").deprecisionTo(27, RoundingMode.UP), equalTo(decimal("100000000000000000000000006")));
        assertThat(decimal("100000000000000000000000002.501").deprecisionTo(27, RoundingMode.UP), equalTo(decimal("100000000000000000000000003")));
        assertThat(decimal("100000000000000000000000001.001").deprecisionTo(27, RoundingMode.UP), equalTo(decimal("100000000000000000000000002")));
        assertThat(decimal("100000000000000000000000000.001").deprecisionTo(27, RoundingMode.UP), equalTo(decimal("100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000000.001").deprecisionTo(27, RoundingMode.UP), equalTo(decimal("-100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000001.001").deprecisionTo(27, RoundingMode.UP), equalTo(decimal("-100000000000000000000000002")));
        assertThat(decimal("-100000000000000000000000002.501").deprecisionTo(27, RoundingMode.UP), equalTo(decimal("-100000000000000000000000003")));
        assertThat(decimal("-100000000000000000000000005.501").deprecisionTo(27, RoundingMode.UP), equalTo(decimal("-100000000000000000000000006")));

        assertThat(decimal("100000000000000000000000005.5").deprecisionTo(27, RoundingMode.DOWN), equalTo(decimal("100000000000000000000000005")));
        assertThat(decimal("100000000000000000000000002.5").deprecisionTo(27, RoundingMode.DOWN), equalTo(decimal("100000000000000000000000002")));
        assertThat(decimal("100000000000000000000000001.6").deprecisionTo(27, RoundingMode.DOWN), equalTo(decimal("100000000000000000000000001")));
        assertThat(decimal("100000000000000000000000001.1").deprecisionTo(27, RoundingMode.DOWN), equalTo(decimal("100000000000000000000000001")));
        assertThat(decimal("100000000000000000000000001.0").deprecisionTo(27, RoundingMode.DOWN), equalTo(decimal("100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000001.0").deprecisionTo(27, RoundingMode.DOWN), equalTo(decimal("-100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000001.1").deprecisionTo(27, RoundingMode.DOWN), equalTo(decimal("-100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000001.6").deprecisionTo(27, RoundingMode.DOWN), equalTo(decimal("-100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000002.5").deprecisionTo(27, RoundingMode.DOWN), equalTo(decimal("-100000000000000000000000002")));
        assertThat(decimal("-100000000000000000000000005.5").deprecisionTo(27, RoundingMode.DOWN), equalTo(decimal("-100000000000000000000000005")));

        assertThat(decimal("100000000000000000000000005.501").deprecisionTo(27, RoundingMode.DOWN), equalTo(decimal("100000000000000000000000005")));
        assertThat(decimal("100000000000000000000000002.501").deprecisionTo(27, RoundingMode.DOWN), equalTo(decimal("100000000000000000000000002")));
        assertThat(decimal("100000000000000000000000001.001").deprecisionTo(27, RoundingMode.DOWN), equalTo(decimal("100000000000000000000000001")));
        assertThat(decimal("100000000000000000000000000.001").deprecisionTo(27, RoundingMode.DOWN), equalTo(decimal("100000000000000000000000000")));
        assertThat(decimal("-100000000000000000000000000.001").deprecisionTo(27, RoundingMode.DOWN), equalTo(decimal("-100000000000000000000000000")));
        assertThat(decimal("-100000000000000000000000001.001").deprecisionTo(27, RoundingMode.DOWN), equalTo(decimal("-100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000002.501").deprecisionTo(27, RoundingMode.DOWN), equalTo(decimal("-100000000000000000000000002")));
        assertThat(decimal("-100000000000000000000000005.501").deprecisionTo(27, RoundingMode.DOWN), equalTo(decimal("-100000000000000000000000005")));

        assertThat(decimal("100000000000000000000000005.5").deprecisionTo(27, RoundingMode.CEILING), equalTo(decimal("100000000000000000000000006")));
        assertThat(decimal("100000000000000000000000002.5").deprecisionTo(27, RoundingMode.CEILING), equalTo(decimal("100000000000000000000000003")));
        assertThat(decimal("100000000000000000000000001.6").deprecisionTo(27, RoundingMode.CEILING), equalTo(decimal("100000000000000000000000002")));
        assertThat(decimal("100000000000000000000000001.1").deprecisionTo(27, RoundingMode.CEILING), equalTo(decimal("100000000000000000000000002")));
        assertThat(decimal("100000000000000000000000001.0").deprecisionTo(27, RoundingMode.CEILING), equalTo(decimal("100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000001.0").deprecisionTo(27, RoundingMode.CEILING), equalTo(decimal("-100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000001.1").deprecisionTo(27, RoundingMode.CEILING), equalTo(decimal("-100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000001.6").deprecisionTo(27, RoundingMode.CEILING), equalTo(decimal("-100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000002.5").deprecisionTo(27, RoundingMode.CEILING), equalTo(decimal("-100000000000000000000000002")));
        assertThat(decimal("-100000000000000000000000005.5").deprecisionTo(27, RoundingMode.CEILING), equalTo(decimal("-100000000000000000000000005")));

        assertThat(decimal("100000000000000000000000005.501").deprecisionTo(27, RoundingMode.CEILING), equalTo(decimal("100000000000000000000000006")));
        assertThat(decimal("100000000000000000000000002.501").deprecisionTo(27, RoundingMode.CEILING), equalTo(decimal("100000000000000000000000003")));
        assertThat(decimal("100000000000000000000000001.001").deprecisionTo(27, RoundingMode.CEILING), equalTo(decimal("100000000000000000000000002")));
        assertThat(decimal("100000000000000000000000000.001").deprecisionTo(27, RoundingMode.CEILING), equalTo(decimal("100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000000.001").deprecisionTo(27, RoundingMode.CEILING), equalTo(decimal("-100000000000000000000000000")));
        assertThat(decimal("-100000000000000000000000001.001").deprecisionTo(27, RoundingMode.CEILING), equalTo(decimal("-100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000002.501").deprecisionTo(27, RoundingMode.CEILING), equalTo(decimal("-100000000000000000000000002")));
        assertThat(decimal("-100000000000000000000000005.501").deprecisionTo(27, RoundingMode.CEILING), equalTo(decimal("-100000000000000000000000005")));

        assertThat(decimal("100000000000000000000000005.5").deprecisionTo(27, RoundingMode.FLOOR), equalTo(decimal("100000000000000000000000005")));
        assertThat(decimal("100000000000000000000000002.5").deprecisionTo(27, RoundingMode.FLOOR), equalTo(decimal("100000000000000000000000002")));
        assertThat(decimal("100000000000000000000000001.6").deprecisionTo(27, RoundingMode.FLOOR), equalTo(decimal("100000000000000000000000001")));
        assertThat(decimal("100000000000000000000000001.1").deprecisionTo(27, RoundingMode.FLOOR), equalTo(decimal("100000000000000000000000001")));
        assertThat(decimal("100000000000000000000000001.0").deprecisionTo(27, RoundingMode.FLOOR), equalTo(decimal("100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000001.0").deprecisionTo(27, RoundingMode.FLOOR), equalTo(decimal("-100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000001.1").deprecisionTo(27, RoundingMode.FLOOR), equalTo(decimal("-100000000000000000000000002")));
        assertThat(decimal("-100000000000000000000000001.6").deprecisionTo(27, RoundingMode.FLOOR), equalTo(decimal("-100000000000000000000000002")));
        assertThat(decimal("-100000000000000000000000002.5").deprecisionTo(27, RoundingMode.FLOOR), equalTo(decimal("-100000000000000000000000003")));
        assertThat(decimal("-100000000000000000000000005.5").deprecisionTo(27, RoundingMode.FLOOR), equalTo(decimal("-100000000000000000000000006")));

        assertThat(decimal("100000000000000000000000005.501").deprecisionTo(27, RoundingMode.FLOOR), equalTo(decimal("100000000000000000000000005")));
        assertThat(decimal("100000000000000000000000002.501").deprecisionTo(27, RoundingMode.FLOOR), equalTo(decimal("100000000000000000000000002")));
        assertThat(decimal("100000000000000000000000001.001").deprecisionTo(27, RoundingMode.FLOOR), equalTo(decimal("100000000000000000000000001")));
        assertThat(decimal("100000000000000000000000000.001").deprecisionTo(27, RoundingMode.FLOOR), equalTo(decimal("100000000000000000000000000")));
        assertThat(decimal("-100000000000000000000000000.001").deprecisionTo(27, RoundingMode.FLOOR), equalTo(decimal("-100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000001.001").deprecisionTo(27, RoundingMode.FLOOR), equalTo(decimal("-100000000000000000000000002")));
        assertThat(decimal("-100000000000000000000000002.501").deprecisionTo(27, RoundingMode.FLOOR), equalTo(decimal("-100000000000000000000000003")));
        assertThat(decimal("-100000000000000000000000005.501").deprecisionTo(27, RoundingMode.FLOOR), equalTo(decimal("-100000000000000000000000006")));

        assertThat(decimal("100000000000000000000000005.5").deprecisionTo(27, RoundingMode.HALF_UP), equalTo(decimal("100000000000000000000000006")));
        assertThat(decimal("100000000000000000000000002.5").deprecisionTo(27, RoundingMode.HALF_UP), equalTo(decimal("100000000000000000000000003")));
        assertThat(decimal("100000000000000000000000001.6").deprecisionTo(27, RoundingMode.HALF_UP), equalTo(decimal("100000000000000000000000002")));
        assertThat(decimal("100000000000000000000000001.1").deprecisionTo(27, RoundingMode.HALF_UP), equalTo(decimal("100000000000000000000000001")));
        assertThat(decimal("100000000000000000000000001.0").deprecisionTo(27, RoundingMode.HALF_UP), equalTo(decimal("100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000001.0").deprecisionTo(27, RoundingMode.HALF_UP), equalTo(decimal("-100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000001.1").deprecisionTo(27, RoundingMode.HALF_UP), equalTo(decimal("-100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000001.6").deprecisionTo(27, RoundingMode.HALF_UP), equalTo(decimal("-100000000000000000000000002")));
        assertThat(decimal("-100000000000000000000000002.5").deprecisionTo(27, RoundingMode.HALF_UP), equalTo(decimal("-100000000000000000000000003")));
        assertThat(decimal("-100000000000000000000000005.5").deprecisionTo(27, RoundingMode.HALF_UP), equalTo(decimal("-100000000000000000000000006")));

        assertThat(decimal("100000000000000000000000005.501").deprecisionTo(27, RoundingMode.HALF_UP), equalTo(decimal("100000000000000000000000006")));
        assertThat(decimal("100000000000000000000000002.501").deprecisionTo(27, RoundingMode.HALF_UP), equalTo(decimal("100000000000000000000000003")));
        assertThat(decimal("100000000000000000000000001.001").deprecisionTo(27, RoundingMode.HALF_UP), equalTo(decimal("100000000000000000000000001")));
        assertThat(decimal("100000000000000000000000000.001").deprecisionTo(27, RoundingMode.HALF_UP), equalTo(decimal("100000000000000000000000000")));
        assertThat(decimal("-100000000000000000000000000.001").deprecisionTo(27, RoundingMode.HALF_UP), equalTo(decimal("-100000000000000000000000000")));
        assertThat(decimal("-100000000000000000000000001.001").deprecisionTo(27, RoundingMode.HALF_UP), equalTo(decimal("-100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000002.501").deprecisionTo(27, RoundingMode.HALF_UP), equalTo(decimal("-100000000000000000000000003")));
        assertThat(decimal("-100000000000000000000000005.501").deprecisionTo(27, RoundingMode.HALF_UP), equalTo(decimal("-100000000000000000000000006")));

        assertThat(decimal("100000000000000000000000005.5").deprecisionTo(27, RoundingMode.HALF_DOWN), equalTo(decimal("100000000000000000000000005")));
        assertThat(decimal("100000000000000000000000002.5").deprecisionTo(27, RoundingMode.HALF_DOWN), equalTo(decimal("100000000000000000000000002")));
        assertThat(decimal("100000000000000000000000001.6").deprecisionTo(27, RoundingMode.HALF_DOWN), equalTo(decimal("100000000000000000000000002")));
        assertThat(decimal("100000000000000000000000001.1").deprecisionTo(27, RoundingMode.HALF_DOWN), equalTo(decimal("100000000000000000000000001")));
        assertThat(decimal("100000000000000000000000001.0").deprecisionTo(27, RoundingMode.HALF_DOWN), equalTo(decimal("100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000001.0").deprecisionTo(27, RoundingMode.HALF_DOWN), equalTo(decimal("-100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000001.1").deprecisionTo(27, RoundingMode.HALF_DOWN), equalTo(decimal("-100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000001.6").deprecisionTo(27, RoundingMode.HALF_DOWN), equalTo(decimal("-100000000000000000000000002")));
        assertThat(decimal("-100000000000000000000000002.5").deprecisionTo(27, RoundingMode.HALF_DOWN), equalTo(decimal("-100000000000000000000000002")));
        assertThat(decimal("-100000000000000000000000005.5").deprecisionTo(27, RoundingMode.HALF_DOWN), equalTo(decimal("-100000000000000000000000005")));

        assertThat(decimal("100000000000000000000000005.501").deprecisionTo(27, RoundingMode.HALF_DOWN), equalTo(decimal("100000000000000000000000006")));
        assertThat(decimal("100000000000000000000000002.501").deprecisionTo(27, RoundingMode.HALF_DOWN), equalTo(decimal("100000000000000000000000003")));
        assertThat(decimal("100000000000000000000000001.001").deprecisionTo(27, RoundingMode.HALF_DOWN), equalTo(decimal("100000000000000000000000001")));
        assertThat(decimal("100000000000000000000000000.001").deprecisionTo(27, RoundingMode.HALF_DOWN), equalTo(decimal("100000000000000000000000000")));
        assertThat(decimal("-100000000000000000000000000.001").deprecisionTo(27, RoundingMode.HALF_DOWN), equalTo(decimal("-100000000000000000000000000")));
        assertThat(decimal("-100000000000000000000000001.001").deprecisionTo(27, RoundingMode.HALF_DOWN), equalTo(decimal("-100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000002.501").deprecisionTo(27, RoundingMode.HALF_DOWN), equalTo(decimal("-100000000000000000000000003")));
        assertThat(decimal("-100000000000000000000000005.501").deprecisionTo(27, RoundingMode.HALF_DOWN), equalTo(decimal("-100000000000000000000000006")));

        assertThat(decimal("100000000000000000000000005.5").deprecisionTo(27, RoundingMode.HALF_EVEN), equalTo(decimal("100000000000000000000000006")));
        assertThat(decimal("100000000000000000000000002.5").deprecisionTo(27, RoundingMode.HALF_EVEN), equalTo(decimal("100000000000000000000000002")));
        assertThat(decimal("100000000000000000000000001.6").deprecisionTo(27, RoundingMode.HALF_EVEN), equalTo(decimal("100000000000000000000000002")));
        assertThat(decimal("100000000000000000000000001.1").deprecisionTo(27, RoundingMode.HALF_EVEN), equalTo(decimal("100000000000000000000000001")));
        assertThat(decimal("100000000000000000000000001.0").deprecisionTo(27, RoundingMode.HALF_EVEN), equalTo(decimal("100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000001.0").deprecisionTo(27, RoundingMode.HALF_EVEN), equalTo(decimal("-100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000001.1").deprecisionTo(27, RoundingMode.HALF_EVEN), equalTo(decimal("-100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000001.6").deprecisionTo(27, RoundingMode.HALF_EVEN), equalTo(decimal("-100000000000000000000000002")));
        assertThat(decimal("-100000000000000000000000002.5").deprecisionTo(27, RoundingMode.HALF_EVEN), equalTo(decimal("-100000000000000000000000002")));
        assertThat(decimal("-100000000000000000000000005.5").deprecisionTo(27, RoundingMode.HALF_EVEN), equalTo(decimal("-100000000000000000000000006")));

        assertThat(decimal("100000000000000000000000005.501").deprecisionTo(27, RoundingMode.HALF_EVEN), equalTo(decimal("100000000000000000000000006")));
        assertThat(decimal("100000000000000000000000002.501").deprecisionTo(27, RoundingMode.HALF_EVEN), equalTo(decimal("100000000000000000000000003")));
        assertThat(decimal("100000000000000000000000001.001").deprecisionTo(27, RoundingMode.HALF_EVEN), equalTo(decimal("100000000000000000000000001")));
        assertThat(decimal("100000000000000000000000000.001").deprecisionTo(27, RoundingMode.HALF_EVEN), equalTo(decimal("100000000000000000000000000")));
        assertThat(decimal("-100000000000000000000000000.001").deprecisionTo(27, RoundingMode.HALF_EVEN), equalTo(decimal("-100000000000000000000000000")));
        assertThat(decimal("-100000000000000000000000001.001").deprecisionTo(27, RoundingMode.HALF_EVEN), equalTo(decimal("-100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000002.501").deprecisionTo(27, RoundingMode.HALF_EVEN), equalTo(decimal("-100000000000000000000000003")));
        assertThat(decimal("-100000000000000000000000005.501").deprecisionTo(27, RoundingMode.HALF_EVEN), equalTo(decimal("-100000000000000000000000006")));

        try { decimal("100000000000000000000000005.5").deprecisionTo(27, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("100000000000000000000000002.5").deprecisionTo(27, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("100000000000000000000000001.6").deprecisionTo(27, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("100000000000000000000000001.1").deprecisionTo(27, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        assertThat(decimal("100000000000000000000000001.0").deprecisionTo(27, RoundingMode.UNNECESSARY), equalTo(decimal("100000000000000000000000001")));
        assertThat(decimal("-100000000000000000000000001.0").deprecisionTo(27, RoundingMode.UNNECESSARY), equalTo(decimal("-100000000000000000000000001")));
        try { decimal("-100000000000000000000000001.1").deprecisionTo(27, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("-100000000000000000000000001.6").deprecisionTo(27, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("-100000000000000000000000002.5").deprecisionTo(27, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("-100000000000000000000000005.5").deprecisionTo(27, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }

        try { decimal("100000000000000000000000005.501").deprecisionTo(27, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("100000000000000000000000002.501").deprecisionTo(27, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("100000000000000000000000001.001").deprecisionTo(27, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("100000000000000000000000000.001").deprecisionTo(27, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("-100000000000000000000000000.001").deprecisionTo(27, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("-100000000000000000000000001.001").deprecisionTo(27, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("-100000000000000000000000002.501").deprecisionTo(27, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
        try { decimal("-100000000000000000000000005.501").deprecisionTo(27, RoundingMode.UNNECESSARY); fail("expected ArithmeticException"); } catch (ArithmeticException expected) { }
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
                assertThat(decimal(value).deprecisionTo(28, roundingMode), equalTo(decimal(value)));
                assertThat(decimal(value).deprecisionTo(Integer.MAX_VALUE, roundingMode), equalTo(decimal(value)));
                assertThat(decimal(value).deprecisionTo(randomInt(29, Integer.MAX_VALUE - 1), roundingMode), equalTo(decimal(value)));
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
                assertThat(decimal(value).deprecisionTo(30, roundingMode), equalTo(decimal(value)));
                assertThat(decimal(value).deprecisionTo(Integer.MAX_VALUE, roundingMode), equalTo(decimal(value)));
                assertThat(decimal(value).deprecisionTo(randomInt(31, Integer.MAX_VALUE - 1), roundingMode), equalTo(decimal(value)));
            }
        });
    }

    @Test
    public void shouldBeAbleToUseZeroPrecision() {
        Decimal positiveHugeDecimal = d(randomBigInteger(positive(), notFitIntoLong(), notDivisibleBy10()), randomInt());
        Decimal negativeHugeDecimal = d(randomBigInteger(negative(), notFitIntoLong(), notDivisibleBy10()), randomInt());

        Decimal positiveLongDecimal = d(randomLong(positive()), randomInt());
        Decimal negativeLongDecimal = d(randomLong(negative()), randomInt());
        Decimal zeroLongDecimal = Decimal.ZERO;

        for (RoundingMode roundingMode : RoundingMode.values()) {
            assertThat(positiveHugeDecimal.deprecisionTo(0, roundingMode), equalTo(Decimal.ZERO));
            assertThat(negativeHugeDecimal.deprecisionTo(0, roundingMode), equalTo(Decimal.ZERO));
            assertThat(positiveLongDecimal.deprecisionTo(0, roundingMode), equalTo(Decimal.ZERO));
            assertThat(negativeLongDecimal.deprecisionTo(0, roundingMode), equalTo(Decimal.ZERO));
            assertThat(zeroLongDecimal.deprecisionTo(0, roundingMode), equalTo(Decimal.ZERO));
        }
    }

    @Test
    public void shouldFailWhenUsingNegativePrecision() {
        Decimal positiveHugeDecimal = d(randomBigInteger(positive(), notFitIntoLong(), notDivisibleBy10()), randomInt());
        Decimal negativeHugeDecimal = d(randomBigInteger(negative(), notFitIntoLong(), notDivisibleBy10()), randomInt());

        Decimal positiveLongDecimal = d(randomLong(positive()), randomInt());
        Decimal negativeLongDecimal = d(randomLong(negative()), randomInt());
        Decimal zeroLongDecimal = Decimal.ZERO;

        for (RoundingMode roundingMode : RoundingMode.values()) {
            for (int precision : newList(-1, Integer.MIN_VALUE, randomInt(Integer.MIN_VALUE + 1, -2))) {
                try {
                    assertThat(positiveHugeDecimal.deprecisionTo(precision, roundingMode), equalTo(Decimal.ZERO));
                    fail("should fail for " + precision + " as negative precision is not allowed");
                } catch (IllegalArgumentException expected) {
                    // expected
                }

                try {
                    assertThat(negativeHugeDecimal.deprecisionTo(precision, roundingMode), equalTo(Decimal.ZERO));
                    fail("should fail for " + precision + " as negative precision is not allowed");
                } catch (IllegalArgumentException expected) {
                    // expected
                }

                try {
                    assertThat(positiveLongDecimal.deprecisionTo(precision, roundingMode), equalTo(Decimal.ZERO));
                    fail("should fail for " + precision + " as negative precision is not allowed");
                } catch (IllegalArgumentException expected) {
                    // expected
                }

                try {
                    assertThat(negativeLongDecimal.deprecisionTo(precision, roundingMode), equalTo(Decimal.ZERO));
                    fail("should fail for " + precision + " as negative precision is not allowed");
                } catch (IllegalArgumentException expected) {
                    // expected
                }

                try {
                    assertThat(zeroLongDecimal.deprecisionTo(precision, roundingMode), equalTo(Decimal.ZERO));
                    fail("should fail for " + precision + " as negative precision is not allowed");
                } catch (IllegalArgumentException expected) {
                    // expected
                }
            }
        }
    }
}
