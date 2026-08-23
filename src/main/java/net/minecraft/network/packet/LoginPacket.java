// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import net.minecraft.world.entity.player.Player;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

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

    public LoginPacket(final String userName, final int clientVersion, final long seed, final byte dimension) {
        this.userName = userName;
        this.clientVersion = clientVersion;
        this.seed = seed;
        this.dimension = dimension;
    }
    
    @Override
    public void read(final DataInputStream dis) throws IOException {
        this.clientVersion = dis.readInt();
        this.userName = Packet.readUTF(dis, Player.MAX_NAME_LENGTH);
        this.seed = dis.readLong();
        this.dimension = dis.readByte();
    }
    
    @Override
    public void write(final DataOutputStream dos) throws IOException {
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
