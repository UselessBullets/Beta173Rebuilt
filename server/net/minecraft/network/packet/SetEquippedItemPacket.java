// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import net.minecraft.world.item.ItemInstance;

public class SetEquippedItemPacket extends Packet
{
    public int entity;
    public int slot;
    public int item;
    public int auxValue;
    
    public SetEquippedItemPacket() {
    }
    
    public SetEquippedItemPacket(final int entity, final int slot, final ItemInstance item) {
        this.entity = entity;
        this.slot = slot;
        if (item == null) {
            this.item = -1;
            this.auxValue = 0;
        }
        else {
            this.item = item.id;
            this.auxValue = item.getAuxValue();
        }
    }
    
    @Override
    public void read(final DataInputStream dis) {
        this.entity = dis.readInt();
        this.slot = dis.readShort();
        this.item = dis.readShort();
        this.auxValue = dis.readShort();
    }
    
    @Override
    public void write(final DataOutputStream dos) {
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
