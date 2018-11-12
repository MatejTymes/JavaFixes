package javafixes.json5;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import static javafixes.common.CollectionUtil.newList;

// todo: test
public class Json5ToJsonReader extends Reader {

    private final Reader json5Reader;

    private List<Character> buffer = newList();
    private boolean finished = false;

    public Json5ToJsonReader(Reader json5Reader) {
        this.json5Reader = json5Reader;
    }

    // todo: implement
    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        ensureCharsInBuffer(len);

        int writtenLength = 0;
        for (int i = 0; i < len; i++) {
            if (buffer.size() > 0) {
                cbuf[off + i] = buffer.remove(0);
                writtenLength++;
            } else {
                break;
            }
        }
        return (writtenLength == 0) ? -1 : writtenLength;
    }

    @Override
    public void close() throws IOException {
        json5Reader.close();
    }

    private void ensureCharsInBuffer(int n) throws IOException {
        while (!finished && buffer.size() < n) {
            parseNextChars();
        }
    }

    private void parseNextChars() throws IOException {
        int nextChar = json5Reader.read();
        if (nextChar == -1) {
            markEndOfStream();
            return;
        }

        char c = (char) nextChar;
        write(c);
    }

    private void markEndOfStream() {
        finished = true;
    }

    private void write(char c) {
        buffer.add(c);
    }
}
