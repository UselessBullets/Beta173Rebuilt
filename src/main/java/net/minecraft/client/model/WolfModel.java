// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.model;

import util.Mth;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.Mob;

import static org.lwjgl.opengl.GL11.*;

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
    private static final int legSize = 8;
    
    public WolfModel() {
        final float g = 0.0f;
        final float headHeight = 12 + 9.5f - legSize;
        this.head = new Cube(0, 0);
        this.head.addBox(-3.0f, -3.0f, -2.0f, 6, 6, 4, g); // Head
        this.head.setPos(-1.0f, headHeight, -7.0f);

        this.body = new Cube(18, 14);
        this.body.addBox(-4.0f, -2.0f, -3.0f, 6, 9, 6, g); // Body
        this.body.setPos(0.0f, 11 + 11 - legSize, 2.0f);

        this.upperBody = new Cube(21, 0);
        this.upperBody.addBox(-4.0f, -3.0f, -3.0f, 8, 6, 7, g); // Body
        this.upperBody.setPos(-1.0f, 11 + 11 - legSize, 2.0f);

        this.leg0 = new Cube(0, 18);
        this.leg0.addBox(-1.0f, 0.0f, -1.0f, 2, legSize, 2, g); // Leg0
        this.leg0.setPos(-2.5f, 18 + 6 - legSize, 7.0f);

        this.leg1 = new Cube(0, 18);
        this.leg1.addBox(-1.0f, 0.0f, -1.0f, 2, legSize, 2, g); // Leg1
        this.leg1.setPos(0.5f, 18 + 6 - legSize, 7.0f);

        this.leg2 = new Cube(0, 18);
        this.leg2.addBox(-1.0f, 0.0f, -1.0f, 2, legSize, 2, g); // Leg2
        this.leg2.setPos(-2.5f, 18 + 6 - legSize, -4.0f);

        this.leg3 = new Cube(0, 18);
        this.leg3.addBox(-1.0f, 0.0f, -1.0f, 2, legSize, 2, g); // Leg3
        this.leg3.setPos(0.5f, 18 + 6 - legSize, -4.0f);

        this.tail = new Cube(9, 18);
        this.tail.addBox(-1.0f, 0.0f, -1.0f, 2, 8, 2, g);
        this.tail.setPos(-1.0f, 2 + 18 - legSize, 8.0f);

        this.ear1 = new Cube(16, 14);
        this.ear1.addBox(-3.0f, -5.0f, 0.0f, 2, 2, 1, g);
        this.ear1.setPos(-1.0f, headHeight, -7.0f);

        this.ear2 = new Cube(16, 14);
        this.ear2.addBox(1.0f, -5.0f, 0.0f, 2, 2, 1, g);
        this.ear2.setPos(-1.0f, headHeight, -7.0f);

        this.mouth = new Cube(0, 10);
        this.mouth.addBox(-2.0f, 0.0f, -5.0f, 3, 3, 4, g);
        this.mouth.setPos(-0.5f, headHeight, -7.0f);
    }
    
    @Override
    public void render(final float time, final float r, final float bob, final float yRot, final float xRot, final float scale) {
        super.render(time, r, bob, yRot, xRot, scale);
        this.setupAnim(time, r, bob, yRot, xRot, scale);

        this.head.renderRollable(scale);
        this.body.render(scale);
        this.leg0.render(scale);
        this.leg1.render(scale);
        this.leg2.render(scale);
        this.leg3.render(scale);
        this.ear1.renderRollable(scale);
        this.ear2.renderRollable(scale);
        this.mouth.renderRollable(scale);
        this.tail.renderRollable(scale);
        this.upperBody.render(scale);
    }
    
    @Override
    public void prepareMobModel(final Mob mob, final float time, final float r, final float a) {
        final Wolf wolf = (Wolf)mob;

        if (wolf.isAngry()) {
            this.tail.yRot = 0.0f;
        }
        else {
            this.tail.yRot = Mth.cos(time * 0.6662f) * 1.4f * r;
        }

        if (wolf.isSitting()) {
            this.upperBody.setPos(-1.0f, 16.0f, -3.0f);
            this.upperBody.xRot = 0.4f * Mth.PI;
            this.upperBody.yRot = 0.0f * Mth.PI;

            this.body.setPos(0.0f, 11 + 15 - legSize, 0.0f);
            this.body.xRot = 0.25f * Mth.PI;

            this.tail.setPos(-1.0f, 11 + 18 - legSize, 6.0f);

            this.leg0.setPos(-2.5f, 18 + 12 - legSize, 2.0f);
            this.leg0.xRot = 1.5f * Mth.PI;
            this.leg1.setPos(0.5f, 18 + 12 - legSize, 2.0f);
            this.leg1.xRot = 1.5f * Mth.PI;

            this.leg2.xRot = 1.85f * Mth.PI;
            this.leg2.setPos(-2.49f, 18 + 7.0f - legSize, -4.0f);
            this.leg3.xRot = 1.85f * Mth.PI;
            this.leg3.setPos(0.51f, 18 + 7.0f - legSize, -4.0f);
        }
        else {
            this.body.setPos(0.0f, 11 + 11 - legSize, 2.0f);
            this.body.xRot = Mth.HALF_PI;

            this.upperBody.setPos(-1.0f, 11 + 11.0f - legSize, -3.0f);
            this.upperBody.xRot = this.body.xRot;

            this.tail.setPos(-1.0f, 2 + 18 - legSize, 8.0f);

            this.leg0.setPos(-2.5f, 18 + 6 - legSize, 7.0f);
            this.leg1.setPos(0.5f, 18 + 6 - legSize, 7.0f);
            this.leg2.setPos(-2.5f, 18 + 6 - legSize, -4.0f);
            this.leg3.setPos(0.5f, 18 + 6 - legSize, -4.0f);

            this.leg0.xRot = Mth.cos(time * 0.6662f) * 1.4f * r;
            this.leg1.xRot = Mth.cos(time * 0.6662f + Mth.PI) * 1.4f * r;
            this.leg2.xRot = Mth.cos(time * 0.6662f + Mth.PI) * 1.4f * r;
            this.leg3.xRot = Mth.cos(time * 0.6662f) * 1.4f * r;
        }
        final float angle = wolf.getHeadRollAngle(a) + wolf.getBodyRollAngle(a, 0.0f);
        this.head.zRot = angle;
        this.ear1.zRot = angle;
        this.ear2.zRot = angle;
        this.mouth.zRot = angle;

        this.upperBody.zRot = wolf.getBodyRollAngle(a, -0.08f);
        this.body.zRot = wolf.getBodyRollAngle(a, -0.16f);
        this.tail.zRot = wolf.getBodyRollAngle(a, -0.2f);

        if (wolf.isWet()) {
            final float brightness = wolf.getBrightness(a) * wolf.getWetShade(a);
            glColor3f(brightness, brightness, brightness);
        }
    }
    
    @Override
    public void setupAnim(final float time, final float r, final float bob, final float yRot, final float xRot, final float scale) {
        super.setupAnim(time, r, bob, yRot, xRot, scale);
        this.head.xRot = xRot * Mth.DEGRAD;
        this.head.yRot = yRot * Mth.DEGRAD;
        this.ear1.yRot = this.head.yRot;
        this.ear1.xRot = this.head.xRot;
        this.ear2.yRot = this.head.yRot;
        this.ear2.xRot = this.head.xRot;
        this.mouth.yRot = this.head.yRot;
        this.mouth.xRot = this.head.xRot;
        this.tail.xRot = bob;
    }
}
