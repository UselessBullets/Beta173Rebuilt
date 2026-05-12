// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.storage;

import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.io.DataOutputStream;
import java.util.zip.InflaterInputStream;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.io.RandomAccessFile;
import java.io.File;

public class RegionFile
{
    private static final byte[] emptySection;
    private final File fileName;
    private RandomAccessFile saveFile;
    private final int[] offsets;
    private final int[] chunkTimestamps;
    private ArrayList sectorFree;
    private int sizeDelta;
    private long lastModified;
    
    public RegionFile(final File path) {
        this.lastModified = 0L;
        this.offsets = new int[1024];
        this.chunkTimestamps = new int[1024];
        this.fileName = path;
        this.debugln("REGION LOAD " + this.fileName);
        this.sizeDelta = 0;
        try {
            if (path.exists()) {
                this.lastModified = path.lastModified();
            }
            this.saveFile = new RandomAccessFile(path, "rw");
            if (this.saveFile.length() < 4096L) {
                for (int i = 0; i < 1024; ++i) {
                    this.saveFile.writeInt(0);
                }
                for (int j = 0; j < 1024; ++j) {
                    this.saveFile.writeInt(0);
                }
                this.sizeDelta += 8192;
            }
            if ((this.saveFile.length() & 0xFFFL) != 0x0L) {
                for (int n = 0; n < (this.saveFile.length() & 0xFFFL); ++n) {
                    this.saveFile.write(0);
                }
            }
            final int initialCapacity = (int)this.saveFile.length() / 4096;
            this.sectorFree = new ArrayList(initialCapacity);
            for (int k = 0; k < initialCapacity; ++k) {
                this.sectorFree.add(true);
            }
            this.sectorFree.set(0, false);
            this.sectorFree.set(1, false);
            this.saveFile.seek(0L);
            for (int l = 0; l < 1024; ++l) {
                final int int1 = this.saveFile.readInt();
                this.offsets[l] = int1;
                if (int1 != 0 && (int1 >> 8) + (int1 & 0xFF) <= this.sectorFree.size()) {
                    for (int n2 = 0; n2 < (int1 & 0xFF); ++n2) {
                        this.sectorFree.set((int1 >> 8) + n2, false);
                    }
                }
            }
            for (int n3 = 0; n3 < 1024; ++n3) {
                this.chunkTimestamps[n3] = this.saveFile.readInt();
            }
        }
        catch (final IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public synchronized int getSizeDelta() {
        final int sizeDelta = this.sizeDelta;
        this.sizeDelta = 0;
        return sizeDelta;
    }
    
    private void debug(final String msg) {
    }
    
    private void debugln(final String msg) {
        this.debug(msg + "\n");
    }
    
    private void debug(final String function, final int x, final int z, final String error) {
        this.debug("REGION " + function + " " + this.fileName.getName() + "[" + x + "," + z + "] = " + error);
    }
    
    private void debug(final String function, final int x, final int z, final int length, final String error) {
        this.debug("REGION " + function + " " + this.fileName.getName() + "[" + x + "," + z + "] " + length + "B = " + error);
    }
    
    private void debugln(final String function, final int x, final int z, final String error) {
        this.debug(function, x, z, error + "\n");
    }
    
    public synchronized DataInputStream getChunkDataInputStream(final int x, final int z) {
        if (this.outOfBounds(x, z)) {
            this.debugln("READ", x, z, "out of bounds");
            return null;
        }
        try {
            final int offset = this.getOffset(x, z);
            if (offset == 0) {
                return null;
            }
            final int n = offset >> 8;
            final int i = offset & 0xFF;
            if (n + i > this.sectorFree.size()) {
                this.debugln("READ", x, z, "invalid sector");
                return null;
            }
            this.saveFile.seek(n * 4096);
            final int int1 = this.saveFile.readInt();
            if (int1 > 4096 * i) {
                this.debugln("READ", x, z, "invalid length: " + int1 + " > 4096 * " + i);
                return null;
            }
            final byte byte1 = this.saveFile.readByte();
            if (byte1 == 1) {
                final byte[] array = new byte[int1 - 1];
                this.saveFile.read(array);
                return new DataInputStream(new GZIPInputStream(new ByteArrayInputStream(array)));
            }
            if (byte1 == 2) {
                final byte[] array2 = new byte[int1 - 1];
                this.saveFile.read(array2);
                return new DataInputStream(new InflaterInputStream(new ByteArrayInputStream(array2)));
            }
            this.debugln("READ", x, z, "unknown version " + byte1);
            return null;
        }
        catch (final IOException ex) {
            this.debugln("READ", x, z, "exception");
            return null;
        }
    }
    
    public DataOutputStream getChunkDataOutputStream(final int x, final int z) {
        if (this.outOfBounds(x, z)) {
            return null;
        }
        return new DataOutputStream(new DeflaterOutputStream(new RegionFile_ChunkBuffer(this, x, z)));
    }
    
    protected synchronized void write(final int x, final int y, final byte[] data, final int length) {
        try {
            final int offset = this.getOffset(x, y);
            final int sectorNumber = offset >> 8;
            final int n = offset & 0xFF;
            final int n2 = (length + 5) / 4096 + 1;
            if (n2 >= 256) {
                return;
            }
            if (sectorNumber != 0 && n == n2) {
                this.debug("SAVE", x, y, length, "rewrite");
                this.write(sectorNumber, data, length);
            }
            else {
                for (int i = 0; i < n; ++i) {
                    this.sectorFree.set(sectorNumber + i, true);
                }
                int index = this.sectorFree.indexOf(true);
                int n3 = 0;
                if (index != -1) {
                    for (int j = index; j < this.sectorFree.size(); ++j) {
                        if (n3 != 0) {
                            if (this.sectorFree.get(j)) {
                                ++n3;
                            }
                            else {
                                n3 = 0;
                            }
                        }
                        else if (this.sectorFree.get(j)) {
                            index = j;
                            n3 = 1;
                        }
                        if (n3 >= n2) {
                            break;
                        }
                    }
                }
                if (n3 >= n2) {
                    this.debug("SAVE", x, y, length, "reuse");
                    final int sectorNumber2 = index;
                    this.setOffset(x, y, sectorNumber2 << 8 | n2);
                    for (int k = 0; k < n2; ++k) {
                        this.sectorFree.set(sectorNumber2 + k, false);
                    }
                    this.write(sectorNumber2, data, length);
                }
                else {
                    this.debug("SAVE", x, y, length, "grow");
                    this.saveFile.seek(this.saveFile.length());
                    final int size = this.sectorFree.size();
                    for (int l = 0; l < n2; ++l) {
                        this.saveFile.write(RegionFile.emptySection);
                        this.sectorFree.add(false);
                    }
                    this.sizeDelta += 4096 * n2;
                    this.write(size, data, length);
                    this.setOffset(x, y, size << 8 | n2);
                }
            }
            this.setTimestamp(x, y, (int)(System.currentTimeMillis() / 1000L));
        }
        catch (final IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private void write(final int sectorNumber, final byte[] data, final int length) {
        this.debugln(" " + sectorNumber);
        this.saveFile.seek(sectorNumber * 4096);
        this.saveFile.writeInt(length + 1);
        this.saveFile.writeByte(2);
        this.saveFile.write(data, 0, length);
    }
    
    private boolean outOfBounds(final int x, final int z) {
        return x < 0 || x >= 32 || z < 0 || z >= 32;
    }
    
    private int getOffset(final int x, final int z) {
        return this.offsets[x + z * 32];
    }
    
    public boolean hasChunk(final int x, final int z) {
        return this.getOffset(x, z) != 0;
    }
    
    private void setOffset(final int x, final int z, final int offset) {
        this.offsets[x + z * 32] = offset;
        this.saveFile.seek((x + z * 32) * 4);
        this.saveFile.writeInt(offset);
    }
    
    private void setTimestamp(final int x, final int z, final int value) {
        this.chunkTimestamps[x + z * 32] = value;
        this.saveFile.seek(4096 + (x + z * 32) * 4);
        this.saveFile.writeInt(value);
    }
    
    public void close() {
        this.saveFile.close();
    }
    
    static {
        emptySection = new byte[4096];
    }
}
