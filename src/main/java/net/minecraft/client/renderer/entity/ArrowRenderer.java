// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import util.Mth;
import net.minecraft.client.renderer.Tesselator;
import org.lwjgl.opengl.GL11;
import net.minecraft.world.entity.projectile.Arrow;

import static org.lwjgl.opengl.GL12.*;

public class ArrowRenderer extends EntityRenderer<Arrow>
{
    public void render(final Arrow arrow, final double x, final double y, final double z, final float rot, final float a) {
        if (arrow.yRotO == 0.0f && arrow.xRotO == 0.0f) return;
        this.bindTexture("/item/arrows.png");

        GL11.glPushMatrix();

        GL11.glTranslatef((float)x, (float)y, (float)z);
        GL11.glRotatef(arrow.yRotO + (arrow.yRot - arrow.yRotO) * a - 90.0f, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(arrow.xRotO + (arrow.xRot - arrow.xRotO) * a, 0.0f, 0.0f, 1.0f);

        final Tesselator t = Tesselator.instance;
        final int type = 0;

        final float u0 = 0 / 32.0f;
        final float u1 = 16 / 32.0f;
        final float v0 = (0 + type * 10) / 32.0f;
        final float v1 = (5 + type * 10) / 32.0f;

        final float u02 = 0 / 32.0f;
        final float u12 = 5 / 32.0f;
        final float v02 = (5 + type * 10) / 32.0f;
        final float v12 = (10 + type * 10) / 32.0f;
        final float ss = 0.05625f;
        GL11.glEnable(GL_RESCALE_NORMAL);
        final float shake = arrow.shakeTime - a;
        if (shake > 0.0f) {
            float pow = -Mth.sin(shake * 3.0f) * shake;
            GL11.glRotatef(pow, 0.0f, 0.0f, 1.0f);
        }
        GL11.glRotatef(45.0f, 1.0f, 0.0f, 0.0f);
        GL11.glScalef(ss, ss, ss);

        GL11.glTranslatef(-4.0f, 0.0f, 0.0f);

        GL11.glNormal3f(ss, 0.0f, 0.0f);
        t.begin();
        t.vertexUV(-7.0, -2.0, -2.0, u02, v02);
        t.vertexUV(-7.0, -2.0, 2.0, u12, v02);
        t.vertexUV(-7.0, 2.0, 2.0, u12, v12);
        t.vertexUV(-7.0, 2.0, -2.0, u02, v12);
        t.end();

        GL11.glNormal3f(-ss, 0.0f, 0.0f);
        t.begin();
        t.vertexUV(-7.0, 2.0, -2.0, u02, v02);
        t.vertexUV(-7.0, 2.0, 2.0, u12, v02);
        t.vertexUV(-7.0, -2.0, 2.0, u12, v12);
        t.vertexUV(-7.0, -2.0, -2.0, u02, v12);
        t.end();

        for (int i = 0; i < 4; ++i) {
            GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
            GL11.glNormal3f(0.0f, 0.0f, ss);
            t.begin();
            t.vertexUV(-8.0, -2.0, 0.0, u0, v0);
            t.vertexUV(8.0, -2.0, 0.0, u1, v0);
            t.vertexUV(8.0, 2.0, 0.0, u1, v1);
            t.vertexUV(-8.0, 2.0, 0.0, u0, v1);
            t.end();
        }
        GL11.glDisable(GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }
}
