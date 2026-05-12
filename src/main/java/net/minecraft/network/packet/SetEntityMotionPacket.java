// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.packet;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import net.minecraft.world.entity.Entity;

public class SetEntityMotionPacket extends Packet
{
    public int id;
    public int xa;
    public int ya;
    public int za;
    
    public SetEntityMotionPacket() {
    }
    
    public SetEntityMotionPacket(final Entity entity) {
        this(entity.entityId, entity.xd, entity.yd, entity.zd);
    }
    
    public SetEntityMotionPacket(final int id, double xd, double yd, double zd) {
        this.id = id;
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
    
    @Override
    public void read(final DataInputStream dis) {
        this.id = dis.readInt();
        this.xa = dis.readShort();
        this.ya = dis.readShort();
        this.za = dis.readShort();
    }
    
    @Override
    public void write(final DataOutputStream dos) {
        dos.writeInt(this.id);
        dos.writeShort(this.xa);
        dos.writeShort(this.ya);
        dos.writeShort(this.za);
    }
    
    @Override
    public void handle(final PacketListener listener) {
        listener.handleSetEntityMotion(this);
    }
    
    @Override
    public int getEstimatedSize() {
        return 10;
    }
}
