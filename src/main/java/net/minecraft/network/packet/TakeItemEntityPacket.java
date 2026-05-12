// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;

public class TakeItemEntityPacket extends Packet
{
    public int itemId;
    public int playerId;
    
    @Override
    public void read(final DataInputStream dis) {
        this.itemId = dis.readInt();
        this.playerId = dis.readInt();
    }
    
    @Override
    public void write(final DataOutputStream dos) {
        dos.writeInt(this.itemId);
        dos.writeInt(this.playerId);
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handleTakeItemEntity(this);
    }
    
    @Override
    public int getEstimatedSize() {
        return 8;
    }
}
