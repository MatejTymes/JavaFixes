package co.uk.matejtymes.tdp;

import co.uk.matejtymes.concurrency.Task;
import co.uk.matejtymes.tdp.Decimal.HugeDecimal;
import co.uk.matejtymes.tdp.Decimal.LongDecimal;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.math.BigDecimal;
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
        int value = randomInt();

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
//        int value = randomInt();
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
        long value = randomLong();

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
        long unscaledValue = randomLong();
        int scale = randomInt();

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
        BigInteger value = randomBigInteger();

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
        BigInteger unscaledValue = randomBigInteger();
        int scale = randomInt();

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
    public void shouldCreateDecimalFromBigDecimal() {
        BigDecimal value = randomBigDecimal();

        Decimal expectedDecimal = mock(Decimal.class);
        mockStaticCalls(() -> {
            when(DecimalParser.parseString(value.toPlainString())).thenReturn(expectedDecimal);
        });

        // When
        Decimal actualDecimal = Decimal.decimal(value);

        // Then
        assertThat(actualDecimal, equalTo(expectedDecimal));

        verifyStaticCalls(() -> {
            DecimalParser.parseString(value.toPlainString());
        });
    }

    @Test
    public void shouldCreateDecimalFromString() {
        String value = randomBigDecimal().toPlainString();

        Decimal expectedDecimal = mock(Decimal.class);
        mockStaticCalls(() -> {
            when(DecimalParser.parseString(value)).thenReturn(expectedDecimal);
        });

        // When
        Decimal actualDecimal = Decimal.decimal(value);

        // Then
        assertThat(actualDecimal, equalTo(expectedDecimal));

        verifyStaticCalls(() -> {
            DecimalParser.parseString(value);
        });
    }

    @Test
    public void shouldCreateDecimalFromString2() {
        String value = randomBigDecimal().toPlainString();

        Decimal expectedDecimal = mock(Decimal.class);
        mockStaticCalls(() -> {
            when(DecimalParser.parseString(value)).thenReturn(expectedDecimal);
        });

        // When
        Decimal actualDecimal = Decimal.d(value);

        // Then
        assertThat(actualDecimal, equalTo(expectedDecimal));

        verifyStaticCalls(() -> {
            DecimalParser.parseString(value);
        });
    }

    @Test
    public void shouldCompareTwoDecimals() {
        Decimal decimal1 = mock(Decimal.class);
        Decimal decimal2 = mock(Decimal.class);

        int expectedComparisonResult = pickRandomValue(-1, 0, 1);
        mockStaticCalls(() -> {
            when(DecimalEqualizer.compare(decimal1, decimal2)).thenReturn(expectedComparisonResult);
        });

        // When
        int actualComparisonResult = Decimal.compare(decimal1, decimal2);

        // Then
        assertThat(actualComparisonResult, equalTo(expectedComparisonResult));

        verifyStaticCalls(() -> {
            DecimalEqualizer.compare(decimal1, decimal2);
        });
    }

    @Test
    public void shouldProvideProperSign() {
        assertThat(new LongDecimal(randomLong(Long.MIN_VALUE, -1), randomInt()).signum(), equalTo(-1));
        assertThat(new LongDecimal(0, randomInt()).signum(), equalTo(0));
        assertThat(new LongDecimal(randomLong(1, Long.MAX_VALUE), randomInt()).signum(), equalTo(1));

        assertThat(new HugeDecimal(randomBigInteger("-9999999999999999999999999999999", "-1"), randomInt()).signum(), equalTo(-1));
        assertThat(new HugeDecimal(BigInteger.ZERO, randomInt()).signum(), equalTo(0));
        assertThat(new HugeDecimal(randomBigInteger("1", "9999999999999999999999999999999"), randomInt()).signum(), equalTo(1));
    }

    @Test
    public void shouldProvideProperScale() {
        int scale = randomInt();

        assertThat(new LongDecimal(randomLong(), scale).scale(), equalTo(scale));
        assertThat(new HugeDecimal(randomBigInteger(), scale).scale(), equalTo(scale));
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