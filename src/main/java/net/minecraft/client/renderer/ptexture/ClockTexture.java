// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.ptexture;

import java.io.IOException;
import javax.imageio.ImageIO;
import net.minecraft.world.item.Item;
import net.minecraft.client.Minecraft;

public class ClockTexture extends DynamicTexture
{
    private Minecraft mc;
    private int[] raw;
    private int[] dialRaw;
    private double rot;
    private double rota;
    
    public ClockTexture(final Minecraft mc) {
        super(Item.clock.getIcon(0));
        this.raw = new int[256];
        this.dialRaw = new int[256];
        this.mc = mc;
        this.textureId = 1;
        try {
            ImageIO.read(Minecraft.class.getResource("/gui/items.png")).getRGB(this.tex % 16 * 16, this.tex / 16 * 16, 16, 16, this.raw, 0, 16);
            ImageIO.read(Minecraft.class.getResource("/misc/dial.png")).getRGB(0, 0, 16, 16, this.dialRaw, 0, 16);
        }
        catch (final IOException ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    public void tick() {
        double n = 0.0;
        if (this.mc.level != null && this.mc.player != null) {
            n = -this.mc.level.getTimeOfDay(1.0f) * 3.1415927f * 2.0f;
            if (this.mc.level.dimension.foggy) {
                n = Math.random() * 3.1415927410125732 * 2.0;
            }
        }
        double n2;
        for (n2 = n - this.rot; n2 < -Math.PI; n2 += 6.283185307179586) {}
        while (n2 >= Math.PI) {
            n2 -= 6.283185307179586;
        }
        if (n2 < -1.0) {
            n2 = -1.0;
        }
        if (n2 > 1.0) {
            n2 = 1.0;
        }
        this.rota += n2 * 0.1;
        this.rota *= 0.8;
        this.rot += this.rota;
        final double sin = Math.sin(this.rot);
        final double cos = Math.cos(this.rot);
        for (int i = 0; i < 256; ++i) {
            int n3 = this.raw[i] >> 24 & 0xFF;
            int n4 = this.raw[i] >> 16 & 0xFF;
            int n5 = this.raw[i] >> 8 & 0xFF;
            int n6 = this.raw[i] >> 0 & 0xFF;
            if (n4 == n6 && n5 == 0 && n6 > 0) {
                final double n7 = -(i % 16 / 15.0 - 0.5);
                final double n8 = i / 16 / 15.0 - 0.5;
                final int n9 = n4;
                final int n10 = ((int)((n7 * cos + n8 * sin + 0.5) * 16.0) & 0xF) + ((int)((n8 * cos - n7 * sin + 0.5) * 16.0) & 0xF) * 16;
                n3 = (this.dialRaw[n10] >> 24 & 0xFF);
                n4 = (this.dialRaw[n10] >> 16 & 0xFF) * n9 / 255;
                n5 = (this.dialRaw[n10] >> 8 & 0xFF) * n9 / 255;
                n6 = (this.dialRaw[n10] >> 0 & 0xFF) * n9 / 255;
            }
            if (this.anaglyph3d) {
                final int n11 = (n4 * 30 + n5 * 59 + n6 * 11) / 100;
                final int n12 = (n4 * 30 + n5 * 70) / 100;
                final int n13 = (n4 * 30 + n6 * 70) / 100;
                n4 = n11;
                n5 = n12;
                n6 = n13;
            }
            this.pixels[i * 4 + 0] = (byte)n4;
            this.pixels[i * 4 + 1] = (byte)n5;
            this.pixels[i * 4 + 2] = (byte)n6;
            this.pixels[i * 4 + 3] = (byte)n3;
        }
    }
}
