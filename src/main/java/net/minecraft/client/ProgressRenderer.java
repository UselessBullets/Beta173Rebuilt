// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client;

import org.lwjgl.opengl.Display;
import net.minecraft.client.renderer.Tesselator;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.gui.ScreenSizeCalculator;
import util.ProgressListener;

import static org.lwjgl.opengl.GL11.*;

public class ProgressRenderer implements ProgressListener
{
    private String status;
    private Minecraft minecraft;
    private String title;
    private long lastTime;
    private boolean noAbort;
    
    public ProgressRenderer(final Minecraft minecraft) {
        this.status = "";
        this.title = "";
        this.lastTime = System.currentTimeMillis();
        this.noAbort = false;
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
        if (this.minecraft.running) {
            this.title = title;
            final ScreenSizeCalculator screenSizeCalculator = new ScreenSizeCalculator(this.minecraft.options, this.minecraft.width, this.minecraft.height);
            GL11.glClear(GL_DEPTH_BUFFER_BIT);
            GL11.glMatrixMode(GL_PROJECTION);
            GL11.glLoadIdentity();
            GL11.glOrtho(0.0, screenSizeCalculator.rawWidth, screenSizeCalculator.rawHeight, 0.0, 100.0, 300.0);
            GL11.glMatrixMode(GL_MODELVIEW);
            GL11.glLoadIdentity();
            GL11.glTranslatef(0.0f, 0.0f, -200.0f);
            return;
        }
        if (this.noAbort) {
            return;
        }
        throw new StopGameException();
    }
    
    public void progressStage(final String status) {
        if (this.minecraft.running) {
            this.lastTime = 0L;
            this.status = status;
            this.progressStagePercentage(-1);
            this.lastTime = 0L;
            return;
        }
        if (this.noAbort) {
            return;
        }
        throw new StopGameException();
    }
    
    public void progressStagePercentage(final int i) {
        if (!this.minecraft.running) {
            if (this.noAbort) {
                return;
            }
            throw new StopGameException();
        }
        else {
            final long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis - this.lastTime < 20L) {
                return;
            }
            this.lastTime = currentTimeMillis;
            final ScreenSizeCalculator screenSizeCalculator = new ScreenSizeCalculator(this.minecraft.options, this.minecraft.width, this.minecraft.height);
            final int width = screenSizeCalculator.getWidth();
            final int height = screenSizeCalculator.getHeight();
            GL11.glClear(GL_DEPTH_BUFFER_BIT);
            GL11.glMatrixMode(GL_PROJECTION);
            GL11.glLoadIdentity();
            GL11.glOrtho(0.0, screenSizeCalculator.rawWidth, screenSizeCalculator.rawHeight, 0.0, 100.0, 300.0);
            GL11.glMatrixMode(GL_MODELVIEW);
            GL11.glLoadIdentity();
            GL11.glTranslatef(0.0f, 0.0f, -200.0f);
            GL11.glClear(16640);
            final Tesselator instance = Tesselator.instance;
            GL11.glBindTexture(GL_TEXTURE_2D, this.minecraft.textures.loadTexture("/gui/background.png"));
            final float n = 32.0f;
            instance.begin();
            instance.color(4210752);
            instance.vertexUV(0.0, height, 0.0, 0.0, height / n);
            instance.vertexUV(width, height, 0.0, width / n, height / n);
            instance.vertexUV(width, 0.0, 0.0, width / n, 0.0);
            instance.vertexUV(0.0, 0.0, 0.0, 0.0, 0.0);
            instance.end();
            if (i >= 0) {
                final int n2 = 100;
                final int n3 = 2;
                final int n4 = width / 2 - n2 / 2;
                final int n5 = height / 2 + 16;
                GL11.glDisable(GL_TEXTURE_2D);
                instance.begin();
                instance.color(8421504);
                instance.vertex(n4, n5, 0.0);
                instance.vertex(n4, n5 + n3, 0.0);
                instance.vertex(n4 + n2, n5 + n3, 0.0);
                instance.vertex(n4 + n2, n5, 0.0);
                instance.color(8454016);
                instance.vertex(n4, n5, 0.0);
                instance.vertex(n4, n5 + n3, 0.0);
                instance.vertex(n4 + i, n5 + n3, 0.0);
                instance.vertex(n4 + i, n5, 0.0);
                instance.end();
                GL11.glEnable(GL_TEXTURE_2D);
            }
            this.minecraft.font.drawShadow(this.title, (width - this.minecraft.font.width(this.title)) / 2, height / 2 - 4 - 16, 16777215);
            this.minecraft.font.drawShadow(this.status, (width - this.minecraft.font.width(this.status)) / 2, height / 2 - 4 + 8, 16777215);
            Display.update();
            try {
                Thread.yield();
            }
            catch (final Exception ex) {}
        }
    }
}
