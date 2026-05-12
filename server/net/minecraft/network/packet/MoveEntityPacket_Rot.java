// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;

public class MoveEntityPacket_Rot extends MoveEntityPacket
{
    public MoveEntityPacket_Rot() {
        this.hasRot = true;
    }
    
    public MoveEntityPacket_Rot(final int id, final byte yRot, final byte xRot) {
        super(id);
        this.yRot = yRot;
        this.xRot = xRot;
        this.hasRot = true;
    }
    
    @Override
    public void read(final DataInputStream dis) {
        super.read(dis);
        this.yRot = dis.readByte();
        this.xRot = dis.readByte();
    }
    
    @Override
    public void write(final DataOutputStream dos) {
        super.write(dos);
        dos.writeByte(this.yRot);
        dos.writeByte(this.xRot);
    }
    
    @Override
    public int getEstimatedSize() {
        return 6;
    }
}
