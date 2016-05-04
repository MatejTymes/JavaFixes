package mtymes.javafixes.beta.decimal;

import mtymes.javafixes.beta.decimal.Decimal.HugeDecimal;
import mtymes.javafixes.beta.decimal.Decimal.LongDecimal;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import static java.lang.Math.max;
import static java.math.RoundingMode.HALF_UP;
import static java.math.RoundingMode.UNNECESSARY;
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
        DecimalNegator.class,
        DecimalAccumulator.class
})
public class DecimalStaticCallTest {

    @Test
    public void shouldCreateDecimalFromInt() {
        int value = randomInt();

        Decimal expectedResult = mock(Decimal.class);
        mockStaticCalls(() -> {
            when(DecimalCreator.createDecimal((long) value, 0)).thenReturn(expectedResult);
        });

        // When
        Decimal actualResult = Decimal.decimal(value);

        // Then
        assertThat(actualResult, equalTo(expectedResult));

        verifyStaticCalls(() -> {
            DecimalCreator.createDecimal((long) value, 0);
        });
    }

    // todo: nicer but i'm not able to make it work
//    @Test
//    public void shouldCreateDecimalFromInt_EasyMock() {
//        int value = randomInt();
//        Decimal expectedResult = mock(Decimal.class);
//
//        mockStatic(DecimalCreator.class);
//        expect(DecimalCreator.createDecimal((long) value, 0)).andReturn(expectedResult);
//        replayAll();
//
//        // When
//        Decimal actualResult = Decimal.decimal(value);
//
//        // Then
//        assertThat(actualResult, equalTo(expectedResult));
//        verifyAll();
//    }

    @Test
    public void shouldCreateDecimalFromLong() {
        long value = randomLong();

        Decimal expectedResult = mock(Decimal.class);
        mockStaticCalls(() -> {
            when(DecimalCreator.createDecimal(value, 0)).thenReturn(expectedResult);
        });

        // When
        Decimal actualResult = Decimal.decimal(value);

        // Then
        assertThat(actualResult, equalTo(expectedResult));

        verifyStaticCalls(() -> {
            DecimalCreator.createDecimal(value, 0);
        });
    }

    @Test
    public void shouldCreateDecimalFromLongAndScale() {
        long unscaledValue = randomLong();
        int scale = randomInt();

        Decimal expectedResult = mock(Decimal.class);
        mockStaticCalls(() -> {
            when(DecimalCreator.createDecimal(unscaledValue, scale)).thenReturn(expectedResult);
        });

        // When
        Decimal actualResult = Decimal.decimal(unscaledValue, scale);

        // Then
        assertThat(actualResult, equalTo(expectedResult));

        verifyStaticCalls(() -> {
            DecimalCreator.createDecimal(unscaledValue, scale);
        });
    }

    @Test
    public void shouldCreateDecimalFromBigInteger() {
        BigInteger value = randomBigInteger();

        Decimal expectedResult = mock(Decimal.class);
        mockStaticCalls(() -> {
            when(DecimalCreator.createDecimal(value, 0)).thenReturn(expectedResult);
        });

        // When
        Decimal actualResult = Decimal.decimal(value);

        // Then
        assertThat(actualResult, equalTo(expectedResult));

        verifyStaticCalls(() -> {
            DecimalCreator.createDecimal(value, 0);
        });
    }

    @Test
    public void shouldCreateDecimalFromBigIntegerAndScale() {
        BigInteger unscaledValue = randomBigInteger();
        int scale = randomInt();

        Decimal expectedResult = mock(Decimal.class);
        mockStaticCalls(() -> {
            when(DecimalCreator.createDecimal(unscaledValue, scale)).thenReturn(expectedResult);
        });

        // When
        Decimal actualResult = Decimal.decimal(unscaledValue, scale);

        // Then
        assertThat(actualResult, equalTo(expectedResult));

        verifyStaticCalls(() -> {
            DecimalCreator.createDecimal(unscaledValue, scale);
        });
    }

    @Test
    public void shouldCreateDecimalFromBigDecimal() {
        BigDecimal value = randomBigDecimal();

        Decimal expectedResult = mock(Decimal.class);
        mockStaticCalls(() -> {
            when(DecimalParser.parseString(value.toPlainString())).thenReturn(expectedResult);
        });

        // When
        Decimal actualResult = Decimal.decimal(value);

        // Then
        assertThat(actualResult, equalTo(expectedResult));

        verifyStaticCalls(() -> {
            DecimalParser.parseString(value.toPlainString());
        });
    }

    @Test
    public void shouldCreateDecimalFromString() {
        String value = randomBigDecimal().toPlainString();

        Decimal expectedResult = mock(Decimal.class);
        mockStaticCalls(() -> {
            when(DecimalParser.parseString(value)).thenReturn(expectedResult);
        });

        // When
        Decimal actualResult = Decimal.decimal(value);

        // Then
        assertThat(actualResult, equalTo(expectedResult));

        verifyStaticCalls(() -> {
            DecimalParser.parseString(value);
        });
    }

    @Test
    public void shouldCreateDecimalFromString2() {
        String value = randomBigDecimal().toPlainString();

        Decimal expectedResult = mock(Decimal.class);
        mockStaticCalls(() -> {
            when(DecimalParser.parseString(value)).thenReturn(expectedResult);
        });

        // When
        Decimal actualResult = Decimal.d(value);

        // Then
        assertThat(actualResult, equalTo(expectedResult));

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
        LongDecimal longDecimal = randomLongDecimal();

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
        HugeDecimal hugeDecimal = randomHugeDecimal();

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
        LongDecimal longDecimal = randomLongDecimal();

        Decimal expectedResult = mock(Decimal.class);
        mockStaticCalls(() -> {
            when(DecimalNegator.negate(longDecimal)).thenReturn(expectedResult);
        });

        // When
        Decimal actualResult = longDecimal.negate();

        // Then
        assertThat(actualResult, equalTo(expectedResult));
        verifyStaticCalls(() -> {
            DecimalNegator.negate(longDecimal);
        });
    }

    @Test
    public void shouldNegateHugeDecimal() {
        HugeDecimal hugeDecimal = randomHugeDecimal();

        Decimal expectedResult = mock(Decimal.class);
        mockStaticCalls(() -> {
            when(DecimalNegator.negate(hugeDecimal)).thenReturn(expectedResult);
        });

        // When
        Decimal actualResult = hugeDecimal.negate();

        // Then
        assertThat(actualResult, equalTo(expectedResult));
        verifyStaticCalls(() -> {
            DecimalNegator.negate(hugeDecimal);
        });
    }

    @Test
    public void shouldAddDecimalToLongDecimal1() {
        LongDecimal longDecimal = randomLongDecimal();
        Decimal decimalToAdd = mock(Decimal.class);
        int scaleToUse = randomInt();
        RoundingMode roundingMode = randomRoundingMode();

        Decimal expectedResult = mock(Decimal.class);
        mockStaticCalls(() -> {
            when(DecimalAccumulator.add(longDecimal, decimalToAdd, scaleToUse, roundingMode)).thenReturn(expectedResult);
        });

        // When
        Decimal actualResult = longDecimal.plus(decimalToAdd, scaleToUse, roundingMode);

        // Then
        assertThat(actualResult, equalTo(expectedResult));
        verifyStaticCalls(() -> {
            DecimalAccumulator.add(longDecimal, decimalToAdd, scaleToUse, roundingMode);
        });
    }

    @Test
    public void shouldAddDecimalToHugeDecimal1() {
        HugeDecimal hugeDecimal = randomHugeDecimal();
        Decimal decimalToAdd = mock(Decimal.class);
        int scaleToUse = randomInt();
        RoundingMode roundingMode = randomRoundingMode();

        Decimal expectedResult = mock(Decimal.class);
        mockStaticCalls(() -> {
            when(DecimalAccumulator.add(hugeDecimal, decimalToAdd, scaleToUse, roundingMode)).thenReturn(expectedResult);
        });

        // When
        Decimal actualResult = hugeDecimal.plus(decimalToAdd, scaleToUse, roundingMode);

        // Then
        assertThat(actualResult, equalTo(expectedResult));
        verifyStaticCalls(() -> {
            DecimalAccumulator.add(hugeDecimal, decimalToAdd, scaleToUse, roundingMode);
        });
    }

    @Test
    public void shouldAddDecimalToLongDecimal2() {
        LongDecimal longDecimal = randomLongDecimal();
        Decimal decimalToAdd = mock(Decimal.class);
        int scaleToUse = randomInt();

        Decimal expectedResult = mock(Decimal.class);
        mockStaticCalls(() -> {
            when(DecimalAccumulator.add(longDecimal, decimalToAdd, scaleToUse, HALF_UP)).thenReturn(expectedResult);
        });

        // When
        Decimal actualResult = longDecimal.plus(decimalToAdd, scaleToUse);

        // Then
        assertThat(actualResult, equalTo(expectedResult));
        verifyStaticCalls(() -> {
            DecimalAccumulator.add(longDecimal, decimalToAdd, scaleToUse, HALF_UP);
        });
    }

    @Test
    public void shouldAddDecimalToHugeDecimal2() {
        HugeDecimal hugeDecimal = randomHugeDecimal();
        Decimal decimalToAdd = mock(Decimal.class);
        int scaleToUse = randomInt();

        Decimal expectedResult = mock(Decimal.class);
        mockStaticCalls(() -> {
            when(DecimalAccumulator.add(hugeDecimal, decimalToAdd, scaleToUse, HALF_UP)).thenReturn(expectedResult);
        });

        // When
        Decimal actualResult = hugeDecimal.plus(decimalToAdd, scaleToUse);

        // Then
        assertThat(actualResult, equalTo(expectedResult));
        verifyStaticCalls(() -> {
            DecimalAccumulator.add(hugeDecimal, decimalToAdd, scaleToUse, HALF_UP);
        });
    }

    @Test
    public void shouldAddDecimalToLongDecimal3() {
        LongDecimal longDecimal = randomLongDecimal();
        Decimal decimalToAdd = mock(Decimal.class);
        int otherScale = randomInt();

        Decimal expectedResult = mock(Decimal.class);
        mockStaticCalls(() -> {
            when(decimalToAdd.scale()).thenReturn(otherScale);
            when(DecimalAccumulator.add(longDecimal, decimalToAdd, max(longDecimal.scale(), otherScale), UNNECESSARY)).thenReturn(expectedResult);
        });

        // When
        Decimal actualResult = longDecimal.plus(decimalToAdd);

        // Then
        assertThat(actualResult, equalTo(expectedResult));
        verifyStaticCalls(() -> {
            DecimalAccumulator.add(longDecimal, decimalToAdd, max(longDecimal.scale(), otherScale), UNNECESSARY);
        });
    }

    @Test
    public void shouldAddDecimalToHugeDecimal3() {
        HugeDecimal hugeDecimal = randomHugeDecimal();
        Decimal decimalToAdd = mock(Decimal.class);
        int otherScale = randomInt();

        Decimal expectedResult = mock(Decimal.class);
        mockStaticCalls(() -> {
            when(decimalToAdd.scale()).thenReturn(otherScale);
            when(DecimalAccumulator.add(hugeDecimal, decimalToAdd, max(hugeDecimal.scale(), otherScale), UNNECESSARY)).thenReturn(expectedResult);
        });

        // When
        Decimal actualResult = hugeDecimal.plus(decimalToAdd);

        // Then
        assertThat(actualResult, equalTo(expectedResult));
        verifyStaticCalls(() -> {
            DecimalAccumulator.add(hugeDecimal, decimalToAdd, max(hugeDecimal.scale(), otherScale), UNNECESSARY);
        });
    }




    private void mockStaticCalls(Runnable task) {
        mockStatic(
                DecimalCreator.class,
                DecimalParser.class,
                DecimalEqualizer.class,
                DecimalPrinter.class,
                DecimalNegator.class,
                DecimalAccumulator.class
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
                DecimalNegator.class,
                DecimalAccumulator.class
        );
    }

    private LongDecimal randomLongDecimal() {
        return new LongDecimal(randomLong(), randomInt());
    }

    private HugeDecimal randomHugeDecimal() {
        return new HugeDecimal(randomBigInteger(), randomInt());
    }
}