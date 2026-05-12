// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;

public class RemoveEntityPacket extends Packet
{
    public int id;
    
    public RemoveEntityPacket() {
    }
    
    public RemoveEntityPacket(final int id) {
        this.id = id;
    }
    
    @Override
    public void read(final DataInputStream dis) {
        this.id = dis.readInt();
    }
    
    @Override
    public void write(final DataOutputStream dos) {
        dos.writeInt(this.id);
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handleRemoveEntity(this);
    }
    
    @Override
    public int getEstimatedSize() {
        return 4;
    }
}
