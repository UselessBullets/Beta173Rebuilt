// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class ContainerSetDataPacket extends Packet
{
    public int containerId;
    public int id;
    public int value;

    public ContainerSetDataPacket() {
    }

    public ContainerSetDataPacket(final int containerId, final int id, final int value) {
        this.containerId = containerId;
        this.id = id;
        this.value = value;
    }

    @Override
    public void handle(final PacketListener listener) {
        listener.handleContainerSetData(this);
    }
    
    @Override
    public void read(final DataInputStream dis) throws IOException {
        this.containerId = dis.readByte();
        this.id = dis.readShort();
        this.value = dis.readShort();
    }
    
    @Override
    public void write(final DataOutputStream dos) throws IOException {
        dos.writeByte(this.containerId);
        dos.writeShort(this.id);
        dos.writeShort(this.value);
    }
    
    @Override
    public int getEstimatedSize() {
        return 5;
    }
}
