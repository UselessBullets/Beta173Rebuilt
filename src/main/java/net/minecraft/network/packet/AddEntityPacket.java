// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;

public class AddEntityPacket extends Packet
{
    public int id;
    public int x;
    public int y;
    public int z;
    public int xa;
    public int ya;
    public int za;
    public int type;
    public int data;
    
    @Override
    public void read(final DataInputStream dis) {
        this.id = dis.readInt();
        this.type = dis.readByte();
        this.x = dis.readInt();
        this.y = dis.readInt();
        this.z = dis.readInt();
        this.data = dis.readInt();
        if (this.data > 0) {
            this.xa = dis.readShort();
            this.ya = dis.readShort();
            this.za = dis.readShort();
        }
    }
    
    @Override
    public void write(final DataOutputStream dos) {
        dos.writeInt(this.id);
        dos.writeByte(this.type);
        dos.writeInt(this.x);
        dos.writeInt(this.y);
        dos.writeInt(this.z);
        dos.writeInt(this.data);
        if (this.data > 0) {
            dos.writeShort(this.xa);
            dos.writeShort(this.ya);
            dos.writeShort(this.za);
        }
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handleAddEntity(this);
    }
    
    @Override
    public int getEstimatedSize() {
        return (21 + this.data > 0) ? 6 : 0;
    }
}
