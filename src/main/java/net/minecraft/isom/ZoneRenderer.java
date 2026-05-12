// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.isom;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;
import java.util.Arrays;
import java.awt.image.BufferedImage;
import net.minecraft.world.level.tile.Tile;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ZoneRenderer
{
    private float[] texCols;
    private int[] pixels;
    private int[] zBuf;
    private int[] waterBuf;
    private int[] waterBr;
    private int[] yBuf;
    private int[] textures;
    
    public ZoneRenderer() {
        this.texCols = new float[768];
        this.pixels = new int[5120];
        this.zBuf = new int[5120];
        this.waterBuf = new int[5120];
        this.waterBr = new int[5120];
        this.yBuf = new int[34];
        this.textures = new int[768];
        try {
            final BufferedImage read = ImageIO.read(ZoneRenderer.class.getResource("/terrain.png"));
            final int[] rgbArray = new int[65536];
            read.getRGB(0, 0, 256, 256, rgbArray, 0, 256);
            for (int i = 0; i < 256; ++i) {
                int n = 0;
                int n2 = 0;
                int n3 = 0;
                final int n4 = i % 16 * 16;
                final int n5 = i / 16 * 16;
                int n6 = 0;
                for (int j = 0; j < 16; ++j) {
                    for (int k = 0; k < 16; ++k) {
                        final int n7 = rgbArray[k + n4 + (j + n5) * 256];
                        if ((n7 >> 24 & 0xFF) > 128) {
                            n += (n7 >> 16 & 0xFF);
                            n2 += (n7 >> 8 & 0xFF);
                            n3 += (n7 & 0xFF);
                            ++n6;
                        }
                    }
                    if (n6 == 0) {
                        ++n6;
                    }
                    this.texCols[i * 3 + 0] = (float)(n / n6);
                    this.texCols[i * 3 + 1] = (float)(n2 / n6);
                    this.texCols[i * 3 + 2] = (float)(n3 / n6);
                }
            }
        }
        catch (final IOException ex) {
            ex.printStackTrace();
        }
        for (int l = 0; l < 256; ++l) {
            if (Tile.tiles[l] != null) {
                this.textures[l * 3 + 0] = Tile.tiles[l].getTexture(1);
                this.textures[l * 3 + 1] = Tile.tiles[l].getTexture(2);
                this.textures[l * 3 + 2] = Tile.tiles[l].getTexture(3);
            }
        }
    }
    
    public void render(final Zone zone) {
        final Level level = zone.level;
        if (level == null) {
            zone.noContent = true;
            zone.rendered = true;
            return;
        }
        final int n = zone.x * 16;
        final int n2 = zone.y * 16;
        final int n3 = n + 16;
        final int n4 = n2 + 16;
        if (level.getChunk(zone.x, zone.y).isEmpty()) {
            zone.noContent = true;
            zone.rendered = true;
            return;
        }
        zone.noContent = false;
        Arrays.fill(this.zBuf, 0);
        Arrays.fill(this.waterBuf, 0);
        Arrays.fill(this.yBuf, 160);
        for (int i = n4 - 1; i >= n2; --i) {
            for (int j = n3 - 1; j >= n; --j) {
                final int n5 = j - n;
                final int n6 = i - n2;
                final int n7 = n5 + n6;
                boolean b = true;
                for (int k = 0; k < 128; ++k) {
                    final int n8 = n6 - n5 - k + 160 - 16;
                    if (n8 < this.yBuf[n7] || n8 < this.yBuf[n7 + 1]) {
                        final Tile tile = Tile.tiles[level.getTile(j, k, i)];
                        if (tile == null) {
                            b = false;
                        }
                        else if (tile.material == Material.water) {
                            final int tile2 = level.getTile(j, k + 1, i);
                            if (tile2 == 0 || Tile.tiles[tile2].material != Material.water) {
                                final float n9 = level.getBrightness(j, k + 1, i) * (k / 127.0f * 0.6f + 0.4f);
                                if (n8 >= 0) {
                                    if (n8 < 160) {
                                        final int n10 = n7 + n8 * 32;
                                        if (n7 >= 0 && n7 <= 32 && this.waterBuf[n10] <= k) {
                                            this.waterBuf[n10] = k;
                                            this.waterBr[n10] = (int)(n9 * 127.0f);
                                        }
                                        if (n7 >= -1 && n7 <= 31 && this.waterBuf[n10 + 1] <= k) {
                                            this.waterBuf[n10 + 1] = k;
                                            this.waterBr[n10 + 1] = (int)(n9 * 127.0f);
                                        }
                                        b = false;
                                    }
                                }
                            }
                        }
                        else {
                            if (b) {
                                if (n8 < this.yBuf[n7]) {
                                    this.yBuf[n7] = n8;
                                }
                                if (n8 < this.yBuf[n7 + 1]) {
                                    this.yBuf[n7 + 1] = n8;
                                }
                            }
                            final float n11 = k / 127.0f * 0.6f + 0.4f;
                            if (n8 >= 0 && n8 < 160) {
                                final int n12 = n7 + n8 * 32;
                                final int n13 = this.textures[tile.id * 3 + 0];
                                final float n14 = (level.getBrightness(j, k + 1, i) * 0.8f + 0.2f) * n11;
                                final int n15 = n13;
                                if (n7 >= 0) {
                                    final float n16 = n14;
                                    if (this.zBuf[n12] <= k) {
                                        this.zBuf[n12] = k;
                                        this.pixels[n12] = (0xFF000000 | (int)(this.texCols[n15 * 3 + 0] * n16) << 16 | (int)(this.texCols[n15 * 3 + 1] * n16) << 8 | (int)(this.texCols[n15 * 3 + 2] * n16));
                                    }
                                }
                                if (n7 < 31) {
                                    final float n17 = n14 * 0.9f;
                                    if (this.zBuf[n12 + 1] <= k) {
                                        this.zBuf[n12 + 1] = k;
                                        this.pixels[n12 + 1] = (0xFF000000 | (int)(this.texCols[n15 * 3 + 0] * n17) << 16 | (int)(this.texCols[n15 * 3 + 1] * n17) << 8 | (int)(this.texCols[n15 * 3 + 2] * n17));
                                    }
                                }
                            }
                            if (n8 >= -1 && n8 < 159) {
                                final int n18 = n7 + (n8 + 1) * 32;
                                final int n19 = this.textures[tile.id * 3 + 1];
                                final float n20 = level.getBrightness(j - 1, k, i) * 0.8f + 0.2f;
                                final int n21 = this.textures[tile.id * 3 + 2];
                                final float n22 = level.getBrightness(j, k, i + 1) * 0.8f + 0.2f;
                                if (n7 >= 0) {
                                    final float n23 = n20 * n11 * 0.6f;
                                    if (this.zBuf[n18] <= k - 1) {
                                        this.zBuf[n18] = k - 1;
                                        this.pixels[n18] = (0xFF000000 | (int)(this.texCols[n19 * 3 + 0] * n23) << 16 | (int)(this.texCols[n19 * 3 + 1] * n23) << 8 | (int)(this.texCols[n19 * 3 + 2] * n23));
                                    }
                                }
                                if (n7 < 31) {
                                    final float n24 = n22 * 0.9f * n11 * 0.4f;
                                    if (this.zBuf[n18 + 1] <= k - 1) {
                                        this.zBuf[n18 + 1] = k - 1;
                                        this.pixels[n18 + 1] = (0xFF000000 | (int)(this.texCols[n21 * 3 + 0] * n24) << 16 | (int)(this.texCols[n21 * 3 + 1] * n24) << 8 | (int)(this.texCols[n21 * 3 + 2] * n24));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        this.postProcess();
        if (zone.image == null) {
            zone.image = new BufferedImage(32, 160, 2);
        }
        zone.image.setRGB(0, 0, 32, 160, this.pixels, 0, 32);
        zone.rendered = true;
    }
    
    private void postProcess() {
        for (int i = 0; i < 32; ++i) {
            for (int j = 0; j < 160; ++j) {
                final int n = i + j * 32;
                if (this.zBuf[n] == 0) {
                    this.pixels[n] = 0;
                }
                if (this.waterBuf[n] > this.zBuf[n]) {
                    final int n2 = this.pixels[n] >> 24 & 0xFF;
                    this.pixels[n] = ((this.pixels[n] & 0xFEFEFE) >> 1) + this.waterBr[n];
                    if (n2 < 128) {
                        this.pixels[n] = Integer.MIN_VALUE + this.waterBr[n] * 2;
                    }
                    else {
                        final int[] pixels = this.pixels;
                        final int n3 = n;
                        pixels[n3] |= 0xFF000000;
                    }
                }
            }
        }
    }
}
