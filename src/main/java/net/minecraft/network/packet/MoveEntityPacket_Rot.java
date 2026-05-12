// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class MoveEntityPacket_Rot extends MoveEntityPacket
{
    public MoveEntityPacket_Rot() {
        this.hasRot = true;
    }
    
    @Override
    public void read(final DataInputStream dis) throws IOException {
        super.read(dis);
        this.yRot = dis.readByte();
        this.xRot = dis.readByte();
    }
    
    @Override
    public void write(final DataOutputStream dos) throws IOException {
        super.write(dos);
        dos.writeByte(this.yRot);
        dos.writeByte(this.xRot);
    }
    
    @Override
    public int getEstimatedSize() {
        return 6;
    }
}
