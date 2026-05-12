// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer;

import java.awt.Graphics;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class MobSkinTextureProcessor implements HttpTextureProcessor
{
    private int[] pixels;
    private int width;
    private int height;
    
    public BufferedImage process(final BufferedImage read) {
        if (read == null) {
            return null;
        }
        this.width = 64;
        this.height = 32;
        final BufferedImage bufferedImage = new BufferedImage(this.width, this.height, 2);
        final Graphics graphics = bufferedImage.getGraphics();
        graphics.drawImage(read, 0, 0, null);
        graphics.dispose();
        this.pixels = ((DataBufferInt)bufferedImage.getRaster().getDataBuffer()).getData();
        this.setNoAlpha(0, 0, 32, 16);
        this.setForceAlpha(32, 0, 64, 32);
        this.setNoAlpha(0, 16, 64, 32);
        boolean b = false;
        for (int i = 32; i < 64; ++i) {
            for (int j = 0; j < 16; ++j) {
                if ((this.pixels[i + j * 64] >> 24 & 0xFF) < 128) {
                    b = true;
                }
            }
        }
        if (!b) {
            for (int k = 32; k < 64; ++k) {
                for (int l = 0; l < 16; ++l) {
                    if ((this.pixels[k + l * 64] >> 24 & 0xFF) < 128) {}
                }
            }
        }
        return bufferedImage;
    }
    
    private void setForceAlpha(final int x0, final int y0, final int x1, final int y1) {
        if (this.hasAlpha(x0, y0, x1, y1)) {
            return;
        }
        for (int i = x0; i < x1; ++i) {
            for (int j = y0; j < y1; ++j) {
                final int[] pixels = this.pixels;
                final int n = i + j * this.width;
                pixels[n] &= 0xFFFFFF;
            }
        }
    }
    
    private void setNoAlpha(final int x0, final int y0, final int x1, final int y1) {
        for (int i = x0; i < x1; ++i) {
            for (int j = y0; j < y1; ++j) {
                final int[] pixels = this.pixels;
                final int n = i + j * this.width;
                pixels[n] |= 0xFF000000;
            }
        }
    }
    
    private boolean hasAlpha(final int x0, final int y0, final int x1, final int y1) {
        for (int i = x0; i < x1; ++i) {
            for (int j = y0; j < y1; ++j) {
                if ((this.pixels[i + j * this.width] >> 24 & 0xFF) < 128) {
                    return true;
                }
            }
        }
        return false;
    }
}
