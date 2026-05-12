// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.util.zip.DataFormatException;
import java.io.IOException;
import java.util.zip.Inflater;
import java.io.DataInputStream;
import java.util.zip.Deflater;
import net.minecraft.world.level.Level;

public class BlockRegionUpdatePacket extends Packet
{
    public int x;
    public int y;
    public int z;
    public int xs;
    public int ys;
    public int zs;
    public byte[] buffer;
    private int size;
    
    public BlockRegionUpdatePacket() {
        this.shouldDelay = true;
    }
    
    public BlockRegionUpdatePacket(final int x, final int y, final int z, final int xs, final int ys, final int zs, final Level level) {
        this.shouldDelay = true;
        this.x = x;
        this.y = y;
        this.z = z;
        this.xs = xs;
        this.ys = ys;
        this.zs = zs;
        final byte[] blocksAndData = level.getBlocksAndData(x, y, z, xs, ys, zs);
        final Deflater deflater = new Deflater(-1);
        try {
            deflater.setInput(blocksAndData);
            deflater.finish();
            this.buffer = new byte[xs * ys * zs * 5 / 2];
            this.size = deflater.deflate(this.buffer);
        }
        finally {
            deflater.end();
        }
    }
    
    @Override
    public void read(final DataInputStream dis) {
        this.x = dis.readInt();
        this.y = dis.readShort();
        this.z = dis.readInt();
        this.xs = dis.read() + 1;
        this.ys = dis.read() + 1;
        this.zs = dis.read() + 1;
        this.size = dis.readInt();
        final byte[] array = new byte[this.size];
        dis.readFully(array);
        this.buffer = new byte[this.xs * this.ys * this.zs * 5 / 2];
        final Inflater inflater = new Inflater();
        inflater.setInput(array);
        try {
            inflater.inflate(this.buffer);
        }
        catch (final DataFormatException ex) {
            throw new IOException("Bad compressed data format");
        }
        finally {
            inflater.end();
        }
    }
    
    @Override
    public void write(final DataOutputStream dos) {
        dos.writeInt(this.x);
        dos.writeShort(this.y);
        dos.writeInt(this.z);
        dos.write(this.xs - 1);
        dos.write(this.ys - 1);
        dos.write(this.zs - 1);
        dos.writeInt(this.size);
        dos.write(this.buffer, 0, this.size);
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handleBlockRegionUpdate(this);
    }
    
    @Override
    public int getEstimatedSize() {
        return 17 + this.size;
    }
}
