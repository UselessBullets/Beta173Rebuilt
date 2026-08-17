// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import net.minecraft.world.entity.Entity;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class EntityActionAtPositionPacket extends Packet
{
    public static final int START_SLEEP = 0;

    public int id;
    public int x;
    public int y;
    public int z;
    public int action;

    public EntityActionAtPositionPacket() {
    }

    public EntityActionAtPositionPacket(final Entity e, final int action, final int x, final int y, final int z) {
        this.action = action;
        this.x = x;
        this.y = y;
        this.z = z;
        this.id = e.entityId;
    }

    @Override
    public void read(final DataInputStream dis) throws IOException {
        this.id = dis.readInt();
        this.action = dis.readByte();
        this.x = dis.readInt();
        this.y = dis.readByte();
        this.z = dis.readInt();
    }
    
    @Override
    public void write(final DataOutputStream dos) throws IOException {
        dos.writeInt(this.id);
        dos.writeByte(this.action);
        dos.writeInt(this.x);
        dos.writeByte(this.y);
        dos.writeInt(this.z);
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handleEntityActionAtPosition(this);
    }
    
    @Override
    public int getEstimatedSize() {
        return 14;
    }
}
