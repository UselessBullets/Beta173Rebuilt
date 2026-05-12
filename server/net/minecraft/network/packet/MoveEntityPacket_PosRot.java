// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;

public class MoveEntityPacket_PosRot extends MoveEntityPacket
{
    public MoveEntityPacket_PosRot() {
        this.hasRot = true;
    }
    
    public MoveEntityPacket_PosRot(final int id, final byte xa, final byte ya, final byte za, final byte yRot, final byte xRot) {
        super(id);
        this.xa = xa;
        this.ya = ya;
        this.za = za;
        this.yRot = yRot;
        this.xRot = xRot;
        this.hasRot = true;
    }
    
    @Override
    public void read(final DataInputStream dis) {
        super.read(dis);
        this.xa = dis.readByte();
        this.ya = dis.readByte();
        this.za = dis.readByte();
        this.yRot = dis.readByte();
        this.xRot = dis.readByte();
    }
    
    @Override
    public void write(final DataOutputStream dos) {
        super.write(dos);
        dos.writeByte(this.xa);
        dos.writeByte(this.ya);
        dos.writeByte(this.za);
        dos.writeByte(this.yRot);
        dos.writeByte(this.xRot);
    }
    
    @Override
    public int getEstimatedSize() {
        return 9;
    }
}
