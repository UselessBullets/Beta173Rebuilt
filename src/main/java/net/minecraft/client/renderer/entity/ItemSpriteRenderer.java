// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.Tesselator;
import org.lwjgl.opengl.GL11;
import net.minecraft.world.entity.Entity;

public class ItemSpriteRenderer extends EntityRenderer
{
    private int icon;
    
    public ItemSpriteRenderer(final int icon) {
        this.icon = icon;
    }
    
    @Override
    public void render(final Entity entity, final double x, final double y, final double z, final float rot, final float partialTick) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y, (float)z);
        GL11.glEnable(32826);
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        this.bindTexture("/gui/items.png");
        final Tesselator instance = Tesselator.instance;
        final float n = (this.icon % 16 * 16 + 0) / 256.0f;
        final float n2 = (this.icon % 16 * 16 + 16) / 256.0f;
        final float n3 = (this.icon / 16 * 16 + 0) / 256.0f;
        final float n4 = (this.icon / 16 * 16 + 16) / 256.0f;
        final float n5 = 1.0f;
        final float n6 = 0.5f;
        final float n7 = 0.25f;
        GL11.glRotatef(180.0f - this.entityRenderDispatcher.playerRotY, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(-this.entityRenderDispatcher.playerRotX, 1.0f, 0.0f, 0.0f);
        instance.begin();
        instance.normal(0.0f, 1.0f, 0.0f);
        instance.vertexUV(0.0f - n6, 0.0f - n7, 0.0, n, n4);
        instance.vertexUV(n5 - n6, 0.0f - n7, 0.0, n2, n4);
        instance.vertexUV(n5 - n6, 1.0f - n7, 0.0, n2, n3);
        instance.vertexUV(0.0f - n6, 1.0f - n7, 0.0, n, n3);
        instance.end();
        GL11.glDisable(32826);
        GL11.glPopMatrix();
    }
}
