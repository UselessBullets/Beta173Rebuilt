// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.model;

import util.Mth;

public class SpiderModel extends Model
{
    public Cube head;
    public Cube body0;
    public Cube body1;
    public Cube leg0;
    public Cube leg1;
    public Cube leg2;
    public Cube leg3;
    public Cube leg4;
    public Cube leg5;
    public Cube leg6;
    public Cube leg7;
    
    public SpiderModel() {
        final float g = 0.0f;
        final int n = 15;
        (this.head = new Cube(32, 4)).addBox(-4.0f, -4.0f, -8.0f, 8, 8, 8, g);
        this.head.setPos(0.0f, (float)(0 + n), -3.0f);
        (this.body0 = new Cube(0, 0)).addBox(-3.0f, -3.0f, -3.0f, 6, 6, 6, g);
        this.body0.setPos(0.0f, (float)n, 0.0f);
        (this.body1 = new Cube(0, 12)).addBox(-5.0f, -4.0f, -6.0f, 10, 8, 12, g);
        this.body1.setPos(0.0f, (float)(0 + n), 9.0f);
        (this.leg0 = new Cube(18, 0)).addBox(-15.0f, -1.0f, -1.0f, 16, 2, 2, g);
        this.leg0.setPos(-4.0f, (float)(0 + n), 2.0f);
        (this.leg1 = new Cube(18, 0)).addBox(-1.0f, -1.0f, -1.0f, 16, 2, 2, g);
        this.leg1.setPos(4.0f, (float)(0 + n), 2.0f);
        (this.leg2 = new Cube(18, 0)).addBox(-15.0f, -1.0f, -1.0f, 16, 2, 2, g);
        this.leg2.setPos(-4.0f, (float)(0 + n), 1.0f);
        (this.leg3 = new Cube(18, 0)).addBox(-1.0f, -1.0f, -1.0f, 16, 2, 2, g);
        this.leg3.setPos(4.0f, (float)(0 + n), 1.0f);
        (this.leg4 = new Cube(18, 0)).addBox(-15.0f, -1.0f, -1.0f, 16, 2, 2, g);
        this.leg4.setPos(-4.0f, (float)(0 + n), 0.0f);
        (this.leg5 = new Cube(18, 0)).addBox(-1.0f, -1.0f, -1.0f, 16, 2, 2, g);
        this.leg5.setPos(4.0f, (float)(0 + n), 0.0f);
        (this.leg6 = new Cube(18, 0)).addBox(-15.0f, -1.0f, -1.0f, 16, 2, 2, g);
        this.leg6.setPos(-4.0f, (float)(0 + n), -1.0f);
        (this.leg7 = new Cube(18, 0)).addBox(-1.0f, -1.0f, -1.0f, 16, 2, 2, g);
        this.leg7.setPos(4.0f, (float)(0 + n), -1.0f);
    }
    
    @Override
    public void render(final float time, final float r, final float bob, final float yRot, final float xRot, final float scale) {
        this.setupAnim(time, r, bob, yRot, xRot, scale);
        this.head.render(scale);
        this.body0.render(scale);
        this.body1.render(scale);
        this.leg0.render(scale);
        this.leg1.render(scale);
        this.leg2.render(scale);
        this.leg3.render(scale);
        this.leg4.render(scale);
        this.leg5.render(scale);
        this.leg6.render(scale);
        this.leg7.render(scale);
    }
    
    @Override
    public void setupAnim(final float time, final float r, final float bob, final float yRot, final float xRot, final float scale) {
        this.head.yRot = yRot * Mth.DEGRAD;
        this.head.xRot = xRot * Mth.DEGRAD;
        final float n = 0.7853982f;
        this.leg0.zRot = -n;
        this.leg1.zRot = n;
        this.leg2.zRot = -n * 0.74f;
        this.leg3.zRot = n * 0.74f;
        this.leg4.zRot = -n * 0.74f;
        this.leg5.zRot = n * 0.74f;
        this.leg6.zRot = -n;
        this.leg7.zRot = n;
        final float n2 = -0.0f;
        final float n3 = 0.3926991f;
        this.leg0.yRot = n3 * 2.0f + n2;
        this.leg1.yRot = -n3 * 2.0f - n2;
        this.leg2.yRot = n3 * 1.0f + n2;
        this.leg3.yRot = -n3 * 1.0f - n2;
        this.leg4.yRot = -n3 * 1.0f + n2;
        this.leg5.yRot = n3 * 1.0f - n2;
        this.leg6.yRot = -n3 * 2.0f + n2;
        this.leg7.yRot = n3 * 2.0f - n2;
        final float n4 = -(Mth.cos(time * 0.6662f * 2.0f + 0.0f) * 0.4f) * r;
        final float n5 = -(Mth.cos(time * 0.6662f * 2.0f + Mth.PI) * 0.4f) * r;
        final float n6 = -(Mth.cos(time * 0.6662f * 2.0f + Mth.HALF_PI) * 0.4f) * r;
        final float n7 = -(Mth.cos(time * 0.6662f * 2.0f + (Mth.HALF_PI * 3f)) * 0.4f) * r;
        final float n8 = Math.abs(Mth.sin(time * 0.6662f + 0.0f) * 0.4f) * r;
        final float n9 = Math.abs(Mth.sin(time * 0.6662f + Mth.PI) * 0.4f) * r;
        final float n10 = Math.abs(Mth.sin(time * 0.6662f + Mth.HALF_PI) * 0.4f) * r;
        final float n11 = Math.abs(Mth.sin(time * 0.6662f + (Mth.HALF_PI * 3f)) * 0.4f) * r;
        final Cube leg0 = this.leg0;
        leg0.yRot += n4;
        final Cube leg2 = this.leg1;
        leg2.yRot += -n4;
        final Cube leg3 = this.leg2;
        leg3.yRot += n5;
        final Cube leg4 = this.leg3;
        leg4.yRot += -n5;
        final Cube leg5 = this.leg4;
        leg5.yRot += n6;
        final Cube leg6 = this.leg5;
        leg6.yRot += -n6;
        final Cube leg7 = this.leg6;
        leg7.yRot += n7;
        final Cube leg8 = this.leg7;
        leg8.yRot += -n7;
        final Cube leg9 = this.leg0;
        leg9.zRot += n8;
        final Cube leg10 = this.leg1;
        leg10.zRot += -n8;
        final Cube leg11 = this.leg2;
        leg11.zRot += n9;
        final Cube leg12 = this.leg3;
        leg12.zRot += -n9;
        final Cube leg13 = this.leg4;
        leg13.zRot += n10;
        final Cube leg14 = this.leg5;
        leg14.zRot += -n10;
        final Cube leg15 = this.leg6;
        leg15.zRot += n11;
        final Cube leg16 = this.leg7;
        leg16.zRot += -n11;
    }
}
