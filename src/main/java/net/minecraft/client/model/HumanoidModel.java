// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.model;

import util.Mth;

public class HumanoidModel extends Model
{
    public Cube head;
    public Cube hair;
    public Cube body;
    public Cube arm0;
    public Cube arm1;
    public Cube leg0;
    public Cube leg1;
    public Cube ear;
    public Cube cloak;
    public boolean holdingLeftHand;
    public boolean holdingRightHand;
    public boolean sneaking;
    
    public HumanoidModel() {
        this(0.0f);
    }
    
    public HumanoidModel(final float g) {
        this(g, 0.0f);
    }
    
    public HumanoidModel(final float g, final float yOffset) {
        this.holdingLeftHand = false;
        this.holdingRightHand = false;
        this.sneaking = false;
        (this.cloak = new Cube(0, 0)).addBox(-5.0f, 0.0f, -1.0f, 10, 16, 1, g);
        (this.ear = new Cube(24, 0)).addBox(-3.0f, -6.0f, -1.0f, 6, 6, 1, g);
        (this.head = new Cube(0, 0)).addBox(-4.0f, -8.0f, -4.0f, 8, 8, 8, g);
        this.head.setPos(0.0f, 0.0f + yOffset, 0.0f);
        (this.hair = new Cube(32, 0)).addBox(-4.0f, -8.0f, -4.0f, 8, 8, 8, g + 0.5f);
        this.hair.setPos(0.0f, 0.0f + yOffset, 0.0f);
        (this.body = new Cube(16, 16)).addBox(-4.0f, 0.0f, -2.0f, 8, 12, 4, g);
        this.body.setPos(0.0f, 0.0f + yOffset, 0.0f);
        (this.arm0 = new Cube(40, 16)).addBox(-3.0f, -2.0f, -2.0f, 4, 12, 4, g);
        this.arm0.setPos(-5.0f, 2.0f + yOffset, 0.0f);
        this.arm1 = new Cube(40, 16);
        this.arm1.mirror = true;
        this.arm1.addBox(-1.0f, -2.0f, -2.0f, 4, 12, 4, g);
        this.arm1.setPos(5.0f, 2.0f + yOffset, 0.0f);
        (this.leg0 = new Cube(0, 16)).addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, g);
        this.leg0.setPos(-2.0f, 12.0f + yOffset, 0.0f);
        this.leg1 = new Cube(0, 16);
        this.leg1.mirror = true;
        this.leg1.addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, g);
        this.leg1.setPos(2.0f, 12.0f + yOffset, 0.0f);
    }
    
    @Override
    public void render(final float time, final float r, final float bob, final float yRot, final float xRot, final float scale) {
        this.setupAnim(time, r, bob, yRot, xRot, scale);
        this.head.render(scale);
        this.body.render(scale);
        this.arm0.render(scale);
        this.arm1.render(scale);
        this.leg0.render(scale);
        this.leg1.render(scale);
        this.hair.render(scale);
    }
    
    @Override
    public void setupAnim(final float time, final float r, final float bob, final float yRot, final float xRot, final float scale) {
        this.head.yRot = yRot / 57.295776f;
        this.head.xRot = xRot / 57.295776f;
        this.hair.yRot = this.head.yRot;
        this.hair.xRot = this.head.xRot;
        this.arm0.xRot = Mth.cos(time * 0.6662f + 3.1415927f) * 2.0f * r * 0.5f;
        this.arm1.xRot = Mth.cos(time * 0.6662f) * 2.0f * r * 0.5f;
        this.arm0.zRot = 0.0f;
        this.arm1.zRot = 0.0f;
        this.leg0.xRot = Mth.cos(time * 0.6662f) * 1.4f * r;
        this.leg1.xRot = Mth.cos(time * 0.6662f + 3.1415927f) * 1.4f * r;
        this.leg0.yRot = 0.0f;
        this.leg1.yRot = 0.0f;
        if (this.riding) {
            final Cube arm0 = this.arm0;
            arm0.xRot -= 0.62831855f;
            final Cube arm2 = this.arm1;
            arm2.xRot -= 0.62831855f;
            this.leg0.xRot = -1.2566371f;
            this.leg1.xRot = -1.2566371f;
            this.leg0.yRot = 0.31415927f;
            this.leg1.yRot = -0.31415927f;
        }
        if (this.holdingLeftHand) {
            this.arm1.xRot = this.arm1.xRot * 0.5f - 0.31415927f;
        }
        if (this.holdingRightHand) {
            this.arm0.xRot = this.arm0.xRot * 0.5f - 0.31415927f;
        }
        this.arm0.yRot = 0.0f;
        this.arm1.yRot = 0.0f;
        if (this.attackTime > -9990.0f) {
            this.body.yRot = Mth.sin(Mth.sqrt(this.attackTime) * 3.1415927f * 2.0f) * 0.2f;
            this.arm0.z = Mth.sin(this.body.yRot) * 5.0f;
            this.arm0.x = -Mth.cos(this.body.yRot) * 5.0f;
            this.arm1.z = -Mth.sin(this.body.yRot) * 5.0f;
            this.arm1.x = Mth.cos(this.body.yRot) * 5.0f;
            final Cube arm3 = this.arm0;
            arm3.yRot += this.body.yRot;
            final Cube arm4 = this.arm1;
            arm4.yRot += this.body.yRot;
            final Cube arm5 = this.arm1;
            arm5.xRot += this.body.yRot;
            final float n = 1.0f - this.attackTime;
            final float n2 = n * n;
            final float sin = Mth.sin((1.0f - n2 * n2) * 3.1415927f);
            final float n3 = Mth.sin(this.attackTime * 3.1415927f) * -(this.head.xRot - 0.7f) * 0.75f;
            final Cube arm6 = this.arm0;
            arm6.xRot -= (float)(sin * 1.2 + n3);
            final Cube arm7 = this.arm0;
            arm7.yRot += this.body.yRot * 2.0f;
            this.arm0.zRot = Mth.sin(this.attackTime * 3.1415927f) * -0.4f;
        }
        if (this.sneaking) {
            this.body.xRot = 0.5f;
            final Cube leg0 = this.leg0;
            leg0.xRot -= 0.0f;
            final Cube leg2 = this.leg1;
            leg2.xRot -= 0.0f;
            final Cube arm8 = this.arm0;
            arm8.xRot += 0.4f;
            final Cube arm9 = this.arm1;
            arm9.xRot += 0.4f;
            this.leg0.z = 4.0f;
            this.leg1.z = 4.0f;
            this.leg0.y = 9.0f;
            this.leg1.y = 9.0f;
            this.head.y = 1.0f;
        }
        else {
            this.body.xRot = 0.0f;
            this.leg0.z = 0.0f;
            this.leg1.z = 0.0f;
            this.leg0.y = 12.0f;
            this.leg1.y = 12.0f;
            this.head.y = 0.0f;
        }
        final Cube arm10 = this.arm0;
        arm10.zRot += Mth.cos(bob * 0.09f) * 0.05f + 0.05f;
        final Cube arm11 = this.arm1;
        arm11.zRot -= Mth.cos(bob * 0.09f) * 0.05f + 0.05f;
        final Cube arm12 = this.arm0;
        arm12.xRot += Mth.sin(bob * 0.067f) * 0.05f;
        final Cube arm13 = this.arm1;
        arm13.xRot -= Mth.sin(bob * 0.067f) * 0.05f;
    }
    
    public void renderEars(final float scale) {
        this.ear.yRot = this.head.yRot;
        this.ear.xRot = this.head.xRot;
        this.ear.x = 0.0f;
        this.ear.y = 0.0f;
        this.ear.render(scale);
    }
    
    public void renderCloak(final float scale) {
        this.cloak.render(scale);
    }
}
