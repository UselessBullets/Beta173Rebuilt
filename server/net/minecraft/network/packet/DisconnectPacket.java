// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;

public class DisconnectPacket extends Packet
{
    public String reason;
    
    public DisconnectPacket() {
    }
    
    public DisconnectPacket(final String reason) {
        this.reason = reason;
    }
    
    @Override
    public void read(final DataInputStream dis) {
        this.reason = Packet.readUTF(dis, 100);
    }
    
    @Override
    public void write(final DataOutputStream dos) {
        Packet.writeUTF(this.reason, dos);
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handleDisconnect(this);
    }
    
    @Override
    public int getEstimatedSize() {
        return this.reason.length();
    }
}
