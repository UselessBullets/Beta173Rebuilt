// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.model;

public class SkeletonModel extends ZombieModel
{
    public SkeletonModel() {
        final float n = 0.0f;
        (this.arm0 = new Cube(40, 16)).addBox(-1.0f, -2.0f, -1.0f, 2, 12, 2, n);
        this.arm0.setPos(-5.0f, 2.0f, 0.0f);
        this.arm1 = new Cube(40, 16);
        this.arm1.mirror = true;
        this.arm1.addBox(-1.0f, -2.0f, -1.0f, 2, 12, 2, n);
        this.arm1.setPos(5.0f, 2.0f, 0.0f);
        (this.leg0 = new Cube(0, 16)).addBox(-1.0f, 0.0f, -1.0f, 2, 12, 2, n);
        this.leg0.setPos(-2.0f, 12.0f, 0.0f);
        this.leg1 = new Cube(0, 16);
        this.leg1.mirror = true;
        this.leg1.addBox(-1.0f, 0.0f, -1.0f, 2, 12, 2, n);
        this.leg1.setPos(2.0f, 12.0f, 0.0f);
    }
}
