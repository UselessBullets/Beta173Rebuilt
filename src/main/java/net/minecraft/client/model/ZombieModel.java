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

        final float attack2 = Mth.sin(this.attackTime * Mth.PI);
        final float attack = Mth.sin((1.0f - (1.0f - this.attackTime) * (1.0f - this.attackTime)) * Mth.PI);
        this.arm0.zRot = 0.0f;
        this.arm1.zRot = 0.0f;
        this.arm0.yRot = -(0.1f - attack2 * 0.6f);
        this.arm1.yRot = 0.1f - attack2 * 0.6f;
        this.arm0.xRot = -Mth.HALF_PI;
        this.arm1.xRot = -Mth.HALF_PI;
        this.arm0.xRot -= attack2 * 1.2f - attack * 0.4f;
        this.arm1.xRot -= attack2 * 1.2f - attack * 0.4f;
        this.arm0.zRot += Mth.cos(bob * 0.09f) * 0.05f + 0.05f;
        this.arm1.zRot -= Mth.cos(bob * 0.09f) * 0.05f + 0.05f;
        this.arm0.xRot += Mth.sin(bob * 0.067f) * 0.05f;
        this.arm1.xRot -= Mth.sin(bob * 0.067f) * 0.05f;
    }
}
