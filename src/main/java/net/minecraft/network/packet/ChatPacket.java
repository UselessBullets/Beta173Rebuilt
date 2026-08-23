// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import net.minecraft.SharedConstants;
import net.minecraft.world.entity.player.Player;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class ChatPacket extends Packet
{
    // longest allowed string is "<" + name + "> " + message
    public static final int MAX_LENGTH = SharedConstants.maxChatLength + Player.MAX_NAME_LENGTH + 3;
    public String message;
    
    public ChatPacket() {
    }
    
    public ChatPacket(String message) {
        if (message.length() > MAX_LENGTH) message = message.substring(0, MAX_LENGTH);
        this.message = message;
    }
    
    @Override
    public void read(final DataInputStream dis) throws IOException {
        this.message = Packet.readUTF(dis, MAX_LENGTH);
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
