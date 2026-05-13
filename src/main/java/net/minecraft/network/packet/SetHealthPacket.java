// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class SetHealthPacket extends Packet
{
    public int health;

    public SetHealthPacket() {
    }

    public SetHealthPacket(final int health) {
        this.health = health;
    }

    @Override
    public void read(final DataInputStream dis) throws IOException {
        this.health = dis.readShort();
    }
    
    @Override
    public void write(final DataOutputStream dos) throws IOException {
        dos.writeShort(this.health);
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handleSetHealth(this);
    }
    
    @Override
    public int getEstimatedSize() {
        return 2;
    }
}
