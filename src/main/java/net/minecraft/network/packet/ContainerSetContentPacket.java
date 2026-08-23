// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;

import net.minecraft.world.item.ItemInstance;

public class ContainerSetContentPacket extends Packet
{
    public int containerId;
    public ItemInstance[] items;

    public ContainerSetContentPacket() {
    }

    public ContainerSetContentPacket(final int containerId, final List<ItemInstance> newItems) {
        this.containerId = containerId;
        this.items = new ItemInstance[newItems.size()];
        for (int i = 0; i < this.items.length; ++i) {
            final ItemInstance item = newItems.get(i);
            this.items[i] = item == null ? null : item.copy();
        }
    }

    @Override
    public void read(final DataInputStream dis) throws IOException {
        this.containerId = dis.readByte();
        final short count = dis.readShort();
        this.items = new ItemInstance[count];
        for (short n = 0; n < count; ++n) {
            final short id = dis.readShort();
            if (id >= 0) {
                this.items[n] = new ItemInstance(id, dis.readByte(), dis.readShort());
            }
        }
    }
    
    @Override
    public void write(final DataOutputStream dos) throws IOException {
        dos.writeByte(this.containerId);
        dos.writeShort(this.items.length);
        for (int i = 0; i < this.items.length; ++i) {
            if (this.items[i] == null) {
                dos.writeShort(-1);
            }
            else {
                dos.writeShort((short)this.items[i].id);
                dos.writeByte((byte)this.items[i].count);
                dos.writeShort((short)this.items[i].getAuxValue());
            }
        }
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handleContainerContent(this);
    }
    
    @Override
    public int getEstimatedSize() {
        return 3 + this.items.length * 5;
    }
}
