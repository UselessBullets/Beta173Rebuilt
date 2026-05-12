// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class MoveEntityPacket extends Packet
{
    public int id;
    public byte xa;
    public byte ya;
    public byte za;
    public byte yRot;
    public byte xRot;
    public boolean hasRot;
    
    public MoveEntityPacket() {
        this.hasRot = false;
    }
    
    @Override
    public void read(final DataInputStream dis) throws IOException {
        this.id = dis.readInt();
    }
    
    @Override
    public void write(final DataOutputStream dos) throws IOException {
        dos.writeInt(this.id);
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handleMoveEntity(this);
    }
    
    @Override
    public int getEstimatedSize() {
        return 4;
    }
}
