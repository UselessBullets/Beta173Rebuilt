// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.world.item.ItemInstance;

public class UseItemPacket extends Packet
{
    public int x, y, z, face;
    public ItemInstance item;
    
    public UseItemPacket() {
    }
    
    public UseItemPacket(final int x, final int y, final int z, final int face, final ItemInstance item) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.face = face;
        this.item = item;
    }
    
    @Override
    public void read(final DataInputStream dis) throws IOException {
        this.x = dis.readInt();
        this.y = dis.read();
        this.z = dis.readInt();
        this.face = dis.read();
        final short id = dis.readShort();
        this.item = id >= 0 ? new ItemInstance(id, dis.readByte(), dis.readShort()) : null;
    }
    
    @Override
    public void write(final DataOutputStream dos) throws IOException {
        dos.writeInt(this.x);
        dos.write(this.y);
        dos.writeInt(this.z);
        dos.write(this.face);

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
    public void handle(final PacketListener listener) {
        listener.handleUseItem(this);
    }
    
    @Override
    public int getEstimatedSize() {
        return 15;
    }
}
