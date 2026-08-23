// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class SetSpawnPositionPacket extends Packet
{
    public int x, y, z;

    public SetSpawnPositionPacket() {
    }

    public SetSpawnPositionPacket(final int x, final int y, final int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void read(final DataInputStream dis) throws IOException {
        this.x = dis.readInt();
        this.y = dis.readInt();
        this.z = dis.readInt();
    }
    
    @Override
    public void write(final DataOutputStream dos) throws IOException {
        dos.writeInt(this.x);
        dos.writeInt(this.y);
        dos.writeInt(this.z);
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handleSetSpawn(this);
    }
    
    @Override
    public int getEstimatedSize() {
        return 3 * 4;
    }
}
