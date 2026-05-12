// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.world.entity.Entity;

public class AnimatePacket extends Packet
{
    public int id;
    public int action;
    
    public AnimatePacket() {
    }
    
    public AnimatePacket(final Entity entity, final int action) {
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
        listener.handleAnimate(this);
    }
    
    @Override
    public int getEstimatedSize() {
        return 5;
    }
}
