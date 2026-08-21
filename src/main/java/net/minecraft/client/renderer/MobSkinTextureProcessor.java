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
    private int width = 64;
    private int height = 32;
    
    public BufferedImage process(final BufferedImage in) {
        if (in == null) return null;

        final BufferedImage out = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
        final Graphics g = out.getGraphics();
        g.drawImage(in, 0, 0, null);
        g.dispose();

        this.pixels = ((DataBufferInt)out.getRaster().getDataBuffer()).getData();

        this.setNoAlpha(0, 0, 32, 16);
        this.setForceAlpha(32, 0, 64, 32);
        this.setNoAlpha(0, 16, 64, 32);
        boolean hasAlpha = false;
        for (int x = 32; x < 64; ++x) {
            for (int y = 0; y < 16; ++y) {
                int pix = this.pixels[x + y * 64];
                if ((pix >> 24 & 0xFF) < 128) hasAlpha = true;
            }
        }

        if (!hasAlpha) {
            for (int x = 32; x < 64; ++x) {
                for (int y = 0; y < 16; ++y) {
                    int pix = this.pixels[x + y * 64];
                    if ((pix >> 24 & 0xFF) < 128) hasAlpha = true; // Useless - B1.2 leak and LCE imply there should be this redundant hasAlpha assignment, both of these for loops are pretty pointless though since they do no affect the pixel data
                }
            }
        }
        return out;
    }
    
    private void setForceAlpha(final int x0, final int y0, final int x1, final int y1) {
        if (this.hasAlpha(x0, y0, x1, y1)) return;

        for (int x = x0; x < x1; ++x) {
            for (int y = y0; y < y1; ++y) {
                this.pixels[x + y * this.width] &= 0x00FFFFFF;
            }
        }
    }
    
    private void setNoAlpha(final int x0, final int y0, final int x1, final int y1) {
        for (int x = x0; x < x1; ++x) {
            for (int y = y0; y < y1; ++y) {
                this.pixels[x + y * this.width] |= 0xFF000000;
            }
        }
    }
    
    private boolean hasAlpha(final int x0, final int y0, final int x1, final int y1) {
        for (int x = x0; x < x1; ++x) {
            for (int y = y0; y < y1; ++y) {
                int pix = this.pixels[x + y * this.width];
                if ((pix >> 24 & 0xFF) < 128) return true;
            }
        }
        return false;
    }
}
