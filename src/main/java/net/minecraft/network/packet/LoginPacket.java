// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;

public class LoginPacket extends Packet
{
    public int clientVersion;
    public String userName;
    public long seed;
    public byte dimension;
    
    public LoginPacket() {
    }
    
    public LoginPacket(final String userName, final int clientVersion) {
        this.userName = userName;
        this.clientVersion = clientVersion;
    }
    
    @Override
    public void read(final DataInputStream dis) {
        this.clientVersion = dis.readInt();
        this.userName = Packet.readUTF(dis, 16);
        this.seed = dis.readLong();
        this.dimension = dis.readByte();
    }
    
    @Override
    public void write(final DataOutputStream dos) {
        dos.writeInt(this.clientVersion);
        Packet.writeUTF(this.userName, dos);
        dos.writeLong(this.seed);
        dos.writeByte(this.dimension);
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handleLogin(this);
    }
    
    @Override
    public int getEstimatedSize() {
        return 4 + this.userName.length() + 4 + 5;
    }
}
