// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class SetEquippedItemPacket extends Packet
{
    public int entity;
    public int slot;
    public int item;
    public int auxValue;
    
    @Override
    public void read(final DataInputStream dis) throws IOException {
        this.entity = dis.readInt();
        this.slot = dis.readShort();
        this.item = dis.readShort();
        this.auxValue = dis.readShort();
    }
    
    @Override
    public void write(final DataOutputStream dos) throws IOException {
        dos.writeInt(this.entity);
        dos.writeShort(this.slot);
        dos.writeShort(this.item);
        dos.writeShort(this.auxValue);
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handleSetEquippedItem(this);
    }
    
    @Override
    public int getEstimatedSize() {
        return 8;
    }
}
