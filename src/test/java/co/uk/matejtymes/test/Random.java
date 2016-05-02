package co.uk.matejtymes.test;

public class Random {

    public static int randomInt(int from, int to) {
        return (int) (Math.random() * (((long) to) - from + 1)) + from;
    }
}
