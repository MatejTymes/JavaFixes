package playground.chv;

public class ByteQueueSample {

    public void sample() {
//        ByteQueue queue = new ByteQueue(); // or new ByteQueue(pageSize); - default page size is 4kb (each time a page is filled a new one is created)
//
//        // storing data
//
//        byte singleByte = ... ;
//        queue.addNext(singleByte);
//
//        byte[] byteArray = ... ;
//        queue.addNext(byteArray);
//
//        byte[] byteArray = ... ;
//        int offset = ... ;
//        int length = ... ;
//        queue.addNext(byteArray, offset, length);
//
//
//        // info about data
//
//        int size = queue.size();
//        boolean isEmpty = queue.isEmpty();
//        boolean hasNext = queue.hasNext();
//
//        // polling data - removes from collection
//
//        // streams through all bytes and removes them from Queue as they are being read
//        ByteIterator pollingIterator = queue.pollingIterator();
//        while(pollingIterator.hasNext()) {
//            byte singleByte = pollingIterator.readNext();
//
//            // or
//            byte[] byteArray =  ... ;
//            int removedByteCount = pollingIterator.readNext(byteArray);
//
//            // or
//            byte[] byteArray =  ... ;
//            int offset = ... ;
//            int length = ... ;
//            int removedByteCount = pollingIterator.readNext(byteArray);
//        }
//
//        byte readByte = queue.pollNext();
//
//        byte[] byteArray = ... ;
//        int removedByteCount = queue.pollNext(byteArray);
//
//        byte[] byteArray = ... ;
//        int offset = ... ;
//        int length = ... ;
//        int removedByteCount = queue.pollNext(byteArray, offset, length);
//
//
//        // peeking at data - doesn't remove from collection
//
//        // allows you to stream through all bytes without removing them from queue
//        ByteIterator peekingIterator = queue.peekingIterator(); // or queue.iterator() is peeking as well
//        while(peekingIterator.hasNext()) {
//            byte singleByte = peekingIterator.readNext();
//
//            // or
//            byte[] byteArray =  ... ;
//            int readBytesCount = peekingIterator.readNext(byteArray);
//
//            // or
//            byte[] byteArray =  ... ;
//            int offset = ... ;
//            int length = ... ;
//            int readBytesCount = peekingIterator.readNext(byteArray);
//        }
//
//        // these operations always look at the first byte/s - even on subsequent calls
//
//        byte firstByteInQueue = queue.peekAtNext();
//
//        byte[] firstBytesInQueue = ... ;
//        int readBytesCount = queue.peekAtNext(firstBytesInQueue);
//
//        byte[] firstBytesInQueue = ... ;
//        int offset = ... ;
//        int length = ... ;
//        int readBytesCount = queue.peekAtNext(firstBytesInQueue, offset, length);
//
//        // getting back a byte array (use only when streaming trough data is not possible as it creates additional byte array)
//
//        byte[] allReadBytes = queue.peekAtAllBytes();
//
//        byte[] allRemovedBytes = queue.pollAllBytes();
    }
}
