package javafixes.math;

public class OverflowUtil {

    public static boolean didOverflowOnLongAddition(long result, long valueA, long valueB) {
        // HD 2-12 Overflow iff both arguments have the opposite sign of the result
        return ((valueA ^ result) & (valueB ^ result)) < 0;
    }
}
