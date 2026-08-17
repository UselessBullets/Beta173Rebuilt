// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import org.lwjgl.opengl.GL11;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.client.model.GhastModel;

public class GhastRenderer extends MobRenderer<Ghast>
{
    public GhastRenderer() {
        super(new GhastModel(), 0.5f);
    }
    
    protected void scale(final Ghast ghast, final float a) {
        float ss = (ghast.oCharge + (ghast.charge - ghast.oCharge) * a) / 20.0f;
        if (ss < 0.0f) ss = 0.0f;
        ss = 1.0f / (ss * ss * ss * ss * ss * 2.0f + 1.0f);
        final float s = (8.0f + ss) / 2.0f;
        final float hs = (8.0f + 1.0f / ss) / 2.0f;
        GL11.glScalef(hs, s, hs);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }
}
