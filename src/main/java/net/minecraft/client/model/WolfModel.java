// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.model;

import org.lwjgl.opengl.GL11;
import util.Mth;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.Mob;

public class WolfModel extends Model
{
    public Cube head;
    public Cube body;
    public Cube leg0;
    public Cube leg1;
    public Cube leg2;
    public Cube leg3;
    Cube ear1;
    Cube ear2;
    Cube mouth;
    Cube tail;
    Cube upperBody;
    
    public WolfModel() {
        final float g = 0.0f;
        final float n = 13.5f;
        (this.head = new Cube(0, 0)).addBox(-3.0f, -3.0f, -2.0f, 6, 6, 4, g);
        this.head.setPos(-1.0f, n, -7.0f);
        (this.body = new Cube(18, 14)).addBox(-4.0f, -2.0f, -3.0f, 6, 9, 6, g);
        this.body.setPos(0.0f, 14.0f, 2.0f);
        (this.upperBody = new Cube(21, 0)).addBox(-4.0f, -3.0f, -3.0f, 8, 6, 7, g);
        this.upperBody.setPos(-1.0f, 14.0f, 2.0f);
        (this.leg0 = new Cube(0, 18)).addBox(-1.0f, 0.0f, -1.0f, 2, 8, 2, g);
        this.leg0.setPos(-2.5f, 16.0f, 7.0f);
        (this.leg1 = new Cube(0, 18)).addBox(-1.0f, 0.0f, -1.0f, 2, 8, 2, g);
        this.leg1.setPos(0.5f, 16.0f, 7.0f);
        (this.leg2 = new Cube(0, 18)).addBox(-1.0f, 0.0f, -1.0f, 2, 8, 2, g);
        this.leg2.setPos(-2.5f, 16.0f, -4.0f);
        (this.leg3 = new Cube(0, 18)).addBox(-1.0f, 0.0f, -1.0f, 2, 8, 2, g);
        this.leg3.setPos(0.5f, 16.0f, -4.0f);
        (this.tail = new Cube(9, 18)).addBox(-1.0f, 0.0f, -1.0f, 2, 8, 2, g);
        this.tail.setPos(-1.0f, 12.0f, 8.0f);
        (this.ear1 = new Cube(16, 14)).addBox(-3.0f, -5.0f, 0.0f, 2, 2, 1, g);
        this.ear1.setPos(-1.0f, n, -7.0f);
        (this.ear2 = new Cube(16, 14)).addBox(1.0f, -5.0f, 0.0f, 2, 2, 1, g);
        this.ear2.setPos(-1.0f, n, -7.0f);
        (this.mouth = new Cube(0, 10)).addBox(-2.0f, 0.0f, -5.0f, 3, 3, 4, g);
        this.mouth.setPos(-0.5f, n, -7.0f);
    }
    
    @Override
    public void render(final float time, final float r, final float bob, final float yRot, final float xRot, final float scale) {
        super.render(time, r, bob, yRot, xRot, scale);
        this.setupAnim(time, r, bob, yRot, xRot, scale);
        this.head.render2(scale);
        this.body.render(scale);
        this.leg0.render(scale);
        this.leg1.render(scale);
        this.leg2.render(scale);
        this.leg3.render(scale);
        this.ear1.render2(scale);
        this.ear2.render2(scale);
        this.mouth.render2(scale);
        this.tail.render2(scale);
        this.upperBody.render(scale);
    }
    
    @Override
    public void prepareMobModel(final Mob mob, final float time, final float r, final float partialTick) {
        final Wolf wolf = (Wolf)mob;
        if (wolf.isAngry()) {
            this.tail.yRot = 0.0f;
        }
        else {
            this.tail.yRot = Mth.cos(time * 0.6662f) * 1.4f * r;
        }
        if (wolf.isSitting()) {
            this.upperBody.setPos(-1.0f, 16.0f, -3.0f);
            this.upperBody.xRot = 1.2566371f;
            this.upperBody.yRot = 0.0f;
            this.body.setPos(0.0f, 18.0f, 0.0f);
            this.body.xRot = 0.7853982f;
            this.tail.setPos(-1.0f, 21.0f, 6.0f);
            this.leg0.setPos(-2.5f, 22.0f, 2.0f);
            this.leg0.xRot = 4.712389f;
            this.leg1.setPos(0.5f, 22.0f, 2.0f);
            this.leg1.xRot = 4.712389f;
            this.leg2.xRot = 5.811947f;
            this.leg2.setPos(-2.49f, 17.0f, -4.0f);
            this.leg3.xRot = 5.811947f;
            this.leg3.setPos(0.51f, 17.0f, -4.0f);
        }
        else {
            this.body.setPos(0.0f, 14.0f, 2.0f);
            this.body.xRot = 1.5707964f;
            this.upperBody.setPos(-1.0f, 14.0f, -3.0f);
            this.upperBody.xRot = this.body.xRot;
            this.tail.setPos(-1.0f, 12.0f, 8.0f);
            this.leg0.setPos(-2.5f, 16.0f, 7.0f);
            this.leg1.setPos(0.5f, 16.0f, 7.0f);
            this.leg2.setPos(-2.5f, 16.0f, -4.0f);
            this.leg3.setPos(0.5f, 16.0f, -4.0f);
            this.leg0.xRot = Mth.cos(time * 0.6662f) * 1.4f * r;
            this.leg1.xRot = Mth.cos(time * 0.6662f + Mth.PI) * 1.4f * r;
            this.leg2.xRot = Mth.cos(time * 0.6662f + Mth.PI) * 1.4f * r;
            this.leg3.xRot = Mth.cos(time * 0.6662f) * 1.4f * r;
        }
        final float n = wolf.getHeadRollAngle(partialTick) + wolf.getBodyRollAngle(partialTick, 0.0f);
        this.head.zRot = n;
        this.ear1.zRot = n;
        this.ear2.zRot = n;
        this.mouth.zRot = n;
        this.upperBody.zRot = wolf.getBodyRollAngle(partialTick, -0.08f);
        this.body.zRot = wolf.getBodyRollAngle(partialTick, -0.16f);
        this.tail.zRot = wolf.getBodyRollAngle(partialTick, -0.2f);
        if (wolf.isWet()) {
            final float n2 = wolf.getBrightness(partialTick) * wolf.getWetShade(partialTick);
            GL11.glColor3f(n2, n2, n2);
        }
    }
    
    @Override
    public void setupAnim(final float time, final float r, final float bob, final float yRot, final float xRot, final float scale) {
        super.setupAnim(time, r, bob, yRot, xRot, scale);
        this.head.xRot = xRot / Mth.RADDEG;
        this.head.yRot = yRot / Mth.RADDEG;
        this.ear1.yRot = this.head.yRot;
        this.ear1.xRot = this.head.xRot;
        this.ear2.yRot = this.head.yRot;
        this.ear2.xRot = this.head.xRot;
        this.mouth.yRot = this.head.yRot;
        this.mouth.xRot = this.head.xRot;
        this.tail.xRot = bob;
    }
}
