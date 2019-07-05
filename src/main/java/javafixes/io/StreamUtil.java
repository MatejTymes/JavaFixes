package javafixes.io;

import java.io.*;
import java.util.UUID;

import static java.io.File.createTempFile;

// todo: test this
public class StreamUtil {

    public static InputStream toTempFileInputStream(InputStream inputStream) throws IOException {
        File file = writeIntoTempFile(inputStream);
        try {
            return new DeleteOnCloseFileInputStream(file);
        } catch (IOException e) {
            file.delete();
            throw e;
        }
    }

    public static File writeIntoTempFile(InputStream inputStream) throws IOException {
        try {
            File contentFile = createTempFile("is", UUID.randomUUID().toString());

            try (FileOutputStream fos = new FileOutputStream(contentFile)) {

                byte[] buffer = new byte[1024 * 4];
                int n;
                while ((n = inputStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, n);
                }

            } catch (IOException e) {
                contentFile.delete();
                throw e;
            }

            return contentFile;
        } finally {
            closeQuietly(inputStream);
        }
    }

    private static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    private static class DeleteOnCloseFileInputStream extends InputStream implements AutoCloseable {

        private final File file;
        private final FileInputStream wrappedStream;

        public DeleteOnCloseFileInputStream(File file) throws IOException {
            this.file = file;
            this.wrappedStream = new FileInputStream(file);
        }

        @Override
        public void close() throws IOException {
            try {
                wrappedStream.close();
            } finally {
                file.delete();
            }
        }

        @Override
        public int read() throws IOException {
            return wrappedStream.read();
        }

        @Override
        public int read(byte[] b) throws IOException {
            return wrappedStream.read(b);
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return wrappedStream.read(b, off, len);
        }
    }
}
