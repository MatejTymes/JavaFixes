package co.uk.matejtymes.tdp;

import co.uk.matejtymes.tdp.Decimal.HugeDecimal;
import co.uk.matejtymes.tdp.Decimal.LongDecimal;

import java.math.BigInteger;
import java.util.Arrays;

import static java.lang.Math.max;

// todo: test it
class DecimalPrinter {

    // todo: maybe add scientific notation as well

    static String toPlainString(Decimal d, int minScaleToUse) {
        StringBuilder sb = new StringBuilder();

        boolean isNegative;
        if (d instanceof LongDecimal) {
            long unscaledValue = ((LongDecimal) d).unscaledValue;
            isNegative = unscaledValue < 0;
            sb.append(Long.toString(unscaledValue));
        } else if (d instanceof HugeDecimal) {
            BigInteger unscaledValue = ((HugeDecimal) d).unscaledValue;
            isNegative = unscaledValue.signum() < 0;
            sb.append(unscaledValue.toString());
        } else {
            throw new UnsupportedDecimalTypeException(d);
        }

        int currentScale = d.scale();
        int scaleToUse = max(currentScale, max(minScaleToUse, 0));
        if (currentScale < scaleToUse) {
            sb.append(arrayOfZeroChars(scaleToUse - currentScale));
        }

        if (scaleToUse > 0) {
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

    private static char[] arrayOfZeroChars(int size) {
        char[] zeros = new char[size];
        Arrays.fill(zeros, '0');
        return zeros;
    }
}
