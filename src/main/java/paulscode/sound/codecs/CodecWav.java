// 
// Decompiled by Procyon v0.6.0
// 

package paulscode.sound.codecs;

import java.nio.ShortBuffer;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import javax.sound.sampled.AudioFormat;
import paulscode.sound.SoundBuffer;
import java.io.IOException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.InputStream;
import javax.sound.sampled.AudioSystem;
import java.io.BufferedInputStream;
import java.net.URL;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemLogger;
import javax.sound.sampled.AudioInputStream;
import paulscode.sound.ICodec;

public class CodecWav implements ICodec
{
    private static final boolean GET = false;
    private static final boolean SET = true;
    private static final boolean XXX = false;
    private boolean endOfStream;
    private boolean initialized;
    private AudioInputStream myAudioInputStream;
    private SoundSystemLogger logger;
    
    public void reverseByteOrder(final boolean boolean1) {
    }
    
    public CodecWav() {
        this.endOfStream = false;
        this.initialized = false;
        this.myAudioInputStream = null;
        this.logger = SoundSystemConfig.getLogger();
    }
    
    public boolean initialize(final URL uRL) {
        this.initialized(true, false);
        this.cleanup();
        if (uRL == null) {
            this.errorMessage("url null in method 'initialize'");
            this.cleanup();
            return false;
        }
        try {
            this.myAudioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(uRL.openStream()));
        }
        catch (final UnsupportedAudioFileException exception) {
            this.errorMessage("Unsupported audio format in method 'initialize'");
            this.printStackTrace(exception);
            return false;
        }
        catch (final IOException exception2) {
            this.errorMessage("Error setting up audio input stream in method 'initialize'");
            this.printStackTrace(exception2);
            return false;
        }
        this.endOfStream(true, false);
        this.initialized(true, true);
        return true;
    }
    
    public boolean initialized() {
        return this.initialized(false, false);
    }
    
    public SoundBuffer read() {
        if (this.myAudioInputStream == null) {
            return null;
        }
        final AudioFormat format = this.myAudioInputStream.getFormat();
        if (format == null) {
            this.errorMessage("Audio Format null in method 'read'");
            return null;
        }
        int n = 0;
        byte[] trimArray = new byte[SoundSystemConfig.getStreamingBufferSize()];
        try {
            while (!this.endOfStream(false, false) && n < trimArray.length) {
                final int read;
                if ((read = this.myAudioInputStream.read(trimArray, n, trimArray.length - n)) <= 0) {
                    this.endOfStream(true, true);
                    break;
                }
                n += read;
            }
        }
        catch (final IOException ex) {
            this.endOfStream(true, true);
            return null;
        }
        if (n <= 0) {
            return null;
        }
        if (n < trimArray.length) {
            trimArray = trimArray(trimArray, n);
        }
        return new SoundBuffer(convertAudioBytes(trimArray, format.getSampleSizeInBits() == 16), format);
    }
    
    public SoundBuffer readAll() {
        if (this.myAudioInputStream == null) {
            this.errorMessage("Audio input stream null in method 'readAll'");
            return null;
        }
        final AudioFormat format = this.myAudioInputStream.getFormat();
        if (format == null) {
            this.errorMessage("Audio Format null in method 'readAll'");
            return null;
        }
        byte[] appendByteArrays = null;
        if (format.getChannels() * (int)this.myAudioInputStream.getFrameLength() * format.getSampleSizeInBits() / 8 > 0) {
            appendByteArrays = new byte[format.getChannels() * (int)this.myAudioInputStream.getFrameLength() * format.getSampleSizeInBits() / 8];
            int off = 0;
            try {
                int read;
                while ((read = this.myAudioInputStream.read(appendByteArrays, off, appendByteArrays.length - off)) != -1 && off < appendByteArrays.length) {
                    off += read;
                }
            }
            catch (final IOException exception) {
                this.errorMessage("Exception thrown while reading from the AudioInputStream (location #1).");
                this.printStackTrace(exception);
                return null;
            }
        }
        else {
            int n = 0;
            int i;
            for (byte[] array = new byte[SoundSystemConfig.getFileChunkSize()]; !this.endOfStream(false, false) && n < SoundSystemConfig.getMaxFileSize(); n += i, appendByteArrays = appendByteArrays(appendByteArrays, array, i)) {
                i = 0;
                try {
                    while (i < array.length) {
                        final int read2;
                        if ((read2 = this.myAudioInputStream.read(array, i, array.length - i)) <= 0) {
                            this.endOfStream(true, true);
                            break;
                        }
                        i += read2;
                    }
                }
                catch (final IOException exception2) {
                    this.errorMessage("Exception thrown while reading from the AudioInputStream (location #2).");
                    this.printStackTrace(exception2);
                    return null;
                }
            }
        }
        final SoundBuffer soundBuffer = new SoundBuffer(convertAudioBytes(appendByteArrays, format.getSampleSizeInBits() == 16), format);
        try {
            this.myAudioInputStream.close();
        }
        catch (final IOException ex) {}
        return soundBuffer;
    }
    
    public boolean endOfStream() {
        return this.endOfStream(false, false);
    }
    
    public void cleanup() {
        if (this.myAudioInputStream != null) {
            try {
                this.myAudioInputStream.close();
            }
            catch (final Exception ex) {}
        }
        this.myAudioInputStream = null;
    }
    
    public AudioFormat getAudioFormat() {
        if (this.myAudioInputStream == null) {
            return null;
        }
        return this.myAudioInputStream.getFormat();
    }
    
    private synchronized boolean initialized(final boolean boolean1, final boolean boolean2) {
        if (boolean1) {
            this.initialized = boolean2;
        }
        return this.initialized;
    }
    
    private synchronized boolean endOfStream(final boolean boolean1, final boolean boolean2) {
        if (boolean1) {
            this.endOfStream = boolean2;
        }
        return this.endOfStream;
    }
    
    private static byte[] trimArray(final byte[] arr, final int integer) {
        Object o = null;
        if (arr != null && arr.length > integer) {
            o = new byte[integer];
            System.arraycopy(arr, 0, o, 0, integer);
        }
        return (byte[])o;
    }
    
    private static byte[] convertAudioBytes(final byte[] arr, final boolean boolean2) {
        final ByteBuffer allocateDirect = ByteBuffer.allocateDirect(arr.length);
        allocateDirect.order(ByteOrder.nativeOrder());
        final ByteBuffer wrap = ByteBuffer.wrap(arr);
        wrap.order(ByteOrder.LITTLE_ENDIAN);
        if (boolean2) {
            final ShortBuffer shortBuffer = allocateDirect.asShortBuffer();
            final ShortBuffer shortBuffer2 = wrap.asShortBuffer();
            while (shortBuffer2.hasRemaining()) {
                shortBuffer.put(shortBuffer2.get());
            }
        }
        else {
            while (wrap.hasRemaining()) {
                allocateDirect.put(wrap.get());
            }
        }
        allocateDirect.rewind();
        if (!allocateDirect.hasArray()) {
            final byte[] dst = new byte[allocateDirect.capacity()];
            allocateDirect.get(dst);
            allocateDirect.clear();
            return dst;
        }
        return allocateDirect.array();
    }
    
    private static byte[] appendByteArrays(final byte[] arr1, final byte[] arr2, final int integer) {
        if (arr1 == null && arr2 == null) {
            return null;
        }
        byte[] array;
        if (arr1 == null) {
            array = new byte[integer];
            System.arraycopy(arr2, 0, array, 0, integer);
        }
        else if (arr2 == null) {
            array = new byte[arr1.length];
            System.arraycopy(arr1, 0, array, 0, arr1.length);
        }
        else {
            array = new byte[arr1.length + integer];
            System.arraycopy(arr1, 0, array, 0, arr1.length);
            System.arraycopy(arr2, 0, array, arr1.length, integer);
        }
        return array;
    }
    
    private void errorMessage(final String string) {
        this.logger.errorMessage("CodecWav", string, 0);
    }
    
    private void printStackTrace(final Exception exception) {
        this.logger.printStackTrace(exception, 1);
    }
}
