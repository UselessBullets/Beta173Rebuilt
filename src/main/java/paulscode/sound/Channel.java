// 
// Decompiled by Procyon v0.6.0
// 

package paulscode.sound;

import javax.sound.sampled.AudioFormat;
import java.util.LinkedList;

public class Channel
{
    protected Class libraryType;
    public int channelType;
    private SoundSystemLogger logger;
    public Source attachedSource;
    
    public Channel(final int integer) {
        this.libraryType = Library.class;
        this.attachedSource = null;
        this.logger = SoundSystemConfig.getLogger();
        this.channelType = integer;
    }
    
    public void cleanup() {
        this.logger = null;
    }
    
    public boolean preLoadBuffers(final LinkedList linkedList) {
        return true;
    }
    
    public boolean queueBuffer(final byte[] arr) {
        return false;
    }
    
    public int feedRawAudioData(final byte[] arr) {
        return 1;
    }
    
    public int buffersProcessed() {
        return 0;
    }
    
    public boolean processBuffer() {
        return false;
    }
    
    public void setAudioFormat(final AudioFormat audioFormat) {
    }
    
    public void flush() {
    }
    
    public void close() {
    }
    
    public void play() {
    }
    
    public void pause() {
    }
    
    public void stop() {
    }
    
    public void rewind() {
    }
    
    public boolean playing() {
        return false;
    }
    
    public String getClassName() {
        final String libraryTitle = SoundSystemConfig.getLibraryTitle(this.libraryType);
        if (libraryTitle.equals("No Sound")) {
            return "Channel";
        }
        return "Channel" + libraryTitle;
    }
    
    protected void message(final String string) {
        this.logger.message(string, 0);
    }
    
    protected void importantMessage(final String string) {
        this.logger.importantMessage(string, 0);
    }
    
    protected boolean errorCheck(final boolean boolean1, final String string) {
        return this.logger.errorCheck(boolean1, this.getClassName(), string, 0);
    }
    
    protected void errorMessage(final String string) {
        this.logger.errorMessage(this.getClassName(), string, 0);
    }
    
    protected void printStackTrace(final Exception exception) {
        this.logger.printStackTrace(exception, 1);
    }
}
