// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import org.lwjgl.opengl.GL11;
import net.minecraft.world.entity.Entity;

public class DefaultRenderer extends EntityRenderer<Entity>
{
    @Override
    public void render(final Entity entity, final double x, final double y, final double z, final float rot, final float partialTick) {
        GL11.glPushMatrix();
        EntityRenderer.render(entity.bb, x - entity.xOld, y - entity.yOld, z - entity.zOld);
        GL11.glPopMatrix();
    }
}
