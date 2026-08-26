// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.storage;

import java.io.ByteArrayOutputStream;
import java.util.zip.DeflaterOutputStream;
import java.io.DataOutputStream;
import java.util.zip.InflaterInputStream;
import java.util.zip.GZIPInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.io.RandomAccessFile;
import java.io.File;

public class RegionFile
{
    private static final int VERSION_GZIP = 1;
    private static final int VERSION_DEFLATE = 2;

    private static final int SECTOR_BYTES = 4096;
    private static final int SECTOR_INTS = SECTOR_BYTES / 4;

    private static final int CHUNK_HEADER_SIZE = 5; // Useless - was 8 in LCE but usage indicates it should be 5 here
    private static final byte[] emptySection = new byte[SECTOR_BYTES];
    private final File fileName;
    private RandomAccessFile saveFile;
    private final int[] offsets = new int[SECTOR_INTS];
    private final int[] chunkTimestamps = new int[SECTOR_INTS];
    private ArrayList<Boolean> sectorFree;
    private int sizeDelta;
    private long lastModified = 0L;
    
    public RegionFile(final File path) {
        this.fileName = path;

        this.debugln("REGION LOAD " + this.fileName);

        this.sizeDelta = 0;
        try {
            if (path.exists()) {
                this.lastModified = path.lastModified();
            }

            this.saveFile = new RandomAccessFile(path, "rw");
            if (this.saveFile.length() < SECTOR_BYTES) {
                for (int i = 0; i < SECTOR_INTS; ++i) {
                    this.saveFile.writeInt(0);
                }
                for (int i = 0; i < SECTOR_INTS; ++i) {
                    this.saveFile.writeInt(0);
                }
                this.sizeDelta += SECTOR_BYTES * 2;
            }
            if ((this.saveFile.length() & 0xFFFL) != 0x0L) {
                for (int i = 0; i < (this.saveFile.length() & 0xFFFL); ++i) {
                    this.saveFile.write(0);
                }
            }
            final int initialCapacity = (int)this.saveFile.length() / SECTOR_BYTES;
            this.sectorFree = new ArrayList(initialCapacity);
            for (int i = 0; i < initialCapacity; ++i) {
                this.sectorFree.add(true);
            }

            this.sectorFree.set(0, false); // chunk offset table
            this.sectorFree.set(1, false); // for the last modified info

            this.saveFile.seek(0L);
            for (int i = 0; i < SECTOR_INTS; ++i) {
                final int offset = this.saveFile.readInt();
                this.offsets[i] = offset;
                if (offset != 0 && (offset >> 8) + (offset & 0xFF) <= this.sectorFree.size()) {
                    for (int sectorNum = 0; sectorNum < (offset & 0xFF); ++sectorNum) {
                        this.sectorFree.set((offset >> 8) + sectorNum, false);
                    }
                }
            }
            for (int i = 0; i < SECTOR_INTS; ++i) {
                this.chunkTimestamps[i] = this.saveFile.readInt();
            }
        }
        catch (final IOException e) {
            e.printStackTrace();
        }
    }
    
    public synchronized int getSizeDelta() {
        final int ret = this.sizeDelta;
        this.sizeDelta = 0;
        return ret;
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

            final int sectorNumber = offset >> 8;
            final int numSectors = offset & 0xFF;

            if (sectorNumber + numSectors > this.sectorFree.size()) {
                this.debugln("READ", x, z, "invalid sector");
                return null;
            }

            this.saveFile.seek(sectorNumber * SECTOR_BYTES);

            final int length = this.saveFile.readInt();
            if (length > SECTOR_BYTES * numSectors) {
                this.debugln("READ", x, z, "invalid length: " + length + " > 4096 * " + numSectors);
                return null;
            }

            final byte version = this.saveFile.readByte();
            if (version == VERSION_GZIP) {
                final byte[] array = new byte[length - 1];
                this.saveFile.read(array);
                return new DataInputStream(new GZIPInputStream(new ByteArrayInputStream(array)));
            }

            if (version == VERSION_DEFLATE) {
                final byte[] array2 = new byte[length - 1];
                this.saveFile.read(array2);
                return new DataInputStream(new InflaterInputStream(new ByteArrayInputStream(array2)));
            }

            this.debugln("READ", x, z, "unknown version " + version);
            return null;
        }
        catch (final IOException e) {
            this.debugln("READ", x, z, "exception");
            return null;
        }
    }
    
    public DataOutputStream getChunkDataOutputStream(final int x, final int z) {
        if (this.outOfBounds(x, z)) return null;
        return new DataOutputStream(new DeflaterOutputStream(new ChunkBuffer(this, x, z)));
    }
    
    protected synchronized void write(final int x, final int y, final byte[] data, final int length) {
        try {
            int offset = this.getOffset(x, y);
            int sectorNumber = offset >> 8;
            int sectorsAllocated = offset & 0xFF;

            int sectorsNeeded = (length + CHUNK_HEADER_SIZE) / SECTOR_BYTES + 1;

            // maximum chunk size is 1MB
            if (sectorsNeeded >= 256) {
                return;
            }

            if (sectorNumber != 0 && sectorsAllocated == sectorsNeeded) {
                /* we can simply overwrite the old sectors */
                this.debug("SAVE", x, y, length, "rewrite");
                this.write(sectorNumber, data, length);
            }
            else {
                /* we need to allocate new sectors */

                /* mark the sectors previously used for this chunk as free */
                for (int i = 0; i < sectorsAllocated; ++i) {
                    this.sectorFree.set(sectorNumber + i, true);
                }

                /* scan for a free space large enough to store this chunk */
                int runStart = this.sectorFree.indexOf(true);
                int runLength = 0;
                if (runStart != -1) {
                    for (int i = runStart; i < this.sectorFree.size(); ++i) {
                        if (runLength != 0) {
                            if (this.sectorFree.get(i)) runLength++;
                            else runLength = 0;
                        }
                        else if (this.sectorFree.get(i)) {
                            runStart = i;
                            runLength = 1;
                        }

                        if (runLength >= sectorsNeeded) {
                            break;
                        }
                    }
                }

                if (runLength >= sectorsNeeded) {
                    /* we found a free space large enough */
                    this.debug("SAVE", x, y, length, "reuse");
                    sectorNumber = runStart;
                    this.setOffset(x, y, sectorNumber << 8 | sectorsNeeded);
                    for (int i = 0; i < sectorsNeeded; ++i) {
                        this.sectorFree.set(sectorNumber + i, false);
                    }
                    this.write(sectorNumber, data, length);
                }
                else {
                    /*
                     * no free space large enough found -- we need to grow the
                     * file
                     */
                    this.debug("SAVE", x, y, length, "grow");
                    this.saveFile.seek(this.saveFile.length());
                    final int size = this.sectorFree.size();
                    for (int i = 0; i < sectorsNeeded; ++i) {
                        this.saveFile.write(RegionFile.emptySection);
                        this.sectorFree.add(false);
                    }
                    this.sizeDelta += SECTOR_BYTES * sectorsNeeded;

                    this.write(size, data, length);
                    this.setOffset(x, y, size << 8 | sectorsNeeded);
                }
            }
            this.setTimestamp(x, y, (int)(System.currentTimeMillis() / 1000L));
        }
        catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /* write a chunk data to the region file at specified sector number */
    private void write(final int sectorNumber, final byte[] data, final int length) throws IOException {
        this.debugln(" " + sectorNumber);
        this.saveFile.seek(sectorNumber * SECTOR_BYTES);
        this.saveFile.writeInt(length + 1);
        this.saveFile.writeByte(2);
        this.saveFile.write(data, 0, length);
    }

    /* is this an invalid chunk coordinate? */
    private boolean outOfBounds(final int x, final int z) {
        return x < 0 || x >= 32 || z < 0 || z >= 32;
    }
    
    private int getOffset(final int x, final int z) {
        return this.offsets[x + z * 32];
    }
    
    public boolean hasChunk(final int x, final int z) {
        return this.getOffset(x, z) != 0;
    }
    
    private void setOffset(final int x, final int z, final int offset) throws IOException {
        this.offsets[x + z * 32] = offset;
        this.saveFile.seek((x + z * 32) * 4);
        this.saveFile.writeInt(offset);
    }
    
    private void setTimestamp(final int x, final int z, final int value) throws IOException {
        this.chunkTimestamps[x + z * 32] = value;
        this.saveFile.seek(SECTOR_BYTES + (x + z * 32) * 4);
        this.saveFile.writeInt(value);
    }
    
    public void close() throws IOException {
        this.saveFile.close();
    }

    static class ChunkBuffer extends ByteArrayOutputStream
    {
        private RegionFile rf;
        private int x;
        private int z;

        public ChunkBuffer(final RegionFile regionFile, final int x, final int z) {
            super(8096);
            this.rf = regionFile;
            this.x = x;
            this.z = z;
        }

        @Override
        public void close() {
            this.rf.write(this.x, this.z, this.buf, this.count);
        }
    }
}
