// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import net.minecraft.world.level.TilePos;

import java.io.IOException;
import java.util.HashSet;
import java.io.DataInputStream;
import java.util.Set;

public class ExplodePacket extends Packet
{
    public double x;
    public double y;
    public double z;
    public float r;
    public Set<TilePos> toBlow;

    public ExplodePacket() {
    }

    public ExplodePacket(final double x, final double y, final double z, final float r, final Set<TilePos> toBlow) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.r = r;
        this.toBlow = new HashSet<>(toBlow);
    }

    @Override
    public void read(final DataInputStream dis) throws IOException {
        this.x = dis.readDouble();
        this.y = dis.readDouble();
        this.z = dis.readDouble();
        this.r = dis.readFloat();
        final int int1 = dis.readInt();
        this.toBlow = new HashSet<>();
        final int n = (int)this.x;
        final int n2 = (int)this.y;
        final int n3 = (int)this.z;
        for (int i = 0; i < int1; ++i) {
            this.toBlow.add(new TilePos(dis.readByte() + n, dis.readByte() + n2, dis.readByte() + n3));
        }
    }
    
    @Override
    public void write(final DataOutputStream dos) throws IOException {
        dos.writeDouble(this.x);
        dos.writeDouble(this.y);
        dos.writeDouble(this.z);
        dos.writeFloat(this.r);
        dos.writeInt(this.toBlow.size());
        final int n = (int)this.x;
        final int n2 = (int)this.y;
        final int n3 = (int)this.z;
        for (final TilePos tilePos : this.toBlow) {
            final int v = tilePos.x - n;
            final int v2 = tilePos.y - n2;
            final int v3 = tilePos.z - n3;
            dos.writeByte(v);
            dos.writeByte(v2);
            dos.writeByte(v3);
        }
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handleExplosion(this);
    }
    
    @Override
    public int getEstimatedSize() {
        return 32 + this.toBlow.size() * 3;
    }
}
