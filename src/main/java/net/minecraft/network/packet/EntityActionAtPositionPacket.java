// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;

public class EntityActionAtPositionPacket extends Packet
{
    public int id;
    public int x;
    public int y;
    public int z;
    public int action;
    
    @Override
    public void read(final DataInputStream dis) {
        this.id = dis.readInt();
        this.action = dis.readByte();
        this.x = dis.readInt();
        this.y = dis.readByte();
        this.z = dis.readInt();
    }
    
    @Override
    public void write(final DataOutputStream dos) {
        dos.writeInt(this.id);
        dos.writeByte(this.action);
        dos.writeInt(this.x);
        dos.writeByte(this.y);
        dos.writeInt(this.z);
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handleEntityActionAtPosition(this);
    }
    
    @Override
    public int getEstimatedSize() {
        return 14;
    }
}
