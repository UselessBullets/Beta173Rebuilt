// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class ChunkTilesUpdatePacket extends Packet
{
    public int xc, zc;
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
        final LevelChunk levelChunk = level.getChunk(xc, zc);
        for (int i = 0; i < count; ++i) {
            final int x = positions[i] >> 12 & 0xF;
            final int z = positions[i] >> 8 & 0xF;
            final int y = positions[i] & 0xFF;

            this.positions[i] = positions[i];
            this.blocks[i] = (byte)levelChunk.getTile(x, y, z);
            this.data[i] = (byte)levelChunk.getData(x, y, z);
        }
    }
    
    @Override
    public void read(final DataInputStream dis) throws IOException {
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
    public void write(final DataOutputStream dos) throws IOException {
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
