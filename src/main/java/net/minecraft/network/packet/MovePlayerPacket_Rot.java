// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class MovePlayerPacket_Rot extends MovePlayerPacket
{
    public MovePlayerPacket_Rot() {
        this.hasRot = true;
    }
    
    public MovePlayerPacket_Rot(final float yRot, final float xRot, final boolean onGround) {
        this.yRot = yRot;
        this.xRot = xRot;
        this.onGround = onGround;
        this.hasRot = true;
    }
    
    @Override
    public void read(final DataInputStream dis) throws IOException {
        this.yRot = dis.readFloat();
        this.xRot = dis.readFloat();
        super.read(dis);
    }
    
    @Override
    public void write(final DataOutputStream dos) throws IOException {
        dos.writeFloat(this.yRot);
        dos.writeFloat(this.xRot);
        super.write(dos);
    }
    
    @Override
    public int getEstimatedSize() {
        return 9;
    }
}
