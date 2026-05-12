// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class KeepAlivePacket extends Packet
{
    @Override
    public void handle(final PacketListener listener) {
    }
    
    @Override
    public void read(final DataInputStream dis) throws IOException {
    }
    
    @Override
    public void write(final DataOutputStream dos) throws IOException {
    }
    
    @Override
    public int getEstimatedSize() {
        return 0;
    }
}
