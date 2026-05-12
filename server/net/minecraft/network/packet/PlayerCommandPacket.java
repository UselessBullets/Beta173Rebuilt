// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;

public class PlayerCommandPacket extends Packet
{
    public int id;
    public int action;
    
    @Override
    public void read(final DataInputStream dis) {
        this.id = dis.readInt();
        this.action = dis.readByte();
    }
    
    @Override
    public void write(final DataOutputStream dos) {
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
