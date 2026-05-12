// 
// Decompiled by Procyon v0.6.0
// 

package paulscode.sound.libraries;

import paulscode.sound.ListenerData;
import paulscode.sound.Source;
import java.nio.ByteBuffer;
import javax.sound.sampled.AudioFormat;
import paulscode.sound.SoundBuffer;
import paulscode.sound.ICodec;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.FilenameURL;
import java.util.Iterator;
import java.nio.IntBuffer;
import paulscode.sound.Channel;
import org.lwjgl.openal.AL10;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import paulscode.sound.SoundSystemException;
import org.lwjgl.openal.AL;
import java.util.HashMap;
import java.nio.FloatBuffer;
import paulscode.sound.Library;

public class LibraryLWJGLOpenAL extends Library
{
    private static final boolean GET = false;
    private static final boolean SET = true;
    private static final boolean XXX = false;
    private FloatBuffer listenerPositionAL;
    private FloatBuffer listenerOrientation;
    private FloatBuffer listenerVelocity;
    private HashMap ALBufferMap;
    private static boolean alPitchSupported;
    
    public LibraryLWJGLOpenAL() {
        this.listenerPositionAL = null;
        this.listenerOrientation = null;
        this.listenerVelocity = null;
        this.ALBufferMap = null;
        this.ALBufferMap = new HashMap();
    }
    
    @Override
    public void init() {
        boolean checkALError;
        try {
            AL.create();
            checkALError = this.checkALError();
        }
        catch (final LWJGLException exception) {
            this.errorMessage("Unable to initialize OpenAL.  Probable cause: OpenAL not supported.");
            this.printStackTrace((Exception)exception);
            throw new SoundSystemException(exception.getMessage(), 6);
        }
        if (checkALError) {
            this.importantMessage("OpenAL did not initialize properly!");
        }
        else {
            this.message("OpenAL initialized.");
        }
        this.listenerPositionAL = BufferUtils.createFloatBuffer(3).put(new float[] { this.listener.position.x, this.listener.position.y, this.listener.position.z });
        this.listenerOrientation = BufferUtils.createFloatBuffer(6).put(new float[] { this.listener.lookAt.x, this.listener.lookAt.y, this.listener.lookAt.z, this.listener.up.x, this.listener.up.y, this.listener.up.z });
        this.listenerVelocity = BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f });
        this.listenerPositionAL.flip();
        this.listenerOrientation.flip();
        this.listenerVelocity.flip();
        AL10.alListener(4100, this.listenerPositionAL);
        final boolean b = this.checkALError() || checkALError;
        AL10.alListener(4111, this.listenerOrientation);
        final boolean b2 = this.checkALError() || b;
        AL10.alListener(4102, this.listenerVelocity);
        if (this.checkALError() || b2) {
            this.importantMessage("OpenAL did not initialize properly!");
            throw new SoundSystemException("Problem encountered while loading OpenAL or creating the listener.  Probably cause:  OpenAL not supported", 6);
        }
        super.init();
        final ChannelLWJGLOpenAL channelLWJGLOpenAL = this.normalChannels.get(1);
        try {
            AL10.alSourcef(channelLWJGLOpenAL.ALSource.get(0), 4099, 1.0f);
            if (this.checkALError()) {
                alPitchSupported(true, false);
                throw new SoundSystemException("OpenAL: AL_PITCH not supported.", 13);
            }
            alPitchSupported(true, true);
        }
        catch (final Exception ex) {
            alPitchSupported(true, false);
            throw new SoundSystemException("OpenAL: AL_PITCH not supported.", 13);
        }
    }
    
    public static boolean libraryCompatible() {
        if (AL.isCreated()) {
            return true;
        }
        try {
            AL.create();
        }
        catch (final Exception ex) {
            return false;
        }
        try {
            AL.destroy();
        }
        catch (final Exception ex2) {}
        return true;
    }
    
    @Override
    protected Channel createChannel(final int integer) {
        final IntBuffer intBuffer = BufferUtils.createIntBuffer(1);
        try {
            AL10.alGenSources(intBuffer);
        }
        catch (final Exception ex) {
            AL10.alGetError();
            return null;
        }
        if (AL10.alGetError() != 0) {
            return null;
        }
        return new ChannelLWJGLOpenAL(integer, intBuffer);
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        final Iterator iterator = this.bufferMap.keySet().iterator();
        while (iterator.hasNext()) {
            final IntBuffer intBuffer = this.ALBufferMap.get(iterator.next());
            if (intBuffer != null) {
                AL10.alDeleteBuffers(intBuffer);
                this.checkALError();
                intBuffer.clear();
            }
        }
        this.bufferMap.clear();
        AL.destroy();
        this.bufferMap = null;
        this.listenerPositionAL = null;
        this.listenerOrientation = null;
        this.listenerVelocity = null;
    }
    
    @Override
    public boolean loadSound(final FilenameURL filenameURL) {
        if (this.bufferMap == null) {
            this.bufferMap = new HashMap();
            this.importantMessage("Buffer Map was null in method 'loadSound'");
        }
        if (this.ALBufferMap == null) {
            this.ALBufferMap = new HashMap();
            this.importantMessage("Open AL Buffer Map was null in method'loadSound'");
        }
        if (this.errorCheck(filenameURL == null, "Filename/URL not specified in method 'loadSound'")) {
            return false;
        }
        if (this.bufferMap.get(filenameURL.getFilename()) != null) {
            return true;
        }
        final ICodec codec = SoundSystemConfig.getCodec(filenameURL.getFilename());
        if (this.errorCheck(codec == null, "No codec found for file '" + filenameURL.getFilename() + "' in method 'loadSound'")) {
            return false;
        }
        codec.initialize(filenameURL.getURL());
        final SoundBuffer all = codec.readAll();
        codec.cleanup();
        if (this.errorCheck(all == null, "Sound buffer null in method 'loadSound'")) {
            return false;
        }
        this.bufferMap.put(filenameURL.getFilename(), all);
        final AudioFormat audioFormat = all.audioFormat;
        int n;
        if (audioFormat.getChannels() == 1) {
            if (audioFormat.getSampleSizeInBits() == 8) {
                n = 4352;
            }
            else {
                if (audioFormat.getSampleSizeInBits() != 16) {
                    this.errorMessage("Illegal sample size in method 'loadSound'");
                    return false;
                }
                n = 4353;
            }
        }
        else {
            if (audioFormat.getChannels() != 2) {
                this.errorMessage("File neither mono nor stereo in method 'loadSound'");
                return false;
            }
            if (audioFormat.getSampleSizeInBits() == 8) {
                n = 4354;
            }
            else {
                if (audioFormat.getSampleSizeInBits() != 16) {
                    this.errorMessage("Illegal sample size in method 'loadSound'");
                    return false;
                }
                n = 4355;
            }
        }
        final IntBuffer intBuffer = BufferUtils.createIntBuffer(1);
        AL10.alGenBuffers(intBuffer);
        if (this.errorCheck(AL10.alGetError() != 0, "alGenBuffers error when loading " + filenameURL.getFilename())) {
            return false;
        }
        final ByteBuffer byteBuffer = BufferUtils.createByteBuffer(all.audioData.length);
        byteBuffer.clear();
        byteBuffer.put(all.audioData);
        byteBuffer.flip();
        AL10.alBufferData(intBuffer.get(0), n, byteBuffer, (int)audioFormat.getSampleRate());
        if (this.errorCheck(AL10.alGetError() != 0, "alBufferData error when loading " + filenameURL.getFilename()) && this.errorCheck(intBuffer == null, "Sound buffer was not created for " + filenameURL.getFilename())) {
            return false;
        }
        this.ALBufferMap.put(filenameURL.getFilename(), intBuffer);
        return true;
    }
    
    @Override
    public void unloadSound(final String string) {
        this.ALBufferMap.remove(string);
        super.unloadSound(string);
    }
    
    @Override
    public void setMasterVolume(final float float1) {
        super.setMasterVolume(float1);
        AL10.alListenerf(4106, float1);
        this.checkALError();
    }
    
    @Override
    public void newSource(final boolean boolean1, final boolean boolean2, final boolean boolean3, final String string, final FilenameURL filenameURL, final float float6, final float float7, final float float8, final int integer, final float float10) {
        IntBuffer intBuffer = null;
        if (!boolean2) {
            if (this.ALBufferMap.get(filenameURL.getFilename()) == null && !this.loadSound(filenameURL)) {
                this.errorMessage("Source '" + string + "' was not created " + "because an error occurred while loading " + filenameURL.getFilename());
                return;
            }
            intBuffer = this.ALBufferMap.get(filenameURL.getFilename());
            if (intBuffer == null) {
                this.errorMessage("Source '" + string + "' was not created " + "because a sound buffer was not found for " + filenameURL.getFilename());
                return;
            }
        }
        SoundBuffer soundBuffer = null;
        if (!boolean2) {
            if (this.bufferMap.get(filenameURL.getFilename()) == null && !this.loadSound(filenameURL)) {
                this.errorMessage("Source '" + string + "' was not created " + "because an error occurred while loading " + filenameURL.getFilename());
                return;
            }
            soundBuffer = this.bufferMap.get(filenameURL.getFilename());
            if (soundBuffer == null) {
                this.errorMessage("Source '" + string + "' was not created " + "because audio data was not found for " + filenameURL.getFilename());
                return;
            }
        }
        this.sourceMap.put(string, new SourceLWJGLOpenAL(this.listenerPositionAL, intBuffer, boolean1, boolean2, boolean3, string, filenameURL, soundBuffer, float6, float7, float8, integer, float10, false));
    }
    
    @Override
    public void rawDataStream(final AudioFormat audioFormat, final boolean boolean2, final String string, final float float4, final float float5, final float float6, final int integer, final float float8) {
        this.sourceMap.put(string, new SourceLWJGLOpenAL(this.listenerPositionAL, audioFormat, boolean2, string, float4, float5, float6, integer, float8));
    }
    
    @Override
    public void quickPlay(final boolean boolean1, final boolean boolean2, final boolean boolean3, final String string, final FilenameURL filenameURL, final float float6, final float float7, final float float8, final int integer, final float float10, final boolean boolean11) {
        IntBuffer intBuffer = null;
        if (!boolean2) {
            if (this.ALBufferMap.get(filenameURL.getFilename()) == null) {
                this.loadSound(filenameURL);
            }
            intBuffer = this.ALBufferMap.get(filenameURL.getFilename());
            if (intBuffer == null) {
                this.errorMessage("Sound buffer was not created for " + filenameURL.getFilename());
                return;
            }
        }
        SoundBuffer soundBuffer = null;
        if (!boolean2) {
            if (this.bufferMap.get(filenameURL.getFilename()) == null && !this.loadSound(filenameURL)) {
                this.errorMessage("Source '" + string + "' was not created " + "because an error occurred while loading " + filenameURL.getFilename());
                return;
            }
            soundBuffer = this.bufferMap.get(filenameURL.getFilename());
            if (soundBuffer == null) {
                this.errorMessage("Source '" + string + "' was not created " + "because audio data was not found for " + filenameURL.getFilename());
                return;
            }
        }
        final SourceLWJGLOpenAL sourceLWJGLOpenAL = new SourceLWJGLOpenAL(this.listenerPositionAL, intBuffer, boolean1, boolean2, boolean3, string, filenameURL, soundBuffer, float6, float7, float8, integer, float10, false);
        this.sourceMap.put(string, sourceLWJGLOpenAL);
        this.play(sourceLWJGLOpenAL);
        if (boolean11) {
            sourceLWJGLOpenAL.setTemporary(true);
        }
    }
    
    @Override
    public void copySources(final HashMap hashMap) {
        if (hashMap == null) {
            return;
        }
        final Iterator iterator = hashMap.keySet().iterator();
        if (this.bufferMap == null) {
            this.bufferMap = new HashMap();
            this.importantMessage("Buffer Map was null in method 'copySources'");
        }
        if (this.ALBufferMap == null) {
            this.ALBufferMap = new HashMap();
            this.importantMessage("Open AL Buffer Map was null in method'copySources'");
        }
        this.sourceMap.clear();
        while (iterator.hasNext()) {
            final String s = (String)iterator.next();
            final Source source = hashMap.get(s);
            if (source != null) {
                SoundBuffer soundBuffer = null;
                if (!source.toStream) {
                    this.loadSound(source.filenameURL);
                    soundBuffer = this.bufferMap.get(source.filenameURL.getFilename());
                }
                if (!source.toStream && soundBuffer == null) {
                    continue;
                }
                this.sourceMap.put(s, new SourceLWJGLOpenAL(this.listenerPositionAL, this.ALBufferMap.get(source.filenameURL.getFilename()), source, soundBuffer));
            }
        }
    }
    
    @Override
    public void setListenerPosition(final float float1, final float float2, final float float3) {
        super.setListenerPosition(float1, float2, float3);
        this.listenerPositionAL.put(0, float1);
        this.listenerPositionAL.put(1, float2);
        this.listenerPositionAL.put(2, float3);
        AL10.alListener(4100, this.listenerPositionAL);
        this.checkALError();
    }
    
    @Override
    public void setListenerAngle(final float float1) {
        super.setListenerAngle(float1);
        this.listenerOrientation.put(0, this.listener.lookAt.x);
        this.listenerOrientation.put(2, this.listener.lookAt.z);
        AL10.alListener(4111, this.listenerOrientation);
        this.checkALError();
    }
    
    @Override
    public void setListenerOrientation(final float float1, final float float2, final float float3, final float float4, final float float5, final float float6) {
        super.setListenerOrientation(float1, float2, float3, float4, float5, float6);
        this.listenerOrientation.put(0, float1);
        this.listenerOrientation.put(1, float2);
        this.listenerOrientation.put(2, float3);
        this.listenerOrientation.put(3, float4);
        this.listenerOrientation.put(4, float5);
        this.listenerOrientation.put(5, float6);
        AL10.alListener(4111, this.listenerOrientation);
        this.checkALError();
    }
    
    @Override
    public void setListenerData(final ListenerData listenerData) {
        super.setListenerData(listenerData);
        this.listenerPositionAL.put(0, listenerData.position.x);
        this.listenerPositionAL.put(1, listenerData.position.y);
        this.listenerPositionAL.put(2, listenerData.position.z);
        AL10.alListener(4100, this.listenerPositionAL);
        this.listenerOrientation.put(0, listenerData.lookAt.x);
        this.listenerOrientation.put(1, listenerData.lookAt.y);
        this.listenerOrientation.put(2, listenerData.lookAt.z);
        this.listenerOrientation.put(3, listenerData.up.x);
        this.listenerOrientation.put(4, listenerData.up.y);
        this.listenerOrientation.put(5, listenerData.up.z);
        AL10.alListener(4111, this.listenerOrientation);
        this.checkALError();
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
    
    public static boolean alPitchSupported() {
        return alPitchSupported(false, false);
    }
    
    private static synchronized boolean alPitchSupported(final boolean boolean1, final boolean boolean2) {
        if (boolean1) {
            LibraryLWJGLOpenAL.alPitchSupported = boolean2;
        }
        return LibraryLWJGLOpenAL.alPitchSupported;
    }
    
    public static String getTitle() {
        return "LWJGL OpenAL";
    }
    
    public static String getDescription() {
        return "The LWJGL binding of OpenAL.  For more information, see http://www.lwjgl.org";
    }
    
    @Override
    public String getClassName() {
        return "LibraryLWJGLOpenAL";
    }
    
    static {
        LibraryLWJGLOpenAL.alPitchSupported = true;
    }
}
