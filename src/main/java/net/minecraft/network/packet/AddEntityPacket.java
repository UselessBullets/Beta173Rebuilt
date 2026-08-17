// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import net.minecraft.world.entity.Entity;
import util.Mth;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class AddEntityPacket extends Packet
{
    public static final int BOAT = 1;
    public static final int MINECART_RIDEABLE = 10;
    public static final int MINECART_CHEST = 11;
    public static final int MINECART_FURNACE = 12;
    public static final int PRIMED_TNT = 50;
    public static final int ARROW = 60;
    public static final int SNOWBALL = 61;
    public static final int EGG = 62;
    public static final int FIREBALL = 63;
    public static final int FALLING_SAND = 70; // Useless - LCE called this just "Falling" and combined sand and gravel into one entity type, presumably in b1.7.3 they were split into two
    public static final int FALLING_GRAVEL = 71;


    public static final int FISH_HOOK = 90;

    public int id;
    public int x;
    public int y;
    public int z;
    public int xa;
    public int ya;
    public int za;
    public int type;
    public int data;

    public AddEntityPacket() {
    }

    public AddEntityPacket(final Entity e, final int type) {
        this(e, type, 0);
    }

    public AddEntityPacket(final Entity e, final int type, final int data) {
        this.id = e.entityId;
        this.x = Mth.floor(e.x * 32.0);
        this.y = Mth.floor(e.y * 32.0);
        this.z = Mth.floor(e.z * 32.0);
        this.type = type;
        this.data = data;
        if (data > 0) {
            double xd = e.xd;
            double yd = e.yd;
            double zd = e.zd;
            final double n = 3.9;
            if (xd < -n) {
                xd = -n;
            }
            if (yd < -n) {
                yd = -n;
            }
            if (zd < -n) {
                zd = -n;
            }
            if (xd > n) {
                xd = n;
            }
            if (yd > n) {
                yd = n;
            }
            if (zd > n) {
                zd = n;
            }
            this.xa = (int)(xd * 8000.0);
            this.ya = (int)(yd * 8000.0);
            this.za = (int)(zd * 8000.0);
        }
    }

    @Override
    public void read(final DataInputStream dis) throws IOException {
        this.id = dis.readInt();
        this.type = dis.readByte();
        this.x = dis.readInt();
        this.y = dis.readInt();
        this.z = dis.readInt();
        this.data = dis.readInt();
        if (this.data > 0) {
            this.xa = dis.readShort();
            this.ya = dis.readShort();
            this.za = dis.readShort();
        }
    }
    
    @Override
    public void write(final DataOutputStream dos) throws IOException {
        dos.writeInt(this.id);
        dos.writeByte(this.type);
        dos.writeInt(this.x);
        dos.writeInt(this.y);
        dos.writeInt(this.z);
        dos.writeInt(this.data);
        if (this.data > 0) {
            dos.writeShort(this.xa);
            dos.writeShort(this.ya);
            dos.writeShort(this.za);
        }
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handleAddEntity(this);
    }
    
    @Override
    public int getEstimatedSize() {
        return (21 + this.data > 0) ? 6 : 0;
    }
}
