package javafixes.io;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static java.util.UUID.randomUUID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ByteCollectingOutputStreamTest {

    @Test
    public void shouldCollectBytes() throws IOException {
        String text = "This is a random string " + randomUUID() + " with some random " + randomUUID() + " values in it";
        String charsetName = "UTF-8";

        ByteCollectingOutputStream stream = new ByteCollectingOutputStream(5);
        stream.write(text.getBytes(charsetName));
        stream.close();

        verifyCollectedData(stream, text, charsetName);


        stream = new ByteCollectingOutputStream(5);
        for (byte b : text.getBytes(charsetName)) {
            stream.write(b);
        }
        stream.close();

        verifyCollectedData(stream, text, charsetName);
    }

    private void verifyCollectedData(ByteCollectingOutputStream stream, String text, String charsetName) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        stream.copyTo(os);

        assertThat(new String(os.toByteArray(), charsetName), equalTo(text));
        assertThat(IOUtils.toString(stream.toInputStream(), charsetName), equalTo(text));
    }
}