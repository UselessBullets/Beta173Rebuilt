// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.model;

import util.Mth;

public class QuadrupedModel extends Model
{
    public Cube head;
    public Cube body;
    public Cube leg0;
    public Cube leg1;
    public Cube leg2;
    public Cube leg3;
    
    public QuadrupedModel(final int legSize, final float g) {
        (this.head = new Cube(0, 0)).addBox(-4.0f, -4.0f, -8.0f, 8, 8, 8, g);
        this.head.setPos(0.0f, (float)(18 - legSize), -6.0f);
        (this.body = new Cube(28, 8)).addBox(-5.0f, -10.0f, -7.0f, 10, 16, 8, g);
        this.body.setPos(0.0f, (float)(17 - legSize), 2.0f);
        (this.leg0 = new Cube(0, 16)).addBox(-2.0f, 0.0f, -2.0f, 4, legSize, 4, g);
        this.leg0.setPos(-3.0f, (float)(24 - legSize), 7.0f);
        (this.leg1 = new Cube(0, 16)).addBox(-2.0f, 0.0f, -2.0f, 4, legSize, 4, g);
        this.leg1.setPos(3.0f, (float)(24 - legSize), 7.0f);
        (this.leg2 = new Cube(0, 16)).addBox(-2.0f, 0.0f, -2.0f, 4, legSize, 4, g);
        this.leg2.setPos(-3.0f, (float)(24 - legSize), -5.0f);
        (this.leg3 = new Cube(0, 16)).addBox(-2.0f, 0.0f, -2.0f, 4, legSize, 4, g);
        this.leg3.setPos(3.0f, (float)(24 - legSize), -5.0f);
    }
    
    @Override
    public void render(final float time, final float r, final float bob, final float yRot, final float xRot, final float scale) {
        this.setupAnim(time, r, bob, yRot, xRot, scale);
        this.head.render(scale);
        this.body.render(scale);
        this.leg0.render(scale);
        this.leg1.render(scale);
        this.leg2.render(scale);
        this.leg3.render(scale);
    }
    
    @Override
    public void setupAnim(final float time, final float r, final float bob, final float yRot, final float xRot, final float scale) {
        this.head.xRot = xRot / Mth.RADDEG;
        this.head.yRot = yRot / Mth.RADDEG;
        this.body.xRot = (Mth.PI / 2f);
        this.leg0.xRot = Mth.cos(time * 0.6662f) * 1.4f * r;
        this.leg1.xRot = Mth.cos(time * 0.6662f + Mth.PI) * 1.4f * r;
        this.leg2.xRot = Mth.cos(time * 0.6662f + Mth.PI) * 1.4f * r;
        this.leg3.xRot = Mth.cos(time * 0.6662f) * 1.4f * r;
    }
}
