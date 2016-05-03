package mtymes.javafixes.beta.decimal;

public class UnsupportedDecimalTypeException extends RuntimeException {

    public UnsupportedDecimalTypeException(Decimal decimal) {
        super("Unsupported Decimal type: " + decimal.getClass().getSimpleName());
    }
}
