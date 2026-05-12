// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import org.lwjgl.opengl.GL11;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.client.model.Model;

public class SquidRenderer extends MobRenderer
{
    public SquidRenderer(final Model model, final float shadow) {
        super(model, shadow);
    }
    
    public void render(final Squid entity, final double x, final double y, final double z, final float rot, final float partialTick) {
        super.render(entity, x, y, z, rot, partialTick);
    }
    
    protected void setupRotations(final Squid mob, final float bob, final float bodyRot, final float partialTick) {
        final float n = mob.xBodyRotO + (mob.xBodyRot - mob.xBodyRotO) * partialTick;
        final float n2 = mob.zBodyRotO + (mob.zBodyRot - mob.zBodyRotO) * partialTick;
        GL11.glTranslatef(0.0f, 0.5f, 0.0f);
        GL11.glRotatef(180.0f - bodyRot, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(n, 1.0f, 0.0f, 0.0f);
        GL11.glRotatef(n2, 0.0f, 1.0f, 0.0f);
        GL11.glTranslatef(0.0f, -1.2f, 0.0f);
    }
    
    protected void scale(final Squid mob, final float partialTick) {
    }
    
    protected float getBob(final Squid mob, final float partialTick) {
        return mob.oldTentacleAngle + (mob.tentacleAngle - mob.oldTentacleAngle) * partialTick;
    }
}
