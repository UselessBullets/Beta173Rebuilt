// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.world.entity.Entity;

public class PlayerCommandPacket extends Packet
{
    public static final int START_SNEAKING = 1;
    public static final int STOP_SNEAKING = 2;
    public static final int STOP_SLEEPING = 3;

    public int id;
    public int action;
    
    public PlayerCommandPacket() {
    }
    
    public PlayerCommandPacket(final Entity entity, final int action) {
        this.id = entity.entityId;
        this.action = action;
    }
    
    @Override
    public void read(final DataInputStream dis) throws IOException {
        this.id = dis.readInt();
        this.action = dis.readByte();
    }
    
    @Override
    public void write(final DataOutputStream dos) throws IOException {
        dos.writeInt(this.id);
        dos.writeByte(this.action);
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handlePlayerCommand(this);
    }
    
    @Override
    public int getEstimatedSize() {
        return 5;
    }
}
