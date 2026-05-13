// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class ContainerOpenPacket extends Packet
{
    public int containerId;
    public int type;
    public String title;
    public int size;

    public ContainerOpenPacket() {
    }

    public ContainerOpenPacket(final int containerId, final int type, final String title, final int size) {
        this.containerId = containerId;
        this.type = type;
        this.title = title;
        this.size = size;
    }

    @Override
    public void handle(final PacketListener listener) {
        listener.handleContainerOpen(this);
    }
    
    @Override
    public void read(final DataInputStream dis) throws IOException {
        this.containerId = dis.readByte();
        this.type = dis.readByte();
        this.title = dis.readUTF();
        this.size = dis.readByte();
    }
    
    @Override
    public void write(final DataOutputStream dos) throws IOException {
        dos.writeByte(this.containerId);
        dos.writeByte(this.type);
        dos.writeUTF(this.title);
        dos.writeByte(this.size);
    }
    
    @Override
    public int getEstimatedSize() {
        return 3 + this.title.length();
    }
}
