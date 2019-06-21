package javafixes.experimental.json5;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Stack;

import static javafixes.common.CollectionUtil.newList;
import static javafixes.experimental.json5.Token.*;

// todo: handle trailing commas
// todo: test
public class Json5ToJsonReader extends Reader {

    private final Reader json5Reader;

    // todo: use LinkedArrayQueue in here instead
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

    private Stack<Token> tokenStack = new Stack<>();

    private void parseNextToken() throws IOException {
        // todo: implement
        boolean repeat;

        do {
            repeat = false;

            int nextChar = json5Reader.read();
            if (nextChar == -1) {
                markEndOfStream();
                return;
            }

            char currChar = (char) nextChar;

            Token lastToken = !tokenStack.isEmpty() ? tokenStack.peek() : null;

            if (currChar == '/') { // commentary
                handleCommentary();
                repeat = true;
            } else if (isJSON5WhiteSpace(currChar)) { // white space
                repeat = true;
            } else if (currChar == '{' || currChar == '[') { // object / array start
                if (lastToken == null || lastToken == semicolon || lastToken == commaInArray) {
                    tokenStack.push(currChar == '{' ? objectStart : arrayStart);
                    write(currChar);
                } else {
                    throw new IOException("Unexpected character: '" + currChar + "' after token '" + lastToken + "'");
                }
            } else if (currChar == '}') { // object end
                if (lastToken == objectStart) {
                    tokenStack.pop();
                    write(currChar);
                } else if (lastToken == valueInObject || lastToken == commaInObject) {
                    tokenStack.pop();
                    tokenStack.pop(); // pop objectStart
                    write(currChar);
                } else {
                    throw new IOException("Unexpected character: '" + currChar + "' after token '" + lastToken + "'");
                }
            } else if (currChar == ']') { // array end
                if (lastToken == arrayStart) {
                    tokenStack.pop();
                } else if (lastToken == valueInArray || lastToken == commaInArray) {
                    tokenStack.pop();
                    tokenStack.pop(); // pop arrayStart
                    write(currChar);
                } else {
                    throw new IOException("Unexpected character: '" + currChar + "' after token '" + lastToken + "'");
                }
            } else if (currChar == ',') { // comma
                if (lastToken == valueInObject) {
                    tokenStack.pop();
                    tokenStack.push(commaInObject);
                } else if (lastToken == valueInArray) {
                    tokenStack.pop();
                    tokenStack.push(commaInArray);
                } else {
                    throw new IOException("Unexpected character: '" + currChar + "' after token '" + lastToken + "'");
                }
            } else if (currChar == ':') { // semicolon
                if (lastToken == keyInObject) {
                    tokenStack.pop();
                    tokenStack.push(semicolon);
                    write(currChar);
                } else {
                    throw new IOException("Unexpected character: '" + currChar + "' after token '" + lastToken + "'");
                }
            } else if (lastToken == arrayStart || lastToken == commaInArray) { // value in array
                if (lastToken == commaInArray) {
                    tokenStack.pop();
                    write(',');
                }
                tokenStack.push(valueInArray);
                // todo: parse value
            } else if (lastToken == objectStart || lastToken == commaInObject) { // key in object
                if (lastToken == commaInObject) {
                    tokenStack.pop();
                    write(',');
                }
                if (currChar == '"') {
                    handleString(currChar);
                    tokenStack.push(keyInObject);
                } else {
                    throw new IOException("Unexpected character: '" + currChar + "' after token '" + lastToken + "'");
                }
            } else if (lastToken == semicolon) {
                tokenStack.pop();
                tokenStack.push(valueInObject);
                // todo: handle all types of values not just strings
                if (currChar == '"') {
                    handleString(currChar);
                    tokenStack.pop();
                }
            } else {
                throw new IOException("Unexpected character: '" + currChar + "' after token '" + lastToken + "'");
            }
        } while (repeat);
    }

    private boolean isJSON5WhiteSpace(char c) {
        return c == 0x0009 || c == 0x000A || c == 0x000B || c == 0x000C || c == 0x000D ||
                c == 0x2028 || c == 0x2029 || c == 0xFEFF || isUnicodeSpaceSeparator(c);
    }

    private boolean isUnicodeSpaceSeparator(char c) {
        return c == 0x0020 || c == 0x00A0 || c == 0x1680 || c == 0x2000 || c == 0x2001 ||
                c == 0x2002 || c == 0x2003 || c == 0x2004 || c == 0x2005 || c == 0x2006 ||
                c == 0x2007 || c == 0x2008 || c == 0x2009 || c == 0x200A || c == 0x202F ||
                c == 0x205F || c == 0x3000;
    }

    private void handleString(char wrappingQuote) throws IOException {
        write('\"');

        boolean isEscaped = false;
        boolean finishedString = false;
        do {
            int nextChar;
            nextChar = json5Reader.read();
            if (nextChar == -1) {
                markEndOfStream();
                return;
            }

            char currChar = (char) nextChar;
            if (!isEscaped) {
                if (currChar == '\"') {
                    finishedString = true;
                    write('\"');
                } else if (currChar == '\\') {
                    isEscaped = true;
                    write(currChar);
                } else {
                    write(currChar);
                }
            } else {
                write(currChar);
                isEscaped = false;
            }

        } while (!finishedString);
    }

    private void handleCommentary() throws IOException {
        int nextChar;
        nextChar = json5Reader.read();
        if (nextChar == -1) {
            throw new IOException("Unexpected String occurred: '/'");
        }
        char currChar = (char) nextChar;
        if (currChar == '/') {
            handleSingleLineComment();
        } else if (currChar == '*') {
            handleMultiLineComment();
        } else {
            throw new IOException("Unexpected String occurred: '/" + currChar + "'");
        }
    }

    private void handleSingleLineComment() throws IOException {
        int nextChar = json5Reader.read();
        if (nextChar == -1) {
            markEndOfStream();
            return;
        }
        char currChar = (char) nextChar;

        boolean finishedComment = false;
        while (!finishedComment) {
            if (currChar == '\n') {
                finishedComment = true;
            } else {
                nextChar = json5Reader.read();
                if (nextChar == -1) {
                    markEndOfStream();
                    return;
                }
                currChar = (char) nextChar;
            }
        }
    }

    private void handleMultiLineComment() throws IOException {
        int nextChar = json5Reader.read();
        if (nextChar == -1) {
            markEndOfStream();
            return;
        }
        char currChar = (char) nextChar;

        boolean finishedComment = false;
        while (!finishedComment) {
            if (currChar == '*') {
                nextChar = json5Reader.read();
                if (nextChar == -1) {
                    markEndOfStream();
                    return;
                }
                currChar = (char) nextChar;
                if (currChar == '/') {
                    finishedComment = true;
                }
            } else {
                nextChar = json5Reader.read();
                if (nextChar == -1) {
                    throw new IOException("Multiline comment never finished");
                }
                currChar = (char) nextChar;
            }
        }
    }


    private void ensureCharsInBuffer(int n) throws IOException {
        while (!finished && buffer.size() < n) {
            parseNextToken();
        }
    }

    private void markEndOfStream() throws IOException {
        if (!tokenStack.isEmpty()) {
            throw new IOException("Unexpected end of JSON5 while not all tokens are closed: " + tokenStack);
        }
        finished = true;
    }

    private void write(char c) {
        buffer.add(c);
    }
}
