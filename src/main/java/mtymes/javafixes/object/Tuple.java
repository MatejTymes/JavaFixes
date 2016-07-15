package mtymes.javafixes.object;

import java.util.Objects;
import java.util.function.BiFunction;

public class Tuple<A, B> extends DataObject {

    public final A a;
    public final B b;

    public Tuple(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public static <A, B> Tuple<A, B> tuple(A a, B b) {
        return new Tuple<>(a, b);
    }

    public A getA() {
        return a;
    }

    public B getB() {
        return b;
    }

    public A a() {
        return a;
    }

    public B b() {
        return b;
    }

    public <C> C map(BiFunction<? super A, ? super B, ? extends C> mapper) {
        Objects.requireNonNull(mapper);
        return mapper.apply(a, b);
    }
}
