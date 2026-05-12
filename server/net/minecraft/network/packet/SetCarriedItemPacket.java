// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;

public class SetCarriedItemPacket extends Packet
{
    public int slot;
    
    @Override
    public void read(final DataInputStream dis) {
        this.slot = dis.readShort();
    }
    
    @Override
    public void write(final DataOutputStream dos) {
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
