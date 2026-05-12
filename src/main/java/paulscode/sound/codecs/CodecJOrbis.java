// 
// Decompiled by Procyon v0.6.0
// 

package paulscode.sound.codecs;

import java.nio.ByteOrder;
import paulscode.sound.SoundBuffer;
import java.net.UnknownServiceException;
import java.io.IOException;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemLogger;
import com.jcraft.jorbis.Info;
import com.jcraft.jorbis.Comment;
import com.jcraft.jorbis.Block;
import com.jcraft.jorbis.DspState;
import com.jcraft.jogg.SyncState;
import com.jcraft.jogg.StreamState;
import com.jcraft.jogg.Page;
import com.jcraft.jogg.Packet;
import javax.sound.sampled.AudioFormat;
import java.io.InputStream;
import java.net.URLConnection;
import java.net.URL;
import paulscode.sound.ICodec;

public class CodecJOrbis implements ICodec
{
    private static final boolean GET = false;
    private static final boolean SET = true;
    private static final boolean XXX = false;
    protected URL url;
    protected URLConnection urlConnection;
    private InputStream inputStream;
    private AudioFormat audioFormat;
    private boolean endOfStream;
    private boolean initialized;
    private byte[] buffer;
    private int bufferSize;
    private int count;
    private int index;
    private int convertedBufferSize;
    private float[][][] pcmInfo;
    private int[] pcmIndex;
    private Packet joggPacket;
    private Page joggPage;
    private StreamState joggStreamState;
    private SyncState joggSyncState;
    private DspState jorbisDspState;
    private Block jorbisBlock;
    private Comment jorbisComment;
    private Info jorbisInfo;
    private SoundSystemLogger logger;
    private static final boolean LITTLE_ENDIAN;
    
    public CodecJOrbis() {
        this.urlConnection = null;
        this.endOfStream = false;
        this.initialized = false;
        this.buffer = null;
        this.count = 0;
        this.index = 0;
        this.joggPacket = new Packet();
        this.joggPage = new Page();
        this.joggStreamState = new StreamState();
        this.joggSyncState = new SyncState();
        this.jorbisDspState = new DspState();
        this.jorbisBlock = new Block(this.jorbisDspState);
        this.jorbisComment = new Comment();
        this.jorbisInfo = new Info();
        this.logger = SoundSystemConfig.getLogger();
    }
    
    public void reverseByteOrder(final boolean boolean1) {
    }
    
    public boolean initialize(final URL uRL) {
        this.initialized(true, false);
        if (this.joggStreamState != null) {
            this.joggStreamState.clear();
        }
        if (this.jorbisBlock != null) {
            this.jorbisBlock.clear();
        }
        if (this.jorbisDspState != null) {
            this.jorbisDspState.clear();
        }
        if (this.jorbisInfo != null) {
            this.jorbisInfo.clear();
        }
        if (this.joggSyncState != null) {
            this.joggSyncState.clear();
        }
        if (this.inputStream != null) {
            try {
                this.inputStream.close();
            }
            catch (final IOException ex) {}
        }
        this.url = uRL;
        this.bufferSize = SoundSystemConfig.getStreamingBufferSize() / 2;
        this.buffer = null;
        this.count = 0;
        this.index = 0;
        this.joggStreamState = new StreamState();
        this.jorbisBlock = new Block(this.jorbisDspState);
        this.jorbisDspState = new DspState();
        this.jorbisInfo = new Info();
        this.joggSyncState = new SyncState();
        try {
            this.urlConnection = uRL.openConnection();
        }
        catch (final UnknownServiceException exception) {
            this.errorMessage("Unable to create a UrlConnection in method 'initialize'.");
            this.printStackTrace(exception);
            this.cleanup();
            return false;
        }
        catch (final IOException exception2) {
            this.errorMessage("Unable to create a UrlConnection in method 'initialize'.");
            this.printStackTrace(exception2);
            this.cleanup();
            return false;
        }
        if (this.urlConnection != null) {
            try {
                this.inputStream = this.openInputStream();
            }
            catch (final IOException exception3) {
                this.errorMessage("Unable to acquire inputstream in method 'initialize'.");
                this.printStackTrace(exception3);
                this.cleanup();
                return false;
            }
        }
        this.endOfStream(true, false);
        this.joggSyncState.init();
        this.joggSyncState.buffer(this.bufferSize);
        this.buffer = this.joggSyncState.data;
        try {
            if (!this.readHeader()) {
                this.errorMessage("Error reading the header");
                return false;
            }
        }
        catch (final IOException ex2) {
            this.errorMessage("Error reading the header");
            return false;
        }
        this.convertedBufferSize = this.bufferSize * 2;
        this.jorbisDspState.synthesis_init(this.jorbisInfo);
        this.jorbisBlock.init(this.jorbisDspState);
        this.audioFormat = new AudioFormat((float)this.jorbisInfo.rate, 16, this.jorbisInfo.channels, true, false);
        this.pcmInfo = new float[1][][];
        this.pcmIndex = new int[this.jorbisInfo.channels];
        this.initialized(true, true);
        return true;
    }
    
    protected InputStream openInputStream() {
        return this.urlConnection.getInputStream();
    }
    
    public boolean initialized() {
        return this.initialized(false, false);
    }
    
    public SoundBuffer read() {
        final byte[] bytes = this.readBytes();
        if (bytes == null) {
            return null;
        }
        return new SoundBuffer(bytes, this.audioFormat);
    }
    
    public SoundBuffer readAll() {
        byte[] array = this.readBytes();
        while (!this.endOfStream(false, false)) {
            array = appendByteArrays(array, this.readBytes());
            if (array != null && array.length >= SoundSystemConfig.getMaxFileSize()) {
                break;
            }
        }
        return new SoundBuffer(array, this.audioFormat);
    }
    
    public boolean endOfStream() {
        return this.endOfStream(false, false);
    }
    
    public void cleanup() {
        this.joggStreamState.clear();
        this.jorbisBlock.clear();
        this.jorbisDspState.clear();
        this.jorbisInfo.clear();
        this.joggSyncState.clear();
        if (this.inputStream != null) {
            try {
                this.inputStream.close();
            }
            catch (final IOException ex) {}
        }
        this.joggStreamState = null;
        this.jorbisBlock = null;
        this.jorbisDspState = null;
        this.jorbisInfo = null;
        this.joggSyncState = null;
        this.inputStream = null;
    }
    
    public AudioFormat getAudioFormat() {
        return this.audioFormat;
    }
    
    private boolean readHeader() {
        this.index = this.joggSyncState.buffer(this.bufferSize);
        int read = this.inputStream.read(this.joggSyncState.data, this.index, this.bufferSize);
        if (read < 0) {
            read = 0;
        }
        this.joggSyncState.wrote(read);
        if (this.joggSyncState.pageout(this.joggPage) != 1) {
            if (read < this.bufferSize) {
                return true;
            }
            this.errorMessage("Ogg header not recognized in method 'readHeader'.");
            return false;
        }
        else {
            this.joggStreamState.init(this.joggPage.serialno());
            this.jorbisInfo.init();
            this.jorbisComment.init();
            if (this.joggStreamState.pagein(this.joggPage) < 0) {
                this.errorMessage("Problem with first Ogg header page in method 'readHeader'.");
                return false;
            }
            if (this.joggStreamState.packetout(this.joggPacket) != 1) {
                this.errorMessage("Problem with first Ogg header packet in method 'readHeader'.");
                return false;
            }
            if (this.jorbisInfo.synthesis_headerin(this.jorbisComment, this.joggPacket) < 0) {
                this.errorMessage("File does not contain Vorbis header in method 'readHeader'.");
                return false;
            }
            int i = 0;
            while (i < 2) {
                while (i < 2) {
                    final int pageout = this.joggSyncState.pageout(this.joggPage);
                    if (pageout == 0) {
                        break;
                    }
                    if (pageout != 1) {
                        continue;
                    }
                    this.joggStreamState.pagein(this.joggPage);
                    while (i < 2) {
                        final int packetout = this.joggStreamState.packetout(this.joggPacket);
                        if (packetout == 0) {
                            break;
                        }
                        if (packetout == -1) {
                            this.errorMessage("Secondary Ogg header corrupt in method 'readHeader'.");
                            return false;
                        }
                        this.jorbisInfo.synthesis_headerin(this.jorbisComment, this.joggPacket);
                        ++i;
                    }
                }
                this.index = this.joggSyncState.buffer(this.bufferSize);
                int read2 = this.inputStream.read(this.joggSyncState.data, this.index, this.bufferSize);
                if (read2 < 0) {
                    read2 = 0;
                }
                if (read2 == 0 && i < 2) {
                    this.errorMessage("End of file reached before finished readingOgg header in method 'readHeader'");
                    return false;
                }
                this.joggSyncState.wrote(read2);
            }
            this.index = this.joggSyncState.buffer(this.bufferSize);
            this.buffer = this.joggSyncState.data;
            return true;
        }
    }
    
    private byte[] readBytes() {
        if (!this.initialized(false, false)) {
            return null;
        }
        if (this.endOfStream(false, false)) {
            return null;
        }
        byte[] appendByteArrays = null;
        switch (this.joggSyncState.pageout(this.joggPage)) {
            case -1:
            case 0: {
                this.endOfStream(true, true);
                break;
            }
            case 1: {
                this.joggStreamState.pagein(this.joggPage);
                if (this.joggPage.granulepos() == 0L) {
                    this.endOfStream(true, true);
                    break;
                }
            Label_0140:
                while (true) {
                    switch (this.joggStreamState.packetout(this.joggPacket)) {
                        case -1:
                        case 0: {
                            break Label_0140;
                        }
                        case 1: {
                            appendByteArrays = appendByteArrays(appendByteArrays, this.decodeCurrentPacket());
                            continue;
                        }
                    }
                }
                if (this.joggPage.eos() != 0) {
                    this.endOfStream(true, true);
                    break;
                }
                break;
            }
        }
        if (!this.endOfStream(false, false)) {
            this.index = this.joggSyncState.buffer(this.bufferSize);
            if (this.index == -1) {
                this.endOfStream(true, true);
            }
            else {
                this.buffer = this.joggSyncState.data;
                try {
                    this.count = this.inputStream.read(this.buffer, this.index, this.bufferSize);
                }
                catch (final Exception exception) {
                    this.printStackTrace(exception);
                    return appendByteArrays;
                }
                this.joggSyncState.wrote(this.count);
                if (this.count == 0) {
                    this.endOfStream(true, true);
                }
            }
        }
        return appendByteArrays;
    }
    
    private byte[] decodeCurrentPacket() {
        final byte[] arr = new byte[this.convertedBufferSize];
        if (this.jorbisBlock.synthesis(this.joggPacket) == 0) {
            this.jorbisDspState.synthesis_blockin(this.jorbisBlock);
        }
        final int n = this.convertedBufferSize / (this.jorbisInfo.channels * 2);
        int integer = 0;
        int synthesis_pcmout;
        while (integer < this.convertedBufferSize && (synthesis_pcmout = this.jorbisDspState.synthesis_pcmout(this.pcmInfo, this.pcmIndex)) > 0) {
            int integer2;
            if (synthesis_pcmout < n) {
                integer2 = synthesis_pcmout;
            }
            else {
                integer2 = n;
            }
            for (int i = 0; i < this.jorbisInfo.channels; ++i) {
                int n2 = i * 2;
                for (int j = 0; j < integer2; ++j) {
                    int n3 = (int)(this.pcmInfo[0][i][this.pcmIndex[i] + j] * 32767.0f);
                    if (n3 > 32767) {
                        n3 = 32767;
                    }
                    if (n3 < -32768) {
                        n3 = -32768;
                    }
                    if (n3 < 0) {
                        n3 |= 0x8000;
                    }
                    if (CodecJOrbis.LITTLE_ENDIAN) {
                        arr[integer + n2] = (byte)n3;
                        arr[integer + n2 + 1] = (byte)(n3 >>> 8);
                    }
                    else {
                        arr[integer + n2 + 1] = (byte)n3;
                        arr[integer + n2] = (byte)(n3 >>> 8);
                    }
                    n2 += 2 * this.jorbisInfo.channels;
                }
            }
            integer += integer2 * this.jorbisInfo.channels * 2;
            this.jorbisDspState.synthesis_read(integer2);
        }
        return trimArray(arr, integer);
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
    
    private static byte[] appendByteArrays(final byte[] arr1, final byte[] arr2) {
        if (arr1 == null && arr2 == null) {
            return null;
        }
        byte[] array;
        if (arr1 == null) {
            array = new byte[arr2.length];
            System.arraycopy(arr2, 0, array, 0, arr2.length);
        }
        else if (arr2 == null) {
            array = new byte[arr1.length];
            System.arraycopy(arr1, 0, array, 0, arr1.length);
        }
        else {
            array = new byte[arr1.length + arr2.length];
            System.arraycopy(arr1, 0, array, 0, arr1.length);
            System.arraycopy(arr2, 0, array, arr1.length, arr2.length);
        }
        return array;
    }
    
    private void errorMessage(final String string) {
        this.logger.errorMessage("CodecJOrbis", string, 0);
    }
    
    private void printStackTrace(final Exception exception) {
        this.logger.printStackTrace(exception, 1);
    }
    
    static {
        LITTLE_ENDIAN = (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN);
    }
}
