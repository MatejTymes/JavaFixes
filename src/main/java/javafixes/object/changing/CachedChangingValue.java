package javafixes.object.changing;

public interface CachedChangingValue<T> extends ChangingValue<T> {

    void forceNewValueReCaching();
}
