// 
// Decompiled by Procyon v0.6.0
// 

package argo.saj;

import java.util.Arrays;
import argo.jdom.JsonListener;
import java.io.Reader;

public final class SajParser
{
    public void parse(final Reader in, final JsonListener jsonListener) {
        final PositionTrackingPushbackReader thingWithPosition = new PositionTrackingPushbackReader(in);
        final char c = (char)thingWithPosition.read();
        switch (c) {
            case 123: {
                thingWithPosition.unread(c);
                jsonListener.startDocument();
                this.b(thingWithPosition, jsonListener);
                break;
            }
            case 91: {
                thingWithPosition.unread(c);
                jsonListener.startDocument();
                this.a(thingWithPosition, jsonListener);
                break;
            }
            default: {
                throw new InvalidSyntaxException("Expected either [ or { but got [" + c + "].", thingWithPosition);
            }
        }
        final int l = this.l(thingWithPosition);
        if (l != -1) {
            throw new InvalidSyntaxException("Got unexpected trailing character [" + (char)l + "].", thingWithPosition);
        }
        jsonListener.endDocument();
    }
    
    private void a(final PositionTrackingPushbackReader lj, final JsonListener wg) {
        final char c = (char)this.l(lj);
        if (c != '[') {
            throw new InvalidSyntaxException("Expected object to start with [ but got [" + c + "].", lj);
        }
        wg.startArray();
        final char c2 = (char)this.l(lj);
        lj.unread(c2);
        if (c2 != ']') {
            this.d(lj, wg);
        }
        int i = 0;
        while (i == 0) {
            final char c3 = (char)this.l(lj);
            switch (c3) {
                case 44: {
                    this.d(lj, wg);
                    continue;
                }
                case 93: {
                    i = 1;
                    continue;
                }
                default: {
                    throw new InvalidSyntaxException("Expected either , or ] but got [" + c3 + "].", lj);
                }
            }
        }
        wg.endArray();
    }
    
    private void b(final PositionTrackingPushbackReader lj, final JsonListener wg) {
        final char c = (char)this.l(lj);
        if (c != '{') {
            throw new InvalidSyntaxException("Expected object to start with { but got [" + c + "].", lj);
        }
        wg.startObject();
        final char c2 = (char)this.l(lj);
        lj.unread(c2);
        if (c2 != '}') {
            this.c(lj, wg);
        }
        int i = 0;
        while (i == 0) {
            final char c3 = (char)this.l(lj);
            switch (c3) {
                case 44: {
                    this.c(lj, wg);
                    continue;
                }
                case 125: {
                    i = 1;
                    continue;
                }
                default: {
                    throw new InvalidSyntaxException("Expected either , or } but got [" + c3 + "].", lj);
                }
            }
        }
        wg.endObject();
    }
    
    private void c(final PositionTrackingPushbackReader lj, final JsonListener wg) {
        final char c = (char)this.l(lj);
        if ('\"' != c) {
            throw new InvalidSyntaxException("Expected object identifier to begin with [\"] but got [" + c + "].", lj);
        }
        lj.unread(c);
        wg.startField(this.i(lj));
        final char c2 = (char)this.l(lj);
        if (c2 != ':') {
            throw new InvalidSyntaxException("Expected object identifier to be followed by : but got [" + c2 + "].", lj);
        }
        this.d(lj, wg);
        wg.endField();
    }
    
    private void d(final PositionTrackingPushbackReader lj, final JsonListener wg) {
        final char c = (char)this.l(lj);
        switch (c) {
            case 34: {
                lj.unread(c);
                wg.stringValue(this.i(lj));
                break;
            }
            case 116: {
                final char[] a = new char[3];
                if (lj.read(a) != 3 || a[0] != 'r' || a[1] != 'u' || a[2] != 'e') {
                    lj.uncount(a);
                    throw new InvalidSyntaxException("Expected 't' to be followed by [[r, u, e]], but got [" + Arrays.toString(a) + "].", lj);
                }
                wg.trueValue();
                break;
            }
            case 102: {
                final char[] a2 = new char[4];
                if (lj.read(a2) != 4 || a2[0] != 'a' || a2[1] != 'l' || a2[2] != 's' || a2[3] != 'e') {
                    lj.uncount(a2);
                    throw new InvalidSyntaxException("Expected 'f' to be followed by [[a, l, s, e]], but got [" + Arrays.toString(a2) + "].", lj);
                }
                wg.falseValue();
                break;
            }
            case 110: {
                final char[] a3 = new char[3];
                if (lj.read(a3) != 3 || a3[0] != 'u' || a3[1] != 'l' || a3[2] != 'l') {
                    lj.uncount(a3);
                    throw new InvalidSyntaxException("Expected 'n' to be followed by [[u, l, l]], but got [" + Arrays.toString(a3) + "].", lj);
                }
                wg.nullValue();
                break;
            }
            case 45:
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57: {
                lj.unread(c);
                wg.numberValue(this.a(lj));
                break;
            }
            case 123: {
                lj.unread(c);
                this.b(lj, wg);
                break;
            }
            case 91: {
                lj.unread(c);
                this.a(lj, wg);
                break;
            }
            default: {
                throw new InvalidSyntaxException("Invalid character at start of value [" + c + "].", lj);
            }
        }
    }
    
    private String a(final PositionTrackingPushbackReader lj) {
        final StringBuilder sb = new StringBuilder();
        final char c = (char)lj.read();
        if ('-' == c) {
            sb.append('-');
        }
        else {
            lj.unread(c);
        }
        sb.append(this.b(lj));
        return sb.toString();
    }
    
    private String b(final PositionTrackingPushbackReader lj) {
        final StringBuilder sb = new StringBuilder();
        final char c = (char)lj.read();
        if ('0' == c) {
            sb.append('0');
            sb.append(this.f(lj));
            sb.append(this.g(lj));
        }
        else {
            lj.unread(c);
            sb.append(this.c(lj));
            sb.append(this.e(lj));
            sb.append(this.f(lj));
            sb.append(this.g(lj));
        }
        return sb.toString();
    }
    
    private char c(final PositionTrackingPushbackReader lj) {
        final char c = (char)lj.read();
        switch (c) {
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57: {
                return c;
            }
            default: {
                throw new InvalidSyntaxException("Expected a digit 1 - 9 but got [" + c + "].", lj);
            }
        }
    }
    
    private char d(final PositionTrackingPushbackReader lj) {
        final char c = (char)lj.read();
        switch (c) {
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57: {
                return c;
            }
            default: {
                throw new InvalidSyntaxException("Expected a digit 1 - 9 but got [" + c + "].", lj);
            }
        }
    }
    
    private String e(final PositionTrackingPushbackReader lj) {
        final StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i == 0) {
            final char c = (char)lj.read();
            switch (c) {
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57: {
                    sb.append(c);
                    continue;
                }
                default: {
                    i = 1;
                    lj.unread(c);
                    continue;
                }
            }
        }
        return sb.toString();
    }
    
    private String f(final PositionTrackingPushbackReader lj) {
        final StringBuilder sb = new StringBuilder();
        final char c = (char)lj.read();
        if (c == '.') {
            sb.append('.');
            sb.append(this.d(lj));
            sb.append(this.e(lj));
        }
        else {
            lj.unread(c);
        }
        return sb.toString();
    }
    
    private String g(final PositionTrackingPushbackReader lj) {
        final StringBuilder sb = new StringBuilder();
        final char c = (char)lj.read();
        if (c == '.' || c == 'E') {
            sb.append('E');
            sb.append(this.h(lj));
            sb.append(this.d(lj));
            sb.append(this.e(lj));
        }
        else {
            lj.unread(c);
        }
        return sb.toString();
    }
    
    private String h(final PositionTrackingPushbackReader lj) {
        final StringBuilder sb = new StringBuilder();
        final char c = (char)lj.read();
        if (c == '+' || c == '-') {
            sb.append(c);
        }
        else {
            lj.unread(c);
        }
        return sb.toString();
    }
    
    private String i(final PositionTrackingPushbackReader lj) {
        final StringBuilder sb = new StringBuilder();
        final char c = (char)lj.read();
        if ('\"' != c) {
            throw new InvalidSyntaxException("Expected [\"] but got [" + c + "].", lj);
        }
        int i = 0;
        while (i == 0) {
            final char c2 = (char)lj.read();
            switch (c2) {
                case 34: {
                    i = 1;
                    continue;
                }
                case 92: {
                    sb.append(this.j(lj));
                    continue;
                }
                default: {
                    sb.append(c2);
                    continue;
                }
            }
        }
        return sb.toString();
    }
    
    private char j(final PositionTrackingPushbackReader lj) {
        final char c = (char)lj.read();
        char c2 = '\0';
        switch (c) {
            case 34: {
                c2 = '\"';
                break;
            }
            case 92: {
                c2 = '\\';
                break;
            }
            case 47: {
                c2 = '/';
                break;
            }
            case 98: {
                c2 = '\b';
                break;
            }
            case 102: {
                c2 = '\f';
                break;
            }
            case 110: {
                c2 = '\n';
                break;
            }
            case 114: {
                c2 = '\r';
                break;
            }
            case 116: {
                c2 = '\t';
                break;
            }
            case 117: {
                c2 = (char)this.k(lj);
                break;
            }
            default: {
                throw new InvalidSyntaxException("Unrecognised escape character [" + c + "].", lj);
            }
        }
        return c2;
    }
    
    private int k(final PositionTrackingPushbackReader lj) {
        final char[] data = new char[4];
        final int read = lj.read(data);
        if (read != 4) {
            throw new InvalidSyntaxException("Expected a 4 digit hexidecimal number but got only [" + read + "], namely [" + String.valueOf(data, 0, read) + "].", lj);
        }
        int int1;
        try {
            int1 = Integer.parseInt(String.valueOf(data), 16);
        }
        catch (final NumberFormatException throwable) {
            lj.uncount(data);
            throw new InvalidSyntaxException("Unable to parse [" + String.valueOf(data) + "] as a hexidecimal number.", throwable, lj);
        }
        return int1;
    }
    
    private int l(final PositionTrackingPushbackReader lj) {
        boolean b = false;
        int read;
        do {
            read = lj.read();
            switch (read) {
                case 9:
                case 10:
                case 13:
                case 32: {
                    continue;
                }
                default: {
                    b = true;
                    continue;
                }
            }
        } while (!b);
        return read;
    }
}
