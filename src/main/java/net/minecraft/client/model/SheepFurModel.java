// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.model;

public class SheepFurModel extends QuadrupedModel
{
    public SheepFurModel() {
        super(12, 0.0f);
        this.head = new Cube(0, 0);
        this.head.addBox(-3.0f, -4.0f, -4.0f, 6, 6, 6, 0.6f); // Head
        this.head.setPos(0.0f, 6.0f, -8.0f);

        this.body = new Cube(28, 8);
        this.body.addBox(-4.0f, -10.0f, -7.0f, 8, 16, 6, 1.75f); // Body
        this.body.setPos(0.0f, 5.0f, 2.0f);

        final float g = 0.5f;
        this.leg0 = new Cube(0, 16);
        this.leg0.addBox(-2.0f, 0.0f, -2.0f, 4, 6, 4, g); // Leg0
        this.leg0.setPos(-3.0f, 12.0f, 7.0f);

        this.leg1 = new Cube(0, 16);
        this.leg1.addBox(-2.0f, 0.0f, -2.0f, 4, 6, 4, g); // Leg1
        this.leg1.setPos(3.0f, 12.0f, 7.0f);

        this.leg2 = new Cube(0, 16);
        this.leg2.addBox(-2.0f, 0.0f, -2.0f, 4, 6, 4, g); // Leg2
        this.leg2.setPos(-3.0f, 12.0f, -5.0f);

        this.leg3 = new Cube(0, 16);
        this.leg3.addBox(-2.0f, 0.0f, -2.0f, 4, 6, 4, g); // Leg3
        this.leg3.setPos(3.0f, 12.0f, -5.0f);
    }
}
