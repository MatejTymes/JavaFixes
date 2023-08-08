package javafixes.math.exception;

import javafixes.math.Decimal;

public class UnsupportedDecimalTypeException extends RuntimeException {

    public UnsupportedDecimalTypeException(Decimal decimal) {
        super("Unsupported Decimal type: " + decimal.getClass().getSimpleName());
    }
}
