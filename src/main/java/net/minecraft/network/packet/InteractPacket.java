// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class InteractPacket extends Packet
{
    public int source;
    public int target;
    public int action;
    
    public InteractPacket() {
    }
    
    public InteractPacket(final int source, final int target, final int action) {
        this.source = source;
        this.target = target;
        this.action = action;
    }
    
    @Override
    public void read(final DataInputStream dis) throws IOException {
        this.source = dis.readInt();
        this.target = dis.readInt();
        this.action = dis.readByte();
    }
    
    @Override
    public void write(final DataOutputStream dos) throws IOException {
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
