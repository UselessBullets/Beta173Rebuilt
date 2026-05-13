// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class SetTimePacket extends Packet
{
    public long time;

    public SetTimePacket() {
    }

    public SetTimePacket(final long time) {
        this.time = time;
    }
    
    @Override
    public void read(final DataInputStream dis) throws IOException {
        this.time = dis.readLong();
    }
    
    @Override
    public void write(final DataOutputStream dos) throws IOException {
        dos.writeLong(this.time);
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handleSetTime(this);
    }
    
    @Override
    public int getEstimatedSize() {
        return 8;
    }
}
