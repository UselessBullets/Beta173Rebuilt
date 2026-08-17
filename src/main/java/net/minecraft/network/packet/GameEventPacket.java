// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class GameEventPacket extends Packet
{
    public static final int NO_RESPAWN_BED_AVAILABLE = 0;
    public static final int START_RAINING = 1;
    public static final int STOP_RAINING = 2;
    public static final String[] EVENT_LANGUAGE_ID = new String[] { "tile.bed.notValid", null, null };
    public int event;

    public GameEventPacket() {
    }

    public GameEventPacket(final int event) {
        this.event = event;
    }

    @Override
    public void read(final DataInputStream dis) throws IOException {
        this.event = dis.readByte();
    }
    
    @Override
    public void write(final DataOutputStream dos) throws IOException {
        dos.writeByte(this.event);
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handleGameEvent(this);
    }
    
    @Override
    public int getEstimatedSize() {
        return 1;
    }

}
