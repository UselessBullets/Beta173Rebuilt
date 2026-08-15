// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui.achievement;

import net.minecraft.SharedConstants;
import net.minecraft.client.Lighting;
import net.minecraft.client.gui.ScreenSizeCalculator;
import net.minecraft.locale.language.I18n;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.stats.Achievement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

public class AchievementPopup extends GuiComponent
{
    private Minecraft mc;
    private int width;
    private int height;
    private String title;
    private String desc;
    private Achievement ach;
    private long startTime;
    private ItemRenderer ir;
    private boolean isHelper;
    
    public AchievementPopup(final Minecraft mc) {
        this.mc = mc;
        this.ir = new ItemRenderer();
    }
    
    public void popup(final Achievement ach) {
        this.title = I18n.get("achievement.get");
        this.desc = ach.name;
        this.startTime = System.currentTimeMillis();
        this.ach = ach;
        this.isHelper = false;
    }
    
    public void permanent(final Achievement ach) {
        this.title = ach.name;
        this.desc = ach.getDescription();
        this.startTime = System.currentTimeMillis() - 2500L;
        this.ach = ach;
        this.isHelper = true;
    }
    
    private void prepareWindow() {
        glViewport(0, 0, this.mc.width, this.mc.height);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        this.width = this.mc.width;
        this.height = this.mc.height;
        final ScreenSizeCalculator screenSizeCalculator = new ScreenSizeCalculator(this.mc.options, this.mc.width, this.mc.height);
        this.width = screenSizeCalculator.getWidth();
        this.height = screenSizeCalculator.getHeight();
        glClear(GL_DEPTH_BUFFER_BIT);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0.0, this.width, this.height, 0.0, 1000.0, 3000.0);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glTranslatef(0.0f, 0.0f, -2000.0f);
    }
    
    public void render() {
        if (Minecraft.warezTime > 0L) {
            glDisable(GL_DEPTH_TEST);
            glDepthMask(false);
            Lighting.turnOff();
            this.prepareWindow();
            final String title = "Minecraft " + SharedConstants.VERSION_STRING + "   Unlicensed Copy :(";
            final String msg1 = "(Or logged in from another location)";
            final String msg2 = "Purchase at minecraft.net";
            this.mc.font.drawShadow(title, 2, 2 + 9 * 0, 0xffffff);
            this.mc.font.drawShadow(msg1, 2, 2 + 9 * 1, 0xffffff);
            this.mc.font.drawShadow(msg2, 2, 2 + 9 * 2, 0xffffff);
            glDepthMask(true);
            glEnable(GL_DEPTH_TEST);
        }
        if (this.ach == null || this.startTime == 0L) { return; }

        final double time = (System.currentTimeMillis() - this.startTime) / 3000.0;
        if (this.isHelper) {
        } else if (!this.isHelper && (time < 0.0 || time > 1.0)) {
            this.startTime = 0L;
            return;
        }

        this.prepareWindow();
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);

        double yo = time * 2.0;
        if (yo > 1.0) { yo = 2.0 - yo; }
        yo = yo * 4.0;
        yo = 1.0 - yo;
        if (yo < 0.0) { yo = 0.0; }
        yo = yo * yo;
        yo = yo * yo;

        final int xx = this.width - 160;
        final int yy = 0 - (int)(yo * 36.0);
        final int tex = this.mc.textures.loadTexture("/achievement/bg.png");
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, tex);
        glDisable(GL_LIGHTING);

        this.blit(xx, yy, 96, 202, 160, 32);

        if (this.isHelper) {
            this.mc.font.drawWordWrapInternal(this.desc, xx + 30, yy + 7, 120, 0xffffffff);
        }
        else {
            this.mc.font.draw(this.title, xx + 30, yy + 7, 0xffffff00);
            this.mc.font.draw(this.desc, xx + 30, yy + 18, 0xffffffff);
        }
        glPushMatrix();
        glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
        Lighting.turnOn();
        glPopMatrix();
        glDisable(GL_LIGHTING);
        glEnable(GL_RESCALE_NORMAL);
        glEnable(GL_COLOR_MATERIAL);
        glEnable(GL_LIGHTING);
        this.ir.renderGuiItem(this.mc.font, this.mc.textures, this.ach.icon, xx + 8, yy + 8);
        glDisable(GL_LIGHTING);
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);
    }
}
