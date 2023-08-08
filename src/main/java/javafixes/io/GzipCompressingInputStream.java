package javafixes.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPOutputStream;

public class GzipCompressingInputStream extends ConvertedInputStream {

    public GzipCompressingInputStream(
            InputStream sourceInputStream
    ) throws IOException {
        super(sourceInputStream, GZIPOutputStream::new);
    }

    public GzipCompressingInputStream(
            InputStream sourceInputStream,
            int outputBufferSize
    ) throws IOException {
        super(sourceInputStream, out -> new GZIPOutputStream(out, outputBufferSize));
    }
}
