// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import org.lwjgl.opengl.GL11;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.client.model.Model;

public class SquidRenderer extends MobRenderer<Squid>
{
    public SquidRenderer(final Model model, final float shadow) {
        super(model, shadow);
    }
    
    public void render(final Squid entity, final double x, final double y, final double z, final float rot, final float a) {
        super.render(entity, x, y, z, rot, a);
    }
    
    protected void setupRotations(final Squid mob, final float bob, final float bodyRot, final float a) {
        final float n = mob.xBodyRotO + (mob.xBodyRot - mob.xBodyRotO) * a;
        final float n2 = mob.zBodyRotO + (mob.zBodyRot - mob.zBodyRotO) * a;
        GL11.glTranslatef(0.0f, 0.5f, 0.0f);
        GL11.glRotatef(180.0f - bodyRot, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(n, 1.0f, 0.0f, 0.0f);
        GL11.glRotatef(n2, 0.0f, 1.0f, 0.0f);
        GL11.glTranslatef(0.0f, -1.2f, 0.0f);
    }
    
    protected void scale(final Squid mob, final float a) {
    }
    
    protected float getBob(final Squid mob, final float a) {
        return mob.oldTentacleAngle + (mob.tentacleAngle - mob.oldTentacleAngle) * a;
    }
}
