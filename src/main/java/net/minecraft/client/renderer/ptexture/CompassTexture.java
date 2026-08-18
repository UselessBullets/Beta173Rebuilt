// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.ptexture;

import net.minecraft.Pos;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import net.minecraft.world.item.Item;
import net.minecraft.client.Minecraft;

public class CompassTexture extends DynamicTexture
{
    private Minecraft mc;
    private int[] raw = new int[16 * 16];
    private double rot, rota;
    
    public CompassTexture(final Minecraft mc) {
        super(Item.compass.getIcon(0));
        this.mc = mc;
        this.textureId = TEXTURE_ITEMS;
        try {
            BufferedImage bi = ImageIO.read(Minecraft.class.getResource("/gui/items.png"));
            int xo = this.tex % 16 * 16;
            int yo = this.tex / 16 * 16;
            bi.getRGB(xo, yo, 16, 16, this.raw, 0, 16);
        }
        catch (final IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void tick() {
        for (int i = 0; i < (16 * 16); ++i) {
            int a = this.raw[i] >> 24 & 0xFF;
            int r = this.raw[i] >> 16 & 0xFF;
            int g = this.raw[i] >> 8 & 0xFF;
            int b = this.raw[i] >> 0 & 0xFF;
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

        double rott = 0.0;
        if (this.mc.level != null && this.mc.player != null) {
            final Pos spawnPos = this.mc.level.getSharedSpawnPos();
            double xa = spawnPos.x - this.mc.player.x;
            double za = spawnPos.z - this.mc.player.z;
            rott = (this.mc.player.yRot - 90.0f) * Math.PI / 180.0 - Math.atan2(za, xa);
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

        for (int d = -4; d <= 4; ++d) {
            int x = (int)(8.5 + cos * d * 0.3);
            int y = (int)(7.5 - sin * d * 0.3 * 0.5);
            final int i = y * 16 + x;

            int r = 100;
            int g = 100;
            int b = 100;
            int a = 255;
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
        for (int d = -8; d <= 16; ++d) {
            int x = (int)(8.5 + sin * d * 0.3);
            int y = (int)(7.5 + cos * d * 0.3 * 0.5);
            int i = y * 16 + x;

            int r = (d >= 0) ? 255 : 100;
            int g = (d >= 0) ? 20 : 100;
            int b = (d >= 0) ? 20 : 100;
            int a = 255;
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
