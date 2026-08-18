// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import util.Mth;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.Model;

import static org.lwjgl.opengl.GL11.*;

public class CreeperRenderer extends MobRenderer<Creeper>
{
    private Model armorModel = new CreeperModel(2.0f);
    
    public CreeperRenderer() {
        super(new CreeperModel(), 0.5f);
    }
    
    protected void scale(final Creeper creeper, final float a) {
        float g = creeper.getSwelling(a);

        final float wobble = 1.0f + Mth.sin(g * 100.0f) * g * 0.01f;
        if (g < 0.0f) g = 0.0f;
        if (g > 1.0f) g = 1.0f;
        g = g * g;
        g = g * g;
        final float s = (1.0f + g * 0.4f) * wobble;
        final float hs = (1.0f + g * 0.1f) / wobble;
        glScalef(s, hs, s);
    }
    
    protected int getOverlayColor(final Creeper creeper, final float br, final float a) {
        final float step = creeper.getSwelling(a);

        if ((int)(step * 10.0f) % 2 == 0) return 0;

        int _a = (int)(step * 0.2f * 255.0f);
        if (_a < 0) _a = 0;
        if (_a > 255) _a = 255;

        int r = 255;
        int g = 255;
        int b = 255;

        return (_a << 24) | (r << 16) | (g << 8) | b;
    }
    
    protected boolean prepareArmor(final Creeper creeper, final int layer, final float a) {
        if (creeper.isPowered()) {
            if (layer == 1) {
                final float time = creeper.tickCount + a;
                this.bindTexture("/armor/power.png");
                glMatrixMode(GL_TEXTURE);
                glLoadIdentity();
                float uo = time * 0.01f;
                float vo = time * 0.01f;
                glTranslatef(uo, vo, 0.0f);
                this.setArmor(this.armorModel);
                glMatrixMode(GL_MODELVIEW);
                glEnable(GL_BLEND);
                final float nr = 0.5f;
                glColor4f(nr, nr, nr, 1.0f);
                glDisable(GL_LIGHTING);
                glBlendFunc(GL_ONE, GL_ONE);
                return true;
            }
            if (layer == 2) {
                glMatrixMode(GL_TEXTURE);
                glLoadIdentity();
                glMatrixMode(GL_MODELVIEW);
                glEnable(GL_LIGHTING);
                glDisable(GL_BLEND);
            }
        }
        return false;
    }
    
    protected boolean prepareArmorOverlay(final Creeper mob, final int layer, final float a) {
        return false;
    }
}
