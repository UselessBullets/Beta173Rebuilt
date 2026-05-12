// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.phys;

import net.minecraft.world.entity.Entity;

public class HitResult
{
    public HitResult_Type type;
    public int x;
    public int y;
    public int z;
    public int f;
    public Vec3 pos;
    public Entity entity;
    
    public HitResult(final int integer1, final int integer2, final int integer3, final int integer4, final Vec3 ba) {
        this.type = HitResult_Type.TILE;
        this.x = integer1;
        this.y = integer2;
        this.z = integer3;
        this.f = integer4;
        this.pos = Vec3.newTemp(ba.x, ba.y, ba.z);
    }
    
    public HitResult(final Entity lq) {
        this.type = HitResult_Type.ENTITY;
        this.entity = lq;
        this.pos = Vec3.newTemp(lq.x, lq.y, lq.z);
    }
}
