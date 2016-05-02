package co.uk.matejtymes.tdp;

import co.uk.matejtymes.concurrency.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.math.BigInteger;

import static co.uk.matejtymes.test.Random.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        DecimalCreator.class,
        DecimalParser.class,
        DecimalEqualizer.class
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

    // todo: nicer but i'm not able to make it work
//    @Test
//    public void shouldCreateDecimalFromInt_EasyMock() {
//        int value = randomInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
//        Decimal expectedDecimal = mock(Decimal.class);
//
//        mockStatic(DecimalCreator.class);
//        expect(DecimalCreator.createDecimal((long) value, 0)).andReturn(expectedDecimal);
//        replayAll();
//
//        // When
//        Decimal actualDecimal = Decimal.decimal(value);
//
//        // Then
//        assertThat(actualDecimal, equalTo(expectedDecimal));
//        verifyAll();
//    }

    @Test
    public void shouldCreateDecimalFromLong() {
        long value = randomLong(Long.MIN_VALUE, Long.MAX_VALUE);
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
        long unscaledValue = randomLong(Long.MIN_VALUE, Long.MAX_VALUE);
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

    @Test
    public void shouldCreateDecimalFromBigInteger() {
        BigInteger value = randomBigInteger("-9999999999999999999", "9999999999999999999");
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
    public void shouldCreateDecimalFromBigIntegerAndScale() {
        BigInteger unscaledValue = randomBigInteger("-9999999999999999999", "9999999999999999999");
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
                DecimalParser.class,
                DecimalEqualizer.class
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
                DecimalParser.class,
                DecimalEqualizer.class
        );
    }
}