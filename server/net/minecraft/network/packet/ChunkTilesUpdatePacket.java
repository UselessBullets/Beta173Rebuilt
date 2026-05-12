// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.Level;

public class ChunkTilesUpdatePacket extends Packet
{
    public int xc;
    public int zc;
    public short[] positions;
    public byte[] blocks;
    public byte[] data;
    public int count;
    
    public ChunkTilesUpdatePacket() {
        this.shouldDelay = true;
    }
    
    public ChunkTilesUpdatePacket(final int xc, final int zc, final short[] positions, final int count, final Level level) {
        this.shouldDelay = true;
        this.xc = xc;
        this.zc = zc;
        this.count = count;
        this.positions = new short[count];
        this.blocks = new byte[count];
        this.data = new byte[count];
        final LevelChunk chunk = level.getChunk(xc, zc);
        for (int i = 0; i < count; ++i) {
            final int n = positions[i] >> 12 & 0xF;
            final int n2 = positions[i] >> 8 & 0xF;
            final int n3 = positions[i] & 0xFF;
            this.positions[i] = positions[i];
            this.blocks[i] = (byte)chunk.getTile(n, n3, n2);
            this.data[i] = (byte)chunk.getData(n, n3, n2);
        }
    }
    
    @Override
    public void read(final DataInputStream dis) {
        this.xc = dis.readInt();
        this.zc = dis.readInt();
        this.count = (dis.readShort() & 0xFFFF);
        this.positions = new short[this.count];
        this.blocks = new byte[this.count];
        this.data = new byte[this.count];
        for (int i = 0; i < this.count; ++i) {
            this.positions[i] = dis.readShort();
        }
        dis.readFully(this.blocks);
        dis.readFully(this.data);
    }
    
    @Override
    public void write(final DataOutputStream dos) {
        dos.writeInt(this.xc);
        dos.writeInt(this.zc);
        dos.writeShort((short)this.count);
        for (int i = 0; i < this.count; ++i) {
            dos.writeShort(this.positions[i]);
        }
        dos.write(this.blocks);
        dos.write(this.data);
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handleChunkTilesUpdate(this);
    }
    
    @Override
    public int getEstimatedSize() {
        return 10 + this.count * 4;
    }
}
