package mtymes.javafixes.beta.decimal;

import org.junit.Test;

import static java.math.RoundingMode.HALF_UP;
import static mtymes.javafixes.beta.decimal.Decimal.d;
import static mtymes.javafixes.beta.decimal.Precision.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class MathConstantsTest {

    @Test
    public void shouldVerifyConstantPrecision() {
        Decimal reallyLongPi = d("3.14159265358979323846264338327950288419716939937510582097"); // taken from wolfram alpha

        assertThat(MathConstants.PI_7, equalTo(reallyLongPi.deprecisionTo(_7_SIGNIFICANT_DIGITS, HALF_UP)));
        assertThat(MathConstants.PI_16, equalTo(reallyLongPi.deprecisionTo(_16_SIGNIFICANT_DIGITS, HALF_UP)));
        assertThat(MathConstants.PI_34, equalTo(reallyLongPi.deprecisionTo(_34_SIGNIFICANT_DIGITS, HALF_UP)));
    }
}