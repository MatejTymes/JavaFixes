package javafixes.object;

// todo: javadoc
public interface Value<T> {

    T value();

    default T getValue() {
        return value();
    }
}
