// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.ptexture;

import net.minecraft.Pos;
import java.io.IOException;
import javax.imageio.ImageIO;
import net.minecraft.world.item.Item;
import net.minecraft.client.Minecraft;

public class CompassTexture extends DynamicTexture
{
    private Minecraft mc;
    private int[] raw;
    private double rot;
    private double rota;
    
    public CompassTexture(final Minecraft mc) {
        super(Item.compass.getIcon(0));
        this.raw = new int[256];
        this.mc = mc;
        this.textureId = 1;
        try {
            ImageIO.read(Minecraft.class.getResource("/gui/items.png")).getRGB(this.tex % 16 * 16, this.tex / 16 * 16, 16, 16, this.raw, 0, 16);
        }
        catch (final IOException ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    public void tick() {
        for (int i = 0; i < 256; ++i) {
            final int n = this.raw[i] >> 24 & 0xFF;
            int n2 = this.raw[i] >> 16 & 0xFF;
            int n3 = this.raw[i] >> 8 & 0xFF;
            int n4 = this.raw[i] >> 0 & 0xFF;
            if (this.anaglyph3d) {
                final int n5 = (n2 * 30 + n3 * 59 + n4 * 11) / 100;
                final int n6 = (n2 * 30 + n3 * 70) / 100;
                final int n7 = (n2 * 30 + n4 * 70) / 100;
                n2 = n5;
                n3 = n6;
                n4 = n7;
            }
            this.pixels[i * 4 + 0] = (byte)n2;
            this.pixels[i * 4 + 1] = (byte)n3;
            this.pixels[i * 4 + 2] = (byte)n4;
            this.pixels[i * 4 + 3] = (byte)n;
        }
        double n8 = 0.0;
        if (this.mc.level != null && this.mc.player != null) {
            final Pos sharedSpawnPos = this.mc.level.getSharedSpawnPos();
            n8 = (this.mc.player.yRot - 90.0f) * 3.141592653589793 / 180.0 - Math.atan2(sharedSpawnPos.z - this.mc.player.z, sharedSpawnPos.x - this.mc.player.x);
            if (this.mc.level.dimension.foggy) {
                n8 = Math.random() * 3.1415927410125732 * 2.0;
            }
        }
        double n9;
        for (n9 = n8 - this.rot; n9 < -3.141592653589793; n9 += 6.283185307179586) {}
        while (n9 >= 3.141592653589793) {
            n9 -= 6.283185307179586;
        }
        if (n9 < -1.0) {
            n9 = -1.0;
        }
        if (n9 > 1.0) {
            n9 = 1.0;
        }
        this.rota += n9 * 0.1;
        this.rota *= 0.8;
        this.rot += this.rota;
        final double sin = Math.sin(this.rot);
        final double cos = Math.cos(this.rot);
        for (int j = -4; j <= 4; ++j) {
            final int n10 = (int)(7.5 - sin * j * 0.3 * 0.5) * 16 + (int)(8.5 + cos * j * 0.3);
            int n11 = 100;
            int n12 = 100;
            int n13 = 100;
            final int n14 = 255;
            if (this.anaglyph3d) {
                final int n15 = (n11 * 30 + n12 * 59 + n13 * 11) / 100;
                final int n16 = (n11 * 30 + n12 * 70) / 100;
                final int n17 = (n11 * 30 + n13 * 70) / 100;
                n11 = n15;
                n12 = n16;
                n13 = n17;
            }
            this.pixels[n10 * 4 + 0] = (byte)n11;
            this.pixels[n10 * 4 + 1] = (byte)n12;
            this.pixels[n10 * 4 + 2] = (byte)n13;
            this.pixels[n10 * 4 + 3] = (byte)n14;
        }
        for (int k = -8; k <= 16; ++k) {
            final int n18 = (int)(7.5 + cos * k * 0.3 * 0.5) * 16 + (int)(8.5 + sin * k * 0.3);
            int n19 = (k >= 0) ? 255 : 100;
            int n20 = (k >= 0) ? 20 : 100;
            int n21 = (k >= 0) ? 20 : 100;
            final int n22 = 255;
            if (this.anaglyph3d) {
                final int n23 = (n19 * 30 + n20 * 59 + n21 * 11) / 100;
                final int n24 = (n19 * 30 + n20 * 70) / 100;
                final int n25 = (n19 * 30 + n21 * 70) / 100;
                n19 = n23;
                n20 = n24;
                n21 = n25;
            }
            this.pixels[n18 * 4 + 0] = (byte)n19;
            this.pixels[n18 * 4 + 1] = (byte)n20;
            this.pixels[n18 * 4 + 2] = (byte)n21;
            this.pixels[n18 * 4 + 3] = (byte)n22;
        }
    }
}
