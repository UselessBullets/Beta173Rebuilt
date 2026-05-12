// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.sound;

import java.io.InputStream;
import paulscode.sound.codecs.CodecJOrbis;

public class CodecMus extends CodecJOrbis
{
    @Override
    protected InputStream openInputStream() {
        return new CodecMus_InputStream(this, this.url, this.urlConnection.getInputStream());
    }
}
