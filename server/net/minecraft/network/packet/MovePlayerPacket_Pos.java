// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;

public class MovePlayerPacket_Pos extends MovePlayerPacket
{
    public MovePlayerPacket_Pos() {
        this.hasPos = true;
    }
    
    @Override
    public void read(final DataInputStream dis) {
        this.x = dis.readDouble();
        this.y = dis.readDouble();
        this.yView = dis.readDouble();
        this.z = dis.readDouble();
        super.read(dis);
    }
    
    @Override
    public void write(final DataOutputStream dos) {
        dos.writeDouble(this.x);
        dos.writeDouble(this.y);
        dos.writeDouble(this.yView);
        dos.writeDouble(this.z);
        super.write(dos);
    }
    
    @Override
    public int getEstimatedSize() {
        return 33;
    }
}
