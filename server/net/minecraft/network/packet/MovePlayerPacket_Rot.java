// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;

public class MovePlayerPacket_Rot extends MovePlayerPacket
{
    public MovePlayerPacket_Rot() {
        this.hasRot = true;
    }
    
    @Override
    public void read(final DataInputStream dis) {
        this.yRot = dis.readFloat();
        this.xRot = dis.readFloat();
        super.read(dis);
    }
    
    @Override
    public void write(final DataOutputStream dos) {
        dos.writeFloat(this.yRot);
        dos.writeFloat(this.xRot);
        super.write(dos);
    }
    
    @Override
    public int getEstimatedSize() {
        return 9;
    }
}
