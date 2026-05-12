// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;

public class InteractPacket extends Packet
{
    public int source;
    public int target;
    public int action;
    
    @Override
    public void read(final DataInputStream dis) {
        this.source = dis.readInt();
        this.target = dis.readInt();
        this.action = dis.readByte();
    }
    
    @Override
    public void write(final DataOutputStream dos) {
        dos.writeInt(this.source);
        dos.writeInt(this.target);
        dos.writeByte(this.action);
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handleInteract(this);
    }
    
    @Override
    public int getEstimatedSize() {
        return 9;
    }
}
