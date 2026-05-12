// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class ChatPacket extends Packet
{
    public String message;
    
    public ChatPacket() {
    }
    
    public ChatPacket(String message) {
        if (message.length() > 119) {
            message = message.substring(0, 119);
        }
        this.message = message;
    }
    
    @Override
    public void read(final DataInputStream dis) throws IOException {
        this.message = Packet.readUTF(dis, 119);
    }
    
    @Override
    public void write(final DataOutputStream dos) throws IOException {
        Packet.writeUTF(this.message, dos);
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handleChat(this);
    }
    
    @Override
    public int getEstimatedSize() {
        return this.message.length();
    }
}
