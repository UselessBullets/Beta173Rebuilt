// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;

public class PlayerActionPacket extends Packet
{
    public int x;
    public int y;
    public int z;
    public int face;
    public int action;
    
    @Override
    public void read(final DataInputStream dis) {
        this.action = dis.read();
        this.x = dis.readInt();
        this.y = dis.read();
        this.z = dis.readInt();
        this.face = dis.read();
    }
    
    @Override
    public void write(final DataOutputStream dos) {
        dos.write(this.action);
        dos.writeInt(this.x);
        dos.write(this.y);
        dos.writeInt(this.z);
        dos.write(this.face);
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handlePlayerAction(this);
    }
    
    @Override
    public int getEstimatedSize() {
        return 11;
    }
}
