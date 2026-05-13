// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.world.item.ItemInstance;

public class ContainerSetSlotPacket extends Packet
{
    public int containerId;
    public int slot;
    public ItemInstance item;

    public ContainerSetSlotPacket() {
    }

    public ContainerSetSlotPacket(final int containerId, final int slot, final ItemInstance item) {
        this.containerId = containerId;
        this.slot = slot;
        this.item = ((item == null) ? item : item.copy());
    }

    @Override
    public void handle(final PacketListener listener) {
        listener.handleContainerSetSlot(this);
    }
    
    @Override
    public void read(final DataInputStream dis) throws IOException {
        this.containerId = dis.readByte();
        this.slot = dis.readShort();
        final short short1 = dis.readShort();
        if (short1 >= 0) {
            this.item = new ItemInstance(short1, dis.readByte(), dis.readShort());
        }
        else {
            this.item = null;
        }
    }
    
    @Override
    public void write(final DataOutputStream dos) throws IOException {
        dos.writeByte(this.containerId);
        dos.writeShort(this.slot);
        if (this.item == null) {
            dos.writeShort(-1);
        }
        else {
            dos.writeShort(this.item.id);
            dos.writeByte(this.item.count);
            dos.writeShort(this.item.getAuxValue());
        }
    }
    
    @Override
    public int getEstimatedSize() {
        return 8;
    }
}
