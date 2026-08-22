// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client;

import org.lwjgl.opengl.Display;
import net.minecraft.client.renderer.Tesselator;
import net.minecraft.client.gui.ScreenSizeCalculator;
import util.ProgressListener;

import static org.lwjgl.opengl.GL11.*;

public class ProgressRenderer implements ProgressListener
{
    private String status = "";
    private Minecraft minecraft;
    private String title = "";
    private long lastTime = System.currentTimeMillis();
    private boolean noAbort = false;
    
    public ProgressRenderer(final Minecraft minecraft) {
        this.minecraft = minecraft;
    }
    
    public void progressStart(final String title) {
        this.noAbort = false;
        this._progressStart(title);
    }
    
    public void progressStartNoAbort(final String string) {
        this.noAbort = true;
        this._progressStart(this.title);
    }
    
    public void _progressStart(final String title) {
        if (!this.minecraft.running) {
            if (this.noAbort) return;
            throw new StopGameException();
        }

        this.title = title;
        final ScreenSizeCalculator ssc = new ScreenSizeCalculator(this.minecraft.options, this.minecraft.width, this.minecraft.height);
        glClear(GL_DEPTH_BUFFER_BIT);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0.0, ssc.rawWidth, ssc.rawHeight, 0.0, 100.0, 300.0);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glTranslatef(0.0f, 0.0f, -200.0f);
    }
    
    public void progressStage(final String status) {
        if (!this.minecraft.running) {
            if (this.noAbort) return;
            throw new StopGameException();
        }
        this.lastTime = 0L;
        this.status = status;
        this.progressStagePercentage(-1);
        this.lastTime = 0L;
    }
    
    public void progressStagePercentage(final int i) {
        if (!this.minecraft.running) {
            if (this.noAbort) return;
            throw new StopGameException();
        }

        final long now = System.currentTimeMillis();
        if (now - this.lastTime < 20L) return;
        this.lastTime = now;

        final ScreenSizeCalculator ssc = new ScreenSizeCalculator(this.minecraft.options, this.minecraft.width, this.minecraft.height);
        final int screenWidth = ssc.getWidth();
        final int screemHeight = ssc.getHeight();

        glClear(GL_DEPTH_BUFFER_BIT);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0.0, ssc.rawWidth, ssc.rawHeight, 0.0, 100.0, 300.0);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glTranslatef(0.0f, 0.0f, -200.0f);

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        final Tesselator t = Tesselator.instance;
        glBindTexture(GL_TEXTURE_2D, this.minecraft.textures.loadTexture("/gui/background.png"));
        final float s = 32.0f;
        t.begin();
        t.color(0x404040);
        t.vertexUV(0.0, screemHeight, 0.0, 0.0, screemHeight / s);
        t.vertexUV(screenWidth, screemHeight, 0.0, screenWidth / s, screemHeight / s);
        t.vertexUV(screenWidth, 0.0, 0.0, screenWidth / s, 0.0);
        t.vertexUV(0.0, 0.0, 0.0, 0.0, 0.0);
        t.end();

        if (i >= 0) {
            final int w = 100;
            final int h = 2;
            final int x = screenWidth / 2 - w / 2;
            final int y = screemHeight / 2 + 16;

            glDisable(GL_TEXTURE_2D);
            t.begin();
            t.color(0x808080);
            t.vertex(x, y, 0.0);
            t.vertex(x, y + h, 0.0);
            t.vertex(x + w, y + h, 0.0);
            t.vertex(x + w, y, 0.0);

            t.color(0x80ff80);
            t.vertex(x, y, 0.0);
            t.vertex(x, y + h, 0.0);
            t.vertex(x + i, y + h, 0.0);
            t.vertex(x + i, y, 0.0);
            t.end();
            glEnable(GL_TEXTURE_2D);
        }

        this.minecraft.font.drawShadow(this.title, (screenWidth - this.minecraft.font.width(this.title)) / 2, screemHeight / 2 - 4 - 16, 0xffffff);
        this.minecraft.font.drawShadow(this.status, (screenWidth - this.minecraft.font.width(this.status)) / 2, screemHeight / 2 - 4 + 8, 0xffffff);
        Display.update();

        try {
            Thread.yield();
        }
        catch (final Exception ex) {}
    }
}
