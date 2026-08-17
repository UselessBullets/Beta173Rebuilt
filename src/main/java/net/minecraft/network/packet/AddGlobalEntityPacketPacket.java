// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.world.entity.global.LightningBolt;
import util.Mth;
import net.minecraft.world.entity.Entity;

public class AddGlobalEntityPacketPacket extends Packet
{
    public static final int LIGHTNING = 1;

    public int id;
    public int x;
    public int y;
    public int z;
    public int type;
    
    public AddGlobalEntityPacketPacket() {
    }
    
    public AddGlobalEntityPacketPacket(final Entity entity) {
        this.id = entity.entityId;
        this.x = Mth.floor(entity.x * 32.0);
        this.y = Mth.floor(entity.y * 32.0);
        this.z = Mth.floor(entity.z * 32.0);
        if (entity instanceof LightningBolt) {
            this.type = 1;
        }
    }
    
    @Override
    public void read(final DataInputStream dis) throws IOException {
        this.id = dis.readInt();
        this.type = dis.readByte();
        this.x = dis.readInt();
        this.y = dis.readInt();
        this.z = dis.readInt();
    }
    
    @Override
    public void write(final DataOutputStream dos) throws IOException {
        dos.writeInt(this.id);
        dos.writeByte(this.type);
        dos.writeInt(this.x);
        dos.writeInt(this.y);
        dos.writeInt(this.z);
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handleAddGlobalEntity(this);
    }
    
    @Override
    public int getEstimatedSize() {
        return 17;
    }
}
