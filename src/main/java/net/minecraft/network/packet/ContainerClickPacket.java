// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.world.item.ItemInstance;

public class ContainerClickPacket extends Packet
{
    public int containerId;
    public int slotNum;
    public int buttonNum;
    public short uid;
    public ItemInstance item;
    public boolean quickKey;
    
    public ContainerClickPacket() {
    }
    
    public ContainerClickPacket(final int containerId, final int slotNum, final int buttonNum, final boolean quickKey, final ItemInstance item, final short uid) {
        this.containerId = containerId;
        this.slotNum = slotNum;
        this.buttonNum = buttonNum;
        this.item = item;
        this.uid = uid;
        this.quickKey = quickKey;
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handleContainerClick(this);
    }
    
    @Override
    public void read(final DataInputStream dis) throws IOException {
        this.containerId = dis.readByte();
        this.slotNum = dis.readShort();
        this.buttonNum = dis.readByte();
        this.uid = dis.readShort();
        this.quickKey = dis.readBoolean();

        final short id = dis.readShort();
        this.item = id >= 0 ? new ItemInstance(id, dis.readByte(), dis.readShort()) : null;
    }
    
    @Override
    public void write(final DataOutputStream dos) throws IOException {
        dos.writeByte(this.containerId);
        dos.writeShort(this.slotNum);
        dos.writeByte(this.buttonNum);
        dos.writeShort(this.uid);
        dos.writeBoolean(this.quickKey);

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
        return 4 + 4 + 2 + 1;
    }
}
