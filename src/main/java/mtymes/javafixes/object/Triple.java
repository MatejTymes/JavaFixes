package mtymes.javafixes.object;

public class Triple<A, B, C> extends DataObject {

    public final A a;
    public final B b;
    public final C c;

    public Triple(A a, B b, C c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public static <A, B, C> Triple<A, B, C> triple(A a, B b, C c) {
        return new Triple<>(a, b, c);
    }

    public A getA() {
        return a;
    }

    public B getB() {
        return b;
    }

    public C getC() {
        return c;
    }

    public A a() {
        return a;
    }

    public B b() {
        return b;
    }

    public C c() {
        return c;
    }
}
