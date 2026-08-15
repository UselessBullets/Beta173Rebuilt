// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Mob;
import org.lwjgl.opengl.GL11;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.client.model.Model;

import static org.lwjgl.opengl.GL11.*;

public class SlimeRenderer extends MobRenderer<Slime>
{
    private Model slimeArmor;
    
    public SlimeRenderer(final Model model, final Model armor, final float shadow) {
        super(model, shadow);
        this.slimeArmor = armor;
    }
    
    protected boolean prepareArmor(final Slime mob, final int layer, final float partialTick) {
        if (layer == 0) {
            this.setArmor(this.slimeArmor);
            GL11.glEnable(2977);
            GL11.glEnable(GL_BLEND);
            GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            return true;
        }
        if (layer == 1) {
            GL11.glDisable(GL_BLEND);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        }
        return false;
    }
    
    protected void scale(final Slime mob, final float partialTick) {
        final int size = mob.getSize();
        final float n = 1.0f / ((mob.oSquish + (mob.squish - mob.oSquish) * partialTick) / (size * 0.5f + 1.0f) + 1.0f);
        final float n2 = (float)size;
        GL11.glScalef(n * n2, 1.0f / n * n2, n * n2);
    }
}
