package javafixes.common;

public class Asserts {

    public static void assertNotNull(Object value, String argumentName) {
        if (value == null) {
            throw new IllegalArgumentException("'" + argumentName + "' can't be null");
        }
    }

    public static void assertNotNull(Object value, String argumentName, String objectType) {
        if (value == null) {
            throw new IllegalArgumentException("'" + argumentName + "' of a " + objectType + " can't be null");
        }
    }
}
