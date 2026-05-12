// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.world.item.ItemInstance;
import util.Mth;
import net.minecraft.world.entity.player.Player;

public class AddPlayerPacket extends Packet
{
    public int id;
    public String name;
    public int x;
    public int y;
    public int z;
    public byte yRot;
    public byte xRot;
    public int carriedItem;
    
    public AddPlayerPacket() {
    }
    
    public AddPlayerPacket(final Player player) {
        this.id = player.entityId;
        this.name = player.name;
        this.x = Mth.floor(player.x * 32.0);
        this.y = Mth.floor(player.y * 32.0);
        this.z = Mth.floor(player.z * 32.0);
        this.yRot = (byte)(player.yRot * 256.0f / 360.0f);
        this.xRot = (byte)(player.xRot * 256.0f / 360.0f);
        final ItemInstance selected = player.inventory.getSelected();
        this.carriedItem = ((selected == null) ? 0 : selected.id);
    }
    
    @Override
    public void read(final DataInputStream dis) throws IOException {
        this.id = dis.readInt();
        this.name = Packet.readUTF(dis, 16);
        this.x = dis.readInt();
        this.y = dis.readInt();
        this.z = dis.readInt();
        this.yRot = dis.readByte();
        this.xRot = dis.readByte();
        this.carriedItem = dis.readShort();
    }
    
    @Override
    public void write(final DataOutputStream dos) throws IOException {
        dos.writeInt(this.id);
        Packet.writeUTF(this.name, dos);
        dos.writeInt(this.x);
        dos.writeInt(this.y);
        dos.writeInt(this.z);
        dos.writeByte(this.yRot);
        dos.writeByte(this.xRot);
        dos.writeShort(this.carriedItem);
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handleAddPlayer(this);
    }
    
    @Override
    public int getEstimatedSize() {
        return 28;
    }
}
