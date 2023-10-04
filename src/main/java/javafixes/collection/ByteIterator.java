package javafixes.collection;

import java.util.Iterator;

// todo: mtymes - add javadoc

/**
 * A byte related enhancement of Iterator interface that allows to retrieve the bytes
 * without the need for boxing/conversion into Byte class.
 * It also provides functionality for batch retrieval of bytes.

 * @author mtymes
 */
public interface ByteIterator extends Iterator<Byte> {

    @Override
    default Byte next() {
        return readNext();
    }

    byte readNext();

    default int readNext(byte[] bytes) {
        return readNext(bytes, 0, bytes.length);
    }

    default int readNext(byte[] bytes, int offset, int length) {
        if (length == 0) {
            return hasNext() ? 0 : -1;
        }

        int bytesAdded = 0;
        int currentOffset = offset;
        int remainingLength = length;
        while (remainingLength > 0 && hasNext()) {
            bytes[currentOffset] = readNext();

            bytesAdded++;
            currentOffset++;
            remainingLength--;
        }

        return (bytesAdded == 0) ? -1 : bytesAdded;
    }
}
