// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.monster.Spider;
import net.minecraft.client.model.SpiderModel;

import static org.lwjgl.opengl.GL11.*;

public class SpiderRenderer extends MobRenderer<Spider>
{
    public SpiderRenderer() {
        super(new SpiderModel(), 1.0f);
        this.setArmor(new SpiderModel());
    }
    
    protected float getFlipDegrees(final Spider spider) {
        return 180.0f;
    }
    
    protected boolean prepareArmor(final Spider spider, final int layer, final float a) {
        if (layer != 0) return false;
        this.bindTexture("/mob/spider_eyes.png");

        final float br = (1.0f - spider.getBrightness(1.0f)) * 0.5f;
        glEnable(GL_BLEND);
        glDisable(GL_ALPHA_TEST);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glColor4f(1.0f, 1.0f, 1.0f, br);
        return true;
    }
}
