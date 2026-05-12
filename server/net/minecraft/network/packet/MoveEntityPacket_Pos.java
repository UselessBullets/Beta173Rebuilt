// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;

public class MoveEntityPacket_Pos extends MoveEntityPacket
{
    public MoveEntityPacket_Pos() {
    }
    
    public MoveEntityPacket_Pos(final int id, final byte xa, final byte ya, final byte za) {
        super(id);
        this.xa = xa;
        this.ya = ya;
        this.za = za;
    }
    
    @Override
    public void read(final DataInputStream dis) {
        super.read(dis);
        this.xa = dis.readByte();
        this.ya = dis.readByte();
        this.za = dis.readByte();
    }
    
    @Override
    public void write(final DataOutputStream dos) {
        super.write(dos);
        dos.writeByte(this.xa);
        dos.writeByte(this.ya);
        dos.writeByte(this.za);
    }
    
    @Override
    public int getEstimatedSize() {
        return 7;
    }
}
