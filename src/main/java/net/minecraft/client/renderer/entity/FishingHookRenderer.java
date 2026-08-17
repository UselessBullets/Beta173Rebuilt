// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import net.minecraft.world.phys.Vec3;
import util.Mth;
import net.minecraft.client.renderer.Tesselator;
import net.minecraft.world.entity.projectile.FishingHook;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

public class FishingHookRenderer extends EntityRenderer<FishingHook>
{
    public void render(final FishingHook entity, final double x, final double y, final double z, final float rot, final float a) {
        glPushMatrix();

        glTranslatef((float)x, (float)y, (float)z);
        glEnable(GL_RESCALE_NORMAL);
        glScalef(1 / 2.0f, 1 / 2.0f, 1 / 2.0f);
        final int xi = 1;
        final int yi = 2;
        this.bindTexture("/particles.png");
        final Tesselator t = Tesselator.instance;

        final float u0 = (xi * 8 + 0) / 128.0f;
        final float u1 = (xi * 8 + 8) / 128.0f;
        final float v0 = (yi * 8 + 0) / 128.0f;
        final float v1 = (yi * 8 + 8) / 128.0f;

        final float r = 1.0f;
        final float xo = 0.5f;
        final float yo = 0.5f;

        glRotatef(180.0f - this.entityRenderDispatcher.playerRotY, 0.0f, 1.0f, 0.0f);
        glRotatef(-this.entityRenderDispatcher.playerRotX, 1.0f, 0.0f, 0.0f);
        t.begin();
        t.normal(0.0f, 1.0f, 0.0f);
        t.vertexUV(0 - xo, 0 - yo, 0, u0, v1);
        t.vertexUV(r - xo, 0 - yo, 0, u1, v1);
        t.vertexUV(r - xo, 1 - yo, 0, u1, v0);
        t.vertexUV(0 - xo, 1 - yo, 0, u0, v0);
        t.end();

        glDisable(GL_RESCALE_NORMAL);
        glPopMatrix();

        if (entity.owner != null) {
            float rr = (entity.owner.yRotO + (entity.owner.yRot - entity.owner.yRotO) * a) * Mth.DEGRAD;
            double ss = Mth.sin(rr);
            double cc = Mth.cos(rr);
            final float swing = Mth.sin(Mth.sqrt(entity.owner.getAttackAnim(a)) * Mth.PI);

            final Vec3 vv = Vec3.newTemp(-0.5, 0.03, 0.8);
            vv.xRot(-(entity.owner.xRotO + (entity.owner.xRot - entity.owner.xRotO) * a) * Mth.DEGRAD);
            vv.yRot(-(entity.owner.yRotO + (entity.owner.yRot - entity.owner.yRotO) * a) * Mth.DEGRAD);
            vv.yRot(swing * 0.5f);
            vv.xRot(-swing * 0.7f);

            double xp = entity.owner.xo + (entity.owner.x - entity.owner.xo) * a + vv.x;
            double yp = entity.owner.yo + (entity.owner.y - entity.owner.yo) * a + vv.y;
            double zp = entity.owner.zo + (entity.owner.z - entity.owner.zo) * a + vv.z;

            if (this.entityRenderDispatcher.options.thirdPersonView) {
                rr = (entity.owner.yBodyRotO + (entity.owner.yBodyRot - entity.owner.yBodyRotO) * a) * Mth.DEGRAD;
                ss = Mth.sin(rr);
                cc = Mth.cos(rr);
                xp = entity.owner.xo + (entity.owner.x - entity.owner.xo) * a - cc * 0.35 - ss * 0.85;
                yp = entity.owner.yo + (entity.owner.y - entity.owner.yo) * a - 0.45;
                zp = entity.owner.zo + (entity.owner.z - entity.owner.zo) * a - ss * 0.35 + cc * 0.85;
            }

            final double xh = entity.xo + (entity.x - entity.xo) * a;
            final double yh = entity.yo + (entity.y - entity.yo) * a + 0.25;
            final double zh = entity.zo + (entity.z - entity.zo) * a;

            final double xa = (float)(xp - xh);
            final double ya = (float)(yp - yh);
            final double za = (float)(zp - zh);

            glDisable(GL_TEXTURE_2D);
            glDisable(GL_LIGHTING);
            t.begin(GL_LINE_STRIP);
            t.color(0x000000);
            int steps = 16;
            for (int i = 0; i <= steps; ++i) {
                final float aa = i / (float)steps;
                t.vertex(x + xa * aa, y + ya * (aa * aa + aa) * 0.5 + 4 / 16.0f, z + za * aa);
            }
            t.end();
            glEnable(GL_LIGHTING);
            glEnable(GL_TEXTURE_2D);
        }
    }
}
