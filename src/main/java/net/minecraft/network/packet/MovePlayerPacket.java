// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;

public class MovePlayerPacket extends Packet
{
    public double x;
    public double y;
    public double z;
    public double yView;
    public float yRot;
    public float xRot;
    public boolean onGround;
    public boolean hasPos;
    public boolean hasRot;
    
    public MovePlayerPacket() {
    }
    
    public MovePlayerPacket(final boolean onGround) {
        this.onGround = onGround;
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handleMovePlayer(this);
    }
    
    @Override
    public void read(final DataInputStream dis) {
        this.onGround = (dis.read() != 0);
    }
    
    @Override
    public void write(final DataOutputStream dos) {
        dos.write(this.onGround ? 1 : 0);
    }
    
    @Override
    public int getEstimatedSize() {
        return 1;
    }
}
