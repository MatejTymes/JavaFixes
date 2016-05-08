package mtymes.javafixes.beta.decimal;

import java.math.RoundingMode;

import static java.lang.Math.abs;
import static java.lang.String.format;
import static java.math.RoundingMode.*;

// todo: test it
class DecimalRounder {

    // todo: division currently doesn't provide flag : hasAdditionalRemainder - which is quite importantly wrong

    // todo: start using this
    static int roundingCorrection(int signum, long valueToRound, int roundingDigit, boolean hasAdditionalRemainder, RoundingMode roundingMode) {
        return roundingCorrection(signum, ((int) valueToRound & 1) == 1, abs(roundingDigit), hasAdditionalRemainder, roundingMode);
    }

    // todo: only HALF_EVEN uses the isDigitToRoundOdd - so maybe don't even bother calculating it for the others

    private static int roundingCorrection(int signum, boolean isDigitToRoundOdd, int roundingDigit, boolean hasAdditionalRemainder, RoundingMode roundingMode) {
        if (signum < -1 || signum > 1) {
            throw new IllegalArgumentException(format("Invalid signum (%d). Should be between -1 and 1", signum));
        } else if (roundingDigit < 0 || roundingDigit > 9) {
            throw new IllegalArgumentException(format("Invalid rounding digit (%d). Should be between 0 and 9", roundingDigit));
        }

        int roundingCorrection = 0;

        if (roundingDigit != 0 || hasAdditionalRemainder) {
            if (roundingMode == HALF_UP) {
                if (roundingDigit >= 5) {
                    roundingCorrection = (signum == -1) ? -1 : 1;
                }
            } else if (roundingMode == UP) {
                if (roundingDigit > 0 || hasAdditionalRemainder) {
                    roundingCorrection = (signum == -1) ? -1 : 1;
                }
            } else if (roundingMode == DOWN) {
                // do nothing
            } else if (roundingMode == CEILING) {
                if ((roundingDigit > 0 || hasAdditionalRemainder) && signum >= 0) {
                    roundingCorrection = 1;
                }
            } else if (roundingMode == FLOOR) {
                if ((roundingDigit > 0 || hasAdditionalRemainder) && signum == -1) {
                    roundingCorrection = -1;
                }
            } else if (roundingMode == HALF_DOWN) {
                if (roundingDigit > 5 || (roundingDigit == 5 && hasAdditionalRemainder)) {
                    roundingCorrection = (signum == -1) ? -1 : 1;
                }
            } else if (roundingMode == HALF_EVEN) {
                if (roundingDigit > 5 || (roundingDigit == 5 && (hasAdditionalRemainder || isDigitToRoundOdd))) {
                    roundingCorrection = (signum == -1) ? -1 : 1;
                }
            } else if (roundingMode == UNNECESSARY) {
                throw new ArithmeticException("Rounding necessary");
            }
        }

        return roundingCorrection;
    }
}
