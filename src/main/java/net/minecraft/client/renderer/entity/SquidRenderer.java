// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.animal.Squid;
import net.minecraft.client.model.Model;

import static org.lwjgl.opengl.GL11.*;

public class SquidRenderer extends MobRenderer<Squid>
{
    public SquidRenderer(final Model model, final float shadow) {
        super(model, shadow);
    }
    
    public void render(final Squid squid, final double x, final double y, final double z, final float rot, final float a) {
        super.render(squid, x, y, z, rot, a);
    }
    
    protected void setupRotations(final Squid squid, final float bob, final float bodyRot, final float a) {
        final float bodyXRot = squid.xBodyRotO + (squid.xBodyRot - squid.xBodyRotO) * a;
        final float bodyZRot = squid.zBodyRotO + (squid.zBodyRot - squid.zBodyRotO) * a;

        glTranslatef(0.0f, 0.5f, 0.0f);
        glRotatef(180.0f - bodyRot, 0.0f, 1.0f, 0.0f);
        glRotatef(bodyXRot, 1.0f, 0.0f, 0.0f);
        glRotatef(bodyZRot, 0.0f, 1.0f, 0.0f);
        glTranslatef(0.0f, -1.2f, 0.0f);
    }
    
    protected void scale(final Squid squid, final float a) {
    }
    
    protected float getBob(final Squid squid, final float a) {
        return squid.oldTentacleAngle + (squid.tentacleAngle - squid.oldTentacleAngle) * a;
    }
}
