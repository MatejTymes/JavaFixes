package mtymes.javafixes.beta.decimal;

import mtymes.javafixes.beta.decimal.Decimal.HugeDecimal;
import mtymes.javafixes.beta.decimal.Decimal.LongDecimal;

import java.util.Arrays;

import static java.lang.Math.max;

// todo: test it
class DecimalPrinter {

    static String toPlainString(Decimal d, int minScaleToUse) {
        StringBuilder sb = new StringBuilder();

        if (d instanceof LongDecimal) {
            sb.append(
                    Long.toString(((LongDecimal) d).unscaledValue)
            );
        } else if (d instanceof HugeDecimal) {
            sb.append(
                    ((HugeDecimal) d).unscaledValue.toString()
            );
        } else {
            throw new UnsupportedDecimalTypeException(d);
        }

        int currentScale = d.scale();
        int scaleToUse = max(currentScale, max(minScaleToUse, 0));
        if (currentScale < scaleToUse) {
            sb.append(arrayOfZeroChars(scaleToUse - currentScale));
        }

        if (scaleToUse > 0) {
            boolean isNegative = d.signum() < 0;
            int prefixZerosOffset = isNegative ? 1 : 0;
            int firstDigitScale = scaleToUse - (sb.length() - prefixZerosOffset) + 1;
            if (firstDigitScale > 0) {
                sb.insert(prefixZerosOffset, arrayOfZeroChars(firstDigitScale));
            }
            int index = sb.length() - scaleToUse;
            sb.insert(index, '.');
        }

        return sb.toString();
    }

    static String toScientificNotation(Decimal d) {
        StringBuilder sb = new StringBuilder();

        if (d instanceof LongDecimal) {
            sb.append(
                    Long.toString(((LongDecimal) d).unscaledValue)
            );
        } else if (d instanceof HugeDecimal) {
            sb.append(
                    ((HugeDecimal) d).unscaledValue.toString()
            );
        } else {
            throw new UnsupportedDecimalTypeException(d);
        }

        int signOffset = (d.signum() < 0) ? 1 : 0;
        int exponent = (sb.length() - signOffset) - d.scale() - 1;

        if (sb.length() > 1 + signOffset) {
            sb.insert(1 + signOffset, '.');
        }
        sb.append("e").append(exponent);

        return sb.toString();
    }

    private static char[] arrayOfZeroChars(int size) {
        char[] zeros = new char[size];
        Arrays.fill(zeros, '0');
        return zeros;
    }
}
