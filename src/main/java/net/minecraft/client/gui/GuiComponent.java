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
            final int tmp = x0;
            x0 = x1;
            x1 = tmp;
        }
        this.fill(x0, y, x1 + 1, y + 1, col);
    }
    
    protected void vLine(final int x, int y0, int y1, final int col) {
        if (y1 < y0) {
            final int tmp = y0;
            y0 = y1;
            y1 = tmp;
        }
        this.fill(x, y0 + 1, x + 1, y1, col);
    }
    
    protected void fill(int x0, int y0, int x1, int y1, final int col) {
        if (x0 < x1) {
            final int tmp = x0;
            x0 = x1;
            x1 = tmp;
        }
        if (y0 < y1) {
            final int tmp = y0;
            y0 = y1;
            y1 = tmp;
        }
        final float a = (col >> 24 & 0xFF) / 255.0f;
        final float r = (col >> 16 & 0xFF) / 255.0f;
        final float g = (col >> 8 & 0xFF) / 255.0f;
        final float b = (col & 0xFF) / 255.0f;
        final Tesselator t = Tesselator.instance;
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glColor4f(r, g, b, a);
        t.begin();
        t.vertex(x0, y1, 0.0);
        t.vertex(x1, y1, 0.0);
        t.vertex(x1, y0, 0.0);
        t.vertex(x0, y0, 0.0);
        t.end();
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
    }
    
    protected void fillGradient(final int x0, final int y0, final int x1, final int y1, final int col1, final int col2) {
        final float a1 = (col1 >> 24 & 0xFF) / 255.0f;
        final float r1 = (col1 >> 16 & 0xFF) / 255.0f;
        final float g1 = (col1 >> 8 & 0xFF) / 255.0f;
        final float b1 = (col1 & 0xFF) / 255.0f;

        final float a2 = (col2 >> 24 & 0xFF) / 255.0f;
        final float r2 = (col2 >> 16 & 0xFF) / 255.0f;
        final float g2 = (col2 >> 8 & 0xFF) / 255.0f;
        final float b2 = (col2 & 0xFF) / 255.0f;
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glDisable(GL_ALPHA_TEST);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glShadeModel(GL_SMOOTH);

        final Tesselator t = Tesselator.instance;
        t.begin();
        t.color(r1, g1, b1, a1);
        t.vertex(x1, y0, 0.0);
        t.vertex(x0, y0, 0.0);
        t.color(r2, g2, b2, a2);
        t.vertex(x0, y1, 0.0);
        t.vertex(x1, y1, 0.0);
        t.end();

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
        final float us = 1 / 256.0f;
        final float vs = 1 / 256.0f;
        final Tesselator t = Tesselator.instance;
        t.begin();
        t.vertexUV(x + 0, y + h, this.blitOffset, (sx + 0) * us, (sy + h) * vs);
        t.vertexUV(x + w, y + h, this.blitOffset, (sx + w) * us, (sy + h) * vs);
        t.vertexUV(x + w, y + 0, this.blitOffset, (sx + w) * us, (sy + 0) * vs);
        t.vertexUV(x + 0, y + 0, this.blitOffset, (sx + 0) * us, (sy + 0) * vs);
        t.end();
    }
}
