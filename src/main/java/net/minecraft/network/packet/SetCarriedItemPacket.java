// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class SetCarriedItemPacket extends Packet
{
    public int slot;
    
    public SetCarriedItemPacket() {
    }
    
    public SetCarriedItemPacket(final int slot) {
        this.slot = slot;
    }
    
    @Override
    public void read(final DataInputStream dis) throws IOException {
        this.slot = dis.readShort();
    }
    
    @Override
    public void write(final DataOutputStream dos) throws IOException {
        dos.writeShort(this.slot);
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handleSetCarriedItem(this);
    }
    
    @Override
    public int getEstimatedSize() {
        return 2;
    }
}
