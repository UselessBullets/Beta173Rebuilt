// 
// Decompiled by Procyon v0.6.0
// 

package paulscode.sound;

import javax.sound.sampled.AudioFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;

public class Library
{
    private SoundSystemLogger logger;
    protected ListenerData listener;
    protected HashMap bufferMap;
    protected HashMap sourceMap;
    private MidiChannel midiChannel;
    protected List streamingChannels;
    protected List normalChannels;
    private String[] streamingChannelSourceNames;
    private String[] normalChannelSourceNames;
    private int nextStreamingChannel;
    private int nextNormalChannel;
    protected StreamThread streamThread;
    
    public Library() {
        this.bufferMap = null;
        this.nextStreamingChannel = 0;
        this.nextNormalChannel = 0;
        this.logger = SoundSystemConfig.getLogger();
        this.bufferMap = new HashMap();
        this.sourceMap = new HashMap();
        this.listener = new ListenerData(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f, 0.0f);
        this.streamingChannels = new LinkedList();
        this.normalChannels = new LinkedList();
        this.streamingChannelSourceNames = new String[SoundSystemConfig.getNumberStreamingChannels()];
        this.normalChannelSourceNames = new String[SoundSystemConfig.getNumberNormalChannels()];
        (this.streamThread = new StreamThread()).start();
    }
    
    public void cleanup() {
        this.streamThread.kill();
        this.streamThread.interrupt();
        for (int n = 0; n < 50 && this.streamThread.alive(); ++n) {
            try {
                Thread.sleep(100L);
            }
            catch (final Exception ex) {}
        }
        if (this.streamThread.alive()) {
            this.errorMessage("Stream thread did not die!");
            this.message("Ignoring errors... continuing clean-up.");
        }
        if (this.midiChannel != null) {
            this.midiChannel.cleanup();
            this.midiChannel = null;
        }
        if (this.streamingChannels != null) {
            while (!this.streamingChannels.isEmpty()) {
                final Channel channel = this.streamingChannels.remove(0);
                channel.close();
                channel.cleanup();
            }
            this.streamingChannels.clear();
            this.streamingChannels = null;
        }
        if (this.normalChannels != null) {
            while (!this.normalChannels.isEmpty()) {
                final Channel channel2 = this.normalChannels.remove(0);
                channel2.close();
                channel2.cleanup();
            }
            this.normalChannels.clear();
            this.normalChannels = null;
        }
        final Iterator iterator = this.sourceMap.keySet().iterator();
        while (iterator.hasNext()) {
            final Source source = this.sourceMap.get(iterator.next());
            if (source != null) {
                source.cleanup();
            }
        }
        this.sourceMap.clear();
        this.sourceMap = null;
        this.listener = null;
        this.streamThread = null;
    }
    
    public void init() {
        for (int i = 0; i < SoundSystemConfig.getNumberStreamingChannels(); ++i) {
            final Channel channel = this.createChannel(1);
            if (channel == null) {
                break;
            }
            this.streamingChannels.add(channel);
        }
        for (int j = 0; j < SoundSystemConfig.getNumberNormalChannels(); ++j) {
            final Channel channel2 = this.createChannel(0);
            if (channel2 == null) {
                break;
            }
            this.normalChannels.add(channel2);
        }
    }
    
    public static boolean libraryCompatible() {
        return true;
    }
    
    protected Channel createChannel(final int integer) {
        return null;
    }
    
    public boolean loadSound(final FilenameURL filenameURL) {
        return true;
    }
    
    public void unloadSound(final String string) {
        this.bufferMap.remove(string);
    }
    
    public void rawDataStream(final AudioFormat audioFormat, final boolean boolean2, final String string, final float float4, final float float5, final float float6, final int integer, final float float8) {
        this.sourceMap.put(string, new Source(audioFormat, boolean2, string, float4, float5, float6, integer, float8));
    }
    
    public void newSource(final boolean boolean1, final boolean boolean2, final boolean boolean3, final String string, final FilenameURL filenameURL, final float float6, final float float7, final float float8, final int integer, final float float10) {
        this.sourceMap.put(string, new Source(boolean1, boolean2, boolean3, string, filenameURL, null, float6, float7, float8, integer, float10, false));
    }
    
    public void quickPlay(final boolean boolean1, final boolean boolean2, final boolean boolean3, final String string, final FilenameURL filenameURL, final float float6, final float float7, final float float8, final int integer, final float float10, final boolean boolean11) {
        this.sourceMap.put(string, new Source(boolean1, boolean2, boolean3, string, filenameURL, null, float6, float7, float8, integer, float10, boolean11));
    }
    
    public void setTemporary(final String string, final boolean boolean2) {
        final Source source = this.sourceMap.get(string);
        if (source != null) {
            source.setTemporary(boolean2);
        }
    }
    
    public void setPosition(final String string, final float float2, final float float3, final float float4) {
        final Source source = this.sourceMap.get(string);
        if (source != null) {
            source.setPosition(float2, float3, float4);
        }
    }
    
    public void setPriority(final String string, final boolean boolean2) {
        final Source source = this.sourceMap.get(string);
        if (source != null) {
            source.setPriority(boolean2);
        }
    }
    
    public void setLooping(final String string, final boolean boolean2) {
        final Source source = this.sourceMap.get(string);
        if (source != null) {
            source.setLooping(boolean2);
        }
    }
    
    public void setAttenuation(final String string, final int integer) {
        final Source source = this.sourceMap.get(string);
        if (source != null) {
            source.setAttenuation(integer);
        }
    }
    
    public void setDistOrRoll(final String string, final float float2) {
        final Source source = this.sourceMap.get(string);
        if (source != null) {
            source.setDistOrRoll(float2);
        }
    }
    
    public int feedRawAudioData(final String string, final byte[] arr) {
        if (string == null || string.equals("")) {
            this.errorMessage("Sourcename not specified in method 'feedRawAudioData'");
            return -1;
        }
        if (this.midiSourcename(string)) {
            this.errorMessage("Raw audio data can not be fed to the MIDI channel.");
            return -1;
        }
        final Source source = this.sourceMap.get(string);
        if (source == null) {
            this.errorMessage("Source '" + string + "' not found in " + "method 'feedRawAudioData'");
        }
        return this.feedRawAudioData(source, arr);
    }
    
    public int feedRawAudioData(final Source source, final byte[] arr) {
        if (source == null) {
            this.errorMessage("Source parameter null in method 'feedRawAudioData'");
            return -1;
        }
        if (!source.toStream) {
            this.errorMessage("Only a streaming source may be specified in method 'feedRawAudioData'");
            return -1;
        }
        if (!source.rawDataStream) {
            this.errorMessage("Streaming source already associated with a file or URL in method'feedRawAudioData'");
            return -1;
        }
        if (!source.playing() || source.channel == null) {
            Channel channel;
            if (source.channel != null && source.channel.attachedSource == source) {
                channel = source.channel;
            }
            else {
                channel = this.getNextChannel(source);
            }
            final int feedRawAudioData = source.feedRawAudioData(channel, arr);
            channel.attachedSource = source;
            this.streamThread.watch(source);
            this.streamThread.interrupt();
            return feedRawAudioData;
        }
        return source.feedRawAudioData(source.channel, arr);
    }
    
    public void play(final String string) {
        if (string == null || string.equals("")) {
            this.errorMessage("Sourcename not specified in method 'play'");
            return;
        }
        if (this.midiSourcename(string)) {
            this.midiChannel.play();
        }
        else {
            final Source source = this.sourceMap.get(string);
            if (source == null) {
                this.errorMessage("Source '" + string + "' not found in " + "method 'play'");
            }
            this.play(source);
        }
    }
    
    public void play(final Source source) {
        if (source == null) {
            return;
        }
        if (source.rawDataStream) {
            return;
        }
        if (!source.active()) {
            return;
        }
        if (!source.playing()) {
            final Channel nextChannel = this.getNextChannel(source);
            if (source != null && nextChannel != null) {
                if (source.channel != null && source.channel.attachedSource != source) {
                    source.channel = null;
                }
                (nextChannel.attachedSource = source).play(nextChannel);
                if (source.toStream) {
                    this.streamThread.watch(source);
                    this.streamThread.interrupt();
                }
            }
        }
    }
    
    public void stop(final String string) {
        if (string == null || string.equals("")) {
            this.errorMessage("Sourcename not specified in method 'stop'");
            return;
        }
        if (this.midiSourcename(string)) {
            this.midiChannel.stop();
        }
        else {
            final Source source = this.sourceMap.get(string);
            if (source != null) {
                source.stop();
            }
        }
    }
    
    public void pause(final String string) {
        if (string == null || string.equals("")) {
            this.errorMessage("Sourcename not specified in method 'stop'");
            return;
        }
        if (this.midiSourcename(string)) {
            this.midiChannel.pause();
        }
        else {
            final Source source = this.sourceMap.get(string);
            if (source != null) {
                source.pause();
            }
        }
    }
    
    public void rewind(final String string) {
        if (this.midiSourcename(string)) {
            this.midiChannel.rewind();
        }
        else {
            final Source source = this.sourceMap.get(string);
            if (source != null) {
                source.rewind();
            }
        }
    }
    
    public void flush(final String string) {
        if (this.midiSourcename(string)) {
            this.errorMessage("You can not flush the MIDI channel");
        }
        else {
            final Source source = this.sourceMap.get(string);
            if (source != null) {
                source.flush();
            }
        }
    }
    
    public void cull(final String string) {
        final Source source = this.sourceMap.get(string);
        if (source != null) {
            source.cull();
        }
    }
    
    public void activate(final String string) {
        final Source source = this.sourceMap.get(string);
        if (source != null) {
            source.activate();
            if (source.toPlay) {
                this.play(source);
            }
        }
    }
    
    public void setMasterVolume(final float float1) {
        SoundSystemConfig.setMasterGain(float1);
        if (this.midiChannel != null) {
            this.midiChannel.resetGain();
        }
    }
    
    public void setVolume(final String string, final float float2) {
        if (this.midiSourcename(string)) {
            this.midiChannel.setVolume(float2);
        }
        else {
            final Source source = this.sourceMap.get(string);
            if (source != null) {
                float sourceVolume = float2;
                if (sourceVolume < 0.0f) {
                    sourceVolume = 0.0f;
                }
                else if (sourceVolume > 1.0f) {
                    sourceVolume = 1.0f;
                }
                source.sourceVolume = sourceVolume;
                source.positionChanged();
            }
        }
    }
    
    public float getVolume(final String string) {
        if (this.midiSourcename(string)) {
            return this.midiChannel.getVolume();
        }
        final Source source = this.sourceMap.get(string);
        if (source != null) {
            return source.sourceVolume;
        }
        return 0.0f;
    }
    
    public void setPitch(final String string, final float float2) {
        if (!this.midiSourcename(string)) {
            final Source source = this.sourceMap.get(string);
            if (source != null) {
                float pitch = float2;
                if (pitch < 0.5f) {
                    pitch = 0.5f;
                }
                else if (pitch > 2.0f) {
                    pitch = 2.0f;
                }
                source.setPitch(pitch);
                source.positionChanged();
            }
        }
    }
    
    public float getPitch(final String string) {
        if (!this.midiSourcename(string)) {
            final Source source = this.sourceMap.get(string);
            if (source != null) {
                return source.getPitch();
            }
        }
        return 1.0f;
    }
    
    public void moveListener(final float float1, final float float2, final float float3) {
        this.setListenerPosition(this.listener.position.x + float1, this.listener.position.y + float2, this.listener.position.z + float3);
    }
    
    public void setListenerPosition(final float float1, final float float2, final float float3) {
        this.listener.setPosition(float1, float2, float3);
        final Iterator iterator = this.sourceMap.keySet().iterator();
        while (iterator.hasNext()) {
            final Source source = this.sourceMap.get(iterator.next());
            if (source != null) {
                source.positionChanged();
            }
        }
    }
    
    public void turnListener(final float float1) {
        this.setListenerAngle(this.listener.angle + float1);
    }
    
    public void setListenerAngle(final float float1) {
        this.listener.setAngle(float1);
        final Iterator iterator = this.sourceMap.keySet().iterator();
        while (iterator.hasNext()) {
            final Source source = this.sourceMap.get(iterator.next());
            if (source != null) {
                source.positionChanged();
            }
        }
    }
    
    public void setListenerOrientation(final float float1, final float float2, final float float3, final float float4, final float float5, final float float6) {
        this.listener.setOrientation(float1, float2, float3, float4, float5, float6);
        final Iterator iterator = this.sourceMap.keySet().iterator();
        while (iterator.hasNext()) {
            final Source source = this.sourceMap.get(iterator.next());
            if (source != null) {
                source.positionChanged();
            }
        }
    }
    
    public void setListenerData(final ListenerData listenerData) {
        this.listener.setData(listenerData);
    }
    
    public void copySources(final HashMap hashMap) {
        if (hashMap == null) {
            return;
        }
        final Iterator iterator = hashMap.keySet().iterator();
        this.sourceMap.clear();
        while (iterator.hasNext()) {
            final String s = (String)iterator.next();
            final Source source = hashMap.get(s);
            if (source != null) {
                this.loadSound(source.filenameURL);
                this.sourceMap.put(s, new Source(source, null));
            }
        }
    }
    
    public void removeSource(final String string) {
        final Source source = this.sourceMap.get(string);
        if (source != null) {
            source.cleanup();
        }
        this.sourceMap.remove(string);
    }
    
    public void removeTemporarySources() {
        final Iterator iterator = this.sourceMap.keySet().iterator();
        while (iterator.hasNext()) {
            final Source source = this.sourceMap.get(iterator.next());
            if (source != null && source.temporary && !source.playing()) {
                source.cleanup();
                iterator.remove();
            }
        }
    }
    
    private Channel getNextChannel(final Source source) {
        if (source == null) {
            return null;
        }
        final String sourcename = source.sourcename;
        if (sourcename == null) {
            return null;
        }
        int n;
        List list;
        String[] array;
        if (source.toStream) {
            n = this.nextStreamingChannel;
            list = this.streamingChannels;
            array = this.streamingChannelSourceNames;
        }
        else {
            n = this.nextNormalChannel;
            list = this.normalChannels;
            array = this.normalChannelSourceNames;
        }
        final int size = list.size();
        for (int i = 0; i < size; ++i) {
            if (sourcename.equals(array[i])) {
                return (Channel)list.get(i);
            }
        }
        int n2 = n;
        for (int j = 0; j < size; ++j) {
            final String key = array[n2];
            Source source2;
            if (key == null) {
                source2 = null;
            }
            else {
                source2 = this.sourceMap.get(key);
            }
            if (source2 == null || !source2.playing()) {
                if (source.toStream) {
                    this.nextStreamingChannel = n2 + 1;
                    if (this.nextStreamingChannel >= size) {
                        this.nextStreamingChannel = 0;
                    }
                }
                else {
                    this.nextNormalChannel = n2 + 1;
                    if (this.nextNormalChannel >= size) {
                        this.nextNormalChannel = 0;
                    }
                }
                array[n2] = sourcename;
                return (Channel)list.get(n2);
            }
            if (++n2 >= size) {
                n2 = 0;
            }
        }
        int n3 = n;
        for (int k = 0; k < size; ++k) {
            final String key2 = array[n3];
            Source source3;
            if (key2 == null) {
                source3 = null;
            }
            else {
                source3 = this.sourceMap.get(key2);
            }
            if (source3 == null || !source3.playing() || !source3.priority) {
                if (source.toStream) {
                    this.nextStreamingChannel = n3 + 1;
                    if (this.nextStreamingChannel >= size) {
                        this.nextStreamingChannel = 0;
                    }
                }
                else {
                    this.nextNormalChannel = n3 + 1;
                    if (this.nextNormalChannel >= size) {
                        this.nextNormalChannel = 0;
                    }
                }
                array[n3] = sourcename;
                return (Channel)list.get(n3);
            }
            if (++n3 >= size) {
                n3 = 0;
            }
        }
        return null;
    }
    
    public void replaySources() {
        for (final String s : this.sourceMap.keySet()) {
            final Source source = this.sourceMap.get(s);
            if (source != null && source.toPlay && !source.playing()) {
                this.play(s);
                source.toPlay = false;
            }
        }
    }
    
    public void queueSound(final String string, final FilenameURL filenameURL) {
        if (this.midiSourcename(string)) {
            this.midiChannel.queueSound(filenameURL);
        }
        else {
            final Source source = this.sourceMap.get(string);
            if (source != null) {
                source.queueSound(filenameURL);
            }
        }
    }
    
    public void dequeueSound(final String string1, final String string2) {
        if (this.midiSourcename(string1)) {
            this.midiChannel.dequeueSound(string2);
        }
        else {
            final Source source = this.sourceMap.get(string1);
            if (source != null) {
                source.dequeueSound(string2);
            }
        }
    }
    
    public void fadeOut(final String string, final FilenameURL filenameURL, final long long3) {
        if (this.midiSourcename(string)) {
            this.midiChannel.fadeOut(filenameURL, long3);
        }
        else {
            final Source source = this.sourceMap.get(string);
            if (source != null) {
                source.fadeOut(filenameURL, long3);
            }
        }
    }
    
    public void fadeOutIn(final String string, final FilenameURL filenameURL, final long long3, final long long4) {
        if (this.midiSourcename(string)) {
            this.midiChannel.fadeOutIn(filenameURL, long3, long4);
        }
        else {
            final Source source = this.sourceMap.get(string);
            if (source != null) {
                source.fadeOutIn(filenameURL, long3, long4);
            }
        }
    }
    
    public void checkFadeVolumes() {
        if (this.midiChannel != null) {
            this.midiChannel.resetGain();
        }
        for (int i = 0; i < this.streamingChannels.size(); ++i) {
            final Channel channel = this.streamingChannels.get(i);
            if (channel != null) {
                final Source attachedSource = channel.attachedSource;
                if (attachedSource != null) {
                    attachedSource.checkFadeOut();
                }
            }
        }
    }
    
    public void loadMidi(final boolean boolean1, final String string, final FilenameURL filenameURL) {
        if (filenameURL == null) {
            this.errorMessage("Filename/URL not specified in method 'loadMidi'.");
            return;
        }
        if (!filenameURL.getFilename().matches(".*[mM][iI][dD][iI]?$")) {
            this.errorMessage("Filename/identifier doesn't end in '.mid' or'.midi' in method loadMidi.");
            return;
        }
        if (this.midiChannel == null) {
            this.midiChannel = new MidiChannel(boolean1, string, filenameURL);
        }
        else {
            this.midiChannel.switchSource(boolean1, string, filenameURL);
        }
    }
    
    public void unloadMidi() {
        if (this.midiChannel != null) {
            this.midiChannel.cleanup();
        }
        this.midiChannel = null;
    }
    
    public boolean midiSourcename(final String string) {
        return this.midiChannel != null && string != null && this.midiChannel.getSourcename() != null && !string.equals("") && string.equals(this.midiChannel.getSourcename());
    }
    
    public Source getSource(final String string) {
        return this.sourceMap.get(string);
    }
    
    public MidiChannel getMidiChannel() {
        return this.midiChannel;
    }
    
    public void setMidiChannel(final MidiChannel midiChannel) {
        if (this.midiChannel != null && this.midiChannel != midiChannel) {
            this.midiChannel.cleanup();
        }
        this.midiChannel = midiChannel;
    }
    
    public void listenerMoved() {
        final Iterator iterator = this.sourceMap.keySet().iterator();
        while (iterator.hasNext()) {
            final Source source = this.sourceMap.get(iterator.next());
            if (source != null) {
                source.listenerMoved();
            }
        }
    }
    
    public HashMap getSources() {
        return this.sourceMap;
    }
    
    public ListenerData getListenerData() {
        return this.listener;
    }
    
    public static String getTitle() {
        return "No Sound";
    }
    
    public static String getDescription() {
        return "Silent Mode";
    }
    
    public String getClassName() {
        return "Library";
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
