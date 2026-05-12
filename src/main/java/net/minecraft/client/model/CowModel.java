// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.model;

public class CowModel extends QuadrupedModel
{
    Cube udder;
    Cube horn1;
    Cube horn2;
    
    public CowModel() {
        super(12, 0.0f);
        (this.head = new Cube(0, 0)).addBox(-4.0f, -4.0f, -6.0f, 8, 8, 6, 0.0f);
        this.head.setPos(0.0f, 4.0f, -8.0f);
        (this.horn1 = new Cube(22, 0)).addBox(-4.0f, -5.0f, -4.0f, 1, 3, 1, 0.0f);
        this.horn1.setPos(0.0f, 3.0f, -7.0f);
        (this.horn2 = new Cube(22, 0)).addBox(3.0f, -5.0f, -4.0f, 1, 3, 1, 0.0f);
        this.horn2.setPos(0.0f, 3.0f, -7.0f);
        (this.udder = new Cube(52, 0)).addBox(-2.0f, -3.0f, 0.0f, 4, 6, 2, 0.0f);
        this.udder.setPos(0.0f, 14.0f, 6.0f);
        this.udder.xRot = 1.5707964f;
        (this.body = new Cube(18, 4)).addBox(-6.0f, -10.0f, -7.0f, 12, 18, 10, 0.0f);
        this.body.setPos(0.0f, 5.0f, 2.0f);
        final Cube leg0 = this.leg0;
        --leg0.x;
        final Cube leg2 = this.leg1;
        ++leg2.x;
        final Cube leg3 = this.leg0;
        leg3.z += 0.0f;
        final Cube leg4 = this.leg1;
        leg4.z += 0.0f;
        final Cube leg5 = this.leg2;
        --leg5.x;
        final Cube leg6 = this.leg3;
        ++leg6.x;
        final Cube leg7 = this.leg2;
        --leg7.z;
        final Cube leg8 = this.leg3;
        --leg8.z;
    }
    
    @Override
    public void render(final float time, final float r, final float bob, final float yRot, final float xRot, final float scale) {
        super.render(time, r, bob, yRot, xRot, scale);
        this.horn1.render(scale);
        this.horn2.render(scale);
        this.udder.render(scale);
    }
    
    @Override
    public void setupAnim(final float time, final float r, final float bob, final float yRot, final float xRot, final float scale) {
        super.setupAnim(time, r, bob, yRot, xRot, scale);
        this.horn1.yRot = this.head.yRot;
        this.horn1.xRot = this.head.xRot;
        this.horn2.yRot = this.head.yRot;
        this.horn2.xRot = this.head.xRot;
    }
}
