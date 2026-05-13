// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class LevelEventPacket extends Packet
{
    public int type;
    public int data;
    public int x;
    public int y;
    public int z;

    public LevelEventPacket() {
    }

    public LevelEventPacket(final int type, final int x, final int y, final int z, final int data) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
        this.data = data;
    }

    @Override
    public void read(final DataInputStream dis) throws IOException {
        this.type = dis.readInt();
        this.x = dis.readInt();
        this.y = dis.readByte();
        this.z = dis.readInt();
        this.data = dis.readInt();
    }
    
    @Override
    public void write(final DataOutputStream dos) throws IOException {
        dos.writeInt(this.type);
        dos.writeInt(this.x);
        dos.writeByte(this.y);
        dos.writeInt(this.z);
        dos.writeInt(this.data);
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handleLevelEvent(this);
    }
    
    @Override
    public int getEstimatedSize() {
        return 20;
    }
}
