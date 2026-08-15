// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Mob;
import org.lwjgl.opengl.GL11;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.SpiderModel;

import static org.lwjgl.opengl.GL11.*;

public class SpiderRenderer extends MobRenderer<Spider>
{
    public SpiderRenderer() {
        super(new SpiderModel(), 1.0f);
        this.setArmor(new SpiderModel());
    }
    
    protected float getFlipDegrees(final Spider mob) {
        return 180.0f;
    }
    
    protected boolean prepareArmor(final Spider mob, final int layer, final float partialTick) {
        if (layer != 0) {
            return false;
        }
        if (layer != 0) {
            return false;
        }
        this.bindTexture("/mob/spider_eyes.png");
        final float n = (1.0f - mob.getBrightness(1.0f)) * 0.5f;
        GL11.glEnable(GL_BLEND);
        GL11.glDisable(GL_ALPHA_TEST);
        GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, n);
        return true;
    }
}
