// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import util.Mth;
import net.minecraft.world.entity.item.ItemEntity;

public class AddItemEntityPacket extends Packet
{
    public int id;
    public int x;
    public int y;
    public int z;
    public byte xa;
    public byte ya;
    public byte za;
    public int itemId;
    public int itemCount;
    public int auxValue;
    
    public AddItemEntityPacket() {
    }
    
    public AddItemEntityPacket(final ItemEntity itemEntity) {
        this.id = itemEntity.entityId;
        this.itemId = itemEntity.item.id;
        this.itemCount = itemEntity.item.count;
        this.auxValue = itemEntity.item.getAuxValue();
        this.x = Mth.floor(itemEntity.x * 32.0);
        this.y = Mth.floor(itemEntity.y * 32.0);
        this.z = Mth.floor(itemEntity.z * 32.0);
        this.xa = (byte)(itemEntity.xd * 128.0);
        this.ya = (byte)(itemEntity.yd * 128.0);
        this.za = (byte)(itemEntity.zd * 128.0);
    }
    
    @Override
    public void read(final DataInputStream dis) {
        this.id = dis.readInt();
        this.itemId = dis.readShort();
        this.itemCount = dis.readByte();
        this.auxValue = dis.readShort();
        this.x = dis.readInt();
        this.y = dis.readInt();
        this.z = dis.readInt();
        this.xa = dis.readByte();
        this.ya = dis.readByte();
        this.za = dis.readByte();
    }
    
    @Override
    public void write(final DataOutputStream dos) {
        dos.writeInt(this.id);
        dos.writeShort(this.itemId);
        dos.writeByte(this.itemCount);
        dos.writeShort(this.auxValue);
        dos.writeInt(this.x);
        dos.writeInt(this.y);
        dos.writeInt(this.z);
        dos.writeByte(this.xa);
        dos.writeByte(this.ya);
        dos.writeByte(this.za);
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handleAddItemEntity(this);
    }
    
    @Override
    public int getEstimatedSize() {
        return 24;
    }
}
