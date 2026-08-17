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
        final int yo = 18 + 6 - 9;

        this.head = new Cube(32, 4);
        this.head.addBox(-4.0f, -4.0f, -8.0f, 8, 8, 8, g); // Head
        this.head.setPos(0.0f, (float)(0 + yo), -3.0f);

        this.body0 = new Cube(0, 0);
        this.body0.addBox(-3.0f, -3.0f, -3.0f, 6, 6, 6, g); // Body
        this.body0.setPos(0.0f, (float)yo, 0.0f);

        this.body1 = new Cube(0, 12);
        this.body1.addBox(-5.0f, -4.0f, -6.0f, 10, 8, 12, g); // Body
        this.body1.setPos(0.0f, (float)(0 + yo), 9.0f);

        this.leg0 = new Cube(18, 0);
        this.leg0.addBox(-15.0f, -1.0f, -1.0f, 16, 2, 2, g); // Leg0
        this.leg0.setPos(-4.0f, (float)(0 + yo), 2.0f);

        this.leg1 = new Cube(18, 0);
        this.leg1.addBox(-1.0f, -1.0f, -1.0f, 16, 2, 2, g); // Leg1
        this.leg1.setPos(4.0f, (float)(0 + yo), 2.0f);

        this.leg2 = new Cube(18, 0);
        this.leg2.addBox(-15.0f, -1.0f, -1.0f, 16, 2, 2, g); // Leg2
        this.leg2.setPos(-4.0f, (float)(0 + yo), 1.0f);

        this.leg3 = new Cube(18, 0);
        this.leg3.addBox(-1.0f, -1.0f, -1.0f, 16, 2, 2, g); // Leg3
        this.leg3.setPos(4.0f, (float)(0 + yo), 1.0f);

        this.leg4 = new Cube(18, 0);
        this.leg4.addBox(-15.0f, -1.0f, -1.0f, 16, 2, 2, g); // Leg0
        this.leg4.setPos(-4.0f, (float)(0 + yo), 0.0f);

        this.leg5 = new Cube(18, 0);
        this.leg5.addBox(-1.0f, -1.0f, -1.0f, 16, 2, 2, g); // Leg1
        this.leg5.setPos(4.0f, (float)(0 + yo), 0.0f);

        this.leg6 = new Cube(18, 0);
        this.leg6.addBox(-15.0f, -1.0f, -1.0f, 16, 2, 2, g); // Leg2
        this.leg6.setPos(-4.0f, (float)(0 + yo), -1.0f);

        this.leg7 = new Cube(18, 0);
        this.leg7.addBox(-1.0f, -1.0f, -1.0f, 16, 2, 2, g); // Leg3
        this.leg7.setPos(4.0f, (float)(0 + yo), -1.0f);
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

        final float sr = Mth.PI / 4.0f;
        this.leg0.zRot = -sr;
        this.leg1.zRot = sr;

        this.leg2.zRot = -sr * 0.74f;
        this.leg3.zRot = sr * 0.74f;

        this.leg4.zRot = -sr * 0.74f;
        this.leg5.zRot = sr * 0.74f;

        this.leg6.zRot = -sr;
        this.leg7.zRot = sr;

        final float ro = -Mth.HALF_PI * 0;
        final float ur = Mth.HALF_PI / 4;
        this.leg0.yRot = +ur * 2.0f + ro;
        this.leg1.yRot = -ur * 2.0f - ro;
        this.leg2.yRot = +ur * 1.0f + ro;
        this.leg3.yRot = -ur * 1.0f - ro;
        this.leg4.yRot = -ur * 1.0f + ro;
        this.leg5.yRot = +ur * 1.0f - ro;
        this.leg6.yRot = -ur * 2.0f + ro;
        this.leg7.yRot = +ur * 2.0f - ro;

        final float c0 = -(Mth.cos(time * 0.6662f * 2.0f + Mth.PI * 2 * 0 / 4.0f) * 0.4f) * r;
        final float c1 = -(Mth.cos(time * 0.6662f * 2.0f + Mth.PI * 2 * 1 / 4.0f) * 0.4f) * r;
        final float c2 = -(Mth.cos(time * 0.6662f * 2.0f + Mth.PI * 2 * 2 / 4.0f) * 0.4f) * r;
        final float c3 = -(Mth.cos(time * 0.6662f * 2.0f + Mth.PI * 2 * 3 / 4.0f) * 0.4f) * r;

        final float s0 = Math.abs(Mth.sin(time * 0.6662f + Mth.PI * 2 * 0 / 4.0f) * 0.4f) * r;
        final float s1 = Math.abs(Mth.sin(time * 0.6662f + Mth.PI * 2 * 1 / 4.0f) * 0.4f) * r;
        final float s2 = Math.abs(Mth.sin(time * 0.6662f + Mth.PI * 2 * 2 / 4.0f) * 0.4f) * r;
        final float s3 = Math.abs(Mth.sin(time * 0.6662f + Mth.PI * 2 * 3 / 4.0f) * 0.4f) * r;

        this.leg0.yRot += c0;
        this.leg1.yRot += -c0;
        this.leg2.yRot += c1;
        this.leg3.yRot += -c1;
        this.leg4.yRot += c2;
        this.leg5.yRot += -c2;
        this.leg6.yRot += c3;
        this.leg7.yRot += -c3;

        this.leg0.zRot += s0;
        this.leg1.zRot += -s0;
        this.leg2.zRot += s1;
        this.leg3.zRot += -s1;
        this.leg4.zRot += s2;
        this.leg5.zRot += -s2;
        this.leg6.zRot += s3;
        this.leg7.zRot += -s3;
    }
}
