// 
// Decompiled by Procyon v0.6.0
// 

package paulscode.sound.libraries;

import java.util.LinkedList;
import javax.sound.sampled.AudioFormat;
import org.lwjgl.openal.AL10;
import org.lwjgl.BufferUtils;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import paulscode.sound.Channel;

public class ChannelLWJGLOpenAL extends Channel
{
    public IntBuffer ALSource;
    public int ALformat;
    public int sampleRate;
    ByteBuffer bufferBuffer;
    
    public ChannelLWJGLOpenAL(final int integer, final IntBuffer intBuffer) {
        super(integer);
        this.bufferBuffer = BufferUtils.createByteBuffer(5242880);
        this.libraryType = LibraryLWJGLOpenAL.class;
        this.ALSource = intBuffer;
    }
    
    @Override
    public void cleanup() {
        if (this.ALSource != null) {
            try {
                AL10.alSourceStop(this.ALSource);
                AL10.alGetError();
            }
            catch (final Exception ex) {}
            try {
                AL10.alDeleteSources(this.ALSource);
                AL10.alGetError();
            }
            catch (final Exception ex2) {}
            this.ALSource.clear();
        }
        this.ALSource = null;
        super.cleanup();
    }
    
    public boolean attachBuffer(final IntBuffer intBuffer) {
        if (this.errorCheck(this.channelType != 0, "Sound buffers may only be attached to normal sources.")) {
            return false;
        }
        AL10.alSourcei(this.ALSource.get(0), 4105, intBuffer.get(0));
        return this.checkALError();
    }
    
    @Override
    public void setAudioFormat(final AudioFormat audioFormat) {
        int aLformat;
        if (audioFormat.getChannels() == 1) {
            if (audioFormat.getSampleSizeInBits() == 8) {
                aLformat = 4352;
            }
            else {
                if (audioFormat.getSampleSizeInBits() != 16) {
                    this.errorMessage("Illegal sample size in method 'setAudioFormat'");
                    return;
                }
                aLformat = 4353;
            }
        }
        else {
            if (audioFormat.getChannels() != 2) {
                this.errorMessage("Audio data neither mono nor stereo in method 'setAudioFormat'");
                return;
            }
            if (audioFormat.getSampleSizeInBits() == 8) {
                aLformat = 4354;
            }
            else {
                if (audioFormat.getSampleSizeInBits() != 16) {
                    this.errorMessage("Illegal sample size in method 'setAudioFormat'");
                    return;
                }
                aLformat = 4355;
            }
        }
        this.ALformat = aLformat;
        this.sampleRate = (int)audioFormat.getSampleRate();
    }
    
    public void setFormat(final int integer1, final int integer2) {
        this.ALformat = integer1;
        this.sampleRate = integer2;
    }
    
    @Override
    public boolean preLoadBuffers(final LinkedList linkedList) {
        if (this.errorCheck(this.channelType != 1, "Buffers may only be queued for streaming sources.")) {
            return false;
        }
        if (this.errorCheck(linkedList == null, "Buffer List null in method 'preLoadBuffers'")) {
            return false;
        }
        final boolean playing = this.playing();
        if (playing) {
            AL10.alSourceStop(this.ALSource.get(0));
            this.checkALError();
        }
        final int alGetSourcei = AL10.alGetSourcei(this.ALSource.get(0), 4118);
        if (alGetSourcei > 0) {
            final IntBuffer intBuffer = BufferUtils.createIntBuffer(alGetSourcei);
            AL10.alGenBuffers(intBuffer);
            if (this.errorCheck(this.checkALError(), "Error clearing stream buffers in method 'preLoadBuffers'")) {
                return false;
            }
            AL10.alSourceUnqueueBuffers(this.ALSource.get(0), intBuffer);
            if (this.errorCheck(this.checkALError(), "Error unqueuing stream buffers in method 'preLoadBuffers'")) {
                return false;
            }
        }
        if (playing) {
            AL10.alSourcePlay(this.ALSource.get(0));
            this.checkALError();
        }
        final IntBuffer intBuffer2 = BufferUtils.createIntBuffer(linkedList.size());
        AL10.alGenBuffers(intBuffer2);
        if (this.errorCheck(this.checkALError(), "Error generating stream buffers in method 'preLoadBuffers'")) {
            return false;
        }
        for (int i = 0; i < linkedList.size(); ++i) {
            this.bufferBuffer.clear();
            this.bufferBuffer.put((byte[])linkedList.get(i), 0, ((byte[])linkedList.get(i)).length);
            this.bufferBuffer.flip();
            try {
                AL10.alBufferData(intBuffer2.get(i), this.ALformat, this.bufferBuffer, this.sampleRate);
            }
            catch (final Exception exception) {
                this.errorMessage("Error creating buffers in method 'preLoadBuffers'");
                this.printStackTrace(exception);
                return false;
            }
            if (this.errorCheck(this.checkALError(), "Error creating buffers in method 'preLoadBuffers'")) {
                return false;
            }
        }
        try {
            AL10.alSourceQueueBuffers(this.ALSource.get(0), intBuffer2);
        }
        catch (final Exception exception2) {
            this.errorMessage("Error queuing buffers in method 'preLoadBuffers'");
            this.printStackTrace(exception2);
            return false;
        }
        if (this.errorCheck(this.checkALError(), "Error queuing buffers in method 'preLoadBuffers'")) {
            return false;
        }
        AL10.alSourcePlay(this.ALSource.get(0));
        return !this.errorCheck(this.checkALError(), "Error playing source in method 'preLoadBuffers'");
    }
    
    @Override
    public boolean queueBuffer(final byte[] arr) {
        if (this.errorCheck(this.channelType != 1, "Buffers may only be queued for streaming sources.")) {
            return false;
        }
        this.bufferBuffer.clear();
        this.bufferBuffer.put(arr, 0, arr.length);
        this.bufferBuffer.flip();
        final IntBuffer intBuffer = BufferUtils.createIntBuffer(1);
        AL10.alSourceUnqueueBuffers(this.ALSource.get(0), intBuffer);
        if (this.checkALError()) {
            return false;
        }
        AL10.alBufferData(intBuffer.get(0), this.ALformat, this.bufferBuffer, this.sampleRate);
        if (this.checkALError()) {
            return false;
        }
        AL10.alSourceQueueBuffers(this.ALSource.get(0), intBuffer);
        return !this.checkALError();
    }
    
    @Override
    public int feedRawAudioData(final byte[] arr) {
        if (this.errorCheck(this.channelType != 1, "Raw audio data can only be fed to streaming sources.")) {
            return -1;
        }
        final ByteBuffer wrap = ByteBuffer.wrap(arr, 0, arr.length);
        final int alGetSourcei = AL10.alGetSourcei(this.ALSource.get(0), 4118);
        IntBuffer intBuffer;
        if (alGetSourcei > 0) {
            intBuffer = BufferUtils.createIntBuffer(alGetSourcei);
            AL10.alGenBuffers(intBuffer);
            if (this.errorCheck(this.checkALError(), "Error clearing stream buffers in method 'feedRawAudioData'")) {
                return -1;
            }
            AL10.alSourceUnqueueBuffers(this.ALSource.get(0), intBuffer);
            if (this.errorCheck(this.checkALError(), "Error unqueuing stream buffers in method 'feedRawAudioData'")) {
                return -1;
            }
        }
        else {
            intBuffer = BufferUtils.createIntBuffer(1);
            AL10.alGenBuffers(intBuffer);
            if (this.errorCheck(this.checkALError(), "Error generating stream buffers in method 'preLoadBuffers'")) {
                return -1;
            }
        }
        AL10.alBufferData(intBuffer.get(0), this.ALformat, wrap, this.sampleRate);
        if (this.checkALError()) {
            return -1;
        }
        AL10.alSourceQueueBuffers(this.ALSource.get(0), intBuffer);
        if (this.checkALError()) {
            return -1;
        }
        if (this.attachedSource != null && this.attachedSource.channel == this && this.attachedSource.active() && !this.playing()) {
            AL10.alSourcePlay(this.ALSource.get(0));
            this.checkALError();
        }
        return alGetSourcei;
    }
    
    @Override
    public int buffersProcessed() {
        if (this.channelType != 1) {
            return 0;
        }
        final int alGetSourcei = AL10.alGetSourcei(this.ALSource.get(0), 4118);
        if (this.checkALError()) {
            return 0;
        }
        return alGetSourcei;
    }
    
    @Override
    public void flush() {
        if (this.channelType != 1) {
            return;
        }
        int i = AL10.alGetSourcei(this.ALSource.get(0), 4117);
        if (this.checkALError()) {
            return;
        }
        final IntBuffer intBuffer = BufferUtils.createIntBuffer(1);
        while (i > 0) {
            try {
                AL10.alSourceUnqueueBuffers(this.ALSource.get(0), intBuffer);
            }
            catch (final Exception ex) {
                return;
            }
            if (this.checkALError()) {
                return;
            }
            --i;
        }
    }
    
    @Override
    public void close() {
        try {
            AL10.alSourceStop(this.ALSource.get(0));
            AL10.alGetError();
        }
        catch (final Exception ex) {}
        if (this.channelType == 1) {
            this.flush();
        }
    }
    
    @Override
    public void play() {
        AL10.alSourcePlay(this.ALSource.get(0));
        this.checkALError();
    }
    
    @Override
    public void pause() {
        AL10.alSourcePause(this.ALSource.get(0));
        this.checkALError();
    }
    
    @Override
    public void stop() {
        AL10.alSourceStop(this.ALSource.get(0));
        this.checkALError();
    }
    
    @Override
    public void rewind() {
        if (this.channelType == 1) {
            return;
        }
        AL10.alSourceRewind(this.ALSource.get(0));
        this.checkALError();
    }
    
    @Override
    public boolean playing() {
        final int alGetSourcei = AL10.alGetSourcei(this.ALSource.get(0), 4112);
        return !this.checkALError() && alGetSourcei == 4114;
    }
    
    private boolean checkALError() {
        switch (AL10.alGetError()) {
            case 0: {
                return false;
            }
            case 40961: {
                this.errorMessage("Invalid name parameter.");
                return true;
            }
            case 40962: {
                this.errorMessage("Invalid parameter.");
                return true;
            }
            case 40963: {
                this.errorMessage("Invalid enumerated parameter value.");
                return true;
            }
            case 40964: {
                this.errorMessage("Illegal call.");
                return true;
            }
            case 40965: {
                this.errorMessage("Unable to allocate memory.");
                return true;
            }
            default: {
                this.errorMessage("An unrecognized error occurred.");
                return true;
            }
        }
    }
}
