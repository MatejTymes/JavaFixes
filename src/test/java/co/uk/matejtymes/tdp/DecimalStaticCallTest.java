package co.uk.matejtymes.tdp;

import co.uk.matejtymes.concurrency.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static co.uk.matejtymes.test.Random.randomInt;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        DecimalCreator.class,
        DecimalParser.class
})
public class DecimalStaticCallTest {

    @Test
    public void shouldCreateDecimalFromInt() {
        int value = randomInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
        Decimal expectedDecimal = mock(Decimal.class);

        mockStaticCalls(() -> {
            when(DecimalCreator.createDecimal((long) value, 0)).thenReturn(expectedDecimal);
        });

        // When
        Decimal actualDecimal = Decimal.decimal(value);

        // Then
        assertThat(actualDecimal, equalTo(expectedDecimal));

        verifyStaticCalls(() -> {
            DecimalCreator.createDecimal((long) value, 0);
        });
    }

    @Test
    public void shouldCreateDecimalFromLong() {
        // todo: create randomLong()
        long value = randomInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
        Decimal expectedDecimal = mock(Decimal.class);

        mockStaticCalls(() -> {
            when(DecimalCreator.createDecimal(value, 0)).thenReturn(expectedDecimal);
        });

        // When
        Decimal actualDecimal = Decimal.decimal(value);

        // Then
        assertThat(actualDecimal, equalTo(expectedDecimal));

        verifyStaticCalls(() -> {
            DecimalCreator.createDecimal(value, 0);
        });
    }

    @Test
    public void shouldCreateDecimalFromLongAndScale() {
        // todo: create randomLong()
        long unscaledValue = randomInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
        int scale = randomInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
        Decimal expectedDecimal = mock(Decimal.class);

        mockStaticCalls(() -> {
            when(DecimalCreator.createDecimal(unscaledValue, scale)).thenReturn(expectedDecimal);
        });

        // When
        Decimal actualDecimal = Decimal.decimal(unscaledValue, scale);

        // Then
        assertThat(actualDecimal, equalTo(expectedDecimal));

        verifyStaticCalls(() -> {
            DecimalCreator.createDecimal(unscaledValue, scale);
        });
    }

    private void mockStaticCalls(Task task) {
        mockStatic(
                DecimalCreator.class,
                DecimalParser.class
        );

        try {
            task.run();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    private void verifyStaticCalls(Task task) {
        verifyStatic();

        try {
            task.run();
        } catch (Exception e) {
            throw new RuntimeException();
        }

        verifyNoMoreInteractions(
                DecimalCreator.class,
                DecimalParser.class
        );
    }
}