// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

import util.Mth;
import net.minecraft.world.entity.Entity;

public class TeleportEntityPacket extends Packet
{
    public int id;
    public int x;
    public int y;
    public int z;
    public byte yRot;
    public byte xRot;
    
    public TeleportEntityPacket() {
    }
    
    public TeleportEntityPacket(final Entity entity) {
        this.id = entity.entityId;
        this.x = Mth.floor(entity.x * 32.0);
        this.y = Mth.floor(entity.y * 32.0);
        this.z = Mth.floor(entity.z * 32.0);
        this.yRot = (byte)(entity.yRot * 256.0f / 360.0f);
        this.xRot = (byte)(entity.xRot * 256.0f / 360.0f);
    }
    
    @Override
    public void read(final DataInputStream dis) throws IOException {
        this.id = dis.readInt();
        this.x = dis.readInt();
        this.y = dis.readInt();
        this.z = dis.readInt();
        this.yRot = (byte)dis.read();
        this.xRot = (byte)dis.read();
    }
    
    @Override
    public void write(final DataOutputStream dos) throws IOException {
        dos.writeInt(this.id);
        dos.writeInt(this.x);
        dos.writeInt(this.y);
        dos.writeInt(this.z);
        dos.write(this.yRot);
        dos.write(this.xRot);
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handleTeleportEntity(this);
    }
    
    @Override
    public int getEstimatedSize() {
        return 34;
    }
}
