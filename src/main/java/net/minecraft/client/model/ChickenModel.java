// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.model;

import util.Mth;

public class ChickenModel extends Model
{
    public Cube head;
    public Cube body;
    public Cube leg0;
    public Cube leg1;
    public Cube wing0;
    public Cube wing1;
    public Cube beak;
    public Cube redThing;
    
    public ChickenModel() {
        final int n = 16;
        (this.head = new Cube(0, 0)).addBox(-2.0f, -6.0f, -2.0f, 4, 6, 3, 0.0f);
        this.head.setPos(0.0f, (float)(-1 + n), -4.0f);
        (this.beak = new Cube(14, 0)).addBox(-2.0f, -4.0f, -4.0f, 4, 2, 2, 0.0f);
        this.beak.setPos(0.0f, (float)(-1 + n), -4.0f);
        (this.redThing = new Cube(14, 4)).addBox(-1.0f, -2.0f, -3.0f, 2, 2, 2, 0.0f);
        this.redThing.setPos(0.0f, (float)(-1 + n), -4.0f);
        (this.body = new Cube(0, 9)).addBox(-3.0f, -4.0f, -3.0f, 6, 8, 6, 0.0f);
        this.body.setPos(0.0f, (float)(0 + n), 0.0f);
        (this.leg0 = new Cube(26, 0)).addBox(-1.0f, 0.0f, -3.0f, 3, 5, 3);
        this.leg0.setPos(-2.0f, (float)(3 + n), 1.0f);
        (this.leg1 = new Cube(26, 0)).addBox(-1.0f, 0.0f, -3.0f, 3, 5, 3);
        this.leg1.setPos(1.0f, (float)(3 + n), 1.0f);
        (this.wing0 = new Cube(24, 13)).addBox(0.0f, 0.0f, -3.0f, 1, 4, 6);
        this.wing0.setPos(-4.0f, (float)(-3 + n), 0.0f);
        (this.wing1 = new Cube(24, 13)).addBox(-1.0f, 0.0f, -3.0f, 1, 4, 6);
        this.wing1.setPos(4.0f, (float)(-3 + n), 0.0f);
    }
    
    @Override
    public void render(final float time, final float r, final float bob, final float yRot, final float xRot, final float scale) {
        this.setupAnim(time, r, bob, yRot, xRot, scale);
        this.head.render(scale);
        this.beak.render(scale);
        this.redThing.render(scale);
        this.body.render(scale);
        this.leg0.render(scale);
        this.leg1.render(scale);
        this.wing0.render(scale);
        this.wing1.render(scale);
    }
    
    @Override
    public void setupAnim(final float time, final float r, final float bob, final float yRot, final float xRot, final float scale) {
        this.head.xRot = -(xRot / Mth.RADDEG);
        this.head.yRot = yRot / Mth.RADDEG;
        this.beak.xRot = this.head.xRot;
        this.beak.yRot = this.head.yRot;
        this.redThing.xRot = this.head.xRot;
        this.redThing.yRot = this.head.yRot;
        this.body.xRot = 1.5707964f;
        this.leg0.xRot = Mth.cos(time * 0.6662f) * 1.4f * r;
        this.leg1.xRot = Mth.cos(time * 0.6662f + 3.1415927f) * 1.4f * r;
        this.wing0.zRot = bob;
        this.wing1.zRot = -bob;
    }
}
