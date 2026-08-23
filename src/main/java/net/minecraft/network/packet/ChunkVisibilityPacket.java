// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class ChunkVisibilityPacket extends Packet
{
    public int x, y;
    public boolean visible;
    
    public ChunkVisibilityPacket() {
        this.shouldDelay = false;
    }

    public ChunkVisibilityPacket(final int x, final int y, final boolean visible) {
        this.shouldDelay = false;
        this.x = x;
        this.y = y;
        this.visible = visible;
    }
    
    @Override
    public void read(final DataInputStream dis) throws IOException {
        this.x = dis.readInt();
        this.y = dis.readInt();
        this.visible = dis.read() != 0;
    }
    
    @Override
    public void write(final DataOutputStream dos) throws IOException {
        dos.writeInt(this.x);
        dos.writeInt(this.y);
        dos.write(this.visible ? 1 : 0);
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handleChunkVisibility(this);
    }
    
    @Override
    public int getEstimatedSize() {
        return 9;
    }
}
