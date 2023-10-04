package javafixes.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;

// todo: mtymes - add javadoc

/**
 * A byte related enhancement of Iterator interface. In addition to the common functionality
 * it adds ability to retrieve the bytes without the need for boxing/conversion into Byte class.
 * as well as provides functionality for batch retrieval of bytes.

 * @author mtymes
 */
public interface ByteIterator extends Iterator<Byte> {

    @Override
    default Byte next() {
        return readNext();
    }

    /**
     * Returns the next {@code byte} in the iteration
     * @return the next {@code byte} in the iteration
     * @throws NoSuchElementException if the iteration has no more bytes
     */
    byte readNext() throws NoSuchElementException;

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
