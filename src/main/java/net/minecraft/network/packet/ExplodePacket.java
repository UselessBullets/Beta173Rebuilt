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
    public double x, y, z;
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
        final int count = dis.readInt();

        this.toBlow = new HashSet<>();
        final int xp = (int)this.x;
        final int yp = (int)this.y;
        final int zp = (int)this.z;
        for (int i = 0; i < count; ++i) {
            int xx = dis.readByte() + xp;
            int yy = dis.readByte() + yp;
            int zz = dis.readByte() + zp;
            this.toBlow.add(new TilePos(xx, yy, zz));
        }
    }
    
    @Override
    public void write(final DataOutputStream dos) throws IOException {
        dos.writeDouble(this.x);
        dos.writeDouble(this.y);
        dos.writeDouble(this.z);
        dos.writeFloat(this.r);
        dos.writeInt(this.toBlow.size());

        final int xp = (int)this.x;
        final int yp = (int)this.y;
        final int zp = (int)this.z;
        for (final TilePos tp : this.toBlow) {
            final int xx = tp.x - xp;
            final int yy = tp.y - yp;
            final int zz = tp.z - zp;
            dos.writeByte(xx);
            dos.writeByte(yy);
            dos.writeByte(zz);
        }
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handleExplosion(this);
    }
    
    @Override
    public int getEstimatedSize() {
        return 8 * 3 + 4 + 4 + this.toBlow.size() * 3;
    }
}
