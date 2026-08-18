// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.ptexture;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import net.minecraft.world.item.Item;
import net.minecraft.client.Minecraft;
import util.Mth;

public class ClockTexture extends DynamicTexture
{
    private Minecraft mc;
    private int[] raw = new int[16 * 16];
    private int[] dialRaw = new int[16 * 16];
    private double rot, rota;
    
    public ClockTexture(final Minecraft mc) {
        super(Item.clock.getIcon(0));
        this.mc = mc;
        this.textureId = TEXTURE_ITEMS;

        try {
            BufferedImage bi = ImageIO.read(Minecraft.class.getResource("/gui/items.png"));
            int xo = this.tex % 16 * 16;
            int yo = this.tex / 16 * 16;
            bi.getRGB(xo, yo, 16, 16, this.raw, 0, 16);
            bi = ImageIO.read(Minecraft.class.getResource("/misc/dial.png"));
            bi.getRGB(0, 0, 16, 16, this.dialRaw, 0, 16);
        }
        catch (final IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void tick() {
        double rott = 0.0;
        if (this.mc.level != null && this.mc.player != null) {
            rott = -this.mc.level.getTimeOfDay(1.0f) * Mth.PI * 2.0f;
            if (this.mc.level.dimension.foggy) {
                rott = Math.random() * Math.PI * 2.0;
            }
        }

        double rotd = rott - this.rot;
        while (rotd < -Math.PI) rotd += Math.PI * 2;
        while (rotd >= Math.PI) rotd -= Math.PI * 2;
        if (rotd < -1.0) rotd = -1.0;
        if (rotd > 1.0) rotd = 1.0;

        this.rota += rotd * 0.1;
        this.rota *= 0.8;

        this.rot += this.rota;
        final double sin = Math.sin(this.rot);
        final double cos = Math.cos(this.rot);

        for (int i = 0; i < (16 * 16); ++i) {
            int a = this.raw[i] >> 24 & 0xFF;
            int r = this.raw[i] >> 16 & 0xFF;
            int g = this.raw[i] >> 8 & 0xFF;
            int b = this.raw[i] >> 0 & 0xFF;
            if (r == b && g == 0 && b > 0) {
                final double xo = -(i % 16 / 15.0 - 0.5);
                final double yo = i / 16 / 15.0 - 0.5;
                final int br = r;
                final int x = (int) ((xo * cos + yo * sin + 0.5) * 16.0);
                final int y = (int)((yo * cos - xo * sin + 0.5) * 16.0);
                final int j = (x & 0xF) + (y & 0xF) * 16;
                a = (this.dialRaw[j] >> 24 & 0xFF);
                r = (this.dialRaw[j] >> 16 & 0xFF) * br / 255;
                g = (this.dialRaw[j] >> 8 & 0xFF) * br / 255;
                b = (this.dialRaw[j] >> 0 & 0xFF) * br / 255;
            }

            if (this.anaglyph3d) {
                final int rr = (r * 30 + g * 59 + b * 11) / 100;
                final int gg = (r * 30 + g * 70) / 100;
                final int bb = (r * 30 + b * 70) / 100;
                r = rr;
                g = gg;
                b = bb;
            }

            this.pixels[i * 4 + 0] = (byte)r;
            this.pixels[i * 4 + 1] = (byte)g;
            this.pixels[i * 4 + 2] = (byte)b;
            this.pixels[i * 4 + 3] = (byte)a;
        }
    }
}
