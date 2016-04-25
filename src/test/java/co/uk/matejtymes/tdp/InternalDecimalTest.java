package co.uk.matejtymes.tdp;

import org.junit.Test;

import static co.uk.matejtymes.tdp.Decimal.d;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class InternalDecimalTest {

    @Test
    public void shouldRemoveTrailingZerosOnCreation() {
        assertThat(d("-12345").unscaledValue(), equalTo(-12345L));
        assertThat(d("-12345").scale(), equalTo(0));

        assertThat(d("14.53000").unscaledValue(), equalTo(1453L));
        assertThat(d("14.53000").scale(), equalTo(2));

        assertThat(d("0.0000352").unscaledValue(), equalTo(352L));
        assertThat(d("0.0000352").scale(), equalTo(7));

        assertThat(d("9100400000.00").unscaledValue(), equalTo(91004L));
        assertThat(d("9100400000.00").scale(), equalTo(-5));

        assertThat(d("-0038.00").unscaledValue(), equalTo(-38L));
        assertThat(d("-0038.00").scale(), equalTo(0));
    }
}