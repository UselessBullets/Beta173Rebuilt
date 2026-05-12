// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class MoveEntityPacket_Pos extends MoveEntityPacket
{
    @Override
    public void read(final DataInputStream dis) throws IOException {
        super.read(dis);
        this.xa = dis.readByte();
        this.ya = dis.readByte();
        this.za = dis.readByte();
    }
    
    @Override
    public void write(final DataOutputStream dos) throws IOException {
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
