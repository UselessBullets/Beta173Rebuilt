// 
// Decompiled by Procyon v0.6.0
// 

package paulscode.sound;

import javax.sound.sampled.AudioFormat;
import java.net.URL;

public interface ICodec
{
    void reverseByteOrder(final boolean boolean1);
    
    boolean initialize(final URL uRL);
    
    boolean initialized();
    
    SoundBuffer read();
    
    SoundBuffer readAll();
    
    boolean endOfStream();
    
    void cleanup();
    
    AudioFormat getAudioFormat();
}
