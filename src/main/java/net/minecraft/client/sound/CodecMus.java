// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.sound;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import paulscode.sound.codecs.CodecJOrbis;

public class CodecMus extends CodecJOrbis
{
    @Override
    protected InputStream openInputStream() throws IOException {
        return new DecoderInputStream(this.url, this.urlConnection.getInputStream());
    }

    private static class DecoderInputStream extends InputStream {
        private int seed;
        private InputStream in;
        byte[] buff;

        public DecoderInputStream(URL url, InputStream in1) {
            buff = new byte[1];
            in = in1;
            final String path = url.getPath();
            this.seed = path.substring(path.lastIndexOf("/") + 1).hashCode();
        }

        @Override
        public int read() throws IOException {
            final int read = this.read(this.buff, 0, 1);
            if (read < 0) {
                return read;
            }
            return this.buff[0];
        }

        @Override
        public int read(final byte[] buff, final int off, int len) throws IOException {
            len = this.in.read(buff, off, len);
            for (int i = 0; i < len; ++i) {
                final int n = off + i;
                final byte b = (byte)(buff[n] ^ this.seed >> 8);
                buff[n] = b;
                this.seed = this.seed * 498729871 + 85731 * b;
            }
            return len;
        }
    }
}
