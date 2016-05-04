package mtymes.javafixes.beta.decimal;

import mtymes.javafixes.beta.decimal.Decimal.HugeDecimal;
import mtymes.javafixes.beta.decimal.Decimal.LongDecimal;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.math.BigDecimal;
import java.math.BigInteger;

import static mtymes.javafixes.test.Random.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        DecimalCreator.class,
        DecimalParser.class,
        DecimalEqualizer.class,
        DecimalPrinter.class,
        DecimalNegator.class
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

    @Test
    public void shouldConvertLongDecimalToBigDecimal() {
        LongDecimal longDecimal = new LongDecimal(randomLong(), randomInt());

        BigDecimal expectedBigDecimal = randomBigDecimal();
        mockStaticCalls(() -> {
            when(DecimalPrinter.toPlainString(longDecimal, 0)).thenReturn(expectedBigDecimal.toPlainString());
        });

        // When
        BigDecimal actualBigDecimal = longDecimal.bigDecimalValue();

        // Then
        assertThat(actualBigDecimal, equalTo(expectedBigDecimal));
        verifyStaticCalls(() -> {
            DecimalPrinter.toPlainString(longDecimal, 0);
        });
    }

    @Test
    public void shouldConvertHugeDecimalToBigDecimal() {
        HugeDecimal hugeDecimal = new HugeDecimal(randomBigInteger(), randomInt());

        BigDecimal expectedBigDecimal = randomBigDecimal();
        mockStaticCalls(() -> {
            when(DecimalPrinter.toPlainString(hugeDecimal, 0)).thenReturn(expectedBigDecimal.toPlainString());
        });

        // When
        BigDecimal actualBigDecimal = hugeDecimal.bigDecimalValue();

        // Then
        assertThat(actualBigDecimal, equalTo(expectedBigDecimal));
        verifyStaticCalls(() -> {
            DecimalPrinter.toPlainString(hugeDecimal, 0);
        });
    }

    @Test
    public void shouldNegateLongDecimal() {
        LongDecimal longDecimal = new LongDecimal(randomLong(), randomInt());

        Decimal expectedDecimal = mock(Decimal.class);
        mockStaticCalls(() -> {
            when(DecimalNegator.negate(longDecimal)).thenReturn(expectedDecimal);
        });

        // When
        Decimal actualDecimal = longDecimal.negate();

        // Then
        assertThat(actualDecimal, equalTo(expectedDecimal));
        verifyStaticCalls(() -> {
            DecimalNegator.negate(longDecimal);
        });
    }

    @Test
    public void shouldNegateHugeDecimal() {
        HugeDecimal hugeDecimal = new HugeDecimal(randomBigInteger(), randomInt());

        Decimal expectedDecimal = mock(Decimal.class);
        mockStaticCalls(() -> {
            when(DecimalNegator.negate(hugeDecimal)).thenReturn(expectedDecimal);
        });

        // When
        Decimal actualDecimal = hugeDecimal.negate();

        // Then
        assertThat(actualDecimal, equalTo(expectedDecimal));
        verifyStaticCalls(() -> {
            DecimalNegator.negate(hugeDecimal);
        });
    }


    private void mockStaticCalls(Runnable task) {
        mockStatic(
                DecimalCreator.class,
                DecimalParser.class,
                DecimalEqualizer.class,
                DecimalPrinter.class,
                DecimalNegator.class
        );

        try {
            task.run();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    private void verifyStaticCalls(Runnable task) {
        verifyStatic();

        try {
            task.run();
        } catch (Exception e) {
            throw new RuntimeException();
        }

        verifyNoMoreInteractions(
                DecimalCreator.class,
                DecimalParser.class,
                DecimalEqualizer.class,
                DecimalPrinter.class,
                DecimalNegator.class
        );
    }
}