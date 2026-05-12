// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Mob;
import org.lwjgl.opengl.GL11;
import net.minecraft.world.entity.monster.Giant;
import net.minecraft.client.model.Model;

public class GiantMobRenderer extends MobRenderer
{
    private float scale;
    
    public GiantMobRenderer(final Model model, final float shadow, final float scale) {
        super(model, shadow * scale);
        this.scale = scale;
    }
    
    protected void scale(final Giant mob, final float partialTick) {
        GL11.glScalef(this.scale, this.scale, this.scale);
    }
}
