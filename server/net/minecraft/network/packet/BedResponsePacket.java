// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;

public class BedResponsePacket extends Packet
{
    public static final String[] BED_RESPONSES;
    public int type;
    
    public BedResponsePacket() {
    }
    
    public BedResponsePacket(final int type) {
        this.type = type;
    }
    
    @Override
    public void read(final DataInputStream dis) {
        this.type = dis.readByte();
    }
    
    @Override
    public void write(final DataOutputStream dos) {
        dos.writeByte(this.type);
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handleBedResponse(this);
    }
    
    @Override
    public int getEstimatedSize() {
        return 1;
    }
    
    static {
        BED_RESPONSES = new String[] { "tile.bed.notValid", null, null };
    }
}
