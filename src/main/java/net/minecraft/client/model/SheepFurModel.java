// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.model;

public class SheepFurModel extends QuadrupedModel
{
    public SheepFurModel() {
        super(12, 0.0f);
        (this.head = new Cube(0, 0)).addBox(-3.0f, -4.0f, -4.0f, 6, 6, 6, 0.6f);
        this.head.setPos(0.0f, 6.0f, -8.0f);
        (this.body = new Cube(28, 8)).addBox(-4.0f, -10.0f, -7.0f, 8, 16, 6, 1.75f);
        this.body.setPos(0.0f, 5.0f, 2.0f);
        final float n = 0.5f;
        (this.leg0 = new Cube(0, 16)).addBox(-2.0f, 0.0f, -2.0f, 4, 6, 4, n);
        this.leg0.setPos(-3.0f, 12.0f, 7.0f);
        (this.leg1 = new Cube(0, 16)).addBox(-2.0f, 0.0f, -2.0f, 4, 6, 4, n);
        this.leg1.setPos(3.0f, 12.0f, 7.0f);
        (this.leg2 = new Cube(0, 16)).addBox(-2.0f, 0.0f, -2.0f, 4, 6, 4, n);
        this.leg2.setPos(-3.0f, 12.0f, -5.0f);
        (this.leg3 = new Cube(0, 16)).addBox(-2.0f, 0.0f, -2.0f, 4, 6, 4, n);
        this.leg3.setPos(3.0f, 12.0f, -5.0f);
    }
}
