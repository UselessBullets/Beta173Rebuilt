// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;

public class TileEventPacket extends Packet
{
    public int x;
    public int y;
    public int z;
    public int b0;
    public int b1;
    
    @Override
    public void read(final DataInputStream dis) {
        this.x = dis.readInt();
        this.y = dis.readShort();
        this.z = dis.readInt();
        this.b0 = dis.read();
        this.b1 = dis.read();
    }
    
    @Override
    public void write(final DataOutputStream dos) {
        dos.writeInt(this.x);
        dos.writeShort(this.y);
        dos.writeInt(this.z);
        dos.write(this.b0);
        dos.write(this.b1);
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handleTileEvent(this);
    }
    
    @Override
    public int getEstimatedSize() {
        return 12;
    }
}
