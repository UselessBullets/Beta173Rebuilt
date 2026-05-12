// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;

public class SignUpdatePacket extends Packet
{
    public int x;
    public int y;
    public int z;
    public String[] lines;
    
    public SignUpdatePacket() {
        this.shouldDelay = true;
    }
    
    public SignUpdatePacket(final int x, final int y, final int z, final String[] lines) {
        this.shouldDelay = true;
        this.x = x;
        this.y = y;
        this.z = z;
        this.lines = lines;
    }
    
    @Override
    public void read(final DataInputStream dis) {
        this.x = dis.readInt();
        this.y = dis.readShort();
        this.z = dis.readInt();
        this.lines = new String[4];
        for (int i = 0; i < 4; ++i) {
            this.lines[i] = Packet.readUTF(dis, 15);
        }
    }
    
    @Override
    public void write(final DataOutputStream dos) {
        dos.writeInt(this.x);
        dos.writeShort(this.y);
        dos.writeInt(this.z);
        for (int i = 0; i < 4; ++i) {
            Packet.writeUTF(this.lines[i], dos);
        }
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handleSignUpdate(this);
    }
    
    @Override
    public int getEstimatedSize() {
        int n = 0;
        for (int i = 0; i < 4; ++i) {
            n += this.lines[i].length();
        }
        return n;
    }
}
