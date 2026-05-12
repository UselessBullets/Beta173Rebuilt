// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class MovePlayerPacket_PosRot extends MovePlayerPacket
{
    public MovePlayerPacket_PosRot() {
        this.hasRot = true;
        this.hasPos = true;
    }
    
    public MovePlayerPacket_PosRot(final double x, final double y, final double yView, final double z, final float yRot, final float xRot, final boolean onGround) {
        this.x = x;
        this.y = y;
        this.yView = yView;
        this.z = z;
        this.yRot = yRot;
        this.xRot = xRot;
        this.onGround = onGround;
        this.hasRot = true;
        this.hasPos = true;
    }
    
    @Override
    public void read(final DataInputStream dis) throws IOException {
        this.x = dis.readDouble();
        this.y = dis.readDouble();
        this.yView = dis.readDouble();
        this.z = dis.readDouble();
        this.yRot = dis.readFloat();
        this.xRot = dis.readFloat();
        super.read(dis);
    }
    
    @Override
    public void write(final DataOutputStream dos) throws IOException {
        dos.writeDouble(this.x);
        dos.writeDouble(this.y);
        dos.writeDouble(this.yView);
        dos.writeDouble(this.z);
        dos.writeFloat(this.yRot);
        dos.writeFloat(this.xRot);
        super.write(dos);
    }
    
    @Override
    public int getEstimatedSize() {
        return 41;
    }
}
