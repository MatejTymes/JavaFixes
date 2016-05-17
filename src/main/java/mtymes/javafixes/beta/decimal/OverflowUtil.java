package mtymes.javafixes.beta.decimal;

public class OverflowUtil {

    public static boolean willNegationOverflow(long value) {
        return value == Long.MIN_VALUE;
    }

    public static boolean didOverflowOnAddition(long result, long valueA, long valueB) {
        // HD 2-12 Overflow iff both arguments have the opposite sign of the result
        return ((valueA ^ result) & (valueB ^ result)) < 0;
    }

    // todo: test this
    public static boolean didOverflowOnAddition(int result, int valueA, int valueB) {
        // HD 2-12 Overflow iff both arguments have the opposite sign of the result
        return ((valueA ^ result) & (valueB ^ result)) < 0;
    }

    public static boolean didOverflowOnMultiplication(long result, long valueA, long valueB) {
        long absA = Math.abs(valueA);
        long absB = Math.abs(valueB);

        if (((absA | absB) >>> 31 != 0)) {
            // Some bits greater than 2^31 that might cause overflow
            // Check the result using the divide operator
            // and check for the special case of Long.MIN_VALUE * -1
            if (((valueB != 0) && (result / valueB != valueA)) || (valueA == Long.MIN_VALUE && valueB == -1)) {
                return true;
            }
        }
        return false;
    }
}
