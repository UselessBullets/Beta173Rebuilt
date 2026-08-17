// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import org.lwjgl.opengl.GL11;
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
    
    protected void scale(final Creeper mob, final float a) {
        float g = mob.getSwelling(a);

        final float wobble = 1.0f + Mth.sin(g * 100.0f) * g * 0.01f;
        if (g < 0.0f) g = 0.0f;
        if (g > 1.0f) g = 1.0f;
        g = g * g;
        g = g * g;
        final float s = (1.0f + g * 0.4f) * wobble;
        final float hs = (1.0f + g * 0.1f) / wobble;
        GL11.glScalef(s, hs, s);
    }
    
    protected int getOverlayColor(final Creeper mob, final float br, final float a) {
        final float step = mob.getSwelling(a);

        if ((int)(step * 10.0f) % 2 == 0) return 0;

        int _a = (int)(step * 0.2f * 255.0f);
        if (_a < 0) _a = 0;
        if (_a > 255) _a = 255;

        int r = 255;
        int g = 255;
        int b = 255;

        return (_a << 24) | (r << 16) | (g << 8) | b;
    }
    
    protected boolean prepareArmor(final Creeper mob, final int layer, final float a) {
        if (mob.isPowered()) {
            if (layer == 1) {
                final float time = mob.tickCount + a;
                this.bindTexture("/armor/power.png");
                GL11.glMatrixMode(GL_TEXTURE);
                GL11.glLoadIdentity();
                float uo = time * 0.01f;
                float vo = time * 0.01f;
                GL11.glTranslatef(uo, vo, 0.0f);
                this.setArmor(this.armorModel);
                GL11.glMatrixMode(GL_MODELVIEW);
                GL11.glEnable(GL_BLEND);
                final float nr = 0.5f;
                GL11.glColor4f(nr, nr, nr, 1.0f);
                GL11.glDisable(GL_LIGHTING);
                GL11.glBlendFunc(GL_ONE, GL_ONE);
                return true;
            }
            if (layer == 2) {
                GL11.glMatrixMode(GL_TEXTURE);
                GL11.glLoadIdentity();
                GL11.glMatrixMode(GL_MODELVIEW);
                GL11.glEnable(GL_LIGHTING);
                GL11.glDisable(GL_BLEND);
            }
        }
        return false;
    }
    
    protected boolean prepareArmorOverlay(final Creeper mob, final int layer, final float a) {
        return false;
    }
}
