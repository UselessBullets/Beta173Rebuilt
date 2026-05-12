// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.world.item.ItemInstance;

public class ContainerSetContentPacket extends Packet
{
    public int containerId;
    public ItemInstance[] items;
    
    @Override
    public void read(final DataInputStream dis) throws IOException {
        this.containerId = dis.readByte();
        final short short1 = dis.readShort();
        this.items = new ItemInstance[short1];
        for (short n = 0; n < short1; ++n) {
            final short short2 = dis.readShort();
            if (short2 >= 0) {
                this.items[n] = new ItemInstance(short2, dis.readByte(), dis.readShort());
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
