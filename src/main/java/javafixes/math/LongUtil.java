package javafixes.math;

class LongUtil {

    static boolean canFitIntoInt(long value) {
        return value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE;
    }
}
