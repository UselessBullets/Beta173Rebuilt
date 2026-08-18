// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class GuiParticles extends GuiComponent
{
    private List<GuiParticle> particles = new ArrayList<>();
    private Minecraft mc;
    
    public GuiParticles(final Minecraft mc) {
        this.mc = mc;
    }
    
    public void tick() {
        for (int i = 0; i < this.particles.size(); ++i) {
            final GuiParticle guiParticle = this.particles.get(i);

            guiParticle.preTick();
            guiParticle.tick(this);

            if (guiParticle.removed) {
                this.particles.remove(i--);
            }
        }
    }

    public void add(GuiParticle guiParticle) {
        this.particles.add(guiParticle);
        guiParticle.preTick();
    }
    
    public void render(final float a) {
        this.mc.textures.bind(this.mc.textures.loadTexture("/gui/particles.png"));
        for (int i = 0; i < this.particles.size(); ++i) {
            final GuiParticle guiParticle = this.particles.get(i);
            final int xx = (int)(guiParticle.xo + (guiParticle.x - guiParticle.xo) * a - 4.0);
            final int yy = (int)(guiParticle.yo + (guiParticle.y - guiParticle.yo) * a - 4.0);

            final float alpha = (float)(guiParticle.oa + (guiParticle.a - guiParticle.oa) * a);
            final float r = (float)(guiParticle.or + (guiParticle.r - guiParticle.or) * a);
            final float g = (float)(guiParticle.og + (guiParticle.g - guiParticle.og) * a);
            final float b = (float)(guiParticle.ob + (guiParticle.b - guiParticle.ob) * a);

            glColor4f(r, g, b, alpha);
            this.blit(xx, yy, 8 * 5, 0, 8, 8);
        }
    }
}
