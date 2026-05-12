// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;

public class ContainerClosePacket extends Packet
{
    public int containerId;
    
    public ContainerClosePacket() {
    }
    
    public ContainerClosePacket(final int containerId) {
        this.containerId = containerId;
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handleContainerClose(this);
    }
    
    @Override
    public void read(final DataInputStream dis) {
        this.containerId = dis.readByte();
    }
    
    @Override
    public void write(final DataOutputStream dos) {
        dos.writeByte(this.containerId);
    }
    
    @Override
    public int getEstimatedSize() {
        return 1;
    }
}
