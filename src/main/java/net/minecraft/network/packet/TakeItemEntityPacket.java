// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class TakeItemEntityPacket extends Packet
{
    public int itemId;
    public int playerId;
    
    @Override
    public void read(final DataInputStream dis) throws IOException {
        this.itemId = dis.readInt();
        this.playerId = dis.readInt();
    }
    
    @Override
    public void write(final DataOutputStream dos) throws IOException {
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
