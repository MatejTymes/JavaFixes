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
import java.util.List;

import static java.lang.Math.max;
import static java.math.RoundingMode.HALF_UP;
import static java.math.RoundingMode.UNNECESSARY;
import static java.util.Arrays.asList;
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
        DecimalAccumulator.class,
        DecimalMultiplier.class,
        DecimalDivider.class
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
    public void shouldConvertDecimalToBigDecimal() {
        for (Decimal decimal : allDecimalTypes()) {

            BigDecimal expectedBigDecimal = randomBigDecimal();
            mockStaticCalls(() -> {
                when(DecimalPrinter.toPlainString(decimal, 0)).thenReturn(expectedBigDecimal.toPlainString());
            });

            // When
            BigDecimal actualBigDecimal = decimal.bigDecimalValue();

            // Then
            assertThat(actualBigDecimal, equalTo(expectedBigDecimal));
            verifyStaticCalls(() -> {
                DecimalPrinter.toPlainString(decimal, 0);
            });
        }
    }

    @Test
    public void shouldNegateDecimal() {
        for (Decimal decimal : allDecimalTypes()) {

            Decimal expectedResult = mock(Decimal.class);
            mockStaticCalls(() -> {
                when(DecimalNegator.negate(decimal)).thenReturn(expectedResult);
            });

            // When
            Decimal actualResult = decimal.negate();

            // Then
            assertThat(actualResult, equalTo(expectedResult));
            verifyStaticCalls(() -> {
                DecimalNegator.negate(decimal);
            });
        }
    }

    @Test
    public void shouldAddDecimalToAnotherDecimal1() {
        for (Decimal decimal : allDecimalTypes()) {

            Decimal decimalToAdd = mock(Decimal.class);
            int scaleToUse = randomInt();
            RoundingMode roundingMode = randomRoundingMode();

            Decimal expectedResult = mock(Decimal.class);
            mockStaticCalls(() -> {
                when(DecimalAccumulator.add(decimal, decimalToAdd, scaleToUse, roundingMode)).thenReturn(expectedResult);
            });

            // When
            Decimal actualResult = decimal.plus(decimalToAdd, scaleToUse, roundingMode);

            // Then
            assertThat(actualResult, equalTo(expectedResult));
            verifyStaticCalls(() -> {
                DecimalAccumulator.add(decimal, decimalToAdd, scaleToUse, roundingMode);
            });
        }
    }

    @Test
    public void shouldAddDecimalToAnotherDecimal2() {
        for (Decimal decimal : allDecimalTypes()) {

            Decimal decimalToAdd = mock(Decimal.class);
            int scaleToUse = randomInt();

            Decimal expectedResult = mock(Decimal.class);
            mockStaticCalls(() -> {
                when(DecimalAccumulator.add(decimal, decimalToAdd, scaleToUse, HALF_UP)).thenReturn(expectedResult);
            });

            // When
            Decimal actualResult = decimal.plus(decimalToAdd, scaleToUse);

            // Then
            assertThat(actualResult, equalTo(expectedResult));
            verifyStaticCalls(() -> {
                DecimalAccumulator.add(decimal, decimalToAdd, scaleToUse, HALF_UP);
            });
        }
    }

    @Test
    public void shouldAddDecimalToAnotherDecimal3() {
        for (Decimal decimal : allDecimalTypes()) {

            Decimal decimalToAdd = mock(Decimal.class);
            int otherScale = randomInt();

            Decimal expectedResult = mock(Decimal.class);
            mockStaticCalls(() -> {
                when(decimalToAdd.scale()).thenReturn(otherScale);
                when(DecimalAccumulator.add(decimal, decimalToAdd, max(decimal.scale(), otherScale), UNNECESSARY)).thenReturn(expectedResult);
            });

            // When
            Decimal actualResult = decimal.plus(decimalToAdd);

            // Then
            assertThat(actualResult, equalTo(expectedResult));
            verifyStaticCalls(() -> {
                DecimalAccumulator.add(decimal, decimalToAdd, max(decimal.scale(), otherScale), UNNECESSARY);
            });
        }
    }

    @Test
    public void shouldSubtractDecimalFromAnotherDecimal1() {
        for (Decimal decimal : allDecimalTypes()) {

            Decimal decimalToSubtract = mock(Decimal.class);
            int scaleToUse = randomInt();
            RoundingMode roundingMode = randomRoundingMode();

            Decimal expectedResult = mock(Decimal.class);
            mockStaticCalls(() -> {
                when(DecimalAccumulator.subtract(decimal, decimalToSubtract, scaleToUse, roundingMode)).thenReturn(expectedResult);
            });

            // When
            Decimal actualResult = decimal.minus(decimalToSubtract, scaleToUse, roundingMode);

            // Then
            assertThat(actualResult, equalTo(expectedResult));
            verifyStaticCalls(() -> {
                DecimalAccumulator.subtract(decimal, decimalToSubtract, scaleToUse, roundingMode);
            });
        }
    }

    @Test
    public void shouldSubtractDecimalFromAnotherDecimal2() {
        for (Decimal decimal : allDecimalTypes()) {

            Decimal decimalToSubtract = mock(Decimal.class);
            int scaleToUse = randomInt();

            Decimal expectedResult = mock(Decimal.class);
            mockStaticCalls(() -> {
                when(DecimalAccumulator.subtract(decimal, decimalToSubtract, scaleToUse, HALF_UP)).thenReturn(expectedResult);
            });

            // When
            Decimal actualResult = decimal.minus(decimalToSubtract, scaleToUse);

            // Then
            assertThat(actualResult, equalTo(expectedResult));
            verifyStaticCalls(() -> {
                DecimalAccumulator.subtract(decimal, decimalToSubtract, scaleToUse, HALF_UP);
            });
        }
    }

    @Test
    public void shouldSubtractDecimalFromAnotherDecimal3() {
        for (Decimal decimal : allDecimalTypes()) {

            Decimal decimalToSubtract = mock(Decimal.class);
            int otherScale = randomInt();

            Decimal expectedResult = mock(Decimal.class);
            mockStaticCalls(() -> {
                when(decimalToSubtract.scale()).thenReturn(otherScale);
                when(DecimalAccumulator.subtract(decimal, decimalToSubtract, max(decimal.scale(), otherScale), UNNECESSARY)).thenReturn(expectedResult);
            });

            // When
            Decimal actualResult = decimal.minus(decimalToSubtract);

            // Then
            assertThat(actualResult, equalTo(expectedResult));
            verifyStaticCalls(() -> {
                DecimalAccumulator.subtract(decimal, decimalToSubtract, max(decimal.scale(), otherScale), UNNECESSARY);
            });
        }
    }

    @Test
    public void shouldMultiplyDecimalWithAnotherDecimal1() {
        for (Decimal decimal : allDecimalTypes()) {

            Decimal decimalToMultiplyWith = mock(Decimal.class);
            int scaleToUse = randomInt();
            RoundingMode roundingMode = randomRoundingMode();

            Decimal expectedResult = mock(Decimal.class);
            mockStaticCalls(() -> {
                when(DecimalMultiplier.multiply(decimal, decimalToMultiplyWith, scaleToUse, roundingMode)).thenReturn(expectedResult);
            });

            // When
            Decimal actualResult = decimal.times(decimalToMultiplyWith, scaleToUse, roundingMode);

            // Then
            assertThat(actualResult, equalTo(expectedResult));
            verifyStaticCalls(() -> {
                DecimalMultiplier.multiply(decimal, decimalToMultiplyWith, scaleToUse, roundingMode);
            });
        }
    }

    @Test
    public void shouldMultiplyDecimalWithAnotherDecimal2() {
        for (Decimal decimal : allDecimalTypes()) {

            Decimal decimalToMultiplyWith = mock(Decimal.class);
            int scaleToUse = randomInt();

            Decimal expectedResult = mock(Decimal.class);
            mockStaticCalls(() -> {
                when(DecimalMultiplier.multiply(decimal, decimalToMultiplyWith, scaleToUse, HALF_UP)).thenReturn(expectedResult);
            });

            // When
            Decimal actualResult = decimal.times(decimalToMultiplyWith, scaleToUse);

            // Then
            assertThat(actualResult, equalTo(expectedResult));
            verifyStaticCalls(() -> {
                DecimalMultiplier.multiply(decimal, decimalToMultiplyWith, scaleToUse, HALF_UP);
            });
        }
    }

    @Test
    public void shouldMultiplyDecimalWithAnotherDecimal3() {
        for (Decimal decimal : allDecimalTypes()) {

            Decimal decimalToMultiplyWith = mock(Decimal.class);
            int otherScale = randomInt();

            Decimal expectedResult = mock(Decimal.class);
            mockStaticCalls(() -> {
                when(decimalToMultiplyWith.scale()).thenReturn(otherScale);
                when(DecimalMultiplier.multiply(decimal, decimalToMultiplyWith, decimal.scale() + otherScale, UNNECESSARY)).thenReturn(expectedResult);
            });

            // When
            Decimal actualResult = decimal.times(decimalToMultiplyWith);

            // Then
            assertThat(actualResult, equalTo(expectedResult));
            verifyStaticCalls(() -> {
                DecimalMultiplier.multiply(decimal, decimalToMultiplyWith, decimal.scale() + otherScale, UNNECESSARY);
            });
        }
    }

    @Test
    public void shouldMultiplyDecimalWithAnotherDecimal1b() {
        for (Decimal decimal : allDecimalTypes()) {

            Decimal decimalToMultiplyWith = mock(Decimal.class);
            int scaleToUse = randomInt();
            RoundingMode roundingMode = randomRoundingMode();

            Decimal expectedResult = mock(Decimal.class);
            mockStaticCalls(() -> {
                when(DecimalMultiplier.multiply(decimal, decimalToMultiplyWith, scaleToUse, roundingMode)).thenReturn(expectedResult);
            });

            // When
            Decimal actualResult = decimal.multiply(decimalToMultiplyWith, scaleToUse, roundingMode);

            // Then
            assertThat(actualResult, equalTo(expectedResult));
            verifyStaticCalls(() -> {
                DecimalMultiplier.multiply(decimal, decimalToMultiplyWith, scaleToUse, roundingMode);
            });
        }
    }

    @Test
    public void shouldMultiplyDecimalWithAnotherDecimal2b() {
        for (Decimal decimal : allDecimalTypes()) {

            Decimal decimalToMultiplyWith = mock(Decimal.class);
            int scaleToUse = randomInt();

            Decimal expectedResult = mock(Decimal.class);
            mockStaticCalls(() -> {
                when(DecimalMultiplier.multiply(decimal, decimalToMultiplyWith, scaleToUse, HALF_UP)).thenReturn(expectedResult);
            });

            // When
            Decimal actualResult = decimal.multiply(decimalToMultiplyWith, scaleToUse);

            // Then
            assertThat(actualResult, equalTo(expectedResult));
            verifyStaticCalls(() -> {
                DecimalMultiplier.multiply(decimal, decimalToMultiplyWith, scaleToUse, HALF_UP);
            });
        }
    }

    @Test
    public void shouldMultiplyDecimalWithAnotherDecimal3b() {
        for (Decimal decimal : allDecimalTypes()) {

            Decimal decimalToMultiplyWith = mock(Decimal.class);
            int otherScale = randomInt();

            Decimal expectedResult = mock(Decimal.class);
            mockStaticCalls(() -> {
                when(decimalToMultiplyWith.scale()).thenReturn(otherScale);
                when(DecimalMultiplier.multiply(decimal, decimalToMultiplyWith, decimal.scale() + otherScale, UNNECESSARY)).thenReturn(expectedResult);
            });

            // When
            Decimal actualResult = decimal.multiply(decimalToMultiplyWith);

            // Then
            assertThat(actualResult, equalTo(expectedResult));
            verifyStaticCalls(() -> {
                DecimalMultiplier.multiply(decimal, decimalToMultiplyWith, decimal.scale() + otherScale, UNNECESSARY);
            });
        }
    }

    @Test
    public void shouldDivideDecimalByAnotherDecimal1() {
        for (Decimal decimal : allDecimalTypes()) {

            Decimal decimalToDivideBy = mock(Decimal.class);
            int scaleToUse = randomInt();
            RoundingMode roundingMode = randomRoundingMode();

            Decimal expectedResult = mock(Decimal.class);
            mockStaticCalls(() -> {
                when(DecimalDivider.divide(decimal, decimalToDivideBy, scaleToUse, roundingMode)).thenReturn(expectedResult);
            });

            // When
            Decimal actualResult = decimal.div(decimalToDivideBy, scaleToUse, roundingMode);

            // Then
            assertThat(actualResult, equalTo(expectedResult));
            verifyStaticCalls(() -> {
                DecimalDivider.divide(decimal, decimalToDivideBy, scaleToUse, roundingMode);
            });
        }
    }

    @Test
    public void shouldDivideDecimalByAnotherDecimal2() {
        for (Decimal decimal : allDecimalTypes()) {

            Decimal decimalToDivideBy = mock(Decimal.class);
            int scaleToUse = randomInt();

            Decimal expectedResult = mock(Decimal.class);
            mockStaticCalls(() -> {
                when(DecimalDivider.divide(decimal, decimalToDivideBy, scaleToUse, HALF_UP)).thenReturn(expectedResult);
            });

            // When
            Decimal actualResult = decimal.div(decimalToDivideBy, scaleToUse);

            // Then
            assertThat(actualResult, equalTo(expectedResult));
            verifyStaticCalls(() -> {
                DecimalDivider.divide(decimal, decimalToDivideBy, scaleToUse, HALF_UP);
            });
        }
    }

    @Test
    public void shouldDivideDecimalByAnotherDecimal3() {
        for (Decimal decimal : allDecimalTypes()) {

            Decimal decimalToDivideBy = mock(Decimal.class);
            int otherScale = randomInt();

            Decimal expectedResult = mock(Decimal.class);
            mockStaticCalls(() -> {
                when(decimalToDivideBy.scale()).thenReturn(otherScale);
                when(DecimalDivider.divide(decimal, decimalToDivideBy, max(18, max(decimal.scale(), otherScale)), HALF_UP)).thenReturn(expectedResult);
            });

            // When
            Decimal actualResult = decimal.div(decimalToDivideBy);

            // Then
            assertThat(actualResult, equalTo(expectedResult));
            verifyStaticCalls(() -> {
                DecimalDivider.divide(decimal, decimalToDivideBy, max(18, max(decimal.scale(), otherScale)), HALF_UP);
            });
        }
    }









    private void mockStaticCalls(Runnable task) {
        mockStatic(
                DecimalCreator.class,
                DecimalParser.class,
                DecimalEqualizer.class,
                DecimalPrinter.class,
                DecimalNegator.class,
                DecimalAccumulator.class,
                DecimalMultiplier.class,
                DecimalDivider.class
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
                DecimalAccumulator.class,
                DecimalMultiplier.class,
                DecimalDivider.class
        );
    }

    private List<Decimal> allDecimalTypes() {
        return asList(randomLongDecimal(), randomHugeDecimal());
    }

    private LongDecimal randomLongDecimal() {
        return new LongDecimal(randomLong(), randomInt());
    }

    private HugeDecimal randomHugeDecimal() {
        return new HugeDecimal(randomBigInteger(), randomInt());
    }
}