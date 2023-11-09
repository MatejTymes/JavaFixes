package javafixes.collection;

public interface ByteIterable extends Iterable<Byte> {

    @Override
    ByteIterator iterator();
}
