// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.model;

import util.Mth;

public class CreeperModel extends Model
{
    public Cube head;
    public Cube hair;
    public Cube body;
    public Cube leg0;
    public Cube leg1;
    public Cube leg2;
    public Cube leg3;
    
    public CreeperModel() {
        this(0.0f);
    }
    
    public CreeperModel(final float g) {
        final int yo = 4;
        this.head = new Cube(0, 0);
        this.head.addBox(-4.0f, -8.0f, -4.0f, 8, 8, 8, g); // Head
        this.head.setPos(0.0f, (float)yo, 0.0f);

        this.hair = new Cube(32, 0);
        this.hair.addBox(-4.0f, -8.0f, -4.0f, 8, 8, 8, g + 0.5f); // Head
        this.hair.setPos(0.0f, (float)yo, 0.0f);

        this.body = new Cube(16, 16);
        this.body.addBox(-4.0f, 0.0f, -2.0f, 8, 12, 4, g); // Body
        this.body.setPos(0.0f, (float)yo, 0.0f);

        this.leg0 = new Cube(0, 16);
        this.leg0.addBox(-2.0f, 0.0f, -2.0f, 4, 6, 4, g); // Leg0
        this.leg0.setPos(-2.0f, (float)(12 + yo), 4.0f);

        this.leg1 = new Cube(0, 16);
        this.leg1.addBox(-2.0f, 0.0f, -2.0f, 4, 6, 4, g); // Leg1
        this.leg1.setPos(2.0f, (float)(12 + yo), 4.0f);

        this.leg2 = new Cube(0, 16);
        this.leg2.addBox(-2.0f, 0.0f, -2.0f, 4, 6, 4, g); // Leg2
        this.leg2.setPos(-2.0f, (float)(12 + yo), -4.0f);

        this.leg3 = new Cube(0, 16);
        this.leg3.addBox(-2.0f, 0.0f, -2.0f, 4, 6, 4, g); // Leg3
        this.leg3.setPos(2.0f, (float)(12 + yo), -4.0f);
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
        this.head.yRot = yRot / Mth.RADDEG;
        this.head.xRot = xRot / Mth.RADDEG;
        this.leg0.xRot = Mth.cos(time * 0.6662f) * 1.4f * r;
        this.leg1.xRot = Mth.cos(time * 0.6662f + Mth.PI) * 1.4f * r;
        this.leg2.xRot = Mth.cos(time * 0.6662f + Mth.PI) * 1.4f * r;
        this.leg3.xRot = Mth.cos(time * 0.6662f) * 1.4f * r;
    }
}
