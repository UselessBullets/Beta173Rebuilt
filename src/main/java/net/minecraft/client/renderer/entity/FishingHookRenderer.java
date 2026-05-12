// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import util.Mth;
import net.minecraft.client.renderer.Tesselator;
import org.lwjgl.opengl.GL11;
import net.minecraft.world.entity.projectile.FishingHook;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

public class FishingHookRenderer extends EntityRenderer<FishingHook>
{
    public void render(final FishingHook entity, final double x, final double y, final double z, final float rot, final float partialTick) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y, (float)z);
        GL11.glEnable(GL_RESCALE_NORMAL);
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        final int n = 1;
        final int n2 = 2;
        this.bindTexture("/particles.png");
        final Tesselator instance = Tesselator.instance;
        final float n3 = (n * 8 + 0) / 128.0f;
        final float n4 = (n * 8 + 8) / 128.0f;
        final float n5 = (n2 * 8 + 0) / 128.0f;
        final float n6 = (n2 * 8 + 8) / 128.0f;
        final float n7 = 1.0f;
        final float n8 = 0.5f;
        final float n9 = 0.5f;
        GL11.glRotatef(180.0f - this.entityRenderDispatcher.playerRotY, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(-this.entityRenderDispatcher.playerRotX, 1.0f, 0.0f, 0.0f);
        instance.begin();
        instance.normal(0.0f, 1.0f, 0.0f);
        instance.vertexUV(0.0f - n8, 0.0f - n9, 0.0, n3, n6);
        instance.vertexUV(n7 - n8, 0.0f - n9, 0.0, n4, n6);
        instance.vertexUV(n7 - n8, 1.0f - n9, 0.0, n4, n5);
        instance.vertexUV(0.0f - n8, 1.0f - n9, 0.0, n3, n5);
        instance.end();
        GL11.glDisable(32826);
        GL11.glPopMatrix();
        if (entity.owner != null) {
            final float n10 = (entity.owner.yRotO + (entity.owner.yRot - entity.owner.yRotO) * partialTick) * 3.1415927f / 180.0f;
            final double n11 = Mth.sin(n10);
            final double n12 = Mth.cos(n10);
            final float sin = Mth.sin(Mth.sqrt(entity.owner.getAttackAnim(partialTick)) * 3.1415927f);
            final Vec3 temp = Vec3.newTemp(-0.5, 0.03, 0.8);
            temp.xRot(-(entity.owner.xRotO + (entity.owner.xRot - entity.owner.xRotO) * partialTick) * 3.1415927f / 180.0f);
            temp.yRot(-(entity.owner.yRotO + (entity.owner.yRot - entity.owner.yRotO) * partialTick) * 3.1415927f / 180.0f);
            temp.yRot(sin * 0.5f);
            temp.xRot(-sin * 0.7f);
            double n13 = entity.owner.xo + (entity.owner.x - entity.owner.xo) * partialTick + temp.x;
            double n14 = entity.owner.yo + (entity.owner.y - entity.owner.yo) * partialTick + temp.y;
            double n15 = entity.owner.zo + (entity.owner.z - entity.owner.zo) * partialTick + temp.z;
            if (this.entityRenderDispatcher.options.thirdPersonView) {
                final float n16 = (entity.owner.yBodyRotO + (entity.owner.yBodyRot - entity.owner.yBodyRotO) * partialTick) * 3.1415927f / 180.0f;
                final double n17 = Mth.sin(n16);
                final double n18 = Mth.cos(n16);
                n13 = entity.owner.xo + (entity.owner.x - entity.owner.xo) * partialTick - n18 * 0.35 - n17 * 0.85;
                n14 = entity.owner.yo + (entity.owner.y - entity.owner.yo) * partialTick - 0.45;
                n15 = entity.owner.zo + (entity.owner.z - entity.owner.zo) * partialTick - n17 * 0.35 + n18 * 0.85;
            }
            final double n19 = entity.xo + (entity.x - entity.xo) * partialTick;
            final double n20 = entity.yo + (entity.y - entity.yo) * partialTick + 0.25;
            final double n21 = entity.zo + (entity.z - entity.zo) * partialTick;
            final double n22 = (float)(n13 - n19);
            final double n23 = (float)(n14 - n20);
            final double n24 = (float)(n15 - n21);
            GL11.glDisable(GL_TEXTURE_2D);
            GL11.glDisable(GL_LIGHTING);
            instance.begin(3);
            instance.color(0);
            for (int n25 = 16, i = 0; i <= n25; ++i) {
                final float n26 = i / (float)n25;
                instance.vertex(x + n22 * n26, y + n23 * (n26 * n26 + n26) * 0.5 + 0.25, z + n24 * n26);
            }
            instance.end();
            GL11.glDisable(GL_LIGHTING);
            GL11.glEnable(GL_TEXTURE_2D);
        }
    }
}
