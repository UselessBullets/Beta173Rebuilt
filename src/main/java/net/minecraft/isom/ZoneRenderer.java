// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.isom;

import net.minecraft.Facing;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.Material;
import java.util.Arrays;
import java.awt.image.BufferedImage;
import net.minecraft.world.level.tile.Tile;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ZoneRenderer
{
    private static final int IMG_WIDTH = 32;
    private static final int IMG_HEIGHT = 160;
    private float[] texCols = new float[Tile.TILE_NUM_COUNT * 3];
    private int[] pixels = new int[IMG_WIDTH * IMG_HEIGHT];
    private int[] zBuf = new int[IMG_WIDTH * IMG_HEIGHT];
    private int[] waterBuf = new int[IMG_WIDTH * IMG_HEIGHT];
    private int[] waterBr = new int[IMG_WIDTH * IMG_HEIGHT];
    private int[] yBuf = new int[17 + 17];
    private int[] textures = new int[Tile.TILE_NUM_COUNT * 3];
    
    public ZoneRenderer() {
        try {
            final BufferedImage read = ImageIO.read(ZoneRenderer.class.getResource("/terrain.png"));
            final int[] rgbArray = new int[256 * 256];
            read.getRGB(0, 0, 256, 256, rgbArray, 0, 256);

            for (int i = 0; i < 256; ++i) {
                int r = 0;
                int g = 0;
                int b = 0;
                final int xo = i % 16 * 16;
                final int yo = i / 16 * 16;
                int count = 0;

                for (int y = 0; y < 16; ++y) {
                    for (int x = 0; x < 16; ++x) {
                        final int col = rgbArray[x + xo + (y + yo) * 256];
                        int a = col >> 24 & 0xFF;
                        if (a > 128) {
                            r += (col >> 16 & 0xFF);
                            g += (col >> 8 & 0xFF);
                            b += (col & 0xFF);
                            ++count;
                        }
                    }

                    if (count == 0) {
                        ++count;
                    }

                    this.texCols[i * 3 + 0] = (float)(r / count);
                    this.texCols[i * 3 + 1] = (float)(g / count);
                    this.texCols[i * 3 + 2] = (float)(b / count);
                }
            }
        }
        catch (final IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < Tile.TILE_NUM_COUNT; ++i) {
            if (Tile.tiles[i] != null) {
                this.textures[i * 3 + 0] = Tile.tiles[i].getTexture(Facing.UP);
                this.textures[i * 3 + 1] = Tile.tiles[i].getTexture(Facing.NORTH);
                this.textures[i * 3 + 2] = Tile.tiles[i].getTexture(Facing.SOUTH);
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

        final int x0 = zone.x * 16;
        final int z0 = zone.y * 16;
        final int x1 = x0 + 16;
        final int z1 = z0 + 16;
        LevelChunk chunk = level.getChunk(zone.x, zone.y);
        if (chunk.isEmpty()) {
            zone.noContent = true;
            zone.rendered = true;
            return;
        }

        zone.noContent = false;
        Arrays.fill(this.zBuf, 0);
        Arrays.fill(this.waterBuf, 0);
        Arrays.fill(this.yBuf, IMG_HEIGHT);

        for (int z = z1 - 1; z >= z0; --z) {
            for (int x = x1 - 1; x >= x0; --x) {
                final int xx = x - x0;
                final int zz = z - z0;
                final int xp = xx + zz;
                boolean solid = true;

                for (int y = Level.minBuildHeight; y < Level.maxBuildHeight; ++y) {
                    final int yp = zz - xx - y + IMG_HEIGHT - 16;
                    if (yp < this.yBuf[xp] || yp < this.yBuf[xp + 1]) {
                        final Tile t = Tile.tiles[level.getTile(x, y, z)];
                        if (t == null) {
                            solid = false;
                        } else if (t.material == Material.water) {
                            final int ta = level.getTile(x, y + 1, z);
                            if (ta == 0 || Tile.tiles[ta].material != Material.water) {
                                float hh = y / 127.0f * 0.6f + 0.4f;
                                final float br = level.getBrightness(x, y + 1, z) * hh;
                                if (yp >= 0 && yp < IMG_HEIGHT) {
                                    final int p = xp + yp * IMG_WIDTH;
                                    if (xp >= 0 && xp <= (IMG_WIDTH) && this.waterBuf[p] <= y) {
                                        this.waterBuf[p] = y;
                                        this.waterBr[p] = (int) (br * 127.0f);
                                    }

                                    if (xp >= -1 && xp <= (IMG_WIDTH - 1) && this.waterBuf[p + 1] <= y) {
                                        this.waterBuf[p + 1] = y;
                                        this.waterBr[p + 1] = (int) (br * 127.0f);
                                    }

                                    solid = false;
                                }
                            }
                        }
                        else {
                            if (solid) {
                                if (yp < this.yBuf[xp]) {
                                    this.yBuf[xp] = yp;
                                }

                                if (yp < this.yBuf[xp + 1]) {
                                    this.yBuf[xp + 1] = yp;
                                }
                            }

                            final float hh = y / 127.0f * 0.6f + 0.4f;
                            if (yp >= 0 && yp < IMG_HEIGHT) {
                                final int p = xp + yp * IMG_WIDTH;
                                final int upTex = this.textures[t.id * 3 + 0];
                                final float upBr = (level.getBrightness(x, y + 1, z) * 0.8f + 0.2f) * hh;
                                final int tex = upTex;
                                if (xp >= 0) {
                                    final float br = upBr;
                                    if (this.zBuf[p] <= y) {
                                        this.zBuf[p] = y;
                                        this.pixels[p] = (0xFF000000
                                                | (int)(this.texCols[tex * 3 + 0] * br) << 16
                                                | (int)(this.texCols[tex * 3 + 1] * br) << 8
                                                | (int)(this.texCols[tex * 3 + 2] * br));
                                    }
                                }

                                if (xp < (IMG_WIDTH - 1)) {
                                    final float br = upBr * 0.9f;
                                    if (this.zBuf[p + 1] <= y) {
                                        this.zBuf[p + 1] = y;
                                        this.pixels[p + 1] = (0xFF000000
                                                | (int)(this.texCols[tex * 3 + 0] * br) << 16
                                                | (int)(this.texCols[tex * 3 + 1] * br) << 8
                                                | (int)(this.texCols[tex * 3 + 2] * br));
                                    }
                                }
                            }

                            if (yp >= -1 && yp < (IMG_HEIGHT - 1)) {
                                final int p = xp + (yp + 1) * IMG_WIDTH;
                                final int lTex = this.textures[t.id * 3 + 1];
                                final float lBr = level.getBrightness(x - 1, y, z) * 0.8f + 0.2f;
                                final int rTex = this.textures[t.id * 3 + 2];
                                final float rBr = level.getBrightness(x, y, z + 1) * 0.8f + 0.2f;
                                if (xp >= 0) {
                                    final float br = lBr * hh * 0.6f;
                                    if (this.zBuf[p] <= y - 1) {
                                        this.zBuf[p] = y - 1;
                                        this.pixels[p] = (0xFF000000
                                                | (int)(this.texCols[lTex * 3 + 0] * br) << 16
                                                | (int)(this.texCols[lTex * 3 + 1] * br) << 8
                                                | (int)(this.texCols[lTex * 3 + 2] * br));
                                    }
                                }

                                if (xp < (IMG_WIDTH - 1)) {
                                    final float br = rBr * 0.9f * hh * 0.4f;
                                    if (this.zBuf[p + 1] <= y - 1) {
                                        this.zBuf[p + 1] = y - 1;
                                        this.pixels[p + 1] = (0xFF000000
                                                | (int)(this.texCols[rTex * 3 + 0] * br) << 16
                                                | (int)(this.texCols[rTex * 3 + 1] * br) << 8
                                                | (int)(this.texCols[rTex * 3 + 2] * br));
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
            zone.image = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, 2);
        }

        zone.image.setRGB(0, 0, IMG_WIDTH, IMG_HEIGHT, this.pixels, 0, IMG_WIDTH);
        zone.rendered = true;
    }
    
    private void postProcess() {
        for (int x = 0; x < IMG_WIDTH; ++x) {
            for (int y = 0; y < IMG_HEIGHT; ++y) {
                final int p = x + y * IMG_WIDTH;
                if (this.zBuf[p] == 0) {
                    this.pixels[p] = 0;
                }
                if (this.waterBuf[p] > this.zBuf[p]) {
                    final int a = this.pixels[p] >> 24 & 0xFF;
                    this.pixels[p] = ((this.pixels[p] & 0xFEFEFE) >> 1) + this.waterBr[p];
                    if (a < 128) {
                        this.pixels[p] = Integer.MIN_VALUE + this.waterBr[p] * 2;
                    }
                    else {
                        this.pixels[p] |= 0xFF000000;
                    }
                }
            }
        }
    }
}
