// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;

public class MoveEntityPacket_Pos extends MoveEntityPacket
{
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
