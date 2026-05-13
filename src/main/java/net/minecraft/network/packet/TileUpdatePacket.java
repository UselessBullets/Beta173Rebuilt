// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import net.minecraft.world.level.Level;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class TileUpdatePacket extends Packet
{
    public int x;
    public int y;
    public int z;
    public int block;
    public int data;

    public TileUpdatePacket() {
        this.shouldDelay = true;
    }

    public TileUpdatePacket(final int x, final int y, final int z, final Level level) {
        this.shouldDelay = true;
        this.x = x;
        this.y = y;
        this.z = z;
        this.block = level.getTile(x, y, z);
        this.data = level.getData(x, y, z);
    }
    
    @Override
    public void read(final DataInputStream dis) throws IOException {
        this.x = dis.readInt();
        this.y = dis.read();
        this.z = dis.readInt();
        this.block = dis.read();
        this.data = dis.read();
    }
    
    @Override
    public void write(final DataOutputStream dos) throws IOException {
        dos.writeInt(this.x);
        dos.write(this.y);
        dos.writeInt(this.z);
        dos.write(this.block);
        dos.write(this.data);
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handleTileUpdate(this);
    }
    
    @Override
    public int getEstimatedSize() {
        return 11;
    }
}
