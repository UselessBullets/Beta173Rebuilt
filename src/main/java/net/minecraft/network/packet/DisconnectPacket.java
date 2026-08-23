// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import net.minecraft.SharedConstants;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class DisconnectPacket extends Packet
{
    public String reason;
    
    public DisconnectPacket() {
    }
    
    public DisconnectPacket(final String reason) {
        this.reason = reason;
    }
    
    @Override
    public void read(final DataInputStream dis) throws IOException {
        this.reason = Packet.readUTF(dis, SharedConstants.maxChatLength);
    }
    
    @Override
    public void write(final DataOutputStream dos) throws IOException {
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
