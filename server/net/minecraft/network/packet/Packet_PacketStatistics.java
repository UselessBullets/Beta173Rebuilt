// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

class Packet_PacketStatistics
{
    private int count;
    private long totalSize;
    
    private Packet_PacketStatistics() {
    }
    
    public void addPacket(final int bytes) {
        ++this.count;
        this.totalSize += bytes;
    }
}
