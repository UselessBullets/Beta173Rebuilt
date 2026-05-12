// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;

public class AwardStatPacket extends Packet
{
    public int statId;
    public int count;
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handleAwardStat(this);
    }
    
    @Override
    public void read(final DataInputStream dis) {
        this.statId = dis.readInt();
        this.count = dis.readByte();
    }
    
    @Override
    public void write(final DataOutputStream dos) {
        dos.writeInt(this.statId);
        dos.writeByte(this.count);
    }
    
    @Override
    public int getEstimatedSize() {
        return 6;
    }
}
