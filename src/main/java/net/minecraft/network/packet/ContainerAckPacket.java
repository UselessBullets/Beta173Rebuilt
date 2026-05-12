// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class ContainerAckPacket extends Packet
{
    public int containerId;
    public short uid;
    public boolean accepted;
    
    public ContainerAckPacket() {
    }
    
    public ContainerAckPacket(final int containerId, final short uid, final boolean accepted) {
        this.containerId = containerId;
        this.uid = uid;
        this.accepted = accepted;
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handleContainerAck(this);
    }
    
    @Override
    public void read(final DataInputStream dis) throws IOException {
        this.containerId = dis.readByte();
        this.uid = dis.readShort();
        this.accepted = (dis.readByte() != 0);
    }
    
    @Override
    public void write(final DataOutputStream dos) throws IOException {
        dos.writeByte(this.containerId);
        dos.writeShort(this.uid);
        dos.writeByte(this.accepted ? 1 : 0);
    }
    
    @Override
    public int getEstimatedSize() {
        return 4;
    }
}
