// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.client.model.Model;

public class PigRenderer extends MobRenderer<Pig>
{
    public PigRenderer(final Model model, final Model armor, final float shadow) {
        super(model, shadow);
        this.setArmor(armor);
    }
    
    protected boolean prepareArmor(final Pig mob, final int layer, final float partialTick) {
        this.bindTexture("/mob/saddle.png");
        return layer == 0 && mob.hasSaddle();
    }
}
