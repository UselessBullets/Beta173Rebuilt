// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import util.Mth;
import net.minecraft.world.entity.EntityIO;
import net.minecraft.world.entity.Mob;

import java.io.IOException;
import java.util.List;
import net.minecraft.world.entity.SynchedEntityData;

public class AddMobPacket extends Packet
{
    public int id;
    public byte type;
    public int x, y, z;
    public byte yRot, xRot;
    private SynchedEntityData entityData;
    private List<SynchedEntityData.DataItem> unpack;
    
    public AddMobPacket() {
    }
    
    public AddMobPacket(final Mob mob) {
        this.id = mob.entityId;

        this.type = (byte)EntityIO.getId(mob);
        this.x = Mth.floor(mob.x * 32.0);
        this.y = Mth.floor(mob.y * 32.0);
        this.z = Mth.floor(mob.z * 32.0);
        this.yRot = (byte)(mob.yRot * 256.0f / 360.0f);
        this.xRot = (byte)(mob.xRot * 256.0f / 360.0f);

        this.entityData = mob.getEntityData();
    }
    
    @Override
    public void read(final DataInputStream dis) throws IOException {
        this.id = dis.readInt();
        this.type = dis.readByte();
        this.x = dis.readInt();
        this.y = dis.readInt();
        this.z = dis.readInt();
        this.yRot = dis.readByte();
        this.xRot = dis.readByte();
        this.unpack = SynchedEntityData.unpack(dis);
    }
    
    @Override
    public void write(final DataOutputStream dos) throws IOException {
        dos.writeInt(this.id);
        dos.writeByte(this.type);
        dos.writeInt(this.x);
        dos.writeInt(this.y);
        dos.writeInt(this.z);
        dos.writeByte(this.yRot);
        dos.writeByte(this.xRot);
        this.entityData.packAll(dos);
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handleAddMob(this);
    }
    
    @Override
    public int getEstimatedSize() {
        return 20;
    }
    
    public List<SynchedEntityData.DataItem> getUnpackedData() {
        return this.unpack;
    }
}
