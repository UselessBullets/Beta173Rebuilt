// 
// Decompiled by Procyon v0.6.0
// 

package paulscode.sound.libraries;

import org.lwjgl.BufferUtils;
import java.util.LinkedList;
import paulscode.sound.Channel;
import org.lwjgl.openal.AL10;
import paulscode.sound.SoundSystemConfig;
import javax.sound.sampled.AudioFormat;
import paulscode.sound.SoundBuffer;
import paulscode.sound.FilenameURL;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import paulscode.sound.Source;

public class SourceLWJGLOpenAL extends Source
{
    private ChannelLWJGLOpenAL channelOpenAL;
    private IntBuffer myBuffer;
    private FloatBuffer listenerPosition;
    private FloatBuffer sourcePosition;
    private FloatBuffer sourceVelocity;
    
    public SourceLWJGLOpenAL(final FloatBuffer floatBuffer, final IntBuffer intBuffer, final boolean boolean3, final boolean boolean4, final boolean boolean5, final String string, final FilenameURL filenameURL, final SoundBuffer soundBuffer, final float float9, final float float10, final float float11, final int integer, final float float13, final boolean boolean14) {
        super(boolean3, boolean4, boolean5, string, filenameURL, soundBuffer, float9, float10, float11, integer, float13, boolean14);
        this.channelOpenAL = (ChannelLWJGLOpenAL)this.channel;
        this.reverseByteOrder = true;
        if (this.codec != null) {
            this.codec.reverseByteOrder(true);
        }
        this.listenerPosition = floatBuffer;
        this.myBuffer = intBuffer;
        this.libraryType = LibraryLWJGLOpenAL.class;
        this.pitch = 1.0f;
        this.resetALInformation();
    }
    
    public SourceLWJGLOpenAL(final FloatBuffer floatBuffer, final IntBuffer intBuffer, final Source source, final SoundBuffer soundBuffer) {
        super(source, soundBuffer);
        this.channelOpenAL = (ChannelLWJGLOpenAL)this.channel;
        this.reverseByteOrder = true;
        if (this.codec != null) {
            this.codec.reverseByteOrder(true);
        }
        this.listenerPosition = floatBuffer;
        this.myBuffer = intBuffer;
        this.libraryType = LibraryLWJGLOpenAL.class;
        this.pitch = 1.0f;
        this.resetALInformation();
    }
    
    public SourceLWJGLOpenAL(final FloatBuffer floatBuffer, final AudioFormat audioFormat, final boolean boolean3, final String string, final float float5, final float float6, final float float7, final int integer, final float float9) {
        super(audioFormat, boolean3, string, float5, float6, float7, integer, float9);
        this.channelOpenAL = (ChannelLWJGLOpenAL)this.channel;
        this.reverseByteOrder = true;
        this.listenerPosition = floatBuffer;
        this.libraryType = LibraryLWJGLOpenAL.class;
        this.pitch = 1.0f;
        this.resetALInformation();
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
    }
    
    public void changeSource(final FloatBuffer floatBuffer, final IntBuffer intBuffer, final boolean boolean3, final boolean boolean4, final boolean boolean5, final String string, final FilenameURL filenameURL, final SoundBuffer soundBuffer, final float float9, final float float10, final float float11, final int integer, final float float13, final boolean boolean14) {
        super.changeSource(boolean3, boolean4, boolean5, string, filenameURL, soundBuffer, float9, float10, float11, integer, float13, boolean14);
        this.reverseByteOrder = true;
        this.listenerPosition = floatBuffer;
        this.myBuffer = intBuffer;
        this.pitch = 1.0f;
        this.resetALInformation();
    }
    
    @Override
    public boolean incrementSoundSequence() {
        if (!this.toStream) {
            this.errorMessage("Method 'incrementSoundSequence' may only be used for streaming sources.");
            return false;
        }
        synchronized (this.soundSequenceLock) {
            if (this.soundSequenceQueue != null && this.soundSequenceQueue.size() > 0) {
                this.filenameURL = this.soundSequenceQueue.remove(0);
                if (this.codec != null) {
                    this.codec.cleanup();
                }
                this.codec = SoundSystemConfig.getCodec(this.filenameURL.getFilename());
                if (this.codec != null) {
                    this.codec.reverseByteOrder(true);
                    if (this.codec.getAudioFormat() == null) {
                        this.codec.initialize(this.filenameURL.getURL());
                    }
                    final AudioFormat audioFormat = this.codec.getAudioFormat();
                    if (audioFormat == null) {
                        this.errorMessage("Audio Format null in method 'incrementSoundSequence'");
                        return false;
                    }
                    int integer1;
                    if (audioFormat.getChannels() == 1) {
                        if (audioFormat.getSampleSizeInBits() == 8) {
                            integer1 = 4352;
                        }
                        else {
                            if (audioFormat.getSampleSizeInBits() != 16) {
                                this.errorMessage("Illegal sample size in method 'incrementSoundSequence'");
                                return false;
                            }
                            integer1 = 4353;
                        }
                    }
                    else {
                        if (audioFormat.getChannels() != 2) {
                            this.errorMessage("Audio data neither mono nor stereo in method 'incrementSoundSequence'");
                            return false;
                        }
                        if (audioFormat.getSampleSizeInBits() == 8) {
                            integer1 = 4354;
                        }
                        else {
                            if (audioFormat.getSampleSizeInBits() != 16) {
                                this.errorMessage("Illegal sample size in method 'incrementSoundSequence'");
                                return false;
                            }
                            integer1 = 4355;
                        }
                    }
                    this.channelOpenAL.setFormat(integer1, (int)audioFormat.getSampleRate());
                    this.preLoad = true;
                }
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void listenerMoved() {
        this.positionChanged();
    }
    
    @Override
    public void setPosition(final float float1, final float float2, final float float3) {
        super.setPosition(float1, float2, float3);
        if (this.sourcePosition == null) {
            this.resetALInformation();
        }
        else {
            this.positionChanged();
        }
        this.sourcePosition.put(0, float1);
        this.sourcePosition.put(1, float2);
        this.sourcePosition.put(2, float3);
        if (this.channel != null && this.channel.attachedSource == this && this.channelOpenAL != null && this.channelOpenAL.ALSource != null) {
            AL10.alSource(this.channelOpenAL.ALSource.get(0), 4100, this.sourcePosition);
            this.checkALError();
        }
    }
    
    @Override
    public void positionChanged() {
        this.calculateDistance();
        this.calculateGain();
        if (this.channel != null && this.channel.attachedSource == this && this.channelOpenAL != null && this.channelOpenAL.ALSource != null) {
            AL10.alSourcef(this.channelOpenAL.ALSource.get(0), 4106, this.gain * this.sourceVolume * Math.abs(this.fadeOutGain) * this.fadeInGain);
            this.checkALError();
        }
        this.checkPitch();
    }
    
    private void checkPitch() {
        if (this.channel != null && this.channel.attachedSource == this && LibraryLWJGLOpenAL.alPitchSupported() && this.channelOpenAL != null && this.channelOpenAL.ALSource != null) {
            AL10.alSourcef(this.channelOpenAL.ALSource.get(0), 4099, this.pitch);
            this.checkALError();
        }
    }
    
    @Override
    public void setLooping(final boolean boolean1) {
        super.setLooping(boolean1);
        if (this.channel != null && this.channel.attachedSource == this && this.channelOpenAL != null && this.channelOpenAL.ALSource != null) {
            if (boolean1) {
                AL10.alSourcei(this.channelOpenAL.ALSource.get(0), 4103, 1);
            }
            else {
                AL10.alSourcei(this.channelOpenAL.ALSource.get(0), 4103, 0);
            }
            this.checkALError();
        }
    }
    
    @Override
    public void setAttenuation(final int integer) {
        super.setAttenuation(integer);
        if (this.channel != null && this.channel.attachedSource == this && this.channelOpenAL != null && this.channelOpenAL.ALSource != null) {
            if (integer == 1) {
                AL10.alSourcef(this.channelOpenAL.ALSource.get(0), 4129, this.distOrRoll);
            }
            else {
                AL10.alSourcef(this.channelOpenAL.ALSource.get(0), 4129, 0.0f);
            }
            this.checkALError();
        }
    }
    
    @Override
    public void setDistOrRoll(final float float1) {
        super.setDistOrRoll(float1);
        if (this.channel != null && this.channel.attachedSource == this && this.channelOpenAL != null && this.channelOpenAL.ALSource != null) {
            if (this.attModel == 1) {
                AL10.alSourcef(this.channelOpenAL.ALSource.get(0), 4129, float1);
            }
            else {
                AL10.alSourcef(this.channelOpenAL.ALSource.get(0), 4129, 0.0f);
            }
            this.checkALError();
        }
    }
    
    @Override
    public void setPitch(final float float1) {
        super.setPitch(float1);
        this.checkPitch();
    }
    
    @Override
    public void play(final Channel channel) {
        if (!this.active()) {
            if (this.toLoop) {
                this.toPlay = true;
            }
            return;
        }
        if (channel == null) {
            this.errorMessage("Unable to play source, because channel was null");
            return;
        }
        boolean b = this.channel != channel;
        if (this.channel != null && this.channel.attachedSource != this) {
            b = true;
        }
        final boolean paused = this.paused();
        super.play(channel);
        this.channelOpenAL = (ChannelLWJGLOpenAL)this.channel;
        if (b) {
            this.setPosition(this.position.x, this.position.y, this.position.z);
            this.checkPitch();
            if (this.channelOpenAL != null && this.channelOpenAL.ALSource != null) {
                if (LibraryLWJGLOpenAL.alPitchSupported()) {
                    AL10.alSourcef(this.channelOpenAL.ALSource.get(0), 4099, this.pitch);
                    this.checkALError();
                }
                AL10.alSource(this.channelOpenAL.ALSource.get(0), 4100, this.sourcePosition);
                this.checkALError();
                AL10.alSource(this.channelOpenAL.ALSource.get(0), 4102, this.sourceVelocity);
                this.checkALError();
                if (this.attModel == 1) {
                    AL10.alSourcef(this.channelOpenAL.ALSource.get(0), 4129, this.distOrRoll);
                }
                else {
                    AL10.alSourcef(this.channelOpenAL.ALSource.get(0), 4129, 0.0f);
                }
                this.checkALError();
                if (this.toLoop && !this.toStream) {
                    AL10.alSourcei(this.channelOpenAL.ALSource.get(0), 4103, 1);
                }
                else {
                    AL10.alSourcei(this.channelOpenAL.ALSource.get(0), 4103, 0);
                }
                this.checkALError();
            }
            if (!this.toStream) {
                if (this.myBuffer == null) {
                    this.errorMessage("No sound buffer to play");
                    return;
                }
                this.channelOpenAL.attachBuffer(this.myBuffer);
            }
        }
        if (!this.playing()) {
            if (this.toStream && !paused) {
                if (this.codec == null) {
                    this.errorMessage("Decoder null in method 'play'");
                    return;
                }
                if (this.codec.getAudioFormat() == null) {
                    this.codec.initialize(this.filenameURL.getURL());
                }
                final AudioFormat audioFormat = this.codec.getAudioFormat();
                if (audioFormat == null) {
                    this.errorMessage("Audio Format null in method 'play'");
                    return;
                }
                int integer1;
                if (audioFormat.getChannels() == 1) {
                    if (audioFormat.getSampleSizeInBits() == 8) {
                        integer1 = 4352;
                    }
                    else {
                        if (audioFormat.getSampleSizeInBits() != 16) {
                            this.errorMessage("Illegal sample size in method 'play'");
                            return;
                        }
                        integer1 = 4353;
                    }
                }
                else {
                    if (audioFormat.getChannels() != 2) {
                        this.errorMessage("Audio data neither mono nor stereo in method 'play'");
                        return;
                    }
                    if (audioFormat.getSampleSizeInBits() == 8) {
                        integer1 = 4354;
                    }
                    else {
                        if (audioFormat.getSampleSizeInBits() != 16) {
                            this.errorMessage("Illegal sample size in method 'play'");
                            return;
                        }
                        integer1 = 4355;
                    }
                }
                this.channelOpenAL.setFormat(integer1, (int)audioFormat.getSampleRate());
                this.preLoad = true;
            }
            this.channel.play();
            if (this.pitch != 1.0f) {
                this.checkPitch();
            }
        }
    }
    
    @Override
    public boolean preLoad() {
        if (this.codec == null) {
            return false;
        }
        this.codec.initialize(this.filenameURL.getURL());
        final LinkedList linkedList = new LinkedList();
        for (int i = 0; i < SoundSystemConfig.getNumberStreamingBuffers(); ++i) {
            this.soundBuffer = this.codec.read();
            if (this.soundBuffer == null) {
                break;
            }
            if (this.soundBuffer.audioData == null) {
                break;
            }
            linkedList.add(this.soundBuffer.audioData);
        }
        this.positionChanged();
        this.channel.preLoadBuffers(linkedList);
        this.preLoad = false;
        return true;
    }
    
    private void resetALInformation() {
        this.sourcePosition = BufferUtils.createFloatBuffer(3).put(new float[] { this.position.x, this.position.y, this.position.z });
        this.sourceVelocity = BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f });
        this.sourcePosition.flip();
        this.sourceVelocity.flip();
        this.positionChanged();
    }
    
    private void calculateDistance() {
        if (this.listenerPosition != null) {
            final double n = this.position.x - this.listenerPosition.get(0);
            final double n2 = this.position.y - this.listenerPosition.get(1);
            final double n3 = this.position.z - this.listenerPosition.get(2);
            this.distanceFromListener = (float)Math.sqrt(n * n + n2 * n2 + n3 * n3);
        }
    }
    
    private void calculateGain() {
        if (this.attModel == 2) {
            if (this.distanceFromListener <= 0.0f) {
                this.gain = 1.0f;
            }
            else if (this.distanceFromListener >= this.distOrRoll) {
                this.gain = 0.0f;
            }
            else {
                this.gain = 1.0f - this.distanceFromListener / this.distOrRoll;
            }
            if (this.gain > 1.0f) {
                this.gain = 1.0f;
            }
            if (this.gain < 0.0f) {
                this.gain = 0.0f;
            }
        }
        else {
            this.gain = 1.0f;
        }
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
