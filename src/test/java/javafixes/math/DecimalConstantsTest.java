package javafixes.math;

import org.junit.Test;

import static javafixes.math.Decimal.d;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class DecimalConstantsTest {

    @Test
    public void shouldHaveCorrectConstants() {
        assertThat(Decimal.ZERO, equalTo(d("0")));
        assertThat(Decimal.ONE, equalTo(d("1")));
        assertThat(Decimal.TEN, equalTo(d("10")));
    }
}