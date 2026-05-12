// 
// Decompiled by Procyon v0.6.0
// 

package paulscode.sound;

import javax.sound.sampled.AudioFormat;

public class SoundBuffer
{
    public byte[] audioData;
    public AudioFormat audioFormat;
    
    public SoundBuffer(final byte[] arr, final AudioFormat audioFormat) {
        this.audioData = arr;
        this.audioFormat = audioFormat;
    }
    
    public void cleanup() {
        this.audioData = null;
        this.audioFormat = null;
    }
    
    public void trimData(final int integer) {
        if (this.audioData == null || integer == 0) {
            this.audioData = null;
        }
        else if (this.audioData.length > integer) {
            final byte[] audioData = new byte[integer];
            System.arraycopy(this.audioData, 0, audioData, 0, integer);
            this.audioData = audioData;
        }
    }
}
