package net.minecraft.isom;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Tile;

// Useless - Isom package was pulled straight from the Beta 1.9-pre6 jar as it had source for this package, thanks @genericpnpmonit0r for telling me this
public class ZoneRenderer {
    private static final int IMG_WIDTH = Zone.CHUNK_SIZE * 2;
    private static final int IMG_HEIGHT = Zone.CHUNK_SIZE * 2 + 128; // Useless - Was + 512 in b1.9-pre6 leak but + 128 matches b1.7.3
    private float[] texCols = new float[256 * 3];

    private int[] pixels = new int[IMG_WIDTH * IMG_HEIGHT];
    private int[] zBuf = new int[IMG_WIDTH * IMG_HEIGHT];
    private int[] waterBuf = new int[IMG_WIDTH * IMG_HEIGHT];
    private int[] waterBr = new int[IMG_WIDTH * IMG_HEIGHT];
    private int[] yBuf = new int[IMG_WIDTH + 2];
    private int[] textures = new int[256 * 3];

    public ZoneRenderer() {
        try {
            BufferedImage img = ImageIO.read(ZoneRenderer.class.getResource("/terrain.png"));
            int[] cols = new int[256 * 256];
            img.getRGB(0, 0, 256, 256, cols, 0, 256);
            for (int i = 0; i < 256; i++) {
                int r = 0;
                int g = 0;
                int b = 0;
                int xo = i % 16 * 16;
                int yo = i / 16 * 16;
                int count = 0;
                for (int y = 0; y < 16; y++) {
                    for (int x = 0; x < 16; x++) {
                        int col = cols[(x + xo) + (y + yo) * 256];
                        int a = (col >> 24 & 0xff);
                        if (a > 128) {
                            r += (col >> 16) & 0xff;
                            g += (col >> 8) & 0xff;
                            b += (col) & 0xff;
                            count++;
                        }
                    }
                    if (count == 0) count++;
                    texCols[i * 3 + 0] = r / count;
                    texCols[i * 3 + 1] = g / count;
                    texCols[i * 3 + 2] = b / count;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < 256; i++) {
            if (Tile.tiles[i] != null) {
                textures[i * 3 + 0] = Tile.tiles[i].getTexture(1);
                textures[i * 3 + 1] = Tile.tiles[i].getTexture(2);
                textures[i * 3 + 2] = Tile.tiles[i].getTexture(3);
            }
        }
    }

    public void render(Zone zone) {

        Level level = zone.level;
        if (level == null) {
            zone.noContent = true;
            zone.rendered = true;
            return;
        }

        int x0 = zone.x * Zone.CHUNK_SIZE;
        int z0 = zone.y * Zone.CHUNK_SIZE;
        int x1 = x0 + Zone.CHUNK_SIZE;
        int z1 = z0 + Zone.CHUNK_SIZE;
        LevelChunk chunk = level.getChunk(zone.x, zone.y);
        if (chunk.isEmpty()) {
            zone.noContent = true;
            zone.rendered = true;
            return;
        }
        zone.noContent = false;

        //        Arrays.fill(pixels, 0);
        Arrays.fill(zBuf, 0);
        Arrays.fill(waterBuf, 0);
        Arrays.fill(yBuf, IMG_HEIGHT);

        for (int z = z1 - 1; z >= z0; z--) {
            for (int x = x1 - 1; x >= x0; x--) {
                int xx = x - x0;
                int zz = z - z0;

                int xp = xx + zz;
                boolean solid = true;
                for (int y = 0; y < Level.MAX_HEIGHT /*Useless - was level.depth in b1.9-pre6*/; y++) {
                    int yp = (zz - xx) - y + IMG_HEIGHT - Zone.CHUNK_SIZE;

                    if (yp >= yBuf[xp] && yp >= yBuf[xp + 1]) continue;


                    Tile t = Tile.tiles[level.getTile(x, y, z)];
                    if (t == null) {
                        solid = false;
                    } else {
                        if (t.material == Material.water) {
                            int ta = level.getTile(x, y + 1, z);
                            if (ta == 0 || Tile.tiles[ta].material != Material.water) {
                                float hh = (y / (Level.MAX_HEIGHT /*Useless - was level.depth in b1.9-pre6*/ - 1.0f)) * 0.6f + 0.4f;
                                float br = level.getBrightness(x, y + 1, z) * hh;

                                if (yp < 0 || yp >= IMG_HEIGHT) continue;
                                //                                if (x < 0 || y < 0 || x >= IMG_WIDTH || y >= IMG_HEIGHT) {
                                //                                    return;
                                //                                }

                                int p = xp + yp * IMG_WIDTH;

                                if (xp >= 0 && xp <= IMG_WIDTH) {
                                    if (waterBuf[p] <= y) {
                                        waterBuf[p] = y;
                                        waterBr[p] = (int) (br * 127);
                                    }
                                }
                                if (xp >= -1 && xp <= IMG_WIDTH - 1) {
                                    if (waterBuf[p + 1] <= y) {
                                        waterBuf[p + 1] = y;
                                        waterBr[p + 1] = (int) (br * 127);
                                    }
                                }


                                //                                blendPix(xp + 0, yp + 0, y, br * hh);
                                //                                blendPix(xp + 1, yp + 0, y, br * hh);
                                solid = false;
                            }
                        } else {
                            if (solid) {
                                if (yp < yBuf[xp]) yBuf[xp] = yp;
                                if (yp < yBuf[xp + 1]) yBuf[xp + 1] = yp;
                            }

                            float hh = (y / (Level.MAX_HEIGHT /*Useless - was level.depth in b1.9-pre6*/ - 1.0f)) * 0.6f + 0.4f;

                            if (yp >= 0 && yp < IMG_HEIGHT) {
                                int p = xp + yp * IMG_WIDTH;
                                int upTex = textures[t.id * 3 + 0];
                                float upBr = (level.getBrightness(x, y + 1, z) * 0.8f + 0.2f) * hh;
                                int tex = upTex;

                                if (xp >= 0) {
                                    float br = upBr;
                                    if (zBuf[p] <= y) {
                                        zBuf[p] = y;
                                        pixels[p] = 255 << 24 | ((int) (texCols[tex * 3 + 0] * br)) << 16 | ((int) (texCols[tex * 3 + 1] * br)) << 8 | ((int) (texCols[tex * 3 + 2] * br));
                                    }
                                }
                                if (xp < IMG_WIDTH - 1) {
                                    float br = upBr * 0.9f;
                                    if (zBuf[p + 1] <= y) {
                                        zBuf[p + 1] = y;
                                        pixels[p + 1] = 255 << 24 | ((int) (texCols[tex * 3 + 0] * br)) << 16 | ((int) (texCols[tex * 3 + 1] * br)) << 8 | ((int) (texCols[tex * 3 + 2] * br));
                                    }
                                }
                            }

                            if (yp >= -1 && yp < IMG_HEIGHT - 1) {
                                int p = xp + (yp + 1) * IMG_WIDTH;
                                int lTex = textures[t.id * 3 + 1];
                                float lBr = level.getBrightness(x - 1, y, z) * 0.8f + 0.2f;
                                int rTex = textures[t.id * 3 + 2];
                                float rBr = level.getBrightness(x, y, z + 1) * 0.8f + 0.2f;

                                if (xp >= 0) {
                                    float br = lBr * hh * 0.6f;
                                    if (zBuf[p] <= y - 1) {
                                        zBuf[p] = y - 1;
                                        pixels[p] = 255 << 24 | ((int) (texCols[lTex * 3 + 0] * br)) << 16 | ((int) (texCols[lTex * 3 + 1] * br)) << 8 | ((int) (texCols[lTex * 3 + 2] * br));
                                    }
                                }
                                if (xp < IMG_WIDTH - 1) {
                                    float br = rBr * 0.9f * hh * 0.4f;
                                    if (zBuf[p + 1] <= y - 1) {
                                        zBuf[p + 1] = y - 1;
                                        pixels[p + 1] = 255 << 24 | ((int) (texCols[rTex * 3 + 0] * br)) << 16 | ((int) (texCols[rTex * 3 + 1] * br)) << 8 | ((int) (texCols[rTex * 3 + 2] * br));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        postProcess();

        if (zone.image == null) {
            zone.image = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        }
        zone.image.setRGB(0, 0, IMG_WIDTH, IMG_HEIGHT, pixels, 0, IMG_WIDTH);
        zone.rendered = true;
    }

    private void postProcess() {
        for (int x = 0; x < IMG_WIDTH; x++) {
            for (int y = 0; y < IMG_HEIGHT; y++) {
                int p = x + y * IMG_WIDTH;
                if (zBuf[p] == 0) {
                    pixels[p] = 0;
                }
                if (waterBuf[p] > zBuf[p]) {
                    int a = (pixels[p] >> 24) & 0xff;
                    pixels[p] = ((pixels[p] & 0xfefefe) >> 1) + waterBr[p];
                    if (a < 128) {
                        pixels[p] = (128 << 24) + waterBr[p] * 2;
                    } else {
                        pixels[p] |= 255 << 24;
                    }
                }
            }
        }
    }
}
