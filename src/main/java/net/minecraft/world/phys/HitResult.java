// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.phys;

import net.minecraft.world.entity.Entity;

public class HitResult
{
    public Type type;
    public int x, y, z, f;
    public Vec3 pos;
    public Entity entity;
    
    public HitResult(final int x, final int y, final int z, final int f, final Vec3 pos) {
        this.type = Type.TILE;
        this.x = x;
        this.y = y;
        this.z = z;
        this.f = f;
        this.pos = Vec3.newTemp(pos.x, pos.y, pos.z);
    }
    
    public HitResult(final Entity entity) {
        this.type = Type.ENTITY;
        this.entity = entity;
        this.pos = Vec3.newTemp(entity.x, entity.y, entity.z);
    }

    public double distanceTo(Entity e) {
        double xd = this.pos.x - e.x;
        double yd = this.pos.y - e.y;
        double zd = this.pos.z - e.z;
        return xd * xd + yd * yd + zd * zd;
    }

    public enum Type
    {
        TILE,
        ENTITY;
    }
}
