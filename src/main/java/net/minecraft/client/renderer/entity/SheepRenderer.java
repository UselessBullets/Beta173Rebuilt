// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import org.lwjgl.opengl.GL11;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.client.model.Model;

public class SheepRenderer extends MobRenderer<Sheep>
{
    public SheepRenderer(final Model model, final Model armor, final float shadow) {
        super(model, shadow);
        this.setArmor(armor);
    }
    
    protected boolean prepareArmor(final Sheep mob, final int layer, final float a) {
        if (layer == 0 && !mob.isSheared()) {
            this.bindTexture("/mob/sheep_fur.png");
            final float brightness = mob.getBrightness(a);
            final int color = mob.getColor();
            GL11.glColor3f(brightness * Sheep.COLOR[color][0], brightness * Sheep.COLOR[color][1], brightness * Sheep.COLOR[color][2]);
            return true;
        }
        return false;
    }
}
