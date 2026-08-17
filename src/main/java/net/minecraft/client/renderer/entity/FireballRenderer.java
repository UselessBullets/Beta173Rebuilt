// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.Tesselator;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.projectile.Fireball;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

public class FireballRenderer extends EntityRenderer<Fireball>
{
    public void render(final Fireball entity, final double x, final double y, final double z, final float rot, final float a) {
        glPushMatrix();

        glTranslatef((float)x, (float)y, (float)z);
        glEnable(GL_RESCALE_NORMAL);
        final float s = 2.0f;
        glScalef(s / 1.0f, s / 1.0f, s / 1.0f);
        final int icon = Item.snowBall.getIcon(0);
        this.bindTexture("/gui/items.png");
        final Tesselator t = Tesselator.instance;

        final float u0 = (icon % 16 * 16 + 0) / 256.0f;
        final float u1 = (icon % 16 * 16 + 16) / 256.0f;
        final float v0 = (icon / 16 * 16 + 0) / 256.0f;
        final float v1 = (icon / 16 * 16 + 16) / 256.0f;

        final float r = 1.0f;
        final float xo = 0.5f;
        final float yo = 0.25f;

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
    }
}
