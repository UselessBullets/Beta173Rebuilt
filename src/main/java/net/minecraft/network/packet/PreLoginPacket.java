// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;

public class PreLoginPacket extends Packet
{
    public String userName;
    
    public PreLoginPacket() {
    }
    
    public PreLoginPacket(final String userName) {
        this.userName = userName;
    }
    
    @Override
    public void read(final DataInputStream dis) {
        this.userName = Packet.readUTF(dis, 32);
    }
    
    @Override
    public void write(final DataOutputStream dos) {
        Packet.writeUTF(this.userName, dos);
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handlePreLogin(this);
    }
    
    @Override
    public int getEstimatedSize() {
        return 4 + this.userName.length() + 4;
    }
}
