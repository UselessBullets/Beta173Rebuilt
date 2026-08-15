// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;

import static org.lwjgl.opengl.GL11.*;
public class Button extends GuiComponent
{
    protected int w;
    protected int h;
    public int x;
    public int y;
    public String msg;
    public int id;
    public boolean active;
    public boolean visible;
    
    public Button(final int id, final int x, final int y, final String msg) {
        this(id, x, y, 200, 20, msg);
    }
    
    public Button(final int id, final int x, final int y, final int w, final int h, final String msg) {
        this.active = true;
        this.visible = true;

        this.id = id;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.msg = msg;
    }
    
    protected int getYImage(final boolean hovered) {
        int res = 1;
        if (!this.active) res = 0;
        else if (hovered) res = 2;
        return res;
    }
    
    public void render(final Minecraft minecraft, final int xm, final int ym) {
        if (!this.visible) return;

        final Font font = minecraft.font;

        glBindTexture(GL_TEXTURE_2D, minecraft.textures.loadTexture("/gui/gui.png"));
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        final boolean hovered = xm >= this.x && ym >= this.y && xm < this.x + this.w && ym < this.y + this.h;
        final int yImage = this.getYImage(hovered);

        this.blit(this.x, this.y, 0, 46 + yImage * 20, this.w / 2, this.h);
        this.blit(this.x + this.w / 2, this.y, 200 - this.w / 2, 46 + yImage * 20, this.w / 2, this.h);

        this.renderBg(minecraft, xm, ym);
        if (!this.active) {
            this.drawCenteredString(font, this.msg, this.x + this.w / 2, this.y + (this.h - 8) / 2, 0xffa0a0a0);
        }
        else if (hovered) {
            this.drawCenteredString(font, this.msg, this.x + this.w / 2, this.y + (this.h - 8) / 2, 0xffffa0);
        }
        else {
            this.drawCenteredString(font, this.msg, this.x + this.w / 2, this.y + (this.h - 8) / 2, 0xe0e0e0);
        }
    }
    
    protected void renderBg(final Minecraft minecraft, final int xm, final int ym) {
    }
    
    public void released(final int mx, final int my) {
    }
    
    public boolean clicked(final Minecraft minecraft, final int mx, final int my) {
        return this.active && mx >= this.x && my >= this.y && mx < this.x + this.w && my < this.y + this.h;
    }
}
