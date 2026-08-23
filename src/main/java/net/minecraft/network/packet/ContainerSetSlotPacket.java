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
    // Useless - these below constants exist both in the LCE and b1.2 leaks, they seem to be mistaken duplicates of ContainerOpenPacket's equivalents?
    public static final int CONTAINER = 0;
    public static final int WORKBENCH = 1;
    public static final int FURNACE = 2;

    public int containerId;
    public int slot;
    public ItemInstance item;

    public ContainerSetSlotPacket() {
    }

    public ContainerSetSlotPacket(final int containerId, final int slot, final ItemInstance item) {
        this.containerId = containerId;
        this.slot = slot;
        this.item = item == null ? null : item.copy();
    }

    @Override
    public void handle(final PacketListener listener) {
        listener.handleContainerSetSlot(this);
    }
    
    @Override
    public void read(final DataInputStream dis) throws IOException {
        this.containerId = dis.readByte();
        this.slot = dis.readShort();
        final short id = dis.readShort();
        if (id >= 0) {
            this.item = new ItemInstance(id, dis.readByte(), dis.readShort());
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
        return 3 + 5;
    }
}
