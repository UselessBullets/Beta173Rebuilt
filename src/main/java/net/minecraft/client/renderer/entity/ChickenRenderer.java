// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import util.Mth;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.client.model.Model;

public class ChickenRenderer extends MobRenderer<Chicken>
{
    public ChickenRenderer(final Model model, final float shadow) {
        super(model, shadow);
    }
    
    public void render(final Chicken chicken, final double x, final double y, final double z, final float rot, final float a) {
        super.render(chicken, x, y, z, rot, a);
    }
    
    protected float getBob(final Chicken chicken, final float a) {
        float flap = chicken.oFlap + (chicken.flap - chicken.oFlap) * a;
        float flapSpeed = chicken.oFlapSpeed + (chicken.flapSpeed - chicken.oFlapSpeed) * a;

        return (Mth.sin(flap) + 1.0f) * flapSpeed;
    }
}
