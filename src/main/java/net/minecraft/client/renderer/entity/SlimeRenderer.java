// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

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
    
    protected boolean prepareArmor(final Slime slime, final int layer, final float a) {
        if (layer == 0) {
            this.setArmor(this.slimeArmor);

            GL11.glEnable(GL_NORMALIZE);
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
    
    protected void scale(final Slime slime, final float a) {
        final int size = slime.getSize();
        final float ss = (slime.oSquish + (slime.squish - slime.oSquish) * a) / (size * 0.5f + 1.0f);
        final float w = 1.0f / (ss + 1.0f);
        GL11.glScalef(w * (float) size, 1.0f / w * (float) size, w * (float) size);
    }
}
