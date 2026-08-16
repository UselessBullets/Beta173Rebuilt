// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.model;

import util.Mth;

public class CowModel extends QuadrupedModel
{
    Cube udder;
    Cube horn1;
    Cube horn2;
    
    public CowModel() {
        super(12, 0.0f);
        this.head = new Cube(0, 0);
        this.head.addBox(-4.0f, -4.0f, -6.0f, 8, 8, 6, 0.0f); // Head
        this.head.setPos(0.0f, 4.0f, -8.0f);

        this.horn1 = new Cube(22, 0);
        this.horn1.addBox(-4.0f, -5.0f, -4.0f, 1, 3, 1, 0.0f); // Horn1
        this.horn1.setPos(0.0f, 3.0f, -7.0f);

        this.horn2 = new Cube(22, 0);
        this.horn2.addBox(3.0f, -5.0f, -4.0f, 1, 3, 1, 0.0f); // Horn2
        this.horn2.setPos(0.0f, 3.0f, -7.0f);

        this.udder = new Cube(52, 0);
        this.udder.addBox(-2.0f, -3.0f, 0.0f, 4, 6, 2, 0.0f); // Udder
        this.udder.setPos(0.0f, 14.0f, 6.0f);
        this.udder.xRot = (Mth.PI / 2f);

        this.body = new Cube(18, 4);
        this.body.addBox(-6.0f, -10.0f, -7.0f, 12, 18, 10, 0.0f); // Body
        this.body.setPos(0.0f, 5.0f, 2.0f);

        this.leg0.x -= 1;
        this.leg1.x += 1;
        this.leg0.z += 0;
        this.leg1.z += 0;
        this.leg2.x -= 1;
        this.leg3.x += 1;
        this.leg2.z -= 1;
        this.leg3.z -= 1;
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
