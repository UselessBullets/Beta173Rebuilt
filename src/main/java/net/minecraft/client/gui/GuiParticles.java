// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

import org.lwjgl.opengl.GL11;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import java.util.List;

public class GuiParticles extends GuiComponent
{
    private List<GuiParticle> particles;
    private Minecraft mc;
    
    public GuiParticles(final Minecraft mc) {
        this.particles = new ArrayList<>();
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
    
    public void render(final float partialTick) {
        this.mc.textures.bind(this.mc.textures.loadTexture("/gui/particles.png"));
        for (int i = 0; i < this.particles.size(); ++i) {
            final GuiParticle guiParticle = this.particles.get(i);
            final int x = (int)(guiParticle.xo + (guiParticle.x - guiParticle.xo) * partialTick - 4.0);
            final int y = (int)(guiParticle.yo + (guiParticle.y - guiParticle.yo) * partialTick - 4.0);
            GL11.glColor4f((float)(guiParticle.or + (guiParticle.r - guiParticle.or) * partialTick), (float)(guiParticle.og + (guiParticle.g - guiParticle.og) * partialTick), (float)(guiParticle.ob + (guiParticle.b - guiParticle.ob) * partialTick), (float)(guiParticle.oa + (guiParticle.a - guiParticle.oa) * partialTick));
            this.blit(x, y, 40, 0, 8, 8);
        }
    }
}
