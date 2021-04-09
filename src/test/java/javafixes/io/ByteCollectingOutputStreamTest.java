package javafixes.io;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static java.util.UUID.randomUUID;
import static javafixes.test.Random.randomInt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ByteCollectingOutputStreamTest {

    @Test
    public void shouldCollectBytes() throws IOException {
        String text = "This is a random string " + randomUUID() + " with some random " + randomUUID() + " values in it";
        String charsetName = "UTF-8";

        ByteCollectingOutputStream stream = new ByteCollectingOutputStream(randomInt(1, 20));
        stream.write(text.getBytes(charsetName));
        stream.close();
        verifyCollectedData(stream, text, charsetName);


        stream = new ByteCollectingOutputStream(randomInt(1, 20));
        int expectedSize = 0;
        for (byte b : text.getBytes(charsetName)) {
            assertThat(stream.getSize(), equalTo(expectedSize));
            stream.write(b);
            expectedSize += 1;
        }
        stream.close();
        verifyCollectedData(stream, text, charsetName);


        stream = new ByteCollectingOutputStream(randomInt(1, 20));
        expectedSize = 0;
        byte[] bytes = text.getBytes(charsetName);
        int readFromIndex = 0;
        while (readFromIndex < bytes.length){
            assertThat(stream.getSize(), equalTo(expectedSize));
            int countOfBytes = randomInt(1, bytes.length - readFromIndex);
            stream.write(bytes, readFromIndex, countOfBytes);
            readFromIndex += countOfBytes;
            expectedSize += countOfBytes;
        }
        stream.close();
        verifyCollectedData(stream, text, charsetName);
    }

//    @Test
//    @Ignore
//    public void performanceCheck() throws Exception{
//        ByteCollectingOutputStream stream = new ByteCollectingOutputStream(1024);
////        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//
//        byte[] bytes = new byte[1024];
//        SecureRandom random = new SecureRandom();
//        random.nextBytes(bytes);
//
//        long startTime = System.currentTimeMillis();
//        for (int i = 0; i < 1024 * 128; i++) {
//            stream.write(bytes, 0, bytes.length);;
//        }
//        long duration = System.currentTimeMillis() - startTime;
//
//        System.out.println("- duration = " + Duration.ofMillis(duration));
//
//        stream.close();
//    }


    private void verifyCollectedData(ByteCollectingOutputStream stream, String text, String charsetName) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        stream.writeTo(os);

        assertThat(stream.getSize(), equalTo(text.getBytes(charsetName).length));
        assertThat(new String(os.toByteArray(), charsetName), equalTo(text));
        assertThat(IOUtils.toString(stream.toInputStream(), charsetName), equalTo(text));
        assertThat(new String(stream.toByteArray(), charsetName), equalTo(text));
    }
}