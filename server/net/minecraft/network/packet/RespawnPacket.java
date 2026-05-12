// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;

public class RespawnPacket extends Packet
{
    public byte dimension;
    
    public RespawnPacket() {
    }
    
    public RespawnPacket(final byte dimension) {
        this.dimension = dimension;
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handleRespawn(this);
    }
    
    @Override
    public void read(final DataInputStream dis) {
        this.dimension = dis.readByte();
    }
    
    @Override
    public void write(final DataOutputStream dos) {
        dos.writeByte(this.dimension);
    }
    
    @Override
    public int getEstimatedSize() {
        return 1;
    }
}
