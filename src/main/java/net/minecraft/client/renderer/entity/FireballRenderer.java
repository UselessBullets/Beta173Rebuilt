// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.Tesselator;
import net.minecraft.world.item.Item;
import org.lwjgl.opengl.GL11;
import net.minecraft.world.entity.projectile.Fireball;

import static org.lwjgl.opengl.GL12.*;

public class FireballRenderer extends EntityRenderer<Fireball>
{
    public void render(final Fireball entity, final double x, final double y, final double z, final float rot, final float a) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y, (float)z);
        GL11.glEnable(GL_RESCALE_NORMAL);
        final float n = 2.0f;
        GL11.glScalef(n / 1.0f, n / 1.0f, n / 1.0f);
        final int icon = Item.snowBall.getIcon(0);
        this.bindTexture("/gui/items.png");
        final Tesselator instance = Tesselator.instance;
        final float n2 = (icon % 16 * 16 + 0) / 256.0f;
        final float n3 = (icon % 16 * 16 + 16) / 256.0f;
        final float n4 = (icon / 16 * 16 + 0) / 256.0f;
        final float n5 = (icon / 16 * 16 + 16) / 256.0f;
        final float n6 = 1.0f;
        final float n7 = 0.5f;
        final float n8 = 0.25f;
        GL11.glRotatef(180.0f - this.entityRenderDispatcher.playerRotY, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(-this.entityRenderDispatcher.playerRotX, 1.0f, 0.0f, 0.0f);
        instance.begin();
        instance.normal(0.0f, 1.0f, 0.0f);
        instance.vertexUV(0.0f - n7, 0.0f - n8, 0.0, n2, n5);
        instance.vertexUV(n6 - n7, 0.0f - n8, 0.0, n3, n5);
        instance.vertexUV(n6 - n7, 1.0f - n8, 0.0, n3, n4);
        instance.vertexUV(0.0f - n7, 1.0f - n8, 0.0, n2, n4);
        instance.end();
        GL11.glDisable(GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }
}
