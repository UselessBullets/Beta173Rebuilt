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
        final int yo = 16;
        this.head = new Cube(0, 0);
        this.head.addBox(-2.0f, -6.0f, -2.0f, 4, 6, 3, 0.0f); // Head
        this.head.setPos(0.0f, (float)(-1 + yo), -4.0f);

        this.beak = new Cube(14, 0);
        this.beak.addBox(-2.0f, -4.0f, -4.0f, 4, 2, 2, 0.0f); // Beak
        this.beak.setPos(0.0f, (float)(-1 + yo), -4.0f);

        this.redThing = new Cube(14, 4);
        this.redThing.addBox(-1.0f, -2.0f, -3.0f, 2, 2, 2, 0.0f); // Beak
        this.redThing.setPos(0.0f, (float)(-1 + yo), -4.0f);

        this.body = new Cube(0, 9);
        this.body.addBox(-3.0f, -4.0f, -3.0f, 6, 8, 6, 0.0f); // Body
        this.body.setPos(0.0f, (float)(0 + yo), 0.0f);

        this.leg0 = new Cube(26, 0);
        this.leg0.addBox(-1.0f, 0.0f, -3.0f, 3, 5, 3); // Leg0
        this.leg0.setPos(-2.0f, (float)(3 + yo), 1.0f);

        this.leg1 = new Cube(26, 0);
        this.leg1.addBox(-1.0f, 0.0f, -3.0f, 3, 5, 3); // Leg1
        this.leg1.setPos(1.0f, (float)(3 + yo), 1.0f);

        this.wing0 = new Cube(24, 13);
        this.wing0.addBox(0.0f, 0.0f, -3.0f, 1, 4, 6); // Wing0
        this.wing0.setPos(-4.0f, (float)(-3 + yo), 0.0f);

        this.wing1 = new Cube(24, 13);
        this.wing1.addBox(-1.0f, 0.0f, -3.0f, 1, 4, 6); // Wing1
        this.wing1.setPos(4.0f, (float)(-3 + yo), 0.0f);
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
        this.head.xRot = -xRot * Mth.DEGRAD;
        this.head.yRot = yRot * Mth.DEGRAD;

        this.beak.xRot = this.head.xRot;
        this.beak.yRot = this.head.yRot;

        this.redThing.xRot = this.head.xRot;
        this.redThing.yRot = this.head.yRot;

        this.body.xRot = Mth.HALF_PI;

        this.leg0.xRot = Mth.cos(time * 0.6662f) * 1.4f * r;
        this.leg1.xRot = Mth.cos(time * 0.6662f + Mth.PI) * 1.4f * r;
        this.wing0.zRot = bob;
        this.wing1.zRot = -bob;
    }
}
