// 
// Decompiled by Procyon v0.6.0
// 

package paulscode.sound;

import java.util.ListIterator;
import java.util.LinkedList;
import javax.sound.sampled.AudioFormat;

public class Source
{
    protected Class libraryType;
    private static final boolean GET = false;
    private static final boolean SET = true;
    private static final boolean XXX = false;
    private SoundSystemLogger logger;
    public boolean rawDataStream;
    public AudioFormat rawDataFormat;
    public boolean temporary;
    public boolean priority;
    public boolean toStream;
    public boolean toLoop;
    public boolean toPlay;
    public String sourcename;
    public FilenameURL filenameURL;
    public Vector3D position;
    public int attModel;
    public float distOrRoll;
    public float gain;
    public float sourceVolume;
    protected float pitch;
    public float distanceFromListener;
    public Channel channel;
    private boolean active;
    private boolean stopped;
    private boolean paused;
    protected SoundBuffer soundBuffer;
    protected ICodec codec;
    protected boolean reverseByteOrder;
    protected LinkedList soundSequenceQueue;
    protected final Object soundSequenceLock;
    public boolean preLoad;
    protected float fadeOutGain;
    protected float fadeInGain;
    protected long fadeOutMilis;
    protected long fadeInMilis;
    protected long lastFadeCheck;
    
    public Source(final boolean boolean1, final boolean boolean2, final boolean boolean3, final String string, final FilenameURL filenameURL, final SoundBuffer soundBuffer, final float float7, final float float8, final float float9, final int integer, final float float11, final boolean boolean12) {
        this.libraryType = Library.class;
        this.rawDataStream = false;
        this.rawDataFormat = null;
        this.temporary = false;
        this.priority = false;
        this.toStream = false;
        this.toLoop = false;
        this.toPlay = false;
        this.sourcename = "";
        this.filenameURL = null;
        this.attModel = 0;
        this.distOrRoll = 0.0f;
        this.gain = 1.0f;
        this.sourceVolume = 1.0f;
        this.pitch = 1.0f;
        this.distanceFromListener = 0.0f;
        this.channel = null;
        this.active = true;
        this.stopped = true;
        this.paused = false;
        this.soundBuffer = null;
        this.codec = null;
        this.reverseByteOrder = false;
        this.soundSequenceQueue = null;
        this.soundSequenceLock = new Object();
        this.preLoad = false;
        this.fadeOutGain = -1.0f;
        this.fadeInGain = 1.0f;
        this.fadeOutMilis = 0L;
        this.fadeInMilis = 0L;
        this.lastFadeCheck = 0L;
        this.logger = SoundSystemConfig.getLogger();
        this.priority = boolean1;
        this.toStream = boolean2;
        this.toLoop = boolean3;
        this.sourcename = string;
        this.filenameURL = filenameURL;
        this.soundBuffer = soundBuffer;
        this.position = new Vector3D(float7, float8, float9);
        this.attModel = integer;
        this.distOrRoll = float11;
        this.temporary = boolean12;
        if (boolean2 && filenameURL != null) {
            this.codec = SoundSystemConfig.getCodec(filenameURL.getFilename());
        }
    }
    
    public Source(final Source source, final SoundBuffer soundBuffer) {
        this.libraryType = Library.class;
        this.rawDataStream = false;
        this.rawDataFormat = null;
        this.temporary = false;
        this.priority = false;
        this.toStream = false;
        this.toLoop = false;
        this.toPlay = false;
        this.sourcename = "";
        this.filenameURL = null;
        this.attModel = 0;
        this.distOrRoll = 0.0f;
        this.gain = 1.0f;
        this.sourceVolume = 1.0f;
        this.pitch = 1.0f;
        this.distanceFromListener = 0.0f;
        this.channel = null;
        this.active = true;
        this.stopped = true;
        this.paused = false;
        this.soundBuffer = null;
        this.codec = null;
        this.reverseByteOrder = false;
        this.soundSequenceQueue = null;
        this.soundSequenceLock = new Object();
        this.preLoad = false;
        this.fadeOutGain = -1.0f;
        this.fadeInGain = 1.0f;
        this.fadeOutMilis = 0L;
        this.fadeInMilis = 0L;
        this.lastFadeCheck = 0L;
        this.logger = SoundSystemConfig.getLogger();
        this.priority = source.priority;
        this.toStream = source.toStream;
        this.toLoop = source.toLoop;
        this.sourcename = source.sourcename;
        this.filenameURL = source.filenameURL;
        this.position = source.position.clone();
        this.attModel = source.attModel;
        this.distOrRoll = source.distOrRoll;
        this.temporary = source.temporary;
        this.sourceVolume = source.sourceVolume;
        this.rawDataStream = source.rawDataStream;
        this.rawDataFormat = source.rawDataFormat;
        this.soundBuffer = soundBuffer;
        if (this.toStream && this.filenameURL != null) {
            this.codec = SoundSystemConfig.getCodec(this.filenameURL.getFilename());
        }
    }
    
    public Source(final AudioFormat audioFormat, final boolean boolean2, final String string, final float float4, final float float5, final float float6, final int integer, final float float8) {
        this.libraryType = Library.class;
        this.rawDataStream = false;
        this.rawDataFormat = null;
        this.temporary = false;
        this.priority = false;
        this.toStream = false;
        this.toLoop = false;
        this.toPlay = false;
        this.sourcename = "";
        this.filenameURL = null;
        this.attModel = 0;
        this.distOrRoll = 0.0f;
        this.gain = 1.0f;
        this.sourceVolume = 1.0f;
        this.pitch = 1.0f;
        this.distanceFromListener = 0.0f;
        this.channel = null;
        this.active = true;
        this.stopped = true;
        this.paused = false;
        this.soundBuffer = null;
        this.codec = null;
        this.reverseByteOrder = false;
        this.soundSequenceQueue = null;
        this.soundSequenceLock = new Object();
        this.preLoad = false;
        this.fadeOutGain = -1.0f;
        this.fadeInGain = 1.0f;
        this.fadeOutMilis = 0L;
        this.fadeInMilis = 0L;
        this.lastFadeCheck = 0L;
        this.logger = SoundSystemConfig.getLogger();
        this.priority = boolean2;
        this.toStream = true;
        this.toLoop = false;
        this.sourcename = string;
        this.filenameURL = null;
        this.soundBuffer = null;
        this.position = new Vector3D(float4, float5, float6);
        this.attModel = integer;
        this.distOrRoll = float8;
        this.temporary = false;
        this.rawDataStream = true;
        this.rawDataFormat = audioFormat;
    }
    
    public void cleanup() {
        if (this.codec != null) {
            this.codec.cleanup();
        }
        synchronized (this.soundSequenceLock) {
            if (this.soundSequenceQueue != null) {
                this.soundSequenceQueue.clear();
            }
            this.soundSequenceQueue = null;
        }
        this.sourcename = null;
        this.filenameURL = null;
        this.position = null;
        this.soundBuffer = null;
        this.codec = null;
    }
    
    public void queueSound(final FilenameURL filenameURL) {
        if (!this.toStream) {
            this.errorMessage("Method 'queueSound' may only be used for streaming and MIDI sources.");
            return;
        }
        if (filenameURL == null) {
            this.errorMessage("File not specified in method 'queueSound'");
            return;
        }
        synchronized (this.soundSequenceLock) {
            if (this.soundSequenceQueue == null) {
                this.soundSequenceQueue = new LinkedList();
            }
            this.soundSequenceQueue.add(filenameURL);
        }
    }
    
    public void dequeueSound(final String string) {
        if (!this.toStream) {
            this.errorMessage("Method 'dequeueSound' may only be used for streaming and MIDI sources.");
            return;
        }
        if (string == null || string.equals("")) {
            this.errorMessage("Filename not specified in method 'dequeueSound'");
            return;
        }
        synchronized (this.soundSequenceLock) {
            if (this.soundSequenceQueue != null) {
                this.soundSequenceQueue.remove(string);
            }
        }
        synchronized (this.soundSequenceLock) {
            if (this.soundSequenceQueue != null) {
                final ListIterator listIterator = this.soundSequenceQueue.listIterator();
                while (listIterator.hasNext()) {
                    if (((FilenameURL)listIterator.next()).getFilename().equals(string)) {
                        listIterator.remove();
                        break;
                    }
                }
            }
        }
    }
    
    public void fadeOut(final FilenameURL filenameURL, final long long2) {
        if (!this.toStream) {
            this.errorMessage("Method 'fadeOut' may only be used for streaming and MIDI sources.");
            return;
        }
        if (long2 < 0L) {
            this.errorMessage("Miliseconds may not be negative in method 'fadeOut'.");
            return;
        }
        this.fadeOutMilis = long2;
        this.fadeInMilis = 0L;
        this.fadeOutGain = 1.0f;
        this.lastFadeCheck = System.currentTimeMillis();
        synchronized (this.soundSequenceLock) {
            if (this.soundSequenceQueue != null) {
                this.soundSequenceQueue.clear();
            }
            if (filenameURL != null) {
                if (this.soundSequenceQueue == null) {
                    this.soundSequenceQueue = new LinkedList();
                }
                this.soundSequenceQueue.add(filenameURL);
            }
        }
    }
    
    public void fadeOutIn(final FilenameURL filenameURL, final long long2, final long long3) {
        if (!this.toStream) {
            this.errorMessage("Method 'fadeOutIn' may only be used for streaming and MIDI sources.");
            return;
        }
        if (filenameURL == null) {
            this.errorMessage("Filename/URL not specified in method 'fadeOutIn'.");
            return;
        }
        if (long2 < 0L || long3 < 0L) {
            this.errorMessage("Miliseconds may not be negative in method 'fadeOutIn'.");
            return;
        }
        this.fadeOutMilis = long2;
        this.fadeInMilis = long3;
        this.fadeOutGain = 1.0f;
        this.lastFadeCheck = System.currentTimeMillis();
        synchronized (this.soundSequenceLock) {
            if (this.soundSequenceQueue == null) {
                this.soundSequenceQueue = new LinkedList();
            }
            this.soundSequenceQueue.clear();
            this.soundSequenceQueue.add(filenameURL);
        }
    }
    
    public boolean checkFadeOut() {
        if (!this.toStream) {
            return false;
        }
        if (this.fadeOutGain == -1.0f && this.fadeInGain == 1.0f) {
            return false;
        }
        final long currentTimeMillis = System.currentTimeMillis();
        final long n = currentTimeMillis - this.lastFadeCheck;
        this.lastFadeCheck = currentTimeMillis;
        if (this.fadeOutGain >= 0.0f) {
            if (this.fadeOutMilis == 0L) {
                this.fadeOutGain = 0.0f;
                this.fadeInGain = 0.0f;
                if (!this.incrementSoundSequence()) {
                    this.stop();
                }
                this.positionChanged();
                this.preLoad = true;
                return false;
            }
            this.fadeOutGain -= n / (float)this.fadeOutMilis;
            if (this.fadeOutGain <= 0.0f) {
                this.fadeOutGain = -1.0f;
                this.fadeInGain = 0.0f;
                if (!this.incrementSoundSequence()) {
                    this.stop();
                }
                this.positionChanged();
                this.preLoad = true;
                return false;
            }
            this.positionChanged();
            return true;
        }
        else {
            if (this.fadeInGain < 1.0f) {
                this.fadeOutGain = -1.0f;
                if (this.fadeInMilis == 0L) {
                    this.fadeOutGain = -1.0f;
                    this.fadeInGain = 1.0f;
                }
                else {
                    this.fadeInGain += n / (float)this.fadeInMilis;
                    if (this.fadeInGain >= 1.0f) {
                        this.fadeOutGain = -1.0f;
                        this.fadeInGain = 1.0f;
                    }
                }
                this.positionChanged();
                return true;
            }
            return false;
        }
    }
    
    public boolean incrementSoundSequence() {
        if (!this.toStream) {
            this.errorMessage("Method 'incrementSoundSequence' may only be used for streaming and MIDI sources.");
            return false;
        }
        synchronized (this.soundSequenceLock) {
            if (this.soundSequenceQueue != null && this.soundSequenceQueue.size() > 0) {
                this.filenameURL = this.soundSequenceQueue.remove(0);
                if (this.codec != null) {
                    this.codec.cleanup();
                }
                this.codec = SoundSystemConfig.getCodec(this.filenameURL.getFilename());
                return true;
            }
        }
        return false;
    }
    
    public void setTemporary(final boolean boolean1) {
        this.temporary = boolean1;
    }
    
    public void listenerMoved() {
    }
    
    public void setPosition(final float float1, final float float2, final float float3) {
        this.position.x = float1;
        this.position.y = float2;
        this.position.z = float3;
    }
    
    public void positionChanged() {
    }
    
    public void setPriority(final boolean boolean1) {
        this.priority = boolean1;
    }
    
    public void setLooping(final boolean boolean1) {
        this.toLoop = boolean1;
    }
    
    public void setAttenuation(final int integer) {
        this.attModel = integer;
    }
    
    public void setDistOrRoll(final float float1) {
        this.distOrRoll = float1;
    }
    
    public float getDistanceFromListener() {
        return this.distanceFromListener;
    }
    
    public void setPitch(final float float1) {
        float pitch = float1;
        if (pitch < 0.5f) {
            pitch = 0.5f;
        }
        else if (pitch > 2.0f) {
            pitch = 2.0f;
        }
        this.pitch = pitch;
    }
    
    public float getPitch() {
        return this.pitch;
    }
    
    public boolean reverseByteOrderRequired() {
        return this.reverseByteOrder;
    }
    
    public void changeSource(final boolean boolean1, final boolean boolean2, final boolean boolean3, final String string, final FilenameURL filenameURL, final SoundBuffer soundBuffer, final float float7, final float float8, final float float9, final int integer, final float float11, final boolean boolean12) {
        this.priority = boolean1;
        this.toStream = boolean2;
        this.toLoop = boolean3;
        this.sourcename = string;
        this.filenameURL = filenameURL;
        this.soundBuffer = soundBuffer;
        this.position.x = float7;
        this.position.y = float8;
        this.position.z = float9;
        this.attModel = integer;
        this.distOrRoll = float11;
        this.temporary = boolean12;
    }
    
    public int feedRawAudioData(final Channel channel, final byte[] arr) {
        if (!this.active(false, false)) {
            this.toPlay = true;
            return -1;
        }
        if (this.channel != channel) {
            (this.channel = channel).close();
            this.channel.setAudioFormat(this.rawDataFormat);
            this.positionChanged();
        }
        this.stopped(true, false);
        this.paused(true, false);
        return this.channel.feedRawAudioData(arr);
    }
    
    public void play(final Channel channel) {
        if (!this.active(false, false)) {
            if (this.toLoop) {
                this.toPlay = true;
            }
            return;
        }
        if (this.channel != channel) {
            (this.channel = channel).close();
        }
        this.stopped(true, false);
        this.paused(true, false);
    }
    
    public boolean stream() {
        if (this.channel == null) {
            return false;
        }
        if (this.preLoad) {
            if (!this.rawDataStream) {
                return this.preLoad();
            }
            this.preLoad = false;
        }
        if (this.rawDataStream) {
            if (this.stopped() || this.paused()) {
                return true;
            }
            if (this.channel.buffersProcessed() > 0) {
                this.channel.processBuffer();
            }
        }
        else {
            if (this.codec == null) {
                return false;
            }
            if (this.stopped()) {
                return false;
            }
            if (this.paused()) {
                return true;
            }
            for (int buffersProcessed = this.channel.buffersProcessed(), i = 0; i < buffersProcessed; ++i) {
                final SoundBuffer read = this.codec.read();
                if (read != null) {
                    if (read.audioData != null) {
                        this.channel.queueBuffer(read.audioData);
                    }
                    read.cleanup();
                }
                if (this.codec.endOfStream()) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean preLoad() {
        if (this.channel == null) {
            return false;
        }
        if (this.codec == null) {
            return false;
        }
        this.codec.initialize(this.filenameURL.getURL());
        for (int i = 0; i < SoundSystemConfig.getNumberStreamingBuffers(); ++i) {
            final SoundBuffer read = this.codec.read();
            if (read != null) {
                if (this.soundBuffer.audioData != null) {
                    this.channel.queueBuffer(this.soundBuffer.audioData);
                }
                read.cleanup();
            }
        }
        return true;
    }
    
    public void pause() {
        this.toPlay = false;
        this.paused(true, true);
        if (this.channel != null) {
            this.channel.pause();
        }
        else {
            this.errorMessage("Channel null in method 'pause'");
        }
    }
    
    public void stop() {
        this.toPlay = false;
        this.stopped(true, true);
        this.paused(true, false);
        if (this.channel != null) {
            this.channel.stop();
        }
        else {
            this.errorMessage("Channel null in method 'stop'");
        }
    }
    
    public void rewind() {
        if (this.paused(false, false)) {
            this.stop();
        }
        if (this.channel != null) {
            final boolean playing = this.playing();
            this.channel.rewind();
            if (this.toStream && playing) {
                this.stop();
                this.play(this.channel);
            }
        }
        else {
            this.errorMessage("Channel null in method 'rewind'");
        }
    }
    
    public void flush() {
        if (this.channel != null) {
            this.channel.flush();
        }
        else {
            this.errorMessage("Channel null in method 'flush'");
        }
    }
    
    public void cull() {
        if (!this.active(false, false)) {
            return;
        }
        if (this.playing() && this.toLoop) {
            this.toPlay = true;
        }
        if (this.rawDataStream) {
            this.toPlay = true;
        }
        this.active(true, false);
        if (this.channel != null) {
            this.channel.close();
        }
        this.channel = null;
    }
    
    public void activate() {
        this.active(true, true);
    }
    
    public boolean active() {
        return this.active(false, false);
    }
    
    public boolean playing() {
        return this.channel != null && this.channel.attachedSource == this && !this.paused() && !this.stopped() && this.channel.playing();
    }
    
    public boolean stopped() {
        return this.stopped(false, false);
    }
    
    public boolean paused() {
        return this.paused(false, false);
    }
    
    private synchronized boolean active(final boolean boolean1, final boolean boolean2) {
        if (boolean1) {
            this.active = boolean2;
        }
        return this.active;
    }
    
    private synchronized boolean stopped(final boolean boolean1, final boolean boolean2) {
        if (boolean1) {
            this.stopped = boolean2;
        }
        return this.stopped;
    }
    
    private synchronized boolean paused(final boolean boolean1, final boolean boolean2) {
        if (boolean1) {
            this.paused = boolean2;
        }
        return this.paused;
    }
    
    public String getClassName() {
        final String libraryTitle = SoundSystemConfig.getLibraryTitle(this.libraryType);
        if (libraryTitle.equals("No Sound")) {
            return "Source";
        }
        return "Source" + libraryTitle;
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
