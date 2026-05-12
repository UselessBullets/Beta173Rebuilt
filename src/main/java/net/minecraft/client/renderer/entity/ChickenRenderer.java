// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.client.model.Model;

public class ChickenRenderer extends MobRenderer
{
    public ChickenRenderer(final Model model, final float shadow) {
        super(model, shadow);
    }
    
    public void render(final Chicken entity, final double x, final double y, final double z, final float rot, final float partialTick) {
        super.render(entity, x, y, z, rot, partialTick);
    }
    
    protected float getBob(final Chicken mob, final float partialTick) {
        return (Mth.sin(mob.oFlap + (mob.flap - mob.oFlap) * partialTick) + 1.0f) * (mob.oFlapSpeed + (mob.flapSpeed - mob.oFlapSpeed) * partialTick);
    }
}
