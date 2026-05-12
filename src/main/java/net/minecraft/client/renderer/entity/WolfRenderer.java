// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.client.model.Model;

public class WolfRenderer extends MobRenderer
{
    public WolfRenderer(final Model model, final float shadow) {
        super(model, shadow);
    }
    
    public void render(final Wolf entity, final double x, final double y, final double z, final float rot, final float partialTick) {
        super.render(entity, x, y, z, rot, partialTick);
    }
    
    protected float getBob(final Wolf mob, final float partialTick) {
        return mob.getTailAngle();
    }
    
    protected void scale(final Wolf mob, final float partialTick) {
    }
}
