// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import util.Mth;
import net.minecraft.client.renderer.Tesselator;
import org.lwjgl.opengl.GL11;
import net.minecraft.world.entity.projectile.Arrow;

import static org.lwjgl.opengl.GL12.*;

public class ArrowRenderer extends EntityRenderer<Arrow>
{
    public void render(final Arrow entity, final double x, final double y, final double z, final float rot, final float partialTick) {
        if (entity.yRotO == 0.0f && entity.xRotO == 0.0f) {
            return;
        }
        this.bindTexture("/item/arrows.png");
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y, (float)z);
        GL11.glRotatef(entity.yRotO + (entity.yRot - entity.yRotO) * partialTick - 90.0f, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(entity.xRotO + (entity.xRot - entity.xRotO) * partialTick, 0.0f, 0.0f, 1.0f);
        final Tesselator instance = Tesselator.instance;
        final int n = 0;
        final float n2 = 0.0f;
        final float n3 = 0.5f;
        final float n4 = (0 + n * 10) / 32.0f;
        final float n5 = (5 + n * 10) / 32.0f;
        final float n6 = 0.0f;
        final float n7 = 0.15625f;
        final float n8 = (5 + n * 10) / 32.0f;
        final float n9 = (10 + n * 10) / 32.0f;
        final float n10 = 0.05625f;
        GL11.glEnable(GL_RESCALE_NORMAL);
        final float n11 = entity.shakeTime - partialTick;
        if (n11 > 0.0f) {
            GL11.glRotatef(-Mth.sin(n11 * 3.0f) * n11, 0.0f, 0.0f, 1.0f);
        }
        GL11.glRotatef(45.0f, 1.0f, 0.0f, 0.0f);
        GL11.glScalef(n10, n10, n10);
        GL11.glTranslatef(-4.0f, 0.0f, 0.0f);
        GL11.glNormal3f(n10, 0.0f, 0.0f);
        instance.begin();
        instance.vertexUV(-7.0, -2.0, -2.0, n6, n8);
        instance.vertexUV(-7.0, -2.0, 2.0, n7, n8);
        instance.vertexUV(-7.0, 2.0, 2.0, n7, n9);
        instance.vertexUV(-7.0, 2.0, -2.0, n6, n9);
        instance.end();
        GL11.glNormal3f(-n10, 0.0f, 0.0f);
        instance.begin();
        instance.vertexUV(-7.0, 2.0, -2.0, n6, n8);
        instance.vertexUV(-7.0, 2.0, 2.0, n7, n8);
        instance.vertexUV(-7.0, -2.0, 2.0, n7, n9);
        instance.vertexUV(-7.0, -2.0, -2.0, n6, n9);
        instance.end();
        for (int i = 0; i < 4; ++i) {
            GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
            GL11.glNormal3f(0.0f, 0.0f, n10);
            instance.begin();
            instance.vertexUV(-8.0, -2.0, 0.0, n2, n4);
            instance.vertexUV(8.0, -2.0, 0.0, n3, n4);
            instance.vertexUV(8.0, 2.0, 0.0, n3, n5);
            instance.vertexUV(-8.0, 2.0, 0.0, n2, n5);
            instance.end();
        }
        GL11.glDisable(32826);
        GL11.glPopMatrix();
    }
}
