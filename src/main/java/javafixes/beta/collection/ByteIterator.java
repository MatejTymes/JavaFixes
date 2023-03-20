package javafixes.beta.collection;

import java.util.Iterator;

// todo: mtymes - add javadoc
public interface ByteIterator extends Iterator<Byte> {

    @Override
    default Byte next() {
        return readNext();
    }

    byte readNext();

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
