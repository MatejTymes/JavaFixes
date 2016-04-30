package co.uk.matejtymes.tdp;

public class UnsupportedDecimalTypeException extends RuntimeException {

    public UnsupportedDecimalTypeException(Decimal decimal) {
        super("Unsupported Decimal type: " + decimal.getClass().getSimpleName());
    }
}
