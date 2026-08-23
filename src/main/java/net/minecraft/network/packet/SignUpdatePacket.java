// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import net.minecraft.world.level.tile.entity.SignTileEntity;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class SignUpdatePacket extends Packet
{
    public int x, y, z;
    public String[] lines;
    
    public SignUpdatePacket() {
        this.shouldDelay = true;
    }
    
    public SignUpdatePacket(final int x, final int y, final int z, final String[] lines) {
        this.shouldDelay = true;
        this.x = x;
        this.y = y;
        this.z = z;
        this.lines = lines;
    }
    
    @Override
    public void read(final DataInputStream dis) throws IOException {
        this.x = dis.readInt();
        this.y = dis.readShort();
        this.z = dis.readInt();
        this.lines = new String[SignTileEntity.MAX_SIGN_LINES];
        for (int i = 0; i < SignTileEntity.MAX_SIGN_LINES; ++i) {
            this.lines[i] = Packet.readUTF(dis, SignTileEntity.MAX_LINE_LENGTH);
        }
    }
    
    @Override
    public void write(final DataOutputStream dos) throws IOException {
        dos.writeInt(this.x);
        dos.writeShort(this.y);
        dos.writeInt(this.z);
        for (int i = 0; i < SignTileEntity.MAX_SIGN_LINES; ++i) {
            Packet.writeUTF(this.lines[i], dos);
        }
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handleSignUpdate(this);
    }
    
    @Override
    public int getEstimatedSize() {
        int l = 0;
        for (int i = 0; i < SignTileEntity.MAX_SIGN_LINES; ++i) {
            l += this.lines[i].length();
        }
        return l;
    }
}
