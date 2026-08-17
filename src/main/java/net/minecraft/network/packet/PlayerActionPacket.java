// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class PlayerActionPacket extends Packet
{
    public static final int START_DESTROY_BLOCK = 0;
    public static final int ABORT_DESTROY_BLOCK = 1;
    public static final int STOP_DESTROY_BLOCK = 2;
    public static final int GET_UPDATED_BLOCK = 3;
    public static final int DROP_ITEM = 4;

    public int x;
    public int y;
    public int z;
    public int face;
    public int action;
    
    public PlayerActionPacket() {
    }
    
    public PlayerActionPacket(final int action, final int x, final int y, final int z, final int face) {
        this.action = action;
        this.x = x;
        this.y = y;
        this.z = z;
        this.face = face;
    }
    
    @Override
    public void read(final DataInputStream dis) throws IOException {
        this.action = dis.read();
        this.x = dis.readInt();
        this.y = dis.read();
        this.z = dis.readInt();
        this.face = dis.read();
    }
    
    @Override
    public void write(final DataOutputStream dos) throws IOException {
        dos.write(this.action);
        dos.writeInt(this.x);
        dos.write(this.y);
        dos.writeInt(this.z);
        dos.write(this.face);
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handlePlayerAction(this);
    }
    
    @Override
    public int getEstimatedSize() {
        return 11;
    }
}
