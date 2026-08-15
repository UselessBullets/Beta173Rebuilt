// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

import net.minecraft.client.renderer.Tesselator;

import static org.lwjgl.opengl.GL11.*;

public class GuiComponent
{
    protected float blitOffset;
    
    public GuiComponent() {
        this.blitOffset = 0.0f;
    }
    
    protected void hLine(int x0, int x1, final int y, final int col) {
        if (x1 < x0) {
            final int n = x0;
            x0 = x1;
            x1 = n;
        }
        this.fill(x0, y, x1 + 1, y + 1, col);
    }
    
    protected void vLine(final int x, int y0, int y1, final int col) {
        if (y1 < y0) {
            final int n = y0;
            y0 = y1;
            y1 = n;
        }
        this.fill(x, y0 + 1, x + 1, y1, col);
    }
    
    protected void fill(int x0, int y0, int x1, int y1, final int col) {
        if (x0 < x1) {
            final int n = x0;
            x0 = x1;
            x1 = n;
        }
        if (y0 < y1) {
            final int n2 = y0;
            y0 = y1;
            y1 = n2;
        }
        final float n3 = (col >> 24 & 0xFF) / 255.0f;
        final float n4 = (col >> 16 & 0xFF) / 255.0f;
        final float n5 = (col >> 8 & 0xFF) / 255.0f;
        final float n6 = (col & 0xFF) / 255.0f;
        final Tesselator instance = Tesselator.instance;
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glColor4f(n4, n5, n6, n3);
        instance.begin();
        instance.vertex(x0, y1, 0.0);
        instance.vertex(x1, y1, 0.0);
        instance.vertex(x1, y0, 0.0);
        instance.vertex(x0, y0, 0.0);
        instance.end();
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
    }
    
    protected void fillGradient(final int x0, final int y0, final int x1, final int y1, final int col1, final int col2) {
        final float a = (col1 >> 24 & 0xFF) / 255.0f;
        final float r = (col1 >> 16 & 0xFF) / 255.0f;
        final float g = (col1 >> 8 & 0xFF) / 255.0f;
        final float b = (col1 & 0xFF) / 255.0f;
        final float a2 = (col2 >> 24 & 0xFF) / 255.0f;
        final float r2 = (col2 >> 16 & 0xFF) / 255.0f;
        final float g2 = (col2 >> 8 & 0xFF) / 255.0f;
        final float b2 = (col2 & 0xFF) / 255.0f;
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glDisable(GL_ALPHA_TEST);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glShadeModel(GL_SMOOTH);
        final Tesselator instance = Tesselator.instance;
        instance.begin();
        instance.color(r, g, b, a);
        instance.vertex(x1, y0, 0.0);
        instance.vertex(x0, y0, 0.0);
        instance.color(r2, g2, b2, a2);
        instance.vertex(x0, y1, 0.0);
        instance.vertex(x1, y1, 0.0);
        instance.end();
        glShadeModel(GL_FLAT);
        glDisable(GL_BLEND);
        glEnable(GL_ALPHA_TEST);
        glEnable(GL_TEXTURE_2D);
    }
    
    public void drawCenteredString(final Font font, final String str, final int x, final int y, final int color) {
        font.drawShadow(str, x - font.width(str) / 2, y, color);
    }
    
    public void drawString(final Font font, final String str, final int x, final int y, final int color) {
        font.drawShadow(str, x, y, color);
    }
    
    public void blit(final int x, final int y, final int sx, final int sy, final int w, final int h) {
        final float n = 0.00390625f;
        final float n2 = 0.00390625f;
        final Tesselator instance = Tesselator.instance;
        instance.begin();
        instance.vertexUV(x + 0, y + h, this.blitOffset, (sx + 0) * n, (sy + h) * n2);
        instance.vertexUV(x + w, y + h, this.blitOffset, (sx + w) * n, (sy + h) * n2);
        instance.vertexUV(x + w, y + 0, this.blitOffset, (sx + w) * n, (sy + 0) * n2);
        instance.vertexUV(x + 0, y + 0, this.blitOffset, (sx + 0) * n, (sy + 0) * n2);
        instance.end();
    }
}
