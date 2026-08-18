// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;

import static org.lwjgl.opengl.GL11.*;

public class DefaultRenderer extends EntityRenderer<Entity>
{
    @Override
    public void render(final Entity entity, final double x, final double y, final double z, final float rot, final float a) {
        glPushMatrix();
        EntityRenderer.render(entity.bb, x - entity.xOld, y - entity.yOld, z - entity.zOld);
        glPopMatrix();
    }
}
