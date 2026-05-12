// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.sound;

import java.net.URL;
import java.io.InputStream;

class CodecMus_InputStream extends InputStream
{
    private int seed;
    private InputStream in;
    byte[] buff;
    final /* synthetic */ CodecMus cm;
    
    public CodecMus_InputStream(final CodecMus cm, final URL url, final InputStream in) {
        this.cm = cm;
        this.buff = new byte[1];
        this.in = in;
        final String path = url.getPath();
        this.seed = path.substring(path.lastIndexOf("/") + 1).hashCode();
    }
    
    @Override
    public int read() {
        final int read = this.read(this.buff, 0, 1);
        if (read < 0) {
            return read;
        }
        return this.buff[0];
    }
    
    @Override
    public int read(final byte[] buff, final int off, int len) {
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
