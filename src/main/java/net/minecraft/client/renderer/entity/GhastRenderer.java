// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Mob;
import org.lwjgl.opengl.GL11;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.GhastModel;

public class GhastRenderer extends MobRenderer<Ghast>
{
    public GhastRenderer() {
        super(new GhastModel(), 0.5f);
    }
    
    protected void scale(final Ghast mob, final float partialTick) {
        float n = (mob.oCharge + (mob.charge - mob.oCharge) * partialTick) / 20.0f;
        if (n < 0.0f) {
            n = 0.0f;
        }
        final float n2 = 1.0f / (n * n * n * n * n * 2.0f + 1.0f);
        final float n3 = (8.0f + n2) / 2.0f;
        final float n4 = (8.0f + 1.0f / n2) / 2.0f;
        GL11.glScalef(n4, n3, n4);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }
}
