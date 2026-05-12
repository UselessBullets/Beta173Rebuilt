// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;

public class SetSpawnPositionPacket extends Packet
{
    public int x;
    public int y;
    public int z;
    
    @Override
    public void read(final DataInputStream dis) {
        this.x = dis.readInt();
        this.y = dis.readInt();
        this.z = dis.readInt();
    }
    
    @Override
    public void write(final DataOutputStream dos) {
        dos.writeInt(this.x);
        dos.writeInt(this.y);
        dos.writeInt(this.z);
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handleSetSpawn(this);
    }
    
    @Override
    public int getEstimatedSize() {
        return 12;
    }
}
