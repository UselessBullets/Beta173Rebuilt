// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import net.minecraft.world.entity.Entity;

public class SetRidingPacket extends Packet
{
    public int riderId;
    public int riddenId;
    
    public SetRidingPacket() {
    }
    
    public SetRidingPacket(final Entity rider, final Entity ridden) {
        this.riderId = rider.entityId;
        this.riddenId = ((ridden != null) ? ridden.entityId : -1);
    }
    
    @Override
    public int getEstimatedSize() {
        return 8;
    }
    
    @Override
    public void read(final DataInputStream dis) {
        this.riderId = dis.readInt();
        this.riddenId = dis.readInt();
    }
    
    @Override
    public void write(final DataOutputStream dos) {
        dos.writeInt(this.riderId);
        dos.writeInt(this.riddenId);
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handleRidePacket(this);
    }
}
