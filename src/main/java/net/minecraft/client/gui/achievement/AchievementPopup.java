// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui.achievement;

import net.minecraft.client.Lighting;
import net.minecraft.client.gui.ScreenSizeCalculator;
import org.lwjgl.opengl.GL11;
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
        GL11.glViewport(0, 0, this.mc.width, this.mc.height);
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        this.width = this.mc.width;
        this.height = this.mc.height;
        final ScreenSizeCalculator screenSizeCalculator = new ScreenSizeCalculator(this.mc.options, this.mc.width, this.mc.height);
        this.width = screenSizeCalculator.getWidth();
        this.height = screenSizeCalculator.getHeight();
        GL11.glClear(256);
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0, (double)this.width, (double)this.height, 0.0, 1000.0, 3000.0);
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0f, 0.0f, -2000.0f);
    }
    
    public void render() {
        if (Minecraft.warezTime > 0L) {
            GL11.glDisable(GL_DEPTH_TEST);
            GL11.glDepthMask(false);
            Lighting.turnOff();
            this.prepareWindow();
            final String str = "Minecraft Beta 1.7.3   Unlicensed Copy :(";
            final String str2 = "(Or logged in from another location)";
            final String str3 = "Purchase at minecraft.net";
            this.mc.font.drawShadow(str, 2, 2, 16777215);
            this.mc.font.drawShadow(str2, 2, 11, 16777215);
            this.mc.font.drawShadow(str3, 2, 20, 16777215);
            GL11.glDepthMask(true);
            GL11.glEnable(GL_DEPTH_TEST);
        }
        if (this.ach == null || this.startTime == 0L) {
            return;
        }
        final double n = (System.currentTimeMillis() - this.startTime) / 3000.0;
        if (!this.isHelper) {
            if (!this.isHelper && (n < 0.0 || n > 1.0)) {
                this.startTime = 0L;
                return;
            }
        }
        this.prepareWindow();
        GL11.glDisable(GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        double n2 = n * 2.0;
        if (n2 > 1.0) {
            n2 = 2.0 - n2;
        }
        double n3 = 1.0 - n2 * 4.0;
        if (n3 < 0.0) {
            n3 = 0.0;
        }
        final double n4 = n3 * n3;
        final double n5 = n4 * n4;
        final int x = this.width - 160;
        final int y = 0 - (int)(n5 * 36.0);
        final int loadTexture = this.mc.textures.loadTexture("/achievement/bg.png");
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnable(GL_TEXTURE_2D);
        GL11.glBindTexture(3553, loadTexture);
        GL11.glDisable(GL_LIGHTING);
        this.blit(x, y, 96, 202, 160, 32);
        if (this.isHelper) {
            this.mc.font.drawWordWrapInternal(this.desc, x + 30, y + 7, 120, -1);
        }
        else {
            this.mc.font.draw(this.title, x + 30, y + 7, -256);
            this.mc.font.draw(this.desc, x + 30, y + 18, -1);
        }
        GL11.glPushMatrix();
        GL11.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
        Lighting.turnOn();
        GL11.glPopMatrix();
        GL11.glDisable(GL_LIGHTING);
        GL11.glEnable(GL_RESCALE_NORMAL);
        GL11.glEnable(GL_COLOR_MATERIAL);
        GL11.glEnable(GL_LIGHTING);
        this.ir.renderGuiItem(this.mc.font, this.mc.textures, this.ach.icon, x + 8, y + 8);
        GL11.glDisable(GL_LIGHTING);
        GL11.glDepthMask(true);
        GL11.glEnable(GL_DEPTH_TEST);
    }
}
