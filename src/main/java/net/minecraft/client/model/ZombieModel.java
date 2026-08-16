// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.model;

import util.Mth;

public class ZombieModel extends HumanoidModel
{
    @Override
    public void setupAnim(final float time, final float r, final float bob, final float yRot, final float xRot, final float scale) {
        super.setupAnim(time, r, bob, yRot, xRot, scale);
        final float sin = Mth.sin(this.attackTime * Mth.PI);
        final float sin2 = Mth.sin((1.0f - (1.0f - this.attackTime) * (1.0f - this.attackTime)) * Mth.PI);
        this.arm0.zRot = 0.0f;
        this.arm1.zRot = 0.0f;
        this.arm0.yRot = -(0.1f - sin * 0.6f);
        this.arm1.yRot = 0.1f - sin * 0.6f;
        this.arm0.xRot = -(Mth.PI / 2f);
        this.arm1.xRot = -(Mth.PI / 2f);
        final Cube arm0 = this.arm0;
        arm0.xRot -= sin * 1.2f - sin2 * 0.4f;
        final Cube arm2 = this.arm1;
        arm2.xRot -= sin * 1.2f - sin2 * 0.4f;
        final Cube arm3 = this.arm0;
        arm3.zRot += Mth.cos(bob * 0.09f) * 0.05f + 0.05f;
        final Cube arm4 = this.arm1;
        arm4.zRot -= Mth.cos(bob * 0.09f) * 0.05f + 0.05f;
        final Cube arm5 = this.arm0;
        arm5.xRot += Mth.sin(bob * 0.067f) * 0.05f;
        final Cube arm6 = this.arm1;
        arm6.xRot -= Mth.sin(bob * 0.067f) * 0.05f;
    }
}
