// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

import net.minecraft.client.Options;

public class ScreenSizeCalculator
{
    private int w;
    private int h;
    public double rawWidth;
    public double rawHeight;
    public int scale;
    
    public ScreenSizeCalculator(final Options options, final int width, final int height) {
        this.w = width;
        this.h = height;
        this.scale = 1;
        int guiScale = options.guiScale;
        if (guiScale == 0) {
            guiScale = 1000;
        }
        while (this.scale < guiScale && this.w / (this.scale + 1) >= 320 && this.h / (this.scale + 1) >= 240) {
            ++this.scale;
        }
        this.rawWidth = this.w / (double)this.scale;
        this.rawHeight = this.h / (double)this.scale;
        this.w = (int)Math.ceil(this.rawWidth);
        this.h = (int)Math.ceil(this.rawHeight);
    }
    
    public int getWidth() {
        return this.w;
    }
    
    public int getHeight() {
        return this.h;
    }
}
