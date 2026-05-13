// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class AwardStatPacket extends Packet
{
    public int statId;
    public int count;

    public AwardStatPacket() {
    }

    public AwardStatPacket(final int statId, final int count) {
        this.statId = statId;
        this.count = count;
    }

    @Override
    public void handle(final PacketListener listener) {
        listener.handleAwardStat(this);
    }
    
    @Override
    public void read(final DataInputStream dis) throws IOException {
        this.statId = dis.readInt();
        this.count = dis.readByte();
    }
    
    @Override
    public void write(final DataOutputStream dos) throws IOException {
        dos.writeInt(this.statId);
        dos.writeByte(this.count);
    }
    
    @Override
    public int getEstimatedSize() {
        return 6;
    }
}
