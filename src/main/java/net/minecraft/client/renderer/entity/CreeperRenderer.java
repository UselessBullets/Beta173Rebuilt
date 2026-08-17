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
    private Model armorModel;
    
    public CreeperRenderer() {
        super(new CreeperModel(), 0.5f);
        this.armorModel = new CreeperModel(2.0f);
    }
    
    protected void scale(final Creeper mob, final float a) {
        float swelling = mob.getSwelling(a);
        final float n = 1.0f + Mth.sin(swelling * 100.0f) * swelling * 0.01f;
        if (swelling < 0.0f) {
            swelling = 0.0f;
        }
        if (swelling > 1.0f) {
            swelling = 1.0f;
        }
        final float n2 = swelling * swelling;
        final float n3 = n2 * n2;
        final float n4 = (1.0f + n3 * 0.4f) * n;
        GL11.glScalef(n4, (1.0f + n3 * 0.1f) / n, n4);
    }
    
    protected int getOverlayColor(final Creeper mob, final float br, final float a) {
        final float swelling = mob.getSwelling(a);
        if ((int)(swelling * 10.0f) % 2 == 0) {
            return 0;
        }
        int n = (int)(swelling * 0.2f * 255.0f);
        if (n < 0) {
            n = 0;
        }
        if (n > 255) {
            n = 255;
        }
        return n << 24 | 255 << 16 | 255 << 8 | 0xFF;
    }
    
    protected boolean prepareArmor(final Creeper mob, final int layer, final float a) {
        if (mob.isPowered()) {
            if (layer == 1) {
                final float n = mob.tickCount + a;
                this.bindTexture("/armor/power.png");
                GL11.glMatrixMode(5890);
                GL11.glLoadIdentity();
                GL11.glTranslatef(n * 0.01f, n * 0.01f, 0.0f);
                this.setArmor(this.armorModel);
                GL11.glMatrixMode(GL_MODELVIEW);
                GL11.glEnable(GL_BLEND);
                final float n2 = 0.5f;
                GL11.glColor4f(n2, n2, n2, 1.0f);
                GL11.glDisable(GL_LIGHTING);
                GL11.glBlendFunc(1, 1);
                return true;
            }
            if (layer == 2) {
                GL11.glMatrixMode(5890);
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
