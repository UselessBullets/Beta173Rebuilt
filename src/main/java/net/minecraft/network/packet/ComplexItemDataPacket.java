// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class ComplexItemDataPacket extends Packet
{
    public short itemType;
    public short itemId;
    public byte[] data;

    public ComplexItemDataPacket() {
        this.shouldDelay = true;
    }

    public ComplexItemDataPacket(final short itemType, final short itemId, final byte[] data) {
        this.shouldDelay = true;
        this.itemType = itemType;
        this.itemId = itemId;
        this.data = data;
    }
    
    @Override
    public void read(final DataInputStream dis) throws IOException {
        this.itemType = dis.readShort();
        this.itemId = dis.readShort();
        dis.readFully(this.data = new byte[dis.readByte() & 0xFF]);
    }
    
    @Override
    public void write(final DataOutputStream dos) throws IOException {
        dos.writeShort(this.itemType);
        dos.writeShort(this.itemId);
        dos.writeByte(this.data.length);

        dos.write(this.data);
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handleComplexItemData(this);
    }
    
    @Override
    public int getEstimatedSize() {
        return 4 + this.data.length;
    }
}
