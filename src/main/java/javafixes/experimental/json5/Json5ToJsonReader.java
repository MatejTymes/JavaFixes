package javafixes.experimental.json5;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.List;
import java.util.Stack;

import static javafixes.common.CollectionUtil.newList;
import static javafixes.experimental.json5.Token.*;

// https://spec.json5.org/
public class Json5ToJsonReader extends Reader {

    // todo: add flags: addZeroAfterTrailingDecimalPoint = {default false}

    private final PushbackReader json5Reader;

    // todo: use LinkedArrayQueue in here instead
    private List<Character> buffer = newList();
    private boolean finished = false;

    public Json5ToJsonReader(Reader json5Reader) {
        this.json5Reader = json5Reader instanceof PushbackReader
                ? (PushbackReader) json5Reader
                : new PushbackReader(json5Reader);
    }

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
                    if (lastToken == semicolon) {
                        tokenStack.pop();
                        tokenStack.push(valueInObject);
                    } else if (lastToken == commaInArray) {
                        write(',');
                        tokenStack.pop();
                        tokenStack.push(valueInArray);
                    }
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
                handleValue(currChar, lastToken);
            } else if (lastToken == objectStart || lastToken == commaInObject) { // key in object
                if (lastToken == commaInObject) {
                    tokenStack.pop();
                    write(',');
                }
                if (isBeginningOfString(currChar)) {
                    handleString(currChar);
                    tokenStack.push(keyInObject);
                } else {
                    throw new IOException("Unexpected character: '" + currChar + "' after token '" + lastToken + "'");
                }
            } else if (lastToken == semicolon) {
                tokenStack.pop();
                tokenStack.push(valueInObject);
                handleValue(currChar, lastToken);
            } else {
                throw new IOException("Unexpected character: '" + currChar + "' after token '" + lastToken + "'");
            }
        } while (repeat);
    }

    private boolean isBeginningOfString(char currChar) {
        return currChar == '"' || currChar == '\'';
    }

    private void handleValue(char currChar, Token lastToken) throws IOException {
        if (isBeginningOfString(currChar)) {
            handleString(currChar);
        } else if (currChar == '-' || currChar == '+' || currChar == '.' || (currChar >= '0' && currChar <= '9')) {
            handleNumber(currChar);
        } else if (currChar == 't') {
            handleTrue(currChar);
        } else if (currChar == 'f') {
            handleFalse(currChar);
        } else if (currChar == 'n') {
            handleNull(currChar);
        } else {
            throw new IOException("Unexpected character: '" + currChar + "' after token '" + lastToken + "'");
        }
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
            char currChar = readNextChar();
            if (!isEscaped) {
                if (currChar == wrappingQuote) {
                    finishedString = true;
                    write('\"');
                } else if (wrappingQuote == '\'' && currChar == '"') {
                    write('\\');
                    write('\"');
                } else if (currChar == '\\') {
                    isEscaped = true;
                } else {
                    write(currChar);
                }
            } else {
                if (wrappingQuote == '\'' && currChar == '\'') {
                    write('\'');
                } else if (currChar == '\n') {
                    // do nothing
//                    write('\\');
//                    write('n');
                } else {
                    write('\\');
                    write(currChar);
                }
                isEscaped = false;
            }

        } while (!finishedString);
    }

    private void handleTrue(char currChar) throws IOException {
        write(currChar);
        currChar = readNextChar();
        if (currChar != 'r') {
            throw new IOException("Unexpected character: '" + currChar + "' while expecting character t-'r'-ue");
        }
        write(currChar);
        currChar = readNextChar();
        if (currChar != 'u') {
            throw new IOException("Unexpected character: '" + currChar + "' while expecting character tr-'u'-e");
        }
        write(currChar);
        currChar = readNextChar();
        if (currChar != 'e') {
            throw new IOException("Unexpected character: '" + currChar + "' while expecting character tru-'e'");
        }
        write(currChar);
    }

    private void handleFalse(char currChar) throws IOException {
        write(currChar);
        currChar = readNextChar();
        if (currChar != 'a') {
            throw new IOException("Unexpected character: '" + currChar + "' while expecting character f-'a'-lse");
        }
        write(currChar);
        currChar = readNextChar();
        if (currChar != 'l') {
            throw new IOException("Unexpected character: '" + currChar + "' while expecting character fa-'l'-se");
        }
        write(currChar);
        currChar = readNextChar();
        if (currChar != 's') {
            throw new IOException("Unexpected character: '" + currChar + "' while expecting character fal-'s'-e");
        }
        write(currChar);
        currChar = readNextChar();
        if (currChar != 'e') {
            throw new IOException("Unexpected character: '" + currChar + "' while expecting character fals-'e'");
        }
        write(currChar);
    }

    private void handleNull(char currChar) throws IOException {
        write(currChar);
        currChar = readNextChar();
        if (currChar != 'u') {
            throw new IOException("Unexpected character: '" + currChar + "' while expecting character n-'u'-ll");
        }
        write(currChar);
        currChar = readNextChar();
        if (currChar != 'l') {
            throw new IOException("Unexpected character: '" + currChar + "' while expecting character nu-'l'-l");
        }
        write(currChar);
        currChar = readNextChar();
        if (currChar != 'l') {
            throw new IOException("Unexpected character: '" + currChar + "' while expecting character nul-'l'");
        }
        write(currChar);
    }

    private void handleNumber(char currChar) throws IOException {
        if (currChar == '.') {
            write('0');
            handleDecimalPart(false);
        } else {
            boolean isZero = (currChar == '0');
            boolean hadAtLeastOneDigit = (currChar >= '0' && currChar <= '9');
            if (currChar != '+') {
                write(currChar);
            }

            while (true) {
                int nextChar = readNextInt();
                currChar = (char) nextChar;

                if (currChar == 'e' || currChar == 'E') {
                    if (!hadAtLeastOneDigit) {
                        throw new IOException("Unexpected exponent character: '" + currChar + "' when expecting first number digit");
                    }
                    handleExponent(currChar);
                } else if (currChar == '.') {
                    if (!hadAtLeastOneDigit) {
                        throw new IOException("Unexpected character: '" + currChar + "' when expecting first number digit");
                    }
                    handleDecimalPart(hadAtLeastOneDigit);
                } else if (!hadAtLeastOneDigit) {
                    if (currChar == '0') {
                        isZero = true;
                    } else if (currChar < '1' || currChar > '9') {
                        throw new IOException("Unexpected character: '" + currChar + "' when expecting first number digit");
                    }
                    hadAtLeastOneDigit = true;
                    write(currChar);
                } else if (currChar >= '0' && currChar <= '9') {
                    if (isZero) {
                        throw new IOException("Unexpected character: '" + currChar + "' after a number starting with 0");
                    }
                    write(currChar);
                } else {
                    json5Reader.unread(nextChar);
                    break;
                }
            }
        }
    }

    private void handleDecimalPart(boolean hadAtLeastWholePartOneDigit) throws IOException {
        boolean hadTheDotBeenWritten = false;
        boolean hadAtLeastOneDecimalDigit = false;

        while (true) {
            int nextChar = readNextInt();
            char currChar = (char) nextChar;
            if (currChar >= '0' && currChar <= '9') {
                if (!hadTheDotBeenWritten) {
                    write('.');
                    hadTheDotBeenWritten = true;
                }
                hadAtLeastOneDecimalDigit = true;
                write(currChar);
            } else if (!hadAtLeastWholePartOneDigit && !hadAtLeastOneDecimalDigit) {
                throw new IOException("Unexpected character: '" + currChar + "' when expecting a decimal digit");
            } else if (currChar == 'e' || currChar == 'E') {
                handleExponent(currChar);
            } else {
                json5Reader.unread(nextChar);
                break;
            }
        }
    }

    private void handleExponent(char currChar) throws IOException {
        boolean hadSign = false;
        write(currChar);

        while (true) {
            int nextChar = readNextInt();
            currChar = (char) nextChar;
            if (currChar == '+' || currChar == '-') {
                if (hadSign) {
                    throw new IOException("Second sign character: '" + currChar + "' while parsing a number's exponent");
                }
                write(currChar);
                hadSign = true;
            } else if (currChar >= '0' && currChar <= '9') {
                write(currChar);
            } else {
                json5Reader.unread(nextChar);
                break;
            }
        }
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


    private char readNextChar() throws IOException {
        return (char) readNextInt();
    }

    private int readNextInt() throws IOException {
        int nextChar = json5Reader.read();
        if (nextChar == -1) {
            markEndOfStream();
        }
        return nextChar;
    }

    private void ensureCharsInBuffer(int n) throws IOException {
        while (!finished && buffer.size() < n) {
            try {
                parseNextToken();
            } catch (IOException e) {
                System.err.println("Failed after filled JSON buffer with: " + buffer);
                throw e;
            }
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
