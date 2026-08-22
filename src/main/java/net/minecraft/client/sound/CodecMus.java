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
        byte[] buff = new byte[1];

        public DecoderInputStream(URL url, InputStream in1) {
            this.in = in1;
            String name = url.getPath();
            name = name.substring(name.lastIndexOf("/") + 1);
            this.seed = name.hashCode();
        }

        @Override
        public int read() throws IOException {
            final int result = this.read(this.buff, 0, 1);
            return result < 0 ? result : this.buff[0];
        }

        @Override
        public int read(final byte[] buff, final int off, int len) throws IOException {
            len = this.in.read(buff, off, len);

            for (int i = 0; i < len; ++i) {
                final int index = off + i;
                final byte val = buff[index] = (byte)(buff[index] ^ this.seed >> 8);
                this.seed = this.seed * 498729871 + 85731 * val;
            }

            return len;
        }
    }
}
