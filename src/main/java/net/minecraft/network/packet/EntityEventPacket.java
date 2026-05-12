// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;

public class EntityEventPacket extends Packet
{
    public int entityId;
    public byte eventId;
    
    @Override
    public void read(final DataInputStream dis) {
        this.entityId = dis.readInt();
        this.eventId = dis.readByte();
    }
    
    @Override
    public void write(final DataOutputStream dos) {
        dos.writeInt(this.entityId);
        dos.writeByte(this.eventId);
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handleEntityEvent(this);
    }
    
    @Override
    public int getEstimatedSize() {
        return 5;
    }
}
